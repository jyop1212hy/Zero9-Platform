package com.zero9platform.domain.grouppurchase_post.model.response;

import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.GppProgressStatus;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GroupPurchasePostDetailResponse {

    private Long id;
    private String productName;
    private Long userId;
    private String content;
    private String image;
    private Long viewCount;
    private Long price;
    private String linkUrl;
    private String category;
    private String gppProgressStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GroupPurchasePostDetailResponse from(GroupPurchasePost groupPurchasePost) {

        Category category = Category.valueOf(groupPurchasePost.getCategory());
        GppProgressStatus gppProgressStatus = GppProgressStatus.valueOf(groupPurchasePost.getGppProgressStatus());

        return new GroupPurchasePostDetailResponse(
                groupPurchasePost.getId(),
                groupPurchasePost.getProductName(),
                groupPurchasePost.getUser().getId(),
                groupPurchasePost.getContent(),
                groupPurchasePost.getImage(),
                groupPurchasePost.getViewCount(),
                groupPurchasePost.getPrice(),
                groupPurchasePost.getLinkUrl(),
                category.getDescription(),
                gppProgressStatus.getDescription(),
                groupPurchasePost.getStartDate(),
                groupPurchasePost.getEndDate(),
                groupPurchasePost.getCreatedAt(),
                groupPurchasePost.getUpdatedAt()
        );
    }

}
