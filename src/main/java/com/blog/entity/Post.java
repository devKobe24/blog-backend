package com.blog.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"comments"})
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id", nullable = false)
	private User author;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Builder.Default
	@Column(name = "view_count")
	private int viewCount = 0;

	@Builder.Default
	@Column(name = "like_count")
	private int likeCount = 0;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Builder.Default
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "post_tags",
		joinColumns = @JoinColumn(name = "post_id"),
		inverseJoinColumns = @JoinColumn(name = "tag_id")
	)
	private List<Tag> tags = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Comment> comments = new ArrayList<>();

	@Builder.Default
	@Column(name = "is_published")
	private boolean isPublished = true;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	// 비즈니스 메서드들
	public void updateContent(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public void publish() {
		this.isPublished = true;
	}

	public void unpublish() {
		this.isPublished = false;
	}

	public void addTag(Tag tag) {
		if (this.tags == null) {
			this.tags = new ArrayList<>();
		}
		this.tags.add(tag);
	}

	public void removeTag(Tag tag) {
		if (this.tags != null) {
			this.tags.remove(tag);
		}
	}

	public void clearTags() {
		if (this.tags != null) {
			this.tags.clear();
		}
	}

	public boolean isAuthor(User user) {
		return this.author != null && this.author.equals(user);
	}

	public boolean isAuthor(String username) {
		return this.author != null && this.author.getUsername().equals(username);
	}

	public void incrementViewCount() {
		this.viewCount++;
	}

	public void incrementLikeCount() {
		this.likeCount++;
	}

	public void decrementLikeCount() {
		if (this.likeCount > 0) {
			this.likeCount--;
		}
	}
}
