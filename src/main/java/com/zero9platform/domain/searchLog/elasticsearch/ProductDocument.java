package com.zero9platform.domain.searchLog.elasticsearch;


import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.product_post.entity.ProductPost;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "product_master_v1") // 인덱스명 분리!
@Setting(settingPath = "elasticsearch/settings.json") // 설정 파일
@Mapping(mappingPath = "elasticsearch/mappings.json") // 매핑 파일
public class ProductDocument {

    @Id
    private String id;        // "PRODUCT_1" 또는 "GPP_1"

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Keyword)
    private String postType;  // "PRODUCT" 또는 "GPP"

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;     // 실제 상품명

    @Field(type = FieldType.Text, analyzer = "nori")
    private String content;   // 선택: 내용 검색 필요할 때만

    @Field(type = FieldType.Text, analyzer = "nori")
    private String nickname;

    // 자동완성 핵심 필드 (ngram 적용)
    @Field(type = FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "standard")
    private String keyword;   // title과 동일한 값을 넣거나 별도 가공

    @Field(type = FieldType.Long)
    private Long price;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime startDate;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime endDate;

    @Builder
    public ProductDocument(String id, String postType, String title, String content, String nickname, String keyword, Long price, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.postType = postType;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.keyword = keyword;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // 1. 판매 상품 게시물(ProductPost) 변환기
    public static ProductDocument from(ProductPost entity) {
        return ProductDocument.builder()
                .id("PRODUCT_" + entity.getId())
                .postType("PRODUCT")
                .title(entity.getTitle())
                .content(entity.getContent())
                .nickname(entity.getUser().getNickname())
                .keyword(entity.getTitle())
                .price(entity.getOriginalPrice())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }

    // 2. 공동구매 홍보 게시물(GroupPurchasePost) 변환기
    public static ProductDocument from(GroupPurchasePost entity) {
        return ProductDocument.builder()
                .id("GPP_" + entity.getId())
                .postType("GPP")
                .title(entity.getProductName())
                .content(entity.getContent())
                .nickname(entity.getUser().getNickname())
                .keyword(entity.getProductName())
                .price(entity.getPrice())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }

    public Long getNumericId() {

        if (this.id == null) {
            return null;
        }

        try {
            String[] parts = this.id.split("_");

            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }


}
