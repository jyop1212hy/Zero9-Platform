//package com.zero9platform.domain.searchLog.elasticsearch;
//
//import com.zero9platform.domain.searchLog.entity.SearchLog;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.*;
//
//import java.time.LocalDateTime;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Document(indexName = "zero9_search_logs_v1")
//@Setting(settingPath = "elasticsearch/settings.json") // 설정 파일
//@Mapping(mappingPath = "elasticsearch/mappings.json") // 매핑 파일
//public class SearchDocument {
//
//    @Id
//    private String id;        // PRODUCT_1 / GPP_5
//
//    @Field(type = FieldType.Long)
//    private Long userId;
//
//    @Field(type = FieldType.Keyword)
//    private String postType;  // PRODUCT / GPP
//
//    @Field(type = FieldType.Text, analyzer = "nori")
//    private String title;     // ProductPost.title 또는 GPP.productName
//
//    @Field(type = FieldType.Text, analyzer = "nori")
//    private String content;   // 선택: 내용 검색 필요할 때만
//
//    @Field(type = FieldType.Text, analyzer = "nori")
//    private String nickname;  // influencer 닉네임 (Keyword 말고 Text 추천)
//
//    @Field(type = FieldType.Long)
//    private Long price;
//
//    @Field(type = FieldType.Text, index = false) // 검색은 안 함, 응답용
//    private String image;
//
//    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
//    private LocalDateTime startDate;
//
//    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
//    private LocalDateTime endDate;
//
//    // N-gram 분석기를 적용해서 자동완성 기능 필드
//    @Field(type = FieldType.Text, analyzer = "ngram_analyzer", searchAnalyzer = "standard")
//    private String keyword;
//
//    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
//    private String createdAt;
//
//    @Builder
//    public SearchDocument(String id, Long userId, String postType, String title, String content, String nickname, Long price, String image, LocalDateTime startDate, LocalDateTime endDate, String keyword, String createdAt) {
//        this.id = id;
//        this.userId = userId;
//        this.postType = postType;
//        this.title = title;
//        this.content = content;
//        this.nickname = nickname;
//        this.price = price;
//        this.image = image;
//        this.startDate = startDate;
//        this.endDate = endDate;
//        this.keyword = keyword;
//        this.createdAt = createdAt;
//    }
//
//    @Builder
//    public SearchDocument(String id, Long userId, String keyword, String createdAt) {
//        this.id = id;
//        this.userId = userId;
//        this.keyword = keyword;
//        this.createdAt = createdAt;
//    }
//
//    // Entity를 Document로 변환하는 정적 팩토리 메서드 (이관 시 편리함)
//    // Entity(DB)데이터를 Document(ES) 지도 데이터로 변환
//    public static SearchDocument from(SearchLog entity) {
//        return SearchDocument.builder()
//                .id("LOG_" + entity.getId().toString())
//                .userId(entity.getUserId())
//                .keyword(entity.getKeyword()) // 사용자가 검색한 단어를 자동완성용으로 저장
//                .createdAt(entity.getCreatedAt().toString())
//                .build();
//    }
//
//    public Long getNumericId() {
//
//        if (this.id == null) {
//            return null;
//        }
//
//        try {
//            String[] parts = this.id.split("_");
//
//            return Long.parseLong(parts[parts.length - 1]);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}