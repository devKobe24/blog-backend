package com.blog.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDetailResponse {
	private Long id;
	private String name;
	private String description;
	private int postCount;
	private List<PostListResponse> recentPosts;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
