package com.blog.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
	private Long id;
	private String content;
	private UserResponse author;
	private Long parentId;
	private List<CommentResponse> replies;
	private int likeCount;
	private boolean isDeleted;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
