package com.zero9platform.domain.searchLog.model.event;

import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.product_post.entity.ProductPost;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchEvent {

    private final String id;
    private final String postType;
    private final String title;
    private final String content;
    private final String nickname;
    private final Long price;
    private final String image;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Long userId;
    private final boolean deleted;

    // ProductPost -> Event 변환
    public static SearchEvent from(ProductPost post, boolean deleted){
        return new SearchEvent(
                "PRODUCT_POST_"+ post.getId().toString(),
                "PRODUCT_POST",
                post.getTitle(),
                post.getContent(),
                post.getUser().getNickname(),
                post.getOriginalPrice(),
                post.getImage(),
                post.getStartDate(),
                post.getEndDate(),
                post.getUser().getId(),
                deleted

        );
    }

    // GroupPurchasePost -> Event 변환
    public static SearchEvent from(GroupPurchasePost gpp, boolean isDelete) {
        return new SearchEvent(
                "GROUP_PURCHASE_POST_" + gpp.getId().toString(),
                "GROUP_PURCHASE_POST",
                gpp.getProductName(),
                gpp.getContent(),
                gpp.getUser().getNickname(),
                gpp.getPrice(),
                gpp.getImage(),
                gpp.getStartDate(),
                gpp.getEndDate(),
                gpp.getUser().getId(),
                isDelete
        );
    }
}
