package com.blog.repository;

import com.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	Page<Post> findByIsPublishedTrue(Pageable pageable);

	Page<Post> findByAuthorIdAndIsPublishedTrue(Long authorId, Pageable pageable);

	Page<Post> findByCategoryIdAndIsPublishedTrue(Long categoryId, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.isPublished = true AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
	Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT p FROM Post p JOIN p.tags t WHERE p.isPublished = true AND t.name IN :tagNames")
	Page<Post> findByTagNames(@Param("tagNames") List<String> tagNames, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.isPublished = true ORDER BY p.viewCount DESC")
	Page<Post> findPopularPosts(Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.isPublished = true ORDER BY p.likeCount DESC")
	Page<Post> findMostLikedPosts(Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.isPublished = true AND p.category.id = :categoryId AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
	Page<Post> searchByCategoryAndKeyword(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT p FROM Post p JOIN p.tags t WHERE p.isPublished = true AND t.name IN :tagNames AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
	Page<Post> searchByTagsAndKeyword(@Param("tagNames") List<String> tagNames, @Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT COUNT(p) FROM Post p WHERE p.author.id = :authorId")
	Long countByAuthorId(@Param("authorId") Long authorId);

	@Query("SELECT p FROM Post p WHERE p.isPublished = true ORDER BY p.createdAt DESC")
	List<Post> findRecentPosts(Pageable pageable);
}
