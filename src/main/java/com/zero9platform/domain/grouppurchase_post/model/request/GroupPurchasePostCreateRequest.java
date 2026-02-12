package com.zero9platform.domain.grouppurchase_post.model.request;

import com.zero9platform.common.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupPurchasePostCreateRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    private String productName;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    private String image;

    @NotNull(message = "가격은 필수입니다.")
    @PositiveOrZero(message = "가격은 0원 이상이어야 합니다.")
    private Long price;

    @NotBlank(message = "구매 링크는 필수입니다.")
    private String linkUrl;

    @NotNull(message = "카테고리는 필수입니다.")
    private Category category;

//    @NotNull(message = "진행 상태는 필수입니다.")
//    private GppProgressStatus gppProgressStatus;

    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수입니다.")
    private LocalDate endDate;
}