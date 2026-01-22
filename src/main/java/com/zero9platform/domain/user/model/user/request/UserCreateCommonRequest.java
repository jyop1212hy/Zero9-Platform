package com.zero9platform.domain.user.model.user.request;

import com.zero9platform.common.enums.UserRole;

public interface UserCreateCommonRequest {

    String getLoginId();
    String getPassword();
    String getName();
    String getEmail();
    String getPhone();
    String getNickname();
    UserRole getRole();
}
