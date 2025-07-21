package com.blog.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagUpdateRequest {
	private String name;
	private String description;
}
