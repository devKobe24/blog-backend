package com.blog.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequest {
	private String title;
	private String content;
	private Long categoryId;
	private List<String> tagNames;
	private boolean isPublished;
}
