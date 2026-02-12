package com.zero9platform.domain.user.model.response;

import com.zero9platform.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserUpdateResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String phone;
    private final String profileImage;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static UserUpdateResponse from(User user, String profileImageUrl) {

        return new UserUpdateResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getPhone(),
                profileImageUrl,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}