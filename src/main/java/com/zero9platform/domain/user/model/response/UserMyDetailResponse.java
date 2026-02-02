package com.zero9platform.domain.user.model.response;

import com.zero9platform.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserMyDetailResponse extends UserDetailResponse {

    private final String phone;
    private final String email;

    private UserMyDetailResponse(User user, String phone, String email) {
        // 부모 클래스(UserDetailResponse) 호출
        super(
                user.getNickname(),
                user.getRole()
        );

        this.phone = phone;
        this.email = email;
    }

    public static UserMyDetailResponse from(User user, String phone, String email) {
        return new UserMyDetailResponse(user, phone, email);
    }
}
