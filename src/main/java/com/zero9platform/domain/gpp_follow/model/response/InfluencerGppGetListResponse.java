package com.zero9platform.domain.gpp_follow.model.response;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class InfluencerGppGetListResponse {

    private final Long gppId;
    private final String nickname;
    private final String productName;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Long price;
    private final String linkUrl;
    private final Long commentCount;

    public static InfluencerGppGetListResponse from(GroupPurchasePost gpp, Long commentCount) {
        return new InfluencerGppGetListResponse(
                gpp.getId(),
                gpp.getUser().getNickname(),
                gpp.getProductName(),
                gpp.getStartDate(),
                gpp.getEndDate(),
                gpp.getPrice(),
                gpp.getLinkUrl(),
                commentCount
        );
    }
}
