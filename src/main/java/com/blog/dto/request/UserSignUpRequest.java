package com.blog.dto.request;

import com.blog.validation.PasswordStrength;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpRequest {

	@NotBlank(message = "사용자명은 필수입니다.")
	@Size(min = 3, max = 20, message = "사용자명은 3자 이상 20자 이하여야 합니다.")
	@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "사용자명은 영문, 숫자, 밑줄(_)만 포함할 수 있습니다.")
	private String username;

	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "올바른 이메일 형식이 아닙니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(max = 100, message = "비밀번호는 100자 이하여야 합니다.")
	@PasswordStrength
	private String password;

	@Size(max = 50, message = "닉네임은 50자 이하여야 합니다.")
	private String nickname;
}
