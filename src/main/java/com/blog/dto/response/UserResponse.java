package com.blog.dto.response;

import com.blog.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
	private Long id;
	private String username;
	private String email;
	private String nickname;
	private String profileImage;
	private String role;
	private boolean isActive;
	private LocalDateTime createdAt;

	public static UserResponse fromEntity(User user) {
		return UserResponse.builder()
			.id(user.getId())
			.username(user.getUsername())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.profileImage(user.getProfileImage())
			.role(user.getRole().name())
			.isActive(user.isActive())
			.createdAt(user.getCreatedAt())
			.build();
	}
}
