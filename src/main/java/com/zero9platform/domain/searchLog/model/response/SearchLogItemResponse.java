package com.zero9platform.domain.searchLog.model.response;

import com.zero9platform.domain.searchLog.elasticsearch.SearchDocument;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class SearchLogItemResponse {

    private final Long postId;
    private final String postType;
    private final String matchType;
    private final Long userId;
    private final String nickname;
    private final String image;
    private final String title;
    private final Long price;
    private final Long favoriteCount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public static SearchLogItemResponse from(SearchDocument doc, String matchType, Long favoriteCount) {

        Long originalId = doc.getNumericId();

        return new SearchLogItemResponse(
                originalId,
                doc.getPostType(),
                matchType,
                doc.getUserId(),
                doc.getNickname(),
                doc.getImage(),
                doc.getTitle(),
                doc.getPrice(),
                favoriteCount,
                doc.getStartDate(),
                doc.getEndDate()
        );
    }
}