package com.blog.dto.response;

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
	private LocalDateTime createdAt;
}
