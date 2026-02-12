package com.zero9platform.domain.gpp_comment.model.response;

import com.zero9platform.domain.gpp_comment.entity.GppComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GppCommentCreateResponse {

    private final Long id;
    private final Long gppId;
    private final Long userId;
    private final String content;
    private final String nickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static GppCommentCreateResponse from(GppComment gppComment) {
        return new GppCommentCreateResponse(
                gppComment.getId(),
                gppComment.getGroupPurchasePost().getId(),
                gppComment.getUser().getId(),
                gppComment.getContent(),
                gppComment.getUser().getNickname(),
                gppComment.getCreatedAt(),
                gppComment.getUpdatedAt()
        );
    }
}