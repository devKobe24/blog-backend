package com.blog.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

	@Size(max = 50, message = "닉네임은 50자 이하여야 합니다.")
	private String nickname;

	@Size(max = 255, message = "프로필 이미지 URL은 255자 이하여야 합니다.")
	@Pattern(regexp = "^(https?://.*|data:image/.*|/.*)$", message = "올바른 이미지 URL 형식이 아닙니다.")
	private String profileImage;
}
