package com.blog.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponse {
	private String accessToken;
	private String refreshToken;
	private UserResponse userInfo;
}
