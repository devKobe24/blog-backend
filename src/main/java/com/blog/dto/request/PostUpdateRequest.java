package com.blog.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequest {

	@Size(min = 1, max = 200, message = "제목은 1자 이상 200자 이하여야 합니다.")
	private String title;

	@Size(min = 10, max = 10000, message = "내용은 10자 이상 10000자 이하여야 합니다.")
	private String content;

	private Long categoryId;

	@Size(max = 10, message = "태그는 최대 10개까지 추가할 수 있습니다.")
	private List<@Size(max = 20, message = "태그명은 20자 이하여야 합니다.") String> tagNames;

	private boolean isPublished;
}
