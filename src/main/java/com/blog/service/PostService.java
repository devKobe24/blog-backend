package com.blog.service;

import com.blog.dto.request.PostCreateRequest;
import com.blog.dto.request.PostSearchRequest;
import com.blog.dto.request.PostUpdateRequest;
import com.blog.dto.response.*;
import com.blog.entity.Category;
import com.blog.entity.Post;
import com.blog.entity.Tag;
import com.blog.entity.User;
import com.blog.repository.CategoryRepository;
import com.blog.repository.PostRepository;
import com.blog.repository.TagRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final TagRepository tagRepository;
	private final UserRepository userRepository;

	@Transactional
	public PostResponse createPost(PostCreateRequest request, String username) {
		User author = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("작성자를 찾을 수 없습니다."));

		Post post = Post.builder()
			.title(request.getTitle())
			.content(request.getContent())
			.author(author)
			.isPublished(true)
			.build();

		// 카테고리 설정
		if (request.getCategoryId() != null) {
			Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
			post.setCategory(category);
		}

		// 태그 설정
		if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
			List<Tag> tags = new ArrayList<>();
			for (String tagName : request.getTagNames()) {
				Tag tag = tagRepository.findByName(tagName)
					.orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
				tags.add(tag);
			}
			post.setTags(tags);
		}

		Post savedPost = postRepository.save(post);
		return convertToPostResponse(savedPost);
	}

	@Transactional
	public PostResponse updatePost(Long postId, PostUpdateRequest request, String username) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		if (!post.getAuthor().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
			throw new SecurityException("게시물을 수정할 권한이 없습니다.");
		}
		if (request.getTitle() != null) {
			post.setTitle(request.getTitle());
		}
		if (request.getContent() != null) {
			post.setContent(request.getContent());
		}
		if (request.getCategoryId() != null) {
			Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
			post.setCategory(category);
		}
		if (request.getTagNames() != null) {
			List<Tag> tags = new ArrayList<>();
			for (String tagName : request.getTagNames()) {
				Tag tag = tagRepository.findByName(tagName)
					.orElseGet(() -> tagRepository.save(Tag.builder().name(tagName).build()));
				tags.add(tag);
			}
			post.setTags(tags);
		}
		post.setPublished(request.isPublished());
		Post updatedPost = postRepository.save(post);
		return convertToPostResponse(updatedPost);
	}

	@Transactional
	public void deletePost(Long postId, String username) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		if (!post.getAuthor().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
			throw new SecurityException("게시물을 삭제할 권한이 없습니다.");
		}
		postRepository.delete(post);
	}

	public PostResponse getPost(Long postId, boolean incrementView) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
		if (incrementView) {
			post.incrementViewCount();
			postRepository.save(post);
		}
		return convertToPostResponse(post);
	}

	public Page<PostListResponse> getPosts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<Post> posts = postRepository.findByIsPublishedTrue(pageable);
		return posts.map(this::convertToListResponse);
	}

	public Page<PostListResponse> searchPosts(PostSearchRequest request) {
		Sort sort  = Sort.by(
			request.getSortOrder().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
			request.getSortBy()
		);
		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
		Page<Post> posts;
		if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
			if (request.getCategoryId() != null && request.getTagNames() != null && !request.getTagNames().isEmpty()) {
				posts = postRepository.searchByTagsAndKeyword(request.getTagNames(), request.getKeyword(), pageable);
			} else if (request.getCategoryId() != null) {
				posts = postRepository.searchByCategoryAndKeyword(request.getCategoryId(), request.getKeyword(), pageable);
			} else if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
				posts = postRepository.findByTagNames(request.getTagNames(), pageable);
			} else {
				posts = postRepository.searchByKeyword(request.getKeyword(), pageable);
			}
		} else {
			if (request.getCategoryId() != null) {
				posts = postRepository.findByCategoryIdAndIsPublishedTrue(request.getCategoryId(), pageable);
			} else if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
				posts = postRepository.findByTagNames(request.getTagNames(), pageable);
			} else {
				posts = postRepository.findByIsPublishedTrue(pageable);
			}
		}
		return posts.map(this::convertToListResponse);
	}

	// DTO 변환 메서드 (실무에서 자주 사용)
	private PostResponse convertToPostResponse(Post post) {
		return PostResponse.builder()
			.id(post.getId())
			.title(post.getTitle())
			.content(post.getContent())
			.viewCount(post.getViewCount())
			.likeCount(post.getLikeCount())
			.author(UserResponse.fromEntity(post.getAuthor()))
			.category(post.getCategory() != null ?
				CategoryResponse.builder()
					.id(post.getCategory().getId())
					.name(post.getCategory().getName())
					.description(post.getCategory().getDescription())
					.createdAt(post.getCategory().getCreatedAt())
					.build() : null)
			.tags(post.getTags().stream().map(tag ->
				TagResponse.builder()
					.id(tag.getId())
					.name(tag.getName())
					.description(tag.getDescription())
					.createdAt(tag.getCreatedAt())
					.build()
			).collect(Collectors.toList()))
			.commentCount(post.getComments() != null ? post.getComments().size() : 0)
			.isPublished(post.isPublished())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.build();
	}

	private PostListResponse convertToListResponse(Post post) {
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
			.category(post.getCategory() != null ?
				CategoryResponse.builder()
					.id(post.getCategory().getId())
					.name(post.getCategory().getName())
					.description(post.getCategory().getDescription())
					.createdAt(post.getCategory().getCreatedAt())
					.build() : null)
			.tags(post.getTags().stream().map(tag ->
				TagResponse.builder()
					.id(tag.getId())
					.name(tag.getName())
					.description(tag.getDescription())
					.createdAt(tag.getCreatedAt())
					.build()
				).collect(Collectors.toList()))
			.commentCount(post.getComments() != null ? post.getComments().size() : 0)
			.createdAt(post.getCreatedAt())
			.build();
	}
}
