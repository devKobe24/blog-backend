package com.blog.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Entity Test")
class UserTest {
	private User user;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.username("testUser")
			.email("test@example.com")
			.password("encodedPassword")
			.nickname("테스트 사용자")
			.role(User.Role.USER)
			.isActive(true)
			.build();

		// @PrePersist를 수동으로 호출하여 createdAt, updatedAt 설정
		user.onCreate(); // 시간 설정
	}

	@Test
	@DisplayName("User Entity 생성 테스트")
	void createUser() {
		// given & when
		User newUser = User.builder()
			.username("newUser")
			.email("new@example.com")
			.password("password123")
			.nickname("새 사용자")
			.role(User.Role.USER)
			.isActive(true)
			.build();

		// @PrePersist를 수동으로 호출
		newUser.onCreate();

		// then
		assertThat(newUser.getUsername()).isEqualTo("newUser");
		assertThat(newUser.getEmail()).isEqualTo("new@example.com");
		assertThat(newUser.getPassword()).isEqualTo("password123");
		assertThat(newUser.getNickname()).isEqualTo("새 사용자");
		assertThat(newUser.getCreatedAt()).isNotNull();
		assertThat(newUser.getUpdatedAt()).isNotNull();
	}

	@Test
	@DisplayName("User Entity 프로필 수정 테스트")
	void updateProfile() {
		// given
		String newNickname = "수정된 닉네임";
		String newProfileImage = "updated-image.jpg";

		// when
		user.updateProfile(newNickname, newProfileImage);

		// then
		assertThat(user.getNickname()).isEqualTo(newNickname);
		assertThat(user.getProfileImage()).isEqualTo(newProfileImage);
	}

	@Test
	@DisplayName("User Entity 비밀번호 변경 테스트")
	void changePassword() {
		// given
		String newPassword = "newEncodedPassword";
		LocalDateTime beforeUpdate = user.getUpdatedAt();

		// when
		user.changePassword(newPassword);
		// @PreUpdate를 수동으로 호출
		user.onUpdate();

		// then
		assertThat(user.getPassword()).isEqualTo(newPassword);
		assertThat(user.getUpdatedAt()).isAfter(beforeUpdate);
	}

	@Test
	@DisplayName("User Entity equals/hashCode 테스트")
	void equalsAndHashCode() {
		// given
		User user1 = User.builder()
			.id(1L)
			.username("user1")
			.email("user1@example.com")
			.role(User.Role.USER)
			.build();

		User user2 = User.builder()
			.id(1L)
			.username("user2")
			.email("user2@example.com")
			.role(User.Role.USER)
			.build();

		User user3 = User.builder()
			.id(2L)
			.username("user1")
			.email("user1@example.com")
			.role(User.Role.USER)
			.build();

		// then
		assertThat(user1).isEqualTo(user2); // ID가 같으면 같은 객체
		assertThat(user1).isNotEqualTo(user3); // ID가 다르면 다른 객체
		assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
	}

	@Test
	@DisplayName("User Entity toString 테스트")
	void toStringTest() {
		// when
		String userString = user.toString();

		// then
		assertThat(userString).contains("testUser");
		assertThat(userString).contains("test@example.com");
		assertThat(userString).contains("테스트 사용자");
	}

	@Test
	@DisplayName("User Entity 계정 비활성화 테스트")
	void deactivate() {
		// given
		assertThat(user.isActive()).isTrue();

		// when
		user.deactivate();

		// then
		assertThat(user.isActive()).isFalse();
		assertThat(user.isEnabled()).isFalse();
	}

	@Test
	@DisplayName("User Entity 계정 활성화 테스트")
	void activate() {
		// given
		user.deactivate();
		assertThat(user.isActive()).isFalse();

		// when
		user.activate();

		// then
		assertThat(user.isActive()).isTrue();
		assertThat(user.isEnabled()).isTrue();
	}

	@Test
	@DisplayName("User Entity 관리자 권한 확인 테스트")
	void isAdmin() {
		// given
		User adminUser = User.builder()
			.username("admin")
			.email("admin@example.com")
			.password("password")
			.role(User.Role.ADMIN)
			.build();

		// when & then
		assertThat(adminUser.isAdmin()).isTrue();
	}
}