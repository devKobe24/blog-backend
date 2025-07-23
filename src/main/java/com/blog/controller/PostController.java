package com.blog.controller;

import com.blog.dto.request.PostCreateRequest;
import com.blog.dto.request.PostSearchRequest;
import com.blog.dto.request.PostUpdateRequest;
import com.blog.dto.response.PostListResponse;
import com.blog.dto.response.PostResponse;
import com.blog.service.PostService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	// 게시물 생성
	@PostMapping
	public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest request, Principal principal) {
		String username = principal.getName();
		return ResponseEntity.ok(postService.createPost(request,username));
	}

	// 게시물 수정
	@PutMapping("/{postId}")
	public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId,
	                                               @RequestBody PostUpdateRequest request,
                                                   Principal principal
	) {
		String username = principal.getName();
		return ResponseEntity.ok(postService.updatePost(postId, request, username));
	}

	// 게시물 삭제
	@DeleteMapping("/{postId}")
	public ResponseEntity<Void> deletePost(@PathVariable Long postId, Principal principal) {
		String username = principal.getName();
		postService.deletePost(postId, username);
		return ResponseEntity.ok().build();
	}

	// 게시물 단건 조회 (조회수 증가)
	@GetMapping("/{postId}")
	public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
		return ResponseEntity.ok(postService.getPost(postId, true));
	}

	// 게시물 목록 조회 (페이징)
	@GetMapping
	public ResponseEntity<Page<PostListResponse>> getPosts(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return ResponseEntity.ok(postService.getPosts(page, size));
	}

	// 게시물 검색
	@GetMapping("/search")
	public ResponseEntity<Page<PostListResponse>> searchPosts(PostSearchRequest request) {
		return ResponseEntity.ok(postService.searchPosts(request));
	}
}
