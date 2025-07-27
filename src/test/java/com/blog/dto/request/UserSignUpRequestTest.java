package com.blog.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserSignUpRequest Validation Test")
class UserSignUpRequestTest {

	private static Validator validator;

	@BeforeAll
	static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	@DisplayName("유효한 회원가입 요청 검증")
	void validSignUpRequest() {
		// given
		UserSignUpRequest request = UserSignUpRequest.builder()
			.username("testuser")
			.email("test@example.com")
			.password("Test123!@#")
			.nickname("테스트 사용자")
			.build();

		// when
		Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).isEmpty();
	}

	@Test
	@DisplayName("사용자명이 null인 경우 검증")
	void usernameNull() {
		// given
		UserSignUpRequest request = UserSignUpRequest.builder()
			.email("test@example.com")
			.password("Test123!@#")
			.nickname("테스트 사용자")
			.build();

		// when
		Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);

		// @NotBlank 오류가 포함되어 있는지 확인
		boolean hasNotBlankError = violations.stream()
			.anyMatch(v -> v.getMessage().equals("사용자명은 필수입니다."));
		assertThat(hasNotBlankError).isTrue();
	}

	@Test
	@DisplayName("사용자명이 빈 문자열인 경우 검증")
	void usernameBlank() {
		// given
		UserSignUpRequest request = UserSignUpRequest.builder()
			.username("")
			.email("test@example.com")
			.password("Test123!@#")
			.nickname("테스트 사용자")
			.build();

		// when
		Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(3);

		// @NotBlank 오류가 포함되어 있는지 확인
		boolean hasNotBlankError = violations.stream()
			.anyMatch(v -> v.getMessage().equals("사용자명은 필수입니다."));
		assertThat(hasNotBlankError).isTrue();
	}

	@Test
	@DisplayName("사용자명이 2자 미만인 경우 검증")
	void usernameTooShort() {
		// given
		UserSignUpRequest request = UserSignUpRequest.builder()
			.username("ab")
			.email("test@example.com")
			.password("Test123!@#")
			.nickname("테스트 사용자")
			.build();

		// when
		Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("사용자명은 3자 이상 20자 이하여야 합니다.");
	}

	@Test
	@DisplayName("사용자명에 특수문자가 포함된 경우 검증")
	void usernameWithSpecialCharacters() {
		// given
		UserSignUpRequest request = UserSignUpRequest.builder()
			.username("test@user")
			.email("test@example.com")
			.password("Test123!@#")
			.nickname("테스트 사용자")
			.build();

		// when
		Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("사용자명은 영문, 숫자, 밑줄(_)만 포함할 수 있습니다.");
	}

	@Test
	@DisplayName("이메일이 유효하지 않은 경우 검증")
	void invalidEmail() {
		// given
		UserSignUpRequest request = UserSignUpRequest.builder()
			.username("testuser")
			.email("invalid-email")
			.password("Test123!@#")
			.nickname("테스트 사용자")
			.build();

		// when
		Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("올바른 이메일 형식이 아닙니다.");
	}

	@Test
	@DisplayName("비밀번호가 유효하지 않은 경우 검증")
	void invalidPassword() {
		// given
		UserSignUpRequest request = UserSignUpRequest.builder()
			.username("testuser")
			.email("test@example.com")
			.password("weak")
			.nickname("테스트 사용자")
			.build();

		// when
		Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("비밀번호는 영문, 숫자, 특수문자를 포함해야 하며 최소 8자 이상이어야 합니다.");
	}

	@Test
	@DisplayName("닉네임이 50자를 초과하는 경우 검증")
	void nicknameTooLong() {
		// given
		String longNickname = "a".repeat(51);
		UserSignUpRequest request = UserSignUpRequest.builder()
			.username("testuser")
			.email("test@example.com")
			.password("Test123!@#")
			.nickname(longNickname)
			.build();

		// when
		Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage()).isEqualTo("닉네임은 50자 이하여야 합니다.");
	}

	@Test
	@DisplayName("모든 필드가 유효하지 않은 경우 검증")
	void allFieldsInvalid() {
		// given
		UserSignUpRequest request = UserSignUpRequest.builder()
			.username("")
			.email("invalid")
			.password("weak")
			.nickname("a".repeat(51))
			.build();

		// when
		Set<ConstraintViolation<UserSignUpRequest>> violations = validator.validate(request);

		// then
		assertThat(violations).hasSize(6); // 사용자명 3개 + 이메일 1개 + 비밀번호 1개 + 닉네임 1개

		// 각 필드별 오류 확인
		long usernameErrors = violations.stream()
			.filter(v -> v.getPropertyPath().toString().equals("username"))
			.count();
		long emailErrors = violations.stream()
			.filter(v -> v.getPropertyPath().toString().equals("email"))
			.count();
		long passwordErrors = violations.stream()
			.filter(v -> v.getPropertyPath().toString().equals("password"))
			.count();
		long nicknameErrors = violations.stream()
			.filter(v -> v.getPropertyPath().toString().equals("nickname"))
			.count();

		assertThat(usernameErrors).isEqualTo(3); // @NotBlank, @Size, @Pattern
		assertThat(emailErrors).isEqualTo(1); // @Email
		assertThat(passwordErrors).isEqualTo(1); // @PasswordStrength
		assertThat(nicknameErrors).isEqualTo(1); // @Size
	}
}