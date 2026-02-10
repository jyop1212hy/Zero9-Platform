package com.zero9platform.domain.searchLog.model.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class RecentSearchResponse {

    private final String keyword;
    private final LocalDateTime searchedAt;

}