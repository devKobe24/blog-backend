package com.blog.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
	private Long id;
	private String title;
	private String content;
	private int viewContent;
	private int likeContent;
	private UserResponse author;
	private CategoryResponse category;
	private List<TagResponse> tags;
	private int commentCount;
	private boolean isPublished;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
