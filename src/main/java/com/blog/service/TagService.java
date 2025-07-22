package com.blog.service;

import com.blog.dto.request.TagCreateRequest;
import com.blog.dto.request.TagUpdateRequest;
import com.blog.dto.response.*;
import com.blog.entity.Post;
import com.blog.entity.Tag;
import com.blog.repository.PostRepository;
import com.blog.repository.TagRepository;
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
public class TagService {

	private final TagRepository tagRepository;
	private final PostRepository postRepository;

	@Transactional
	public TagResponse createTag(TagCreateRequest request) {
		if (tagRepository.existsByName(request.getName())) {
			throw new IllegalArgumentException("이미 존재하는 태그명입니다.");
		}
		Tag tag = Tag.builder()
			.name(request.getName())
			.description(request.getDescription())
			.build();
		Tag saved = tagRepository.save(tag);
		return convertToResponse(saved);
	}

	@Transactional
	public TagResponse updateTag(Long tagId, TagUpdateRequest request) {
		Tag tag = tagRepository.findById(tagId)
			.orElseThrow(() -> new IllegalArgumentException("태그를 찾을 수 없습니다."));
		if (request.getName() != null) tag.setName(request.getName());
		if (request.getDescription() != null) tag.setDescription(request.getDescription());
		return convertToResponse(tagRepository.save(tag));
	}

	@Transactional
	public void deleteTag(Long tagId) {
		Tag tag = tagRepository.findById(tagId)
			.orElseThrow(() -> new IllegalArgumentException("태그를 찾을 수 없습니다."));
		tagRepository.delete(tag);
	}

	public List<TagListResponse> getAllTags() {
		List<Tag> tags = tagRepository.findAllOrderByName();
		return tags.stream()
			.map(this::convertToListResponse)
			.collect(Collectors.toList());
	}

	public TagDetailResponse getTagDetail(Long tagId) {
		Tag tag = tagRepository.findById(tagId)
			.orElseThrow(() -> new IllegalArgumentException("태그를 찾을 수 없습니다."));
		List<Post> recentPosts = postRepository.findRecentPosts(
			PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
		);
		return TagDetailResponse.builder()
			.id(tag.getId())
			.name(tag.getName())
			.description(tag.getDescription())
			.postCount(tag.getPosts() != null ? tag.getPosts().size() : 0)
			.recentPosts(recentPosts.stream().map(this::convertToPostListResponse).collect(Collectors.toList()))
			.createdAt(tag.getCreatedAt())
			.updatedAt(tag.getUpdatedAt())
			.build();
	}

	public List<TagListResponse> searchTags(String keyword) {
		return tagRepository.searchByName(keyword).stream()
			.map(this::convertToListResponse)
			.collect(Collectors.toList());
	}

	// DTO 변환 메서드
	private TagResponse convertToResponse(Tag tag) {
		return TagResponse.builder()
			.id(tag.getId())
			.name(tag.getName())
			.description(tag.getDescription())
			.createdAt(tag.getCreatedAt())
			.build();
	}

	private TagListResponse convertToListResponse(Tag tag) {
		return TagListResponse.builder()
			.id(tag.getId())
			.name(tag.getName())
			.description(tag.getDescription())
			.postCount(tag.getPosts() != null ? tag.getPosts().size() : 0)
			.createdAt(tag.getCreatedAt())
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
