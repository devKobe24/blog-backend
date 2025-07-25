package com.blog.exception;

import com.blog.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 전역 예회 처리 핸들러
 *
 * @author Minseong Kang, devKobe24
 * @version 1.0
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@Value("${spring.profiles.active:prod}")
	private String activeProfile;

	/**
	 * 비즈니스 예외 처리
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(
		BusinessException e, HttpServletRequest request) {

		log.warn("Business Exception: {} - {}", e.getErrorCode(), e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			e.getErrorCode(),
			e.getMessage(),
			request.getRequestURI()
		);

		return ResponseEntity.badRequest().body(errorResponse);
	}

	/**
	 * 인증 예외 처리
	 */
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedException(
		UnauthorizedException e, HttpServletRequest request) {

		log.warn("Unauthorized: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			e.getErrorCode(),
			e.getMessage(),
			request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}

	/**
	 * 권한 없음 예외 처리
	 */
	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ErrorResponse> handleForbiddenException(
		ForbiddenException e, HttpServletRequest request) {

		log.warn("Forbidden: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			e.getErrorCode(),
			e.getMessage(),
			request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}

	/**
	 * Spring Security 인증 예외 처리
	 */
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthenticationException(
		AuthenticationException e, HttpServletRequest request) {

		log.warn("Authentication Exception: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			"AUTHENTICATION_FAILED",
			"인증에 실패했습니다.",
			request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}

	/**
	 * Spring Security 권한 예외 처리
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(
		AccessDeniedException e, HttpServletRequest request) {

		log.warn("Access Denied: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			"ACCESS_DENIED",
			"접근 권한이 없습니다.",
			request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}

	/**
	 * 잘못된 자격 증명 예외 처리
	 */
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentialsException(
		BadCredentialsException e, HttpServletRequest request) {

		log.warn("Bad Credentials: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			"BAD_CREDENTIALS",
			"아이디 또는 비밀번호가 올바르지 않습니다.",
			request.getRequestURI()
		);

		return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}

	/**
	 * 유효성 검증 예외 처리 (@Valid)
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e, HttpServletRequest request) {

		log.warn("Validation Error: {}", e.getMessage());

		List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
			.map(error -> ErrorResponse.FieldError.builder()
				.field(error.getField())
				.value(error.getRejectedValue() != null ? error.getRejectedValue().toString() : "")
				.reason(error.getDefaultMessage())
				.build())
			.collect(Collectors.toList());

		ErrorResponse errorResponse = ErrorResponse.builder()
			.code("VALIDATION_ERROR")
			.message("입력값이 올바르지 않습니다.")
			.timestamp(LocalDateTime.now())
			.path(request.getRequestURI())
			.fieldErrors(fieldErrors)
			.build();

		return ResponseEntity.badRequest().body(errorResponse);
	}

	/**
	 * 바인딩 예외 처리
	 */
	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse> handleBindException(
		BindException e, HttpServletRequest request) {

		log.warn("Binding Error: {}", e.getMessage());

		List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
			.map(error -> ErrorResponse.FieldError.builder()
				.field(error.getField())
				.value(error.getRejectedValue() != null ? error.getRejectedValue().toString() : "")
				.reason(error.getDefaultMessage())
				.build())
			.collect(Collectors.toList());

		ErrorResponse errorResponse = ErrorResponse.builder()
			.code("BINDING_ERROR")
			.message("요청 데이터 바인딩에 실패했습니다.")
			.timestamp(LocalDateTime.now())
			.path(request.getRequestURI())
			.fieldErrors(fieldErrors)
			.build();

		return ResponseEntity.badRequest().body(errorResponse);
	}

	/**
	 * 제약 조건 위반 예외 처리
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(
		ConstraintViolationException e, HttpServletRequest request) {

		log.warn("Constraint Violation: {}", e.getMessage());

		List<ErrorResponse.FieldError> fieldErrors = e.getConstraintViolations().stream()
			.map(violation -> ErrorResponse.FieldError.builder()
				.field(violation.getPropertyPath().toString())
				.value(violation.getInvalidValue() != null ? violation.getInvalidValue().toString() : "")
				.reason(violation.getMessage())
				.build())
			.collect(Collectors.toList());

		ErrorResponse errorResponse = ErrorResponse.builder()
			.code("CONSTRAINT_VIOLATION")
			.message("제약 조건을 위반했습니다.")
			.timestamp(LocalDateTime.now())
			.path(request.getRequestURI())
			.fieldErrors(fieldErrors)
			.build();

		return ResponseEntity.badRequest().body(errorResponse);
	}

	/**
	 * 데이터 무결성 위반 예외 처리
	 */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
		DataIntegrityViolationException e, HttpServletRequest request) {

		log.error("Data Integrity Violation: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			"DATA_INTEGRITY_VIOLATION",
			"데이터 무결성을 위반했습니다.",
			request.getRequestURI()
		);

		return ResponseEntity.badRequest().body(errorResponse);
	}

	/**
	 * HTTP 메시지 읽기 실패 예외 처리
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException e, HttpServletRequest request) {

		log.warn("HTTP Message Not Readable: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			"INVALID_REQUEST_BODY",
			"요청 본문을 읽을 수 없습니다.",
			request.getRequestURI()
		);

		return ResponseEntity.badRequest().body(errorResponse);
	}

	/**
	 * 메서드 인수 타입 불일치 예외 처리
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e, HttpServletRequest request) {

		log.warn("Method Argument Type Mismatch: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			"INVALID_ARGUMENT_TYPE",
			String.format("'%s'의 타입이 올바르지 않습니다.", e.getName()),
			request.getRequestURI()
		);

		return ResponseEntity.badRequest().body(errorResponse);
	}

	/**
	 * HTTP 메서드 지원 안함 예외 처리
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
		HttpRequestMethodNotSupportedException e, HttpServletRequest request) {

		log.warn("HTTP Method Not Supported: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			"METHOD_NOT_ALLOWED",
			String.format("'%s' 메서드는 지원하지 않습니다.", e.getMethod()),
			request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
	}

	/**
	 * 핸들러 없음 예외 처리
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
		NoHandlerFoundException e, HttpServletRequest request) {

		log.warn("Missing Handler Found: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			"NOT_FOUND",
			"요청한 리소스를 찾을 수 없습니다.",
			request.getRequestURI()
		);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	/**
	 * 요청 파라미터 누락 예외 처리
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException e, HttpServletRequest request) {

		log.warn("Missing Request Parameter: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			"MISSING_PARAMETER",
			String.format("필수 파라미터 '%s'가 누락되었습니다.", e.getParameterName()),
			request.getRequestURI()
		);

		return ResponseEntity.badRequest().body(errorResponse);
	}

	/**
	 * IllegalArgumentException 처리
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
		IllegalArgumentException e, HttpServletRequest request) {

		log.warn("Illegal Argument: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			"ILLEGAL_ARGUMENT",
			e.getMessage(),
			request.getRequestURI()
		);

		return ResponseEntity.badRequest().body(errorResponse);
	}

	/**
	 * SecurityException 처리
	 */
	@ExceptionHandler(SecurityException.class)
	public ResponseEntity<ErrorResponse> handleSecurityException(
		SecurityException e, HttpServletRequest request) {

		log.warn("Security Exception: {}", e.getMessage());

		ErrorResponse errorResponse = ErrorResponse.of(
			"SECURITY_ERROR",
			e.getMessage(),
			request.getRequestURI()
		);

		return ResponseEntity.badRequest().body(errorResponse);
	}

	/**
	 * 기타 모든 예외 처리
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(
		Exception e, HttpServletRequest request) {

		log.error("Unexpected Exception: {}", e.getMessage());

		String message = "INTERNAL_SERVER_ERROR".equals(activeProfile)
			? "서버 내부 오류가 발생했습니다."
			: e.getMessage();

		String detail = "dev".equals(activeProfile) ? e.toString() : null;

		ErrorResponse errorResponse = ErrorResponse.builder()
			.code("INTERNAL_SERVER_ERROR")
			.message(message)
			.timestamp(LocalDateTime.now())
			.path(request.getRequestURI())
			.detail(detail)
			.build();

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}
