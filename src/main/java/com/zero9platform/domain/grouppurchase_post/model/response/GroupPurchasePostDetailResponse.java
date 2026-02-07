package com.zero9platform.domain.grouppurchase_post.model.response;

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

    public static GroupPurchasePostDetailResponse from(GroupPurchasePost groupPurchasePost, String image) {

        return new GroupPurchasePostDetailResponse(
                groupPurchasePost.getId(),
                groupPurchasePost.getProductName(),
                groupPurchasePost.getUser().getId(),
                groupPurchasePost.getContent(),
                image,
                groupPurchasePost.getViewCount(),
                groupPurchasePost.getPrice(),
                groupPurchasePost.getLinkUrl(),
                groupPurchasePost.getCategoryDescription(),
                groupPurchasePost.getProgressStatusDescription(),
                groupPurchasePost.getStartDate(),
                groupPurchasePost.getEndDate(),
                groupPurchasePost.getCreatedAt(),
                groupPurchasePost.getUpdatedAt()
        );
    }

}
