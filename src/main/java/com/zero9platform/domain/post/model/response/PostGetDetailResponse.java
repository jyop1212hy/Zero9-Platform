package com.zero9platform.domain.post.model.response;

import com.zero9platform.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostGetDetailResponse {

    private final Long id;
    private final Long userId;
    private final String title;
    private final String content;
    private final String image;
    private final Long viewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PostGetDetailResponse from(Post post) {

        return new PostGetDetailResponse(
                post.getId(),
                post.getUser().getId(),
                post.getTitle(),
                post.getContent(),
                post.getImage(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}