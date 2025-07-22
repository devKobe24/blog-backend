package com.blog.controller;

import com.blog.dto.request.UserChangePasswordRequest;
import com.blog.dto.request.UserLoginRequest;
import com.blog.dto.request.UserSignUpRequest;
import com.blog.dto.request.UserUpdateRequest;
import com.blog.dto.response.UserLoginResponse;
import com.blog.dto.response.UserResponse;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<UserResponse> signUp(@RequestBody UserSignUpRequest request) {
		return ResponseEntity.ok(userService.signUp(request));
	}

	// 로그인
	@PostMapping("/login")
	public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
		return ResponseEntity.ok(userService.login(request));
	}

	// 내 정보 조회
	@GetMapping("/me")
	public ResponseEntity<UserResponse> getCurrentUser(Principal principal) {
		return ResponseEntity.ok(userService.getCurrentUser());
	}

	// 프로필 수정
	@PutMapping("/profile")
	public ResponseEntity<UserResponse> updateProfile(@RequestBody UserUpdateRequest request, Principal principal) {
		return ResponseEntity.ok(userService.updateProfile(request));
	}

	// 비밀번호 변경
	@PutMapping("/password")
	public ResponseEntity<Void> changePassword(@RequestBody UserChangePasswordRequest request, Principal principal) {
		userService.changePassword(request);
		return ResponseEntity.ok().build();
	}
}
