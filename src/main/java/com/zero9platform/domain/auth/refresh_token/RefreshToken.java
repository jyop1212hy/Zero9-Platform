package com.zero9platform.domain.auth.refresh_token;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime expireAt;

    @Column(nullable = false)
    private boolean used = false;

    public RefreshToken(String refreshToken, Long userId, LocalDateTime expireAt) {
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.expireAt = expireAt;
    }

    /**
     * used 상태 변경
     */
    public void updateUsed() {
        this.used = true;
    }
}