package com.blog.controller;

import com.blog.dto.request.TagCreateRequest;
import com.blog.dto.request.TagUpdateRequest;
import com.blog.dto.response.TagDetailResponse;
import com.blog.dto.response.TagListResponse;
import com.blog.dto.response.TagResponse;
import com.blog.service.TagService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@Tag(name = "Tag", description = "태그 관련 API")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

	private final TagService tagService;

	/**
	 * 태그 생성 (관리자 전용)
	 *
	 * @param request 태그 생성 요청
	 * @param principal 인증된 사용자 정보
	 * @return 생성된 태그 정보
	 */
	@Operation(summary = "태그 생성", description = "새로운 태그를 생성합니다 (관리자 전용).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "태그 생성 성공",
		content = @Content(schema = @Schema(implementation = TagResponse.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 필요"),
		@ApiResponse(responseCode = "403", description = "권한 없음")
	})
	@SecurityRequirement(name = "Bearer Authentication")
	@PostMapping
	public ResponseEntity<TagResponse> createTag(
		@Parameter(description = "태그 생성 요청", required = true) @RequestBody TagCreateRequest request,
		Principal principal) {

		log.info("태그 생성 요청 - 이름: {}, 작성자: {}", request.getName(), principal.getName());

		try {
			TagResponse response = tagService.createTag(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			log.error("태그 생성 실패: {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		} catch (SecurityException e) {
			log.error("태그 생성 실패 - 권한 없음: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	/**
	 * 태그 수정 (관리자 전용)
	 *
	 * @param tagId 태그 ID
	 * @param request 태그 수정 요청
	 * @param principal 인증된 사용자 정보
	 * @return 수정된 태그 정보
	 */
	@PutMapping("/{tagId}")
	public ResponseEntity<TagResponse> updateTag(
		@PathVariable Long tagId,
		@RequestBody TagUpdateRequest request,
		Principal principal) {

		log.info("태그 수정 요청 - 태그 ID: {}, 수정자: {}", tagId, principal.getName());

		try {
			TagResponse response = tagService.updateTag(tagId, request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			log.error("태그 수정 실패 - 태그를 찾을 수 없음: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (SecurityException e) {
			log.error("태그 수정 실패 - 권한 없음: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	/**
	 * 태그 삭제 (관리자 전용)
	 *
	 * @param tagId 태그 ID
	 * @param principal 인증된 사용자 정보
	 * @return 삭제 성공 응답
	 */
	@DeleteMapping("/{tagId}")
	public ResponseEntity<Void> deleteTag(
		@PathVariable Long tagId,
		Principal principal) {

		log.info("태그 삭제 요청 - 태그 ID: {}, 삭제자: {}", tagId, principal.getName());

		try {
			tagService.deleteTag(tagId);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			log.error("태그 삭제 실패 - 태그를 찾을 수 없음: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (SecurityException e) {
			log.error("태그 삭제 실패 - 권한 없음: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	/**
	 * 전체 태그 목록 조회
	 *
	 * @return 태그 목록
	 */
	@GetMapping
	public ResponseEntity<List<TagListResponse>> getAllTags() {

		log.info("전체 태그 목록 조회 요청");

		try {
			List<TagListResponse> tags = tagService.getAllTags();
			return ResponseEntity.ok(tags);
		} catch (Exception e) {
			log.error("태그 목록 조회 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 태그 상세 정보 조회
	 *
	 * @param tagId 태그 ID
	 * @return 태그 상세 정보 (포스트 수, 최근 포스트 포함)
	 */
	@GetMapping("/{tagId}")
	public ResponseEntity<TagDetailResponse> getTagDetail(@PathVariable Long tagId) {

		log.info("태그 상세 조회 - 태그 ID: {}", tagId);

		try {
			TagDetailResponse tag = tagService.getTagDetail(tagId);
			return ResponseEntity.ok(tag);
		} catch (IllegalArgumentException e) {
			log.error("태그 상세 조회 실패 - 태그를 찾을 수 없음: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			log.error("태그 상세 조회 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	    /**
     * 태그 검색 (페이징)
     * 
     * @param keyword 검색 키워드
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return 검색된 태그 목록 (페이징)
     */
    @GetMapping("/search")
    public ResponseEntity<Page<TagListResponse>> searchTags(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("태그 검색 요청 - 키워드: {}, 페이지: {}, 크기: {}", keyword, page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TagListResponse> tags = tagService.searchTags(keyword, pageable);
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            log.error("태그 검색 실패: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

	/**
	 * 태그 존재 여부 확인
	 *
	 * @param tagId 태그 ID
	 * @return 존재 여부
	 */
	@GetMapping("/{tagId}/exists")
	public ResponseEntity<Boolean> checkTagExists(@PathVariable Long tagId) {

		log.info("태그 존재 여부 확인 - 태그 ID: {}", tagId);

		try {
			boolean exists = tagService.existsById(tagId);
			return ResponseEntity.ok(exists);
		} catch (Exception e) {
			log.error("태그 존재 여부 확인 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 태그별 포스트 수 조회
	 *
	 * @param tagId 태그 ID
	 * @return 포스트 수
	 */
	@GetMapping("/{tagId}/post-count")
	public ResponseEntity<Integer> getTagPostCount(@PathVariable Long tagId) {

		log.info("태그 포스트 수 조회 - 태그 ID: {}", tagId);

		try {
			int postCount = tagService.getPostCount(tagId);
			return ResponseEntity.ok(postCount);
		} catch (IllegalArgumentException e) {
			log.error("태그 포스트 수 조회 실패 - 태그를 찾을 수 없음: {}", e.getMessage());
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			log.error("태그 포스트 수 조회 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 인기 태그 목록 조회 (포스트 수 기준)
	 *
	 * @param limit 조회할 태그 수 (기본값: 10)
	 * @return 인기 태그 목록
	 */
	@GetMapping("/popular")
	public ResponseEntity<List<TagListResponse>> getPopularTags(
		@RequestParam(defaultValue = "10") int limit) {

		log.info("인기 태그 목록 조회 요청 - 제한: {}", limit);

		try {
			List<TagListResponse> popularTags = tagService.getPopularTags(limit);
			return ResponseEntity.ok(popularTags);
		} catch (Exception e) {
			log.error("인기 태그 목록 조회 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	/**
	 * 태그 자동완성
	 *
	 * @param keyword 검색 키워드
	 * @param limit 조회할 태그 수 (기본값: 5)
	 * @return 자동완성 태그 목록
	 */
	@GetMapping("/autocomplete")
	public ResponseEntity<List<TagResponse>> getTagAutoComplete(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "5") int limit) {

		log.info("태그 자동완성 요청 - 키워드: {}, 제한: {}, keyword, limit");

		try {
			List<TagResponse> autocompleteTags = tagService.getTagAutoComplete(keyword, limit);
			return ResponseEntity.ok(autocompleteTags);
		} catch (Exception e) {
			log.error("태그 자동완성 실패: {}", e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}
}
