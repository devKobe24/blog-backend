package com.blog.service;

import com.blog.dto.request.CategoryCreateRequest;
import com.blog.dto.request.CategoryUpdateRequest;
import com.blog.dto.response.*;
import com.blog.entity.Category;
import com.blog.entity.Post;
import com.blog.repository.CategoryRepository;
import com.blog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final PostRepository postRepository;

	@Transactional
	public CategoryResponse createCategory(CategoryCreateRequest request) {
		if (categoryRepository.existsByName(request.getName())) {
			throw new IllegalArgumentException("이미 존재하는 카테고리명입니다.");
		}
		Category category = Category.builder()
			.name(request.getName())
			.description(request.getDescription())
			.build();
		Category saved = categoryRepository.save(category);
		return convertToResponse(saved);
	}

	@Transactional
	public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest request) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
		if (request.getName() != null) category.setName(request.getName());
		if (request.getDescription() != null) category.setDescription(request.getDescription());
		return convertToResponse(categoryRepository.save(category));
	}

	@Transactional
	public void deleteCategory(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
		categoryRepository.delete(category);
	}

	public List<CategoryListResponse> getAllCategories() {
		List<Category> categories = categoryRepository.findAllOrderByName();
		return categories.stream()
			.map(this::convertToListResponse)
			.collect(Collectors.toList());
	}

	public CategoryDetailResponse getCategoryDetail(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
		List<Post> recentPosts = postRepository.findRecentPosts(
			PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
		);
		return CategoryDetailResponse.builder()
			.id(category.getId())
			.name(category.getName())
			.description(category.getDescription())
			.postCount(category.getPosts() != null ? category.getPosts().size() : 0)
			.recentPosts(recentPosts.stream().map(this::convertToPostListResponse).collect(Collectors.toList()))
			.createdAt(category.getCreatedAt())
			.updatedAt(category.getUpdatedAt())
			.build();
	}

	public List<CategoryListResponse> searchCategories(String keyword) {
		return categoryRepository.searchByName(keyword).stream()
			.map(this::convertToListResponse)
			.collect(Collectors.toList());
	}

	// DTO 변환 메서드
	private CategoryResponse convertToResponse(Category category) {
		return CategoryResponse.builder()
			.id(category.getId())
			.name(category.getName())
			.description(category.getDescription())
			.createdAt(category.getCreatedAt())
			.build();
	}

	private CategoryListResponse convertToListResponse(Category category) {
		return CategoryListResponse.builder()
			.id(category.getId())
			.name(category.getName())
			.description(category.getDescription())
			.postCount(category.getPosts() != null ? category.getPosts().size() : 0)
			.createdAt(category.getCreatedAt())
			.build();
	}

	private PostListResponse convertToPostListResponse(Post post) {
		String contentPreview = post.getContent().length() > 200
			? post.getContent().substring(0, 200) + "..."
			: post.getContent();
		return PostListResponse.builder()
			.id(post.getId())
			.title(post.getTitle())
			.contentPreview(contentPreview)
			.viewCount(post.getViewCount())
			.likeCount(post.getLikeCount())
			.author(UserResponse.fromEntity(post.getAuthor()))
			.category(null) // 필요시 category 변환 추가
			.tags(null) // 필요시 tags 변환 추가
			.commentCount(post.getComments() != null ? post.getComments().size() : 0)
			.createdAt(post.getCreatedAt())
			.build();
	}
}
