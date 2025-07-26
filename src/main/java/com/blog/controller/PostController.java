package com.blog.controller;

import com.blog.dto.request.PostCreateRequest;
import com.blog.dto.request.PostSearchRequest;
import com.blog.dto.request.PostUpdateRequest;
import com.blog.dto.response.PostListResponse;
import com.blog.dto.response.PostResponse;
import com.blog.service.PostService;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Tag(name = "Post", description = "게시물 관련 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@Operation(summary = "게시물 생성", description = "새로운 게시물을 생성합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "게시물 생성 성공",
			content = @Content(schema = @Schema(implementation = PostResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 필요"),
		@ApiResponse(responseCode = "403", description = "권한 없음")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@PostMapping
	public ResponseEntity<PostResponse> createPost(
		@Parameter(description = "게시물 생성 요청", required = true) @Valid @RequestBody PostCreateRequest request,
		Principal principal) {
		String username = principal.getName();
		PostResponse response = postService.createPost(request, username);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "게시물 수정", description = "기존 게시물을 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시물 수정 성공",
			content = @Content(schema = @Schema(implementation = PostResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 필요"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@PutMapping("/{postId}")
	public ResponseEntity<PostResponse> updatePost(
		@Parameter(description = "게시물 ID", required = true) @PathVariable Long postId,
		@Parameter(description = "게시물 수정 요청", required = true) @Valid @RequestBody PostUpdateRequest request,
		Principal principal) {
		String username = principal.getName();
		return ResponseEntity.ok(postService.updatePost(postId, request, username));
	}

	@Operation(summary = "게시물 삭제", description = "게시물을 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "게시물 삭제 성공"),
		@ApiResponse(responseCode = "401", description = "인증 필요"),
		@ApiResponse(responseCode = "403", description = "권한 없음"),
		@ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@DeleteMapping("/{postId}")
	public ResponseEntity<Void> deletePost(
		@Parameter(description = "게시물 ID", required = true) @PathVariable Long postId, 
		Principal principal) {
		String username = principal.getName();
		postService.deletePost(postId, username);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "게시물 단건 조회", description = "게시물을 조회합니다. 조회수가 증가합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시물 조회 성공",
			content = @Content(schema = @Schema(implementation = PostResponse.class))),
		@ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음")
	})
	@GetMapping("/{postId}")
	public ResponseEntity<PostResponse> getPost(
		@Parameter(description = "게시물 ID", required = true) @PathVariable Long postId) {
		return ResponseEntity.ok(postService.getPost(postId, true));
	}

	@Operation(summary = "게시물 목록 조회", description = "페이징을 지원하는 게시물 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시물 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = Page.class)))
	})
	@GetMapping
	public ResponseEntity<Page<PostListResponse>> getPosts(
		@Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
		@Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
	) {
		return ResponseEntity.ok(postService.getPosts(page, size));
	}

	@Operation(summary = "게시물 검색", description = "조건에 맞는 게시물을 검색합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "게시물 검색 성공",
			content = @Content(schema = @Schema(implementation = Page.class)))
	})
	@GetMapping("/search")
	public ResponseEntity<Page<PostListResponse>> searchPosts(
		@Parameter(description = "검색 조건") PostSearchRequest request) {
		return ResponseEntity.ok(postService.searchPosts(request));
	}
}
