package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCreateRequest {

	@NotBlank(message = "카테고리명은 필수입니다.")
	@Size(min = 1, max = 50, message = "카테고리명은 1자 이상 50자 이하여야 합니다.")
	private String name;

	@Size(max = 200, message = "카테고리 설명은 200자 이하여야 합니다.")
	private String description;
}
