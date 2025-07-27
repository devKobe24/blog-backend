package com.blog.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {

	@Override
	public void initialize(PasswordStrength constraintAnnotation) {
		// 초기화 작업이 필요할 경우 여기에 작성합니다.
		// 현재는 특별한 초기화가 필요하지 않으므로 비워둡니다.
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (password == null || password.trim().isEmpty()) {
			return false; // 비밀번호가 null이거나 빈 문자열인 경우 유효하지 않음
		}

		// 비밀번호의 길이 검사
		// 최소 8자 이상
		if (password.length() < 8) {
			return false;
		}

		// 영문 포함
		boolean hasLetter = password.matches(".*[a-zA-Z].*");

		// 숫자 포함
		boolean hasDigit = password.matches(".*[0-9].*");

		// 특수문자 포함 (일반적인 특수문자들)
		boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

		return hasLetter && hasDigit && hasSpecial;
	}
}
