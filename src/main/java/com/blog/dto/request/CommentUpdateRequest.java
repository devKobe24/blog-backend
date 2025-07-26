package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateRequest {

	@NotBlank(message = "댓글 내용은 필수입니다.")
	@Size(min = 1, max = 1000, message = "댓글 내용은 1자 이상 1000자 이하여야 합니다.")
	private String content;
}
