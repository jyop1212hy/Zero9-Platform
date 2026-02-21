package com.zero9platform.domain.gpp_follow.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class GppFollowGetDetailResponse {

    private final Long gppId;
    private final String nickname;
    private final String productName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime endDate;

    public static GppFollowGetDetailResponse from(GroupPurchasePost gpp) {
        return new GppFollowGetDetailResponse(
                gpp.getId(),
                gpp.getUser().getNickname(),
                gpp.getProductName(),
                gpp.getStartDate(),
                gpp.getEndDate()
        );
    }
}