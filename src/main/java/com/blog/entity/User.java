package com.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"posts", "comments"})
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "profile_image")
	private String profileImage;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Builder.Default
	@Column(name = "is_active")
	private boolean isActive = true;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Post> posts;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Comment> comments;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public boolean isEnabled() {
		return isActive;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return isActive;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	public enum Role {
		USER, ADMIN
	}

	// 비즈니스 메서드들
	public void updateProfile(String nickname, String profileImage) {
		this.nickname = nickname;
		this.profileImage = profileImage;
		// @PreUpdate가 자동으로 updatedAt을 설정합니다.
	}

	public void changePassword(String newPassword) {
		this.password = newPassword;
		// @PreUpdate가 자동으로 updatedAt을 설정합니다.
	}

	public void deactivate() {
		this.isActive = false;
		// @PreUpdate가 자동으로 updatedAt을 설정합니다.
	}

	public void activate() {
		this.isActive = true;
		// @PreUpdate가 자동으로 updatedAt을 설정합니다.
	}

	public boolean isAdmin() {
		return this.role == Role.ADMIN;
	}
}
