package com.blog.controller;

import com.blog.dto.request.CategoryCreateRequest;
import com.blog.dto.request.CategoryUpdateRequest;
import com.blog.dto.response.CategoryDetailResponse;
import com.blog.dto.response.CategoryListResponse;
import com.blog.dto.response.CategoryResponse;
import com.blog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@Tag(name = "Category", description = "카테고리 관련 API")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	/**
	 * 카테고리 생성 (관리자 전용)
	 *
	 * @param request 카테고리 생성 요청
	 * @param principal 인증된 사용자 정보
	 * @return 생성된 카테고리 정보
	 */
	@Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다 (관리자 전용).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "카테고리 생성 성공",
		content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 필요"),
		@ApiResponse(responseCode = "403", description = "권한 없음")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@PostMapping
	public ResponseEntity<CategoryResponse> createCategory(
		@Parameter(description = "카테고리 생성 요청", required = true) @RequestBody CategoryCreateRequest request,
		Principal principal) {

		log.info("카테고리 생성 요청 - 이름: {}, 작성자: {}", request.getName(), principal.getName());

		try {
			CategoryResponse response = categoryService.createCategory(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			log.error("카테고리 생성 실패: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (SecurityException e) {
			log.error("카테고리 생성 실패 - 권한 없음: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	/**
	 * 카테고리 수정 (관리자 전용)
	 *
	 * @param categoryId 카테고리 ID
	 * @param request 카테고리 수정 요청
	 * @param principal 인증된 사용자 정보
	 * @return 수정된 카테고리 정보
	 */
	@PutMapping("/{categoryId}")
	public ResponseEntity<CategoryResponse> updateCategory(
		@PathVariable Long categoryId,
		@RequestBody CategoryUpdateRequest request,
		Principal principal) {

		log.info("카테고리 수정 요청 - 카테고리 ID: {}, 수정자: {}", categoryId, principal.getName());

		try {
			CategoryResponse response = categoryService.updateCategory(categoryId, request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			log.error("카테고리 수정 실패 - 카테고리를 찾을 수 없음: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (SecurityException e) {
			log.error("카테고리 수정 실패 - 권한 없음: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	/**
	 * 카테고리 삭제 (관리자 전용)
	 *
	 * @param categoryId 카테고리 ID
	 * @param principal 인증된 사용자 정보
	 * @return 삭제 성공 응답
	 */
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<Void> deleteCategory(
		@PathVariable Long categoryId,
		Principal principal) {

		log.info("카테고리 삭제 요청 - 카테고리 ID: {}, 삭제자: {}",categoryId, principal.getName());

		try {
			categoryService.deleteCategory(categoryId);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			log.error("카테고리 삭제 실패 - 카테고리를 찾을 수 없음: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (SecurityException e) {
			log.error("카테고리 삭제 실패 - 권한 없음: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	/**
	 * 전체 카테고리 목록 조회
	 *
	 * @return 카테고리 목록
	 */
	@GetMapping
	public ResponseEntity<List<CategoryListResponse>> getAllCategories() {

		log.info("전체 카테고리 목록 조회 요청");

		try {
			List<CategoryListResponse> categories = categoryService.getAllCategories();
			return ResponseEntity.ok(categories);
		} catch (Exception e) {
			log.error("카테고리 목록 조회 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 카테고리 상세 정보 조회
	 *
	 * @param categoryId 카테고리 ID
	 * @return 카테고리 상세 정보 (포스트 수, 최근 포스트 포함)
	 */
	@GetMapping("/{categoryId}")
	public ResponseEntity<CategoryDetailResponse> getCategoryDetail(@PathVariable Long categoryId) {

		log.info("카테고리 상세 조회 - 카테고리 ID: {}", categoryId);

		try {
			CategoryDetailResponse category = categoryService.getCategoryDetail(categoryId);
			return ResponseEntity.ok(category);
		} catch (IllegalArgumentException e) {
			log.error("카테고리 상세 조회 실패 - 카테고리를 찾을 수 없음: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			log.error("카테고리 상세 조회 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 카테고리 검색
	 *
	 * @param keyword 검색 키워드
	 * @return 검색된 카테고리 목록
	 */
	@GetMapping("/search")
	public ResponseEntity<List<CategoryListResponse>> searchCategories(
		@RequestParam String keyword) {

		log.info("카테고리 검색 요청 - 키워드: {}", keyword);

		try {
			List<CategoryListResponse> categories = categoryService.searchCategories(keyword);
			return ResponseEntity.ok(categories);
		} catch (Exception e) {
			log.error("카테고리 검색 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 카테고리 존재 여부 확인
	 *
	 * @param categoryId 카테고리 ID
	 * @return 존재 여부
	 */
	@GetMapping("/{categoryId}/exists")
	public ResponseEntity<Boolean> checkCategoryExists(@PathVariable Long categoryId) {

		log.info("카테고리 존재 여부 확인 - 카테고리 ID: {}", categoryId);

		try {
			boolean exists = categoryService.existsById(categoryId);
			return ResponseEntity.ok(exists);
		} catch (Exception e) {
			log.error("카테고리 존재 여부 확인 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 카테고리별 포스트 수 조회
	 *
	 * @param categoryId 카테고리 ID
	 * @return 포스트 수
	 */
	@GetMapping("/{categoryId}/post-count")
	public ResponseEntity<Integer> getCategoryPostCount(@PathVariable Long categoryId) {

		log.info("카테고리 포스트 수 조회 - 카테고리 ID: {}", categoryId);

		try {
			int postCount = categoryService.getPostCount(categoryId);
			return ResponseEntity.ok(postCount);
		} catch (IllegalArgumentException e) {
			log.error("카테고리 포스트 수 조회 실패 - 카테고리를 찾을 수 없음: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			log.error("카테고리 포스트 수 조회 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}
}
