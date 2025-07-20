package com.blog.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSearchRequest {
	private String keyword;
	private String sortBy = "createdAt";
	private String sortOrder = "desc";
	private Long categoryId;
	private List<String> tagNames;
	private int page = 0;
	private int size = 10;
}
