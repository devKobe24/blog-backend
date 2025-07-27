package com.blog.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordStrength {
	String message() default "비밀번호는 영문, 숫자, 특수문자를 포함해야 하며 최소 8자 이상이어야 합니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
