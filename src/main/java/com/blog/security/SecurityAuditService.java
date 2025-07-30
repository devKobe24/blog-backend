package com.blog.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityAuditService {

	private final RedisTemplate<String, String> redisTemplate;

	private static final String AUDIT_LOG_PREFIX = "security:audit:";
	private static final String SECURITY_EVENT_PREFIX = "security:event:";
	private static final String SUSPICIOUS_ACTIVITY_PREFIX = "security:suspicious:";

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * 로그인 성공 이벤트 로깅
	 */
	public void logLoginSuccess(String username, String ipAddress, String userAgent) {
		Map<String, Object> event = createSecurityEvent("LOGIN_SUCCESS", username, ipAddress, userAgent);
		event.put("details", "사용자 로그인 성공");

		logSecurityEvent(event);
		log.info("로그인 성공 - 사용자: {}, IP: {}", username, maskIpAddress(ipAddress));
	}

	/**
	 * 로그인 실패 이벤트 로깅
	 */
	public void logLoginFailure(String username, String ipAddress, String userAgent, String reason) {
		Map<String, Object> event = createSecurityEvent("LOGIN_FAILURE", username, ipAddress, userAgent);
		event.put("details", "로그인 실패");
		event.put("reason", reason);

		logSecurityEvent(event);
		log.warn("로그인 실패 - 사용자: {}, IP: {}, 사유: {}", username, maskIpAddress(ipAddress), reason);

		// 의심스러운 활동 체크
		checkSuspiciousActivity(username, ipAddress, "LOGIN_FAILURE");
	}

	/**
	 * 로그아웃 이벤트 로깅
	 */
	public void logLogout(String username, String ipAddress, String userAgent) {
		Map<String, Object> event = createSecurityEvent("LOGOUT", username, ipAddress, userAgent);
		event.put("details", "사용자 로그아웃");

		logSecurityEvent(event);
		log.info("로그아웃 - 사용자: {}, IP: {}", username, maskIpAddress(ipAddress));
	}

	/**
	 * 토큰 무효화 이벤트 로깅
	 */
	public void logTokenInvalidation(String username, String ipAddress, String userAgent, String reason) {
		Map<String, Object> event = createSecurityEvent("TOKEN_INVALIDATION", username, ipAddress, userAgent);
		event.put("details", "토큰 무효화");
		event.put("reason", reason);

		logSecurityEvent(event);
		log.warn("토큰 무효화 - 사용자: {}, IP: {}, 사유: {}", username, maskIpAddress(ipAddress), reason);
	}

	/**
	 * Rate Limit 초과 이벤트 로깅
	 */
	public void logRateLimitExceeded(String identifier, String ipAddress, String endpoint, String type) {
		Map<String, Object> event = createSecurityEvent("RATE_LIMIT_EXCEEDED", identifier, ipAddress, null);
		event.put("details", "Rate Limit 초과");
		event.put("endpoint", endpoint);
		event.put("type", type);

		logSecurityEvent(event);
		log.warn("Rate Limit 초과 - 식별자: {}, IP: {}, 엔드포인트: {}, 타입: {}",
			identifier, maskIpAddress(ipAddress), endpoint, type);

		// 의심스러운 활동 체크
		checkSuspiciousActivity(identifier, ipAddress, "RATE_LIMIT_EXCEEDED");
	}

	/**
	 * 권한 없음 이벤트 로깅
	 */
	public void logAccessDenied(String username, String ipAddress, String userAgent, String resource) {
		Map<String, Object> event = createSecurityEvent("ACCESS_DENIED", username, ipAddress, userAgent);
		event.put("details", "접근 거부");
		event.put("resource", resource);

		logSecurityEvent(event);
		log.warn("접근 거부 - 사용자: {}, IP: {}, 리소스: {}", username, maskIpAddress(ipAddress), resource);

		// 의심스러운 활동 체크
		checkSuspiciousActivity(username, ipAddress, "ACCESS_DENIED");
	}

	/**
	 * 비밀번호 변경 이벤트 로깅
	 */
	public void logPasswordChange(String username, String ipAddress, String userAgent) {
		Map<String, Object> event = createSecurityEvent("PASSWORD_CHANGE", username, ipAddress, userAgent);
		event.put("details", "비밀번호 변경");

		logSecurityEvent(event);
		log.info("비밀번호 변경 - 사용자: {}, IP: {}", username, maskIpAddress(ipAddress));
	}

	/**
	 * 계정 잠금 이벤트 로깅
	 */
	public void logAccountLocked(String username, String ipAddress, String userAgent, String reason) {
		Map<String, Object> event = createSecurityEvent("ACCOUNT_LOCKED", username, ipAddress, userAgent);
		event.put("details", "계정 잠금");
		event.put("reason", reason);

		logSecurityEvent(event);
		log.error("계정 잠금 - 사용자: {}, IP: {}, 사유: {}", username, maskIpAddress(ipAddress), reason);
	}

	/**
	 * 보안 이벤트 생성
	 */
	private Map<String, Object> createSecurityEvent(String eventType, String username, String ipAddress, String userAgent) {
		Map<String, Object> event = new HashMap<>();
		event.put("eventType", eventType);
		event.put("username", username);
		event.put("ipAddress", ipAddress);
		event.put("userAgent", userAgent);
		event.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
		event.put("sessionId", generateSessionId());

		return event;
	}

	/**
	 * 보안 이벤트 로깅
	 */
	private void logSecurityEvent(Map<String, Object> event) {
		try {
			String eventKey = SECURITY_EVENT_PREFIX + System.currentTimeMillis();
			String eventJson = convertToJson(event);

			// Redis에 이벤트 저장 (30일 보관)
			redisTemplate.opsForValue().set(eventKey, eventJson, 30, TimeUnit.DAYS);

			// 로그 파일에 기록
			logSecurityToFile(event);

		} catch (Exception e) {
			log.error("보안 이벤트 로깅 실패: {}", e.getMessage(), e);
		}
	}

	/**
	 * 의심스러운 활동 체크
	 */
	private void checkSuspiciousActivity(String identifier, String ipAddress, String activityType) {
		try {
			String key = SUSPICIOUS_ACTIVITY_PREFIX + identifier + ":" + ipAddress;
			String currentCount = redisTemplate.opsForValue().get(key);
			int count = currentCount != null ? Integer.parseInt(currentCount) : 0;

			// 활동 카운트 증가
			redisTemplate.opsForValue().increment(key);

			// 첫 번째 활동인 경우 TTL 설정 (1시간)
			if (count == 0) {
				redisTemplate.expire(key, 1, TimeUnit.HOURS);
			}

			// 의심스러운 활동 임계값 체크
			if (count >= 5) {
				logSuspiciousActivity(identifier, ipAddress, activityType, count + 1);
			}

		} catch (Exception e) {
			log.error("의심스러운 활동 체크 실패: {}", e.getMessage(), e);
		}
	}

	/**
	 * 의심스러운 활동 로깅
	 */
	private void logSuspiciousActivity(String identifier, String ipAddress, String activityType, int count) {
		Map<String, Object> event = new HashMap<>();
		event.put("eventType", "SUSPICIOUS_ACTIVITY");
		event.put("identifier", identifier);
		event.put("ipAddress", ipAddress);
		event.put("activityType", activityType);
		event.put("count", count);
		event.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));
		event.put("details", "의심스러운 활동 감지");

		logSecurityEvent(event);
		log.error("의심스러운 활동 감지 - 식별자: {}, IP: {}, 활동: {}, 횟수: {}",
			identifier, maskIpAddress(ipAddress), activityType, count);
	}

	/**
	 * 보안 로그 파일에 기록
	 */
	private void logSecurityToFile(Map<String, Object> event) {
		// 별도의 보안 로그 파일에 기록
		log.info("SECURITY_AUDIT: {}", convertToJson(event));
	}

	/**
	 * IP 주소 마스킹
	 */
	private String maskIpAddress(String ipAddress) {
		if (ipAddress == null || ipAddress.isEmpty()) {
			return "unknown";
		}

		String[] parts = ipAddress.split("\\.");
		if (parts.length == 4) {
			return parts[0] + "." + parts[1] + ".*.*";
		}

		return ipAddress;
	}

	/**
	 * 세션 ID 생성
	 */
	private String generateSessionId() {
		return "session_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
	}

	/**
	 * JSON 변환 (간단한 구현)
	 */
	private String convertToJson(Map<String, Object> map) {
		StringBuilder json = new StringBuilder("{");
		boolean first = true;

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (!first) {
				json.append(",");
			}
			json.append("\"").append(entry.getKey()).append("\":");

			Object value = entry.getValue();
			if (value instanceof String) {
				json.append("\"").append(value).append("\"");
			} else {
				json.append(value);
			}

			first = false;
		}

		json.append("}");
		return json.toString();
	}

	/**
	 * 보안 이벤트 통계 조회 (관리자용)
	 */
	public Map<String, Object> getSecurityEventStats() {
		try {
			String eventPattern = SECURITY_EVENT_PREFIX + "*";
			String suspiciousPattern = SUSPICIOUS_ACTIVITY_PREFIX + "*";

			var eventKeys = redisTemplate.keys(eventPattern);
			var suspiciousKeys = redisTemplate.keys(suspiciousPattern);

			Map<String, Object> stats = new HashMap<>();
			stats.put("totalEvents", eventKeys != null ? eventKeys.size() : 0);
			stats.put("suspiciousActivities", suspiciousKeys != null ? suspiciousKeys.size() : 0);
			stats.put("timestamp", LocalDateTime.now().format(DATE_FORMATTER));

			return stats;
		} catch (Exception e) {
			log.error("보안 이벤트 통계 조회 실패: {}", e.getMessage(), e);
			return new HashMap<>();
		}
	}
}
