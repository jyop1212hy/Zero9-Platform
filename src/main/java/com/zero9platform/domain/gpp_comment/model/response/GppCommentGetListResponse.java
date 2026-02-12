package com.zero9platform.domain.gpp_comment.model.response;

import com.zero9platform.domain.gpp_comment.entity.GppComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GppCommentGetListResponse {

    private final Long id;
    private final Long userId;
    private final String nickName;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static GppCommentGetListResponse from(GppComment gppComment) {
        return new GppCommentGetListResponse(
                gppComment.getId(),
                gppComment.getUser().getId(),
                gppComment.getUser().getNickname(),
                gppComment.getContent(),
                gppComment.getCreatedAt(),
                gppComment.getUpdatedAt()
        );
    }
}