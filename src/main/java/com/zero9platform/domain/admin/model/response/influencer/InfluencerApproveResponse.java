package com.zero9platform.domain.admin.model.response.influencer;

import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.entity.Influencer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InfluencerApproveResponse {

    private final Long id;
    private final String LoginId;
    private final String email;
    private final String name;
    private final String nickname;
    private final Boolean status;
    private final LocalDateTime approveAt;

    public static InfluencerApproveResponse from(Influencer influencer){
        User user = influencer.getUser();

        return new InfluencerApproveResponse(
                user.getId(),
                user.getLoginId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                influencer.getInfluencerApprovalStatus(),
                influencer.getApprovalAt()
        );
    }
}
