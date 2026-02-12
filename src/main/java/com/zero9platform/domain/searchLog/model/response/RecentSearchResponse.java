package com.zero9platform.domain.searchLog.model.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@JsonPropertyOrder({ "keyword", "createdAt" })
public class RecentSearchResponse {

    private final String keyword;
    private final LocalDateTime searchedAt;
}