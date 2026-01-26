package com.zero9platform.domain.product_post.model.request;

import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.ProductPostProgressStatus;
import com.zero9platform.common.enums.ProductPostStatus;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ProductPostCreateRequest {

    private String title;
    private String content;
    private Integer stock;
    private List<ProductPostOptionCreateRequest> optionList;
    private String image;
    private Category category;
    private ProductPostProgressStatus productPostProgressStatus;
    private ProductPostStatus productPostStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
