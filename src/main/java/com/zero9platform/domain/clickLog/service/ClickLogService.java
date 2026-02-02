package com.zero9platform.domain.clickLog.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.clickLog.entity.ClickLog;
import com.zero9platform.domain.clickLog.model.response.ClickLogProductPostDetailResponse;
import com.zero9platform.domain.clickLog.repository.ClickLogRepository;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.searchLog.repository.SearchContextRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClickLogService {

    private final ClickLogRepository clickLogRepository;
    private final ProductPostRepository productPostRepository;
    private final SearchContextRepository searchContextRepository;
    private final UserRepository userRepository;

    @Transactional
    public ClickLogProductPostDetailResponse productDetailClick(Long productPostId, AuthUser authUser) {

        //상품 정보 가져오기
        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        //토큰이 null 이면 저장하지 않고 값을 반환한다.
        if (authUser == null) {
            return ClickLogProductPostDetailResponse.from(productPost);
        }

        // 로그인 유저 조회
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 만약 null 이 아니면 저장한다.
        if (authUser.getAuthorities() != null || authUser.getAuthorities().isEmpty()) {
            searchContextRepository
                    .findLatestContext(
                            user.getId(),
                            productPostId,
                            PageRequest.of(0, 1)
                    )
                    .stream()
                    .findFirst()
                    .ifPresent(searchContext ->
                            clickLogRepository.save(
                                    new ClickLog(
                                            user.getId(),
                                            productPost.getId(),
                                            searchContext.getKeyword()
                                    )
                            )
                    );
        }
        return ClickLogProductPostDetailResponse.from(productPost);
    }
}