package com.zero9platform.domain.product_post.model.request;

import com.zero9platform.common.enums.Category;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProductPostCreateRequest {

    private String title;
    private String name;
    private String content;
    private Long originalPrice;
    private List<ProductPostOptionCreateRequest> optionList;
    private String image;
    private Category category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
