package com.zero9platform.domain.webSocket;

import com.zero9platform.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OnlineCheckResponse {

    private  Long userId;
    private  String userName;
    private  Boolean onlineStatus;

    public static OnlineCheckResponse from(User user, Boolean onlineStatus) {
        return new OnlineCheckResponse(
                user.getId(),
                user.getName(),
                onlineStatus
        );
    }
}
