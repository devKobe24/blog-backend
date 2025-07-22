package com.blog.service;

import com.blog.dto.request.UserSignUpRequest;
import com.blog.dto.request.UserLoginRequest;
import com.blog.dto.request.UserUpdateRequest;
import com.blog.dto.request.UserChangePasswordRequest;
import com.blog.dto.response.UserResponse;
import com.blog.dto.response.UserLoginResponse;
import com.blog.entity.User;
import com.blog.repository.UserRepository;
import com.blog.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public UserResponse signUp(UserSignUpRequest request) {
		// 중복 검사
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
		}

		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
		}

		// 사용자 생성
		User user = User.builder()
			.username(request.getUsername())
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword()))
			.nickname(request.getNickname() != null ? request.getNickname() : request.getUsername())
			.role(User.Role.USER)
			.isActive(true)
			.build();

		User savedUser = userRepository.save(user);
		return convertToUserResponse(savedUser);
	}

	public UserLoginResponse login(UserLoginRequest request) {
		// 인증 처리
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		// 사용자 정보 조회
		User user = userRepository.findByUsername(request.getUsername())
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
		String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

		return UserLoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.userInfo(convertToUserResponse(user))
			.build();
	}

	public UserResponse getCurrentUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		return convertToUserResponse(user);
	}

	@Transactional
	public UserResponse updateProfile(UserUpdateRequest request) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		if (request.getNickname() != null) {
			user.setNickname(request.getNickname());
		}
		if (request.getProfileImage() != null) {
			user.setProfileImage(request.getProfileImage());
		}

		User updatedUser = userRepository.save(user);
		return convertToUserResponse(updatedUser);
	}

	@Transactional
	public void changePassword(UserChangePasswordRequest request) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		// 현재 비밀번호 확인
		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
		}

		// 새 비밀번호 설정
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userRepository.save(user);
	}

	public List<UserResponse> getAllUsers() {
		return userRepository.findAllActiveUsers().stream()
			.map(this::convertToUserResponse)
			.collect(Collectors.toList());
	}

	public List<UserResponse> searchUsers(String keyword) {
		return userRepository.searchUsers(keyword).stream()
			.map(this::convertToUserResponse)
			.collect(Collectors.toList());
	}

	public User getUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
	}

	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
	}

	private UserResponse convertToUserResponse(User user) {
		return UserResponse.builder()
			.id(user.getId())
			.username(user.getUsername())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.profileImage(user.getProfileImage())
			.role(user.getRole().name())
			.createdAt(user.getCreatedAt())
			.build();
	}
}
