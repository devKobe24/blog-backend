package com.blog.exception;

/**
 * 비즈니스 로직 관련 예외
 *
 * @author Minseong Kang, devKobe24
 * @version 1.0
 */

public class BusinessException extends RuntimeException {

	private final String errorCode;

	public BusinessException(String message) {
		super(message);
		this.errorCode = "BUSINESS_ERROR";
	}

	public BusinessException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
		this.errorCode = "BUSINESS_ERROR";
	}

	public BusinessException(String errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
}