package com.zero9platform.domain.searchLog.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SearchLogListResponse {

    private final Long rank;
    private final String keyword;
    private final Long count;

    public static SearchLogListResponse from(Long rank, String keyword , Long count) {
        return new SearchLogListResponse(rank, keyword, count);

    }
}
