package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagCreateRequest {

	@NotBlank(message = "태그명은 필수입니다.")
	@Size(min = 1, max = 20, message = "태그명은 1자 이상 20자 이하여야 합니다.")
	@Pattern(regexp = "^[a-zA-Z0-9가-힣_]+$", message = "태그명은 영문, 숫자, 한글, 언더스코어(`_`)만 사용 가능합니다.")
	private String name;

	@Size(max = 100, message = "태그 설명은 100자 이하여야 합니다.")
	private String description;
}
