package com.blog.exception;

public class ForbiddenException extends BusinessException {

	public ForbiddenException(String message) {
		super("FORBIDDEN", message);
	}

	public ForbiddenException() {
		super("FORBIDDEN", "접근 권한이 없습니다.");
	}

	public static ForbiddenException of(String message) {
		return new ForbiddenException(message);
	}
}
