package com.zero9platform.domain.searchLog.elasticsearch;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Document(indexName = "zero9_search_logs_v1")
public class SearchDocument {

    @Id
    private String id;        // PRODUCT_1 / GPP_5

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Keyword)
    private String postType;  // PRODUCT / GPP

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;     // ProductPost.title 또는 GPP.productName

    @Field(type = FieldType.Text, analyzer = "nori")
    private String content;   // 선택: 내용 검색 필요할 때만

    @Field(type = FieldType.Text, analyzer = "nori")
    private String nickname;  // influencer 닉네임 (Keyword 말고 Text 추천)

    @Field(type = FieldType.Long)
    private Long price;

    @Field(type = FieldType.Text, index = false) // 검색은 안 함, 응답용
    private String image;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime startDate;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime endDate;

    @Builder
    public SearchDocument(String id, Long userId, String postType, String title, String content,
                          String nickname, Long price, String image,
                          LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.userId = userId;
        this.postType = postType;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.price = price;
        this.image = image;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getNumericId() {
        if (this.id == null) return null;
        try {
            String[] parts = this.id.split("_");
            return Long.parseLong(parts[parts.length - 1]);
        } catch (Exception e) {
            return null;
        }
    }
}