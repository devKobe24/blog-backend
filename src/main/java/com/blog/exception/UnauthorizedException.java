package com.blog.exception;

/**
 * 인증되지 않은 요청에 대한 예외
 *
 * @author Minseong Kang, devKobe24
 * @version 1.0
 */
public class UnauthorizedException extends BusinessException {

	public UnauthorizedException(String message) {
		super("UNAUTHORIZED", message);
	}

	public UnauthorizedException() {
		super("UNAUTHORIZED", "인증이 필요합니다.");
	}

	public static UnauthorizedException of(String message) {
		return new UnauthorizedException(message);
	}
}
