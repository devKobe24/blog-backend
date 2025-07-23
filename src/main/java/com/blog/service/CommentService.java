package com.blog.service;

import com.blog.dto.request.CommentCreateRequest;
import com.blog.dto.request.CommentUpdateRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.dto.response.UserResponse;
import com.blog.entity.Comment;
import com.blog.entity.Post;
import com.blog.entity.User;
import com.blog.repository.CommentRepository;
import com.blog.repository.PostRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	@Transactional
	public CommentResponse createComment(Long postId, CommentCreateRequest request, String username) {
		User author = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("작성자를 찾을 수 없습니다."));
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

		Comment parent = null;
		if (request.getParentId() != null) {
			parent = commentRepository.findById(request.getParentId())
				.orElseThrow(() -> new IllegalArgumentException("부모 뎃글을 찾을 수 없습니다."));
		}

		Comment comment = Comment.builder()
			.content(request.getContent())
			.post(post)
			.author(author)
			.parent(parent)
			.isDeleted(false)
			.build();

		Comment saved = commentRepository.save(comment);
		return convertToResponse(saved);
	}

	@Transactional
	public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, String username) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		if (!comment.getAuthor().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
			throw new SecurityException("댓글을 수정할 권한이 없습니다.");
		}

		comment.setContent(request.getContent());
		return convertToResponse(commentRepository.save(comment));
	}

	@Transactional
	public void deleteComment(Long commentId, String username) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		if (!comment.getAuthor().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
			throw new SecurityException("댓글을 삭제할 권한이 없습니다.");
		}

		comment.setDeleted(true); // 소프트 삭제
		commentRepository.save(comment);
	}

	public Page<CommentResponse> getCommentsByPost(Long postId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
		return commentRepository.findByPostIdAndParentIsNullAndIsDeletedFalseOrderByCreatedAtDesc(postId, pageable)
			.map(this::convertToResponse);
	}

	public List<CommentResponse> getReplies(Long parentId) {
		return commentRepository.findByParentIdAndIsDeletedFalseOrderByCreatedAtAsc(parentId)
			.stream()
			.map(this::convertToResponse)
			.collect(Collectors.toList());
	}

	public Page<CommentResponse> getCommentsByAuthor(Long authorId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		return commentRepository.findByAuthorId(authorId, pageable)
			.map(this::convertToResponse);
	}

	public Page<CommentResponse> getRepliesByParentId(Long parentId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
		return commentRepository.findByParentIdAndIsDeletedFalse(parentId, pageable)
			.map(this::convertToResponse);
	}

	public CommentResponse getCommentById(Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
		return convertToResponse(comment);
	}

	// DTO 변환
	private CommentResponse convertToResponse(Comment comment) {
		return CommentResponse.builder()
			.id(comment.getId())
			.content(comment.getContent())
			.author(UserResponse.fromEntity(comment.getAuthor()))
			.parentId(comment.getParent() != null ? comment.getParent().getId() : null)
			.replies(comment.getReplies() != null
				? comment.getReplies().stream().map(this::convertToResponse).collect(Collectors.toList())
				: null)
			.likeCount(comment.getLikeCount())
			.isDeleted(comment.isDeleted())
			.createdAt(comment.getCreatedAt())
			.updatedAt(comment.getUpdatedAt())
			.build();
	}
}
