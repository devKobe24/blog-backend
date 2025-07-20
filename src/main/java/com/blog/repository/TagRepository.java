package com.blog.repository;

import com.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

	Optional<Tag> findByName(String name);

	boolean existsByName(String name);

	List<Tag> findByNameIn(List<String> names);

	@Query("SELECT t FROM Tag t ORDER BY t.name ASC")
	List<Tag> findAllOrderByName();

	@Query("SELECT t FROM Tag t WHERE t.name LIKE %:keyword%")
	List<Tag> searchByName(@Param("keyword") String keyword);

	@Query("SELECT t FROM Tag t JOIN t.posts p WHERE p.id = :postId")
	List<Tag> findByPostId(@Param("postId") Long postId);
}
