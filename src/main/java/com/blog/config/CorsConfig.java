package com.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

	@Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
	private String allowedOrigins;

	@Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS")
	private String allowedMethods;

	@Value("${cors.allowed-headers:Authorization,Content-Type,X-Requested-With")
	private String allowedHeaders;

	@Value("${cors.exposed-headers:Authorization,X-API-Version}")
	private String exposedHeaders;

	@Value("${cors.allow-credentials:true}")
	private boolean allowCredentials;

	@Value("${cors.max-age:3600")
	private long maxAge;

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 허용된 오리진 설정
		List<String> origins = Arrays.asList(allowedOrigins.split(","));
		configuration.setAllowedOriginPatterns(origins);

		// 허용된 HTTP 메서드
		List<String> methods = Arrays.asList(allowedMethods.split(","));
		configuration.setAllowedMethods(methods);

		// 허용된 헤더 설정
		List<String> headers = Arrays.asList(allowedHeaders.split(","));
		configuration.setAllowedHeaders(headers);

		// 노출할 헤더 설정
		List<String> exposed = Arrays.asList(exposedHeaders.split(","));

		// 인증 정보 허용 설정
		configuration.setAllowCredentials(allowCredentials);

		// 프리플라이트 요청 캐시 시간 설정
		configuration.setMaxAge(maxAge);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", configuration);

		return source;
	}
}
