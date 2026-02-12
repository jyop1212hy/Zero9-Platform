package com.zero9platform.domain.user.model.response;

import com.zero9platform.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserInfluencerCreateResponse extends UserCreateResponse {

    private final String influencerSocialLink;

    private UserInfluencerCreateResponse(User user, String influencerSocialLink) {

        // 부모 클래스(UserCreateResponse) 생성자 호출
        super(
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

        this.influencerSocialLink = influencerSocialLink;
    }

    public static UserInfluencerCreateResponse from(User user, String influencer) {

        return new UserInfluencerCreateResponse(user, influencer);
    }
}