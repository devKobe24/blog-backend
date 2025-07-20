package com.blog.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChangePasswordRequest {
	private String currentPassword;
	private String newPassword;
}
