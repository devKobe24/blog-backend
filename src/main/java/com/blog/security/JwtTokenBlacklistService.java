package com.blog.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenBlacklistService {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

	/**
	 * 토큰을 블랙리스트에 추가
	 * @param token JWT 토큰
	 * @param expirationTime 토큰 만료 시간 (밀리초)
	 */
	public void addToBlacklist(String token, long expirationTime) {
		try {
			String key = BLACKLIST_PREFIX + token;
			// 토큰의 남은 만료 시간만큼만 블랙리스트에 저장
			long timeToExpire = calculateTimeToExpire(expirationTime);
			if (timeToExpire > 0) {
				redisTemplate.opsForValue().set(key, "blacklisted", timeToExpire, TimeUnit.MILLISECONDS);
				log.info("토큰이 블랙리스트에 추가되었습니다. 만료 시간: {}ms", timeToExpire);
			}
		} catch (Exception e) {
			log.error("톸큰 블랙리스트 추가 중 오류 발생: {}", e.getMessage(), e);
		}
	}

	/**
	 * 토큰이 블랙리스트에 있는지 확인
	 * @param token JWT 토큰
	 * @return 블랙리스트에 있으면 true, 없으면 false
	 */
	public boolean isBlacklisted(String token) {
		try {
			String key = BLACKLIST_PREFIX + token;
			String value = redisTemplate.opsForValue().get(key);
			boolean isBlacklisted = value != null;

			if (isBlacklisted) {
				log.warn("블랙리스트된 토큰이 사용되었습니다: {}", maskToken(token));
			}

			return isBlacklisted;
		} catch (Exception e) {
			log.error("토큰 블랙리스트 확인 중 오류 발생: {}", e.getStackTrace(), e);
			// Redis 오류 시 보안을 위해 true 반환 (토큰 거부)
			return true;
		}
	}

	/**
	 * 블랙리스트에서 토큰 제거 (필요시 사용)
	 * @param token JWT 토큰
	 */
	public void removeFromBlacklist(String token) {
		try {
			String key = BLACKLIST_PREFIX + token;
			redisTemplate.delete(key);
			log.info("토큰이 블랙리스트에서 제거되었습니다: {}", maskToken(token));
		} catch (Exception e) {
			log.error("토큰 블랙리스트 제거 중 오류 발생: {}", e.getMessage(), e);
		}
	}

	/**
	 * 토큰의 남은 만료 시간 계산
	 * @param expirationTime 토큰 만료 시간 (밀리초)
	 * @return 남은 시간 (밀리초)
	 */
	private long calculateTimeToExpire(long expirationTime) {
		long currentTime = System.currentTimeMillis();
		return Math.max(0, expirationTime - currentTime);
	}

	/**
	 * 토큰 마스킹 (로그용)
	 * @param token 원본 토큰
	 * @return 마스킹된 토큰
	 */
	private String maskToken(String token) {
		if (token == null || token.length() < 10) {
			return "***";
		}
		return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
	}

	/**
	 * 블랙리스트 크기 조회 (모니터링용)
	 * @return 블랙리스트에 있는 토큰 수
	 */
	public long getBlacklistSize() {
		try {
			return redisTemplate.keys(BLACKLIST_PREFIX + "*").size();
		} catch (Exception e) {
			log.error("블랙리스트 크기 조회 중 오류 발생: {}", e.getMessage(), e);
			return -1;
		}
	}
}
