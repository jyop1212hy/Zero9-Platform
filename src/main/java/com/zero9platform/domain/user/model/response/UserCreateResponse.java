package com.zero9platform.domain.user.model.response;

import com.zero9platform.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserCreateResponse {

    private final Long id;
    private final String role;
    private final String loginId;
    private final String name;
    private final String email;
    private final String phone;
    private final String nickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static UserCreateResponse from(User user) {

        return new UserCreateResponse(
                user.getId(),
                user.getRole(),
                user.getLoginId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getNickname(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}