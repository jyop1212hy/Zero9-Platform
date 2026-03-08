package com.zero9platform.domain.user.entity;

import com.zero9platform.common.entity.BaseEntity;
import com.zero9platform.common.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LOCAL만 사용 (OAUTH는 null 가능)
    @Column(length = 100, unique = true)
    private String loginId;

    // LOCAL만 사용 (OAUTH는 null 가능)
    @Column
    private String password;

    // LOCAL은 필수, OAUTH는 선택(동의/제공 안 될 수 있음)
    @Column(length = 100, unique = true)
    private String email;

    // LOCAL은 필수, OAUTH는 선택
    @Column(length = 50)
    private String name;

    @Column(length = 20, nullable = false)
    private String role;

    // LOCAL은 필수, OAUTH는 선택(카카오는 기본 제공 안 함)
    @Column(length = 20, unique = true)
    private String phone;

    // 유니크를 강제하고 싶으면 유지(중복 시 자동 suffix 부여 필요)
    @Column(length = 50, unique = true)
    private String nickname;

    // 프로필 이미지 URL(소셜/로컬 공용)
    @Column(name = "profile_image")
    private String profileImage;

    @Column
    private LocalDateTime deletedAt;

    // 인증 제공자
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider = AuthProvider.LOCAL;

    // 소셜 제공자 사용자 고유 ID (LOCAL은 null)
    @Column(name = "provider_id", length = 255)
    private String providerId;

    /**
     * User Local 생성자
     */
    public static User createLocal(String loginId, String encodedPassword, String email, String name, String phone, String nickname, String role) {
        User user = new User();
        user.provider = AuthProvider.LOCAL;
        user.loginId = loginId;
        user.password = encodedPassword;
        user.email = email;
        user.name = name;
        user.phone = phone;
        user.nickname = nickname;
        user.role = role;
        return user;
    }

    /**
     * User kakao 생성자
     */
    public static User createOAuth(AuthProvider provider, String providerId, String nickname, String profileImage, String role) {
        User user = new User();
        user.provider = provider;
        user.providerId = providerId;
        user.nickname = nickname;
        user.profileImage = profileImage;
        user.role = role;
        return user;
    }

    /**
     * 사용자 프로필 업데이트
     */
    public void userUpdate(String email, String nickname, String phone, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.phone = phone;
        this.profileImage = profileImage;
    }

    public void updateOAuthProfile(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    /**
     * 회원 탈퇴
     */
    public void userDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}