package com.zero9platform.domain.user.model.response;

import com.zero9platform.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDetailResponse {

    private final String nickname;
    private final String role;

    public static UserDetailResponse from(User user) {
        return new UserDetailResponse(
                user.getNickname(),
                user.getRole()
        );
    }
}
