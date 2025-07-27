package com.blog.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PasswordStrengthValidator Test")
class PasswordStrengthValidatorTest {

	private PasswordStrengthValidator validator;

	@BeforeEach
	void setUp() {
		validator = new PasswordStrengthValidator();
	}

	@Test
	@DisplayName("유효한 비밀번호 검증")
	void validPassword() {
		// given
		String validPassword = "Test123!@#";

		// when
		boolean result = validator.isValid(validPassword, null);

		// then
		assertThat(result).isTrue();
	}

	@Test
	@DisplayName("영문이 없는 비밀번호 검증")
	void passwordWithoutLetter() {
		// given
		String password = "123456!@#";

		// when
		boolean result = validator.isValid(password, null);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("숫자가 없는 비밀번호 검증")
	void passwordWithoutDigit() {
		// given
		String password = "TestPassword!@#";

		// when
		boolean result = validator.isValid(password, null);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("특수문자가 없는 비밀번호 검증")
	void passwordWithoutSpecial() {
		// given
		String password = "TestPassword123";

		// when
		boolean result = validator.isValid(password, null);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("8자 미만 비밀번호 검증")
	void passwordTooShort() {
		// given
		String password = "Test1!";

		// when
		boolean result = validator.isValid(password, null);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("null 비밀번호 검증")
	void nullPassword() {
		// when
		boolean result = validator.isValid(null, null);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("빈 문자열 비밀번호 검증")
	void emptyPassword() {
		// given
		String password = "";

		// when
		boolean result = validator.isValid(password, null);

		// then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("공백만 있는 비밀번호 검증")
	void whitespaceOnlyPassword() {
		// given
		String password = "   ";

		// when
		boolean result = validator.isValid(password, null);

		// then
		assertThat(result).isFalse();
	}
}