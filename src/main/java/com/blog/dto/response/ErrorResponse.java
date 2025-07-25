package com.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API 에러 응답 DTO
 *
 * @Author Minseong Kang, devKobe24
 * @version 1.0
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

	/**
	 * 에러 코드
	 */
	private String code;

	/**
	 * 에러 메시지
	 */
	private String message;

	/**
	 * 에러 발생 시간
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;

	/**
	 * 요청 경로
	 */
	private String path;

	/**
	 * 상세 에러 정보 (개발 환경에서만 사용)
	 */
	private String detail;

	/**
	 * 필드별 에러 정보
	 */
	private List<FieldError> fieldErrors;

	/**
	 * 필드 에러 정보
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class FieldError {
		private String field;
		private String value;
		private String reason;
	}

	/**
	 * 기본 에러 응답 생성
	 */
	public static ErrorResponse of(String code, String message, String path) {
		return ErrorResponse.builder()
			.code(code)
			.message(message)
			.timestamp(LocalDateTime.now())
			.path(path)
			.build();
	}

	/**
	 * 상세 에러 응답 생성 (개발 환경용)
	 */
	public static ErrorResponse of(String code, String message, String path, String detail) {
		return ErrorResponse.builder()
			.code(code)
			.message(message)
			.timestamp(LocalDateTime.now())
			.path(path)
			.detail(detail)
			.build();
	}
}
