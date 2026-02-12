package com.zero9platform.domain.activity_feed.model.response;

import com.zero9platform.common.enums.FeedType;
import com.zero9platform.domain.activity_feed.entity.ActivityFeed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ActivityFeedResponse {

    private Long id;
    private String type;
    private String message;
    private Long targetId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ActivityFeedResponse from(ActivityFeed feed, long count) {

        FeedType feedType = FeedType.valueOf(feed.getType());
        String dynamicMessage = feedType.toMessage(feed.getTargetName(), count);

        return new ActivityFeedResponse(
                feed.getId(),
                feed.getType(),
                dynamicMessage,
                feed.getTargetId(),
                feed.getCreatedAt(),
                feed.getUpdatedAt()
        );
    }
}