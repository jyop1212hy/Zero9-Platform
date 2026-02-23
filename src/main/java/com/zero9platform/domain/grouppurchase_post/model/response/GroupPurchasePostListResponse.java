package com.zero9platform.domain.grouppurchase_post.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GroupPurchasePostListResponse {

    private Long id;
    private String productName;
    private Long userId;
    private String image;
    private Long viewCount;
    private Long price;
    private String category;
    private String gppProgressStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GroupPurchasePostListResponse from(GroupPurchasePost groupPurchasePost, String image) {

        return new GroupPurchasePostListResponse(
                groupPurchasePost.getId(),
                groupPurchasePost.getProductName(),
                groupPurchasePost.getUser().getId(),
                image,
                groupPurchasePost.getViewCount(),
                groupPurchasePost.getPrice(),
                groupPurchasePost.getCategoryDescription(),
                groupPurchasePost.getProgressStatusDescription(),
                groupPurchasePost.getStartDate(),
                groupPurchasePost.getEndDate(),
                groupPurchasePost.getCreatedAt(),
                groupPurchasePost.getUpdatedAt()
        );
    }
}