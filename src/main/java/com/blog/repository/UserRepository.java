package com.blog.repository;

import com.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	@Query("SELECT u FROM User u WHERE u.isActive = true")
	List<User> findAllActiveUsers();

	@Query("SELECT u FROM User u WHERE u.role = :role")
	List<User> findByRole(@Param("role") User.Role role);

	@Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.nickname LIKE %:keyword%")
	List<User> searchUsers(@Param("keyword") String keyword);

	@Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
	List<User> findAllAdmins();

	@Query("SELECT u FROM User u WHERE u.isActive = false")
	List<User> findAllInactiveUsers();

	@Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ADMIN'")
	long countAdmins();

	@Query("SELECT u FROM User u WHERE u.username = :username AND u.isActive = true")
	Optional<User> findActiveUserByUsername(@Param("username") String username);
}
