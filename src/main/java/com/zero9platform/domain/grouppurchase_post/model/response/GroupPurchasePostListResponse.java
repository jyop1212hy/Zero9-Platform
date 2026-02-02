package com.zero9platform.domain.grouppurchase_post.model.response;

import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.GppProgressStatus;
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
//    private Long favoriteCount; // 추후 좋아요 기능 연동
    private Long price;
    private String category;
    private String gppProgressStatus;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GroupPurchasePostListResponse from(GroupPurchasePost groupPurchasePost) {

//        Category category = Category.valueOf(groupPurchasePost.getCategory());
//        GppProgressStatus gppProgressStatus = GppProgressStatus.valueOf(groupPurchasePost.getGppProgressStatus());

        return new GroupPurchasePostListResponse(
                groupPurchasePost.getId(),
                groupPurchasePost.getProductName(),
                groupPurchasePost.getUser().getId(),
                groupPurchasePost.getImage(),
                groupPurchasePost.getViewCount(),
//                0L, // favoriteCount 임시값 (추후 연관관계 추가 시 수정)
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
