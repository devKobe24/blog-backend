package com.blog.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

	private final RateLimitService rateLimitService;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String clientIp = getClientIpAddress(request);
		String endpoint = getEndpoint(request);

		// Rate Limit이 적용되지 않는 엔드포인트는 건너뛰기
		if (shouldSkipRateLimit(endpoint)) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			// IP 기반 Rate Limit 확인
			if (rateLimitService.isRateLimitExceeded(clientIp, endpoint)) {
				handleRateLimitExceeded(response, clientIp, endpoint, "IP");
				return;
			}

			// 인증된 사용자의 경우 사용자 기반 Rate Limit도 확인
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {

				String username = authentication.getName();
				if (rateLimitService.isUserRateLimitExceeded(username, endpoint)) {
					handleRateLimitExceeded(response, clientIp, endpoint, "User");
					return;
				}
			}

			// Rate Limit 헤더 추가
			addRateLimitHeaders(response, clientIp, endpoint);

			filterChain.doFilter(request, response);

		} catch (Exception e) {
			log.error("Rate limit filter error for IP: {}, endpoint: {}", clientIp, endpoint, e);
			filterChain.doFilter(request, response);
		}
	}

	/**
	 * 클라이언트 IP 주소 추출
	 */
	private String getClientIpAddress(HttpServletRequest request) {
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (StringUtils.hasText(xForwardedFor)) {
			return xForwardedFor.split(",")[0].trim();
		}

		String xRealIp = request.getHeader("X-Real-IP");
		if (StringUtils.hasText(xRealIp)) {
			return xRealIp;
		}

		return request.getRemoteAddr();
	}

	/**
	 * 엔드포인트 추출
	 */
	private String getEndpoint(HttpServletRequest request) {
		String path = request.getRequestURI();

		if (path.startsWith("/api/auth/login")) {
			return "login";
		} else if (path.startsWith("/api/")) {
			return "api";
		} else {
			return "default";
		}
	}

	/**
	 * Rate Limit을 건너뛸 엔드포인트 확인
	 */
	private boolean shouldSkipRateLimit(String endpoint) {
		return "health".equals(endpoint) || "info".equals(endpoint);
	}

	/**
	 * Rate Limit 초과 처리
	 */
	private void handleRateLimitExceeded(HttpServletResponse response, String identifier, String endpoint, String type) throws IOException {

		log.warn("Rate limit exceeded - Type: {}, Identifier: {}, Endpoint: {}", type, identifier, endpoint);

		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("error", "Rate limit exceeded");
		errorResponse.put("message", "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");
		errorResponse.put("type", type);
		errorResponse.put("endpoint", endpoint);
		errorResponse.put("timestamp", System.currentTimeMillis());

		// Jackson을 사용하여 JSON 변환
		String jsonResponse = objectMapper.writeValueAsString(errorResponse);
		response.getWriter().write(jsonResponse);
	}

	private void addRateLimitHeaders(HttpServletResponse response, String clientIp, String endpoint) {
		try {
			RateLimitService.RateLimitInfo info = rateLimitService.getRateLimitInfo(clientIp, endpoint);

			response.setHeader("X-RateLimit-Limit", String.valueOf(info.getMaxRequests()));
			response.setHeader("X-RateLimit-Remaining", String.valueOf(info.getRemaining()));
			response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + info.getTtlSeconds()));

			if (info.getRemaining() <= 10) {
				response.setHeader("X-RateLimit-Warning", "Rate limit approaching");
			}
		} catch (Exception e) {
			log.error("Failed to add rate limit headers", e);
		}
	}
}