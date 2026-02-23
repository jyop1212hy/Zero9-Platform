package com.zero9platform.domain.comment.model.response;

import com.zero9platform.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentGetListResponse {

    private final Long id;
    private final Long userId;
    private final String nickName;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static CommentGetListResponse from(Comment comment) {
        return new CommentGetListResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
    public static CommentGetListResponse maskContent(Comment comment) {
        return new CommentGetListResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                "비밀글의 답변입니다.",
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}