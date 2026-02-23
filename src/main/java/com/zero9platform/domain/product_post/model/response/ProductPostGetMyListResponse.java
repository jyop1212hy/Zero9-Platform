package com.zero9platform.domain.product_post.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ProductPostGetMyListResponse {

    private final Long id;
    private final String title;
    private final Long originalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime startDatetime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private final LocalDateTime endDatetime;

    private final Long favoriteCount;
}
