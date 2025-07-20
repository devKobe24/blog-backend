package com.blog.repository;

import org.springframework.data.domain.Page;
import com.blog.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	Page<Comment> findByPostIdAndParentIsNullAndIsDeletedFalseOrderByCreatedAtDesc(Long postId, Pageable pageable);

	List<Comment> findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(Long postId);

	List<Comment> findByParentIdAndIsDeletedFalseOrderByCreatedAtAsc(Long parentId);

	@Query("SELECT c FROM Comment c WHERE c.author.id = :authorId AND c.isDeleted = false")
	Page<Comment> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

	@Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId AND c.isDeleted = false")
	Long countByPostId(@Param("postId") Long postId);

	@Query("SELECT COUNT(c) FROM Comment c WHERE c.author.id = :authorId AND c.isDeleted = false")
	Long countByAuthorId(@Param("authorId") Long authorId);

	@Query("SELECT c FROM Comment c WHERE c.isDeleted = false ORDER BY c.createdAt DESC")
	Page<Comment> findRecentComments(Pageable pageable);

	@Query("SELECT c FROM Comment c WHERE c.content LIKE %:keyword% AND c.isDeleted = false")
	Page<Comment> searchByContent(@Param("keyword") String keyword, Pageable pageable);
}
