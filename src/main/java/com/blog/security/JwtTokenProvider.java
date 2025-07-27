package com.blog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private Long expiration;

	@Value("${jwt.refresh-expiration}")
	private Long refreshExpiration;

	private final JwtTokenBlacklistService blacklistService;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String generateAccessToken(String username) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration);
		return Jwts.builder()
			.subject(username)
			.issuedAt(now)
			.expiration(expiryDate)
			.signWith(getSigningKey())
			.compact();
	}

	public String generateRefreshToken(String username) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + refreshExpiration);
		return Jwts.builder()
			.subject(username)
			.issuedAt(now)
			.expiration(expiryDate)
			.signWith(getSigningKey())
			.compact();
	}

	public boolean validateToken(String token) {
		try {
			// 블랙리스트 확인
			if (blacklistService.isBlacklisted(token)) {
				log.warn("블랙리스트된 토큰이 사용되었습니다: {}", maskToken(token));
			}
			Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			log.warn("토큰 검증 실패: {}", e.getMessage());
			return false;
		}
	}

	public boolean isTokenExpired(String token) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
			return claims.getExpiration().before(new Date());
		} catch (JwtException | IllegalArgumentException e) {
			return true;
		}
	}

	/**
	 * 토큰에서 사용자 명 추출
	 *
	 * @param token JWT 토큰
	 * @return 사용자명
	 */
	public String getUsernameFromToken(String token) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
			return claims.getSubject();
		} catch (JwtException | IllegalArgumentException e) {
			log.warn("토큰에서 사용자명 추출 실패: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * 토큰의 만료 시간 추출
	 *
	 * @param token JWT 토큰
	 * @return 만료 시간 (밀리초)
	 */
	public long getExpirationTime(String token) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
			return claims.getExpiration().getTime();
		} catch (JwtException | IllegalArgumentException e) {
			log.warn("토큰에서 만료 시간 추출 실패: {}", e.getMessage());
			return 0;
		}
	}

	/**
	 * 토큰을 블랙리스트에 추가
	 *
	 * @param token JWT 토큰
	 */
	public void invalidateToken(String token) {
		long expirationTime = getExpirationTime(token);
		if (expirationTime > 0) {
			blacklistService.addToBlacklist(token, expirationTime);
			log.info("토큰이 무효화되었습니다: {}", maskToken(token));
		}
	}

	/**
	 * 토큰 마스킹 (로그용)
	 *
	 * @param token 원본 토큰
	 * @return 마스킹된 토큰
	 */
	private String maskToken(String token) {
		if (token == null || token.length() < 10) {
			return "***";
		}
		return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
	}
}
