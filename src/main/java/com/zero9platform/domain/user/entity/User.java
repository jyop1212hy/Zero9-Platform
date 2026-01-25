package com.zero9platform.domain.user.entity;

import com.zero9platform.common.entity.BaseEntity;
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

    @Column(length = 100, nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String role;

    @Column(length = 20, nullable = false, unique = true)
    private String phone;

    @Column(length = 20, nullable = false, unique = true)
    private String nickname;

    @Column
    private String profileImage;

    @Column
    private LocalDateTime deletedAt;

    /**
     * User 생성자
     */
    public User(String loginId, String password, String email, String name, String role, String phone, String nickname) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.nickname = nickname;
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

    /**
     * 회원 탈퇴
     */
    public void userDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
