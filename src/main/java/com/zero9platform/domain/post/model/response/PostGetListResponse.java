package com.zero9platform.domain.post.model.response;

import com.zero9platform.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostGetListResponse {

    private final Long id;
    private final Long userId;
    private final String type;
    private final String title;
    private final boolean isSecret;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PostGetListResponse from(Post post) {

        return new PostGetListResponse(
                post.getId(),
                post.getUser().getId(),
                post.getType(),
                post.getTitle(),
                post.isSecret(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}