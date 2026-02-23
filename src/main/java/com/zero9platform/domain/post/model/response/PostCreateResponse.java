package com.zero9platform.domain.post.model.response;

import com.zero9platform.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostCreateResponse {

    private final Long id;
    private final Long userId;
    private final String type;
    private final String title;
    private final String content;
    private final boolean isSecret;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PostCreateResponse from(Post post) {

        return new PostCreateResponse(
                post.getId(),
                post.getUser().getId(),
                post.getType(),
                post.getTitle(),
                post.getContent(),
                post.isSecret(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}