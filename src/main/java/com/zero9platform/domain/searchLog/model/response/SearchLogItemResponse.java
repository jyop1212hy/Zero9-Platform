package com.zero9platform.domain.searchLog.model.response;

import com.zero9platform.domain.searchLog.elasticsearch.ProductDocument;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchLogItemResponse {

    private Long postId;              // 게시글 원본 ID
    private String postType;          // PRODUCT / GPP
    private String matchType;         // 제목 매칭 / 내용 매칭 등
    private String searchedKeyword;   // 사용자가 실제 검색한 단어
    private Long userId;              // 판매자 ID
    private String nickname;          // 판매자 닉네임
    private String title;             // 상품명
    private Long price;               // 가격
    private Long favoriteCount;       // 찜 수
    private LocalDateTime startDate;  // 게시 시작일
    private LocalDateTime endDate;    // 게시 종료일

    /**
     * static 팩토리 메서드
     */
    public static SearchLogItemResponse from(ProductDocument doc, String matchType, Long favoriteCount, String cleanKeyword) {
        return SearchLogItemResponse.builder()
                .postId(doc.getNumericId())
                .postType(doc.getPostType())
                .matchType(matchType)
                .searchedKeyword(cleanKeyword)
                .userId(doc.getUserId())
                .nickname(doc.getNickname())
                .title(doc.getTitle())
                .price(doc.getPrice())
                .favoriteCount(favoriteCount)
                .startDate(doc.getStartDate())
                .endDate(doc.getEndDate())
                .build();
    }
}