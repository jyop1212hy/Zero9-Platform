package com.zero9platform.domain.activity_feed.event;

import com.zero9platform.common.enums.FeedType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedCreateEvent {

    private final FeedType type;
    private final Long targetId;
    private final String title;
    private final Long userId;
}