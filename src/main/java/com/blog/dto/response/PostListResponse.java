package com.blog.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListResponse {
	private Long id;
	private String title;
	private String contentPreview;
	private int viewCount;
	private int likeCount;
	private UserResponse author;
	private CategoryResponse category;
	private List<TagResponse> tags;
	private int commentCount;
	private LocalDateTime createdAt;
}
