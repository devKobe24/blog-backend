package com.blog.controller;

import com.blog.security.JwtTokenBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Security", description = "보안 모니터링 관련 API")
@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
public class SecurityController {

	private final JwtTokenBlacklistService blacklistService;

	@Operation(summary = "블랙리스트 상태 조회", description = "JWT 토큰 블랙리스트의 현재 상태를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "블랙리스트 상태 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 필요"),
		@ApiResponse(responseCode = "403", description = "권한 없음")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/blacklist/status")
	public ResponseEntity<Map<String, Object>> getBlacklistStatus() {
		Map<String, Object> status = new HashMap<>();
		status.put("blacklistSize", blacklistService.getBlacklistSize());
		status.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.ok(status);
	}

	@Operation(summary = "보안 헤더 테스트", description = "보안 헤더가 올바르게 설정되었는지 확인합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "보안 헤더 테스트 성공"),
		@ApiResponse(responseCode = "401", description = "인증 필요")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping("/headers/test")
	public ResponseEntity<Map<String, Object>> testSecurityHeaders() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "보안 헤더가 설정되었습니다.");
		response.put("timestamp", System.currentTimeMillis());
		response.put("status", "secure");

		return ResponseEntity.ok(response);
	}
}
