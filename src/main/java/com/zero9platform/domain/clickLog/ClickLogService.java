package com.zero9platform.domain.clickLog;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.clickLog.entity.ClickLog;
import com.zero9platform.domain.clickLog.model.ClickLogProductPostDetailResponse;
import com.zero9platform.domain.clickLog.response.ClickLogRepository;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.searchLog.repository.SearchContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClickLogService {

    private final ClickLogRepository clickLogRepository;
    private final ProductPostRepository productPostRepository;
    private final SearchContextRepository searchContextRepository;

    @Transactional
    public ClickLogProductPostDetailResponse productPostDetail(Long productPostId, String keyword) {

        // 상품 조회
        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        // 클릭 로그 저장
        if (keyword != null && !keyword.isBlank()) {
            clickLogRepository.save(new ClickLog(productPost, keyword));
            return ClickLogProductPostDetailResponse.from(productPost);
        }

        // 검색 컨텍스트 조회
        searchContextRepository.findTopByProductPostIdOrderByCreatedAtDesc(productPostId)
                .ifPresent(ctx -> clickLogRepository.save(
                        new ClickLog(productPost, ctx.getKeyword()))
        );

        return ClickLogProductPostDetailResponse.from(productPost);
    }
}
