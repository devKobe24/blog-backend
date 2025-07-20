package com.blog.repository;

import com.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	Optional<Category> findByName(String name);

	boolean existsByName(String name);

	@Query("SELECT c FROM Category c ORDER BY c.name ASC")
	List<Category> findAllOrderByName();

	@Query("SELECT c FROM Category c WHERE c.name LIKE %:keyword%")
	List<Category> searchByName(@Param("keyword") String keyword);
}
