package com.blog.controller;

import com.blog.dto.request.UserChangePasswordRequest;
import com.blog.dto.request.UserLoginRequest;
import com.blog.dto.request.UserSignUpRequest;
import com.blog.dto.request.UserUpdateRequest;
import com.blog.dto.response.UserLoginResponse;
import com.blog.dto.response.UserResponse;
import com.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "User", description = "사용자 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원가입 성공",
			content = @Content(schema = @Schema(implementation = UserResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@PostMapping("/signup")
	public ResponseEntity<UserResponse> signUp(
		@Parameter(description = "회원가입 요청", required = true) @RequestBody UserSignUpRequest request) {
		return ResponseEntity.ok(userService.signUp(request));
	}

	@Operation(summary = "로그인", description = "사용자 로그인을 수행합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그인 성공",
			content = @Content(schema = @Schema(implementation = UserLoginResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	@PostMapping("/login")
	public ResponseEntity<UserLoginResponse> login(
		@Parameter(description = "로그인 요청", required = true) @RequestBody UserLoginRequest request) {
		return ResponseEntity.ok(userService.login(request));
	}

	@Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
			content = @Content(schema = @Schema(implementation = UserResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 필요")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping("/me")
	public ResponseEntity<UserResponse> getCurrentUser(Principal principal) {
		return ResponseEntity.ok(userService.getCurrentUser());
	}

	@Operation(summary = "프로필 수정", description = "현재 사용자의 프로필을 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "프로필 수정 성공",
			content = @Content(schema = @Schema(implementation = UserResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 필요")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@PutMapping("/profile")
	public ResponseEntity<UserResponse> updateProfile(
		@Parameter(description = "프로필 수정 요청", required = true) @RequestBody UserUpdateRequest request, 
		Principal principal) {
		return ResponseEntity.ok(userService.updateProfile(request));
	}

	@Operation(summary = "비밀번호 변경", description = "현재 사용자의 비밀번호를 변경합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 필요")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@PutMapping("/password")
	public ResponseEntity<Void> changePassword(
		@Parameter(description = "비밀번호 변경 요청", required = true) @RequestBody UserChangePasswordRequest request, 
		Principal principal) {
		userService.changePassword(request);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "사용자 비활성화", description = "관리자가 사용자 계정을 비활성화합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용자 비활성화 성공",
		content = @Content(schema = @Schema(implementation = UserResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 필요"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@PutMapping("/users/{userId}/deactivate")
	public ResponseEntity<UserResponse> deactivateUser(
		@Parameter(description = "사용자 ID", required = true) @PathVariable Long userId,
		Principal principal) {
		return ResponseEntity.ok(userService.deactivateUser(userId));
	}

	@Operation(summary = "사용자 활성화", description = "관리자가 사용자 계정을 활성화합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용자 활성화 성공",
		content = @Content(schema = @Schema(implementation = UserResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 필요"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@PutMapping("/users/{userId}/activate")
	public ResponseEntity<UserResponse> activateUser(
		@Parameter(description = "사용자 ID", required = true) @PathVariable Long userId,
		Principal principal) {
		return ResponseEntity.ok(userService.activateUser(userId));
	}

	@Operation(summary = "관리자 권한 확인", description = "현재 사용자가 관리자인지 확인합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "권한 확인 성공"),
		@ApiResponse(responseCode = "401", description = "인증 필요")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping("/admin/check")
	public ResponseEntity<Boolean> checkAdminRole(Principal principal) {
		boolean isAdmin = userService.isUserAdmin(principal.getName());
		return ResponseEntity.ok(isAdmin);
	}
}
