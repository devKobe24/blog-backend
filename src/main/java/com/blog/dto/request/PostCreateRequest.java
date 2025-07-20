package com.blog.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequest {
	private String title;
	private String content;
	private Long categoryId;
	private List<String> tagNames;
}
