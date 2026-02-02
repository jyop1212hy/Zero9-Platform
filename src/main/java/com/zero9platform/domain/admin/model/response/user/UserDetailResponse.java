package com.zero9platform.domain.admin.model.response.user;

import com.zero9platform.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class UserDetailResponse {

    private final Long id;
    private final String loginId;
    private final String name;
    private final String email;
    private final String phone;
    private final String nickname;
    private final String role;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Boolean deleted;

    public static UserDetailResponse from(User user) {
        return new UserDetailResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getNickname(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt() != null
        );
    }
}
