package com.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@Configuration
public class SecurityHeadersConfig {

	@Bean
	public HeaderWriter securityHeadersWriter() {
		return new StaticHeadersWriter("X-Content-Type-Options", "nosniff");
	}

	@Bean
	public HeaderWriter frameOptionsWriter() {
		return new StaticHeadersWriter("X-Frame-Options", "DENY");
	}

	@Bean
	public HeaderWriter xssProtectionWriter() {
		return new StaticHeadersWriter("X-XSS-Protection", "1; mode=block");
	}

	@Bean
	public HeaderWriter referrerPolicyWriter() {
		return new StaticHeadersWriter("Referrer-Policy", "strict-origin-when-cross-origin");
	}

	@Bean
	public HeaderWriter permissionsPolicyWriter() {
		return new StaticHeadersWriter("Permissions-Policy",
			"camera=(), microphone=(), geolocation=(), payment=()");
	}

	@Bean
	public HeaderWriter contentSecurityPolicyWriter() {
		return new StaticHeadersWriter("Content-Security-Policy",
			"default-src 'self'; " +
				"script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net; " +
				"style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
				"font-src 'self' https://fonts.gstatic.com; " +
				"img-src 'self' data: https:; " +
				"connect-src 'self' https://api.example.com; " +
				"frame-ancestors 'none'; " +
				"base-uri 'self'; " +
				"form-action 'self'");
	}

	@Bean
	public HeaderWriter strictTransportSecurityWriter() {
		return new StaticHeadersWriter("Strict-Transport-Security",
			"max-age=31536000; includeSubDomains; preload");
	}

	@Bean
	public HeaderWriter cacheControlWriter() {
		return new StaticHeadersWriter("Cache-Control",
			"no-cache, no-store, max-age=0, must-revalidate");
	}

	@Bean
	public HeaderWriter pragmaWriter() {
		return new StaticHeadersWriter("Pragma", "no-cache");
	}

	@Bean
	public HeaderWriter expiresWriter() {
		return new StaticHeadersWriter("Expires", "0");
	}

	@Bean
	public HeaderWriter apiVersionWriter() {
		return new StaticHeadersWriter("X-API-Version", "1.0");
	}

	@Bean
	public HeaderWriter staticCacheWriter() {
		return new StaticHeadersWriter("X-Static-Cache", "public, max-age=31536000");
	}
}
