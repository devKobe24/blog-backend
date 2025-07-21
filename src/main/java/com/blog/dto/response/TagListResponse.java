package com.blog.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagListResponse {
	private Long id;
	private String name;
	private String description;
	private int postCount;
	private LocalDateTime createdAt;
}
