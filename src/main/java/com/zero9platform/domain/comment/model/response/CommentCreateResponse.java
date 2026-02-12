package com.zero9platform.domain.comment.model.response;

import com.zero9platform.domain.comment.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentCreateResponse {

    private final Long id;
    private final Long postId;
    private final Long userId;
    private final String content;
    private final String nickname;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static CommentCreateResponse from(Comment comment) {
        return new CommentCreateResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getUser().getId(),
                comment.getContent(),
                comment.getUser().getNickname(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}