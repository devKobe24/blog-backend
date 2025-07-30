package com.blog.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

	private final RedisTemplate<String, String> redisTemplate;

	private static final String RATE_LIMIT_PREFIX = "rate_limit:";
	private static final String IP_PREFIX = "ip:";
	private static final String USER_PREFIX = "user:";

	// 기본 Rate Limit 설정
	private static final int DEFAULT_MAX_REQUESTS = 100;
	private static final int DEFAULT_WINDOW_SECONDS = 60;

	// 로그인 Rate Limit 설정 (Brute Force 방지)
	private static final int LOGIN_MAX_REQUESTS = 5;
	private static final int LOGIN_WINDOW_SECONDS = 300; // 5분

	// API Rate Limit 설정
	private static final int API_MAX_REQUESTS = 1000;
	private static final int API_WINDOW_SECONDS = 3600; // 1시간

	/**
	 * IP 기반 Rate Limit 확인
	 * @param ipAddress IP 주소
	 * @param endpoint 엔드포인트 (예: "login", "api")
	 * @return Rate Limit 초과 여부
	 */
	public boolean isRateLimitExceeded(String ipAddress, String endpoint) {
		String key = RATE_LIMIT_PREFIX + IP_PREFIX + endpoint + ":" + ipAddress;
		return checkRateLimit(key, getMaxRequests(endpoint), getWindowSeconds(endpoint));
	}

	/**
	 * 사용자 기반 Rate Limit 확인
	 * @param username 사용자명
	 * @param endpoint 엔드포인트
	 * @return Rate Limit 초과 여부
	 */
	public boolean isUserRateLimitExceeded(String username, String endpoint) {
		String key = RATE_LIMIT_PREFIX + USER_PREFIX + endpoint + ":" + username;
		return checkRateLimit(key, getMaxRequests(endpoint), getWindowSeconds(endpoint));
	}

	/**
	 * Rate Limit 확인 및 업데이트
	 * @param key Redis 키
	 * @param maxRequests 최대 요청 수
	 * @param windowSeconds 시간 윈도우 (초)
	 * @return Rate Limit 초과 여부
	 */
	private boolean checkRateLimit(String key, int maxRequests, int windowSeconds) {
		try {
			String currentCount = redisTemplate.opsForValue().get(key);
			int count = currentCount != null ? Integer.parseInt(currentCount) : 0;

			if (count >= maxRequests) {
				log.warn("Rate limit exceeded for key: {}", key);
				return true;
			}

			// 요청 수 증가
			redisTemplate.opsForValue().increment(key);

			// 첫 번째 요청인 경우 TTL 설정
			if (count == 0) {
				redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
			}

			return false;
		} catch (Exception e) {
			log.error("Rate limit check failed for key: {}", key, e);
			// Redis 오류 시 보안을 위해 false 반환 (요청 허용)
			return false;
		}
	}

	/**
	 * 엔드포인트별 최대 요청 수 반환
	 * @param endpoint 엔드포인트
	 * @return 최대 요청 수
	 */
	private int getMaxRequests(String endpoint) {
		return switch (endpoint.toLowerCase()) {
			case "login" -> LOGIN_MAX_REQUESTS;
			case "api" -> API_MAX_REQUESTS;
			default -> DEFAULT_MAX_REQUESTS;
		};
	}

	/**
	 * 엔드포인트별 시간 윈도우 반환
	 * @param endpoint 엔드포인트
	 * @return 시간 윈도우 (초)
	 */
	private int getWindowSeconds(String endpoint) {
		return switch (endpoint.toLowerCase()) {
			case "login" -> LOGIN_WINDOW_SECONDS;
			case "api" -> API_WINDOW_SECONDS;
			default -> DEFAULT_WINDOW_SECONDS;
		};
	}

	/**
	 * Rate Limit 정보 조회
	 * @param ipAddress IP 주소
	 * @param endpoint 엔드포인트
	 * @return Rate Limit 정보
	 */
	public RateLimitInfo getRateLimitInfo(String ipAddress, String endpoint) {
		String key = RATE_LIMIT_PREFIX + IP_PREFIX + endpoint + ":" + ipAddress;

		try {
			String currentCount = redisTemplate.opsForValue().get(key);
			Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

			int count = currentCount != null ? Integer.parseInt(currentCount) : 0;
			int maxRequests = getMaxRequests(endpoint);
			int remaining = Math.max(0, maxRequests - count);

			return new RateLimitInfo(count, maxRequests, remaining, ttl != null ? ttl : 0);
		} catch (Exception e) {
			log.error("Failed to get rate limit info for key: {}", key, e);
			return new RateLimitInfo(0, getMaxRequests(endpoint), getMaxRequests(endpoint), 0);
		}
	}

	/**
	 * Rate Limit 리셋 (관리자용)
	 * @param ipAddress IP 주소
	 * @param endpoint 엔드포인트
	 */
	public void resetRateLimit(String ipAddress, String endpoint) {
		String key = RATE_LIMIT_PREFIX + IP_PREFIX + endpoint + ":" + ipAddress;
		try {
			redisTemplate.delete(key);
			log.info("Rate limit reset for key: {}", key);
		} catch (Exception e) {
			log.error("Failed to reset rate limit for key: {}", key, e);
		}
	}

	/**
	 * Rate Limit 통계 조회 (관리자용)
	 * @return Rate Limit 통계
	 */
	public RateLimitStats getRateLimitStats() {
		try {
			String pattern = RATE_LIMIT_PREFIX + "*";
			var keys = redisTemplate.keys(pattern);

			int totalKeys = keys != null ? keys.size() : 0;
			int ipLimits = keys != null ? (int) keys.stream().filter(key -> key.contains(IP_PREFIX)).count() : 0;
			int userLimits = keys != null ? (int) keys.stream().filter(key -> key.contains(USER_PREFIX)).count() : 0;

			return new RateLimitStats(totalKeys, ipLimits, userLimits);
		} catch (Exception e) {
			log.error("Failed to get rate limit stats", e);
			return new RateLimitStats(0, 0, 0);
		}
	}

	/**
	 * Rate Limit 정보 클래스
	 */
	public static class RateLimitInfo {
		private final int currentCount;
		private final int maxRequests;
		private final int remaining;
		private final long ttlSeconds;

		public RateLimitInfo(int currentCount, int maxRequests, int remaining, long ttlSeconds) {
			this.currentCount = currentCount;
			this.maxRequests = maxRequests;
			this.remaining = remaining;
			this.ttlSeconds = ttlSeconds;
		}

		public int getCurrentCount() { return currentCount; }
		public int getMaxRequests() { return maxRequests; }
		public int getRemaining() { return remaining; }
		public long getTtlSeconds() { return ttlSeconds; }
	}

	/**
	 * Rate Limit 통계 클래스
	 */
	public static class RateLimitStats {
		private final int totalKeys;
		private final int ipLimits;
		private final int userLimits;

		public RateLimitStats(int totalKeys, int ipLimits, int userLimits) {
			this.totalKeys = totalKeys;
			this.ipLimits = ipLimits;
			this.userLimits = userLimits;
		}

		public int getTotalKeys() { return totalKeys; }
		public int getIpLimits() { return ipLimits; }
		public int getUserLimits() { return userLimits; }
	}
}
