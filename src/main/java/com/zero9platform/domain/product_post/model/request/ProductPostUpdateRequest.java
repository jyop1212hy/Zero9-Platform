package com.zero9platform.domain.product_post.model.request;

import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.ProductPostProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductPostUpdateRequest {

    private String title;
    private String content;
    private Integer stock;
    private String image;
    private Category category;
    private ProductPostProgressStatus productPostProgressStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
