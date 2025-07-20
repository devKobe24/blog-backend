package com.blog.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponse {
	private Long id;
	private String name;
	private String description;
	private LocalDateTime createdAt;
}
