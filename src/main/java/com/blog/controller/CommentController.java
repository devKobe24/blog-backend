package com.blog.controller;

import com.blog.dto.request.CommentCreateRequest;
import com.blog.dto.request.CommentUpdateRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.service.CommentService;
import io.jsonwebtoken.security.SecurityException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@Tag(name = "Comment", description = "댓글 관련 API")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	/**
	 *
	 * @param postId 게시글 ID
	 * @param request 댓글 생성 요청
	 * @param principal 인증된 사용자 정보
	 * @return 생성된 댓글 정보
	 */
	@Operation(summary = "댓글 생성", description = "게시물에 댓글을 작성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "댓글 생성 성공.",
		content = @Content(schema = @Schema(implementation = CommentResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 필요")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@PostMapping("/posts/{postId}")
	public ResponseEntity<CommentResponse> createComment(
		@Parameter(description = "게시물 ID", required = true) @PathVariable Long postId,
		@Parameter(description = "댓글 생성 요청", required = true) @Valid @RequestBody CommentCreateRequest request,
		Principal principal) {

		log.info("댓글 생성 요청 - 게시글 ID: {}, 작성자: {}", postId, principal.getName());

		try {
			CommentResponse response = commentService.createComment(postId, request, principal.getName());
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			log.error("댓글 생성 실패: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * 댓글 수정
	 *
	 * @param commentId 댓글 ID
	 * @param request 댓글 수정 요청
	 * @param principal 인증된 사용자 정보
	 * @return 수정된 댓글 정보
	 */
	@Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "댓글 수정 성공",
		content = @Content(schema = @Schema(implementation = CommentResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 필요"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@PutMapping("/{commentId}")
	public ResponseEntity<CommentResponse> updateComment(
		@Parameter(description = "댓글 ID", required = true) @PathVariable Long commentId,
		@Parameter(description = "댓글 수정 요청", required = true) @Valid @RequestBody CommentUpdateRequest request,
		Principal principal) {

		log.info("댓글 수정 요청 - 댓글 ID: {}, 수정자: {}", commentId, principal.getName());

		try {
			CommentResponse response = commentService.updateComment(commentId, request, principal.getName());
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			log.error("댓글 수정 실패 - 댓글을 찾을 수 없음: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (SecurityException e) {
			log.error("댓글 수정 실패 - 권한 없음: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	/**
	 * 댓글 삭제 (소프트 삭제)
	 *
	 * @param commentId 댓글 ID
	 * @param principal 인증된 사용자 정보
	 * @return 삭제 성공 응답
	 */
	@Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다 (소프트 삭제).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
		@ApiResponse(responseCode = "401", description = "인증 필요"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(
		@Parameter(description = "댓글 ID", required = true) @PathVariable Long commentId,
		Principal principal) {

		log.info("댓글 삭제 요청 - 댓글 ID: {}. 삭제자: {}", commentId, principal.getName());

		try {
			commentService.deleteComment(commentId, principal.getName());
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			log.error("댓글 삭제 실패 - 댓글을 찾을 수 없음: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (SecurityException e) {
			log.error("댓글 삭제 실패 - 권한 없음: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	/**
	 * 게시글별 댓글 목록 조회 (페이징)
	 *
	 * @param postId 게시글 ID
	 * @param page 페이지 번호 (기본값: 0)
	 * @param size 페이지 크기 (기본값: 10)
	 * @return 댓글 목록 (페이징)
	 */
	@Operation(summary = "게시물별 댓글 목록 조회", description = "게시물의 댓글 목록을 페이징하여 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공",
		content = @Content(schema = @Schema(implementation = Page.class)))
	})
	@GetMapping("/posts/{postId}")
	public ResponseEntity<Page<CommentResponse>> getCommentsByPost(
		@Parameter(description = "게시물 ID", required = true) @PathVariable Long postId,
		@Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
		@Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

		log.info("게시글 댓글 목록 조회 - 게시글 ID: {}, 페이지: {}, 크기: {}", postId, page, size);

		try {
			Page<CommentResponse> comments = commentService.getCommentsByPost(postId, page, size);
			return ResponseEntity.ok(comments);
		} catch (Exception e) {
			log.error("사용자별 댓글 목록 조회 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 댓글 상세 조회
	 *
	 * @param commentId 댓글 ID
	 * @return 댓글 상세 정보
	 */
	@Operation(summary = "댓글 상세 조회", description = "댓글의 상세 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "댓글 상세 조회 성공",
		content = @Content(schema = @Schema(implementation = CommentResponse.class))),
		@ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
	})
	@GetMapping("/{commentId}/detail")
	public ResponseEntity<CommentResponse> getCommentDetail(
		@Parameter(description = "댓글 ID", required = true) @PathVariable Long commentId) {

		log.info("댓글 상세 조회 - 댓글 ID: {}", commentId);

		try {
			CommentResponse comment = commentService.getCommentById(commentId);
			return ResponseEntity.ok(comment);
		} catch (IllegalArgumentException e) {
			log.error("댓글 상세 조회 실패 - 댓글을 찾을 수 없음: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			log.error("댓글 상세 조회 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}
}
