package com.blog.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentListResponse {
	private Long id;
	private String content;
	private UserResponse author;
	private Long parentId;
	private int likeCount;
	private boolean isDeleted;
	private LocalDateTime createdAt;
}
