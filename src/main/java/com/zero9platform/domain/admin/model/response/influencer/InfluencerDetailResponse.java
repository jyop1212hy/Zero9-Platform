package com.zero9platform.domain.admin.model.response.influencer;

import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.entity.Influencer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InfluencerDetailResponse {

    private final Long id;
    private final String loginId;
    private final String email;
    private final String name;
    private final String nickName;
    private final String phone;
    private final Boolean status;
    private final LocalDateTime approvalAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static InfluencerDetailResponse from(Influencer influencer) {

        User user = influencer.getUser();

        return new InfluencerDetailResponse(
                user.getId(),
                user.getLoginId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getPhone(),
                influencer.getInfluencerApprovalStatus(),
                influencer.getApprovalAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}