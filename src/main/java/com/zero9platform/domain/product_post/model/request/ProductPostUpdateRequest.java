package com.zero9platform.domain.product_post.model.request;

import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.ProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductPostUpdateRequest {

    private Category category;
    private String title;
    private String name;
    private String content;
    private Long originalPrice;
    private String image;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
