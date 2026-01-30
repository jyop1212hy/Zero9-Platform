package com.zero9platform.domain.product_post.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.activity_feed.service.ActivityFeedService;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.model.request.ProductPostCreateRequest;
import com.zero9platform.domain.product_post.model.request.ProductPostUpdateRequest;
import com.zero9platform.domain.product_post.model.response.ProductPostCreateResponse;
import com.zero9platform.domain.product_post.model.response.ProductPostGetDetailResponse;
import com.zero9platform.domain.product_post.model.response.ProductPostGetListResponse;
import com.zero9platform.domain.product_post.model.response.ProductPostUpdateResponse;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionCreateRequest;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductPostService {

    private final UserRepository userRepository;
    private final ProductPostRepository productPostRepository;
    private final ActivityFeedService activityFeedService;

    /**
     * 상품 판매 게시물 생성 - Influencer, Admin
     */
    @Transactional
    public ProductPostCreateResponse productPostCreate(Long userId, ProductPostCreateRequest request) {

        // 인가 확인 (사용자 제외)
        User user = validPermission(userId);

        // 판매일 검증
        validProductPostSaleDate(request.getStartDate(), request.getEndDate());

        // 상품판매 게시물 생성
        ProductPost productPost = new ProductPost(user, request.getTitle(), request.getName(), request.getContent(), request.getOriginalPrice(), request.getImage(), request.getCategory().name(), request.getStartDate(), request.getEndDate());

        // 옵션 생성
        for (ProductPostOptionCreateRequest optionRequest: request.getOptionList()) {
            ProductPostOption option = new ProductPostOption(productPost, optionRequest.getName(), optionRequest.getSalePrice(), optionRequest.getStockQuantity());
            productPost.addOption(option);
        }

        ProductPost savedProductPost = productPostRepository.save(productPost);

        // 피드 생성 호출
        activityFeedService.feedCreate("SOON", savedProductPost.getId(), savedProductPost.getTitle());

        return ProductPostCreateResponse.from(savedProductPost);
    }

    /**
     * 상품 게시물 상세 조회
     */
    @Transactional
    public ProductPostGetDetailResponse productPostGetDetail(Long productPostId) {

        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        // 상품 게시물의 하위 옵션 리스트가 전부 "비활성화" 상태 체크 (조회는 가능 -> 추후 주문 상품 생성 불가)
        productPost.allOptionIsEmpty();

        return ProductPostGetDetailResponse.from(productPost);
    }

    /**
     * 상품 게시물 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductPostGetListResponse> productPostGetList(Pageable pageable) {

        Page<ProductPost> productPostsPage = productPostRepository.findAllByOrderByUpdatedAtDesc(pageable);

        Page<ProductPostGetListResponse> responsePage = productPostsPage.map(ProductPostGetListResponse::from);

        return PageResponse.from(responsePage);
    }

    /**
     * 상품 게시물 수정
     */
    @Transactional
    public ProductPostUpdateResponse productPostUpdate(Long userId, Long productPostId, ProductPostUpdateRequest request) {

        // 인가 확인 (사용자 제외)
        User user = validPermission(userId);

        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        // 본인만 수정 가능
        validProductPostOwner(user, productPost);

        String category = request.getCategory() != null ? request.getCategory().name() : null;

        productPost.update(category, request.getTitle(), request.getName(), request.getContent(), request.getOriginalPrice(), request.getImage(), request.getStartDate(), request.getEndDate());

        return ProductPostUpdateResponse.from(productPost);
    }

//    /**
//     * 상품 게시물 삭제
//     */
//    @Transactional
//    public void productPostDelete(Long userId, Long productpostId) {
//
//        User user = validPermission(userId);
//
//        ProductPost productPost = productPostRepository.findByIdAndDeletedAtIsNull(productpostId)
//                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));
//
//        // 본인만 삭제 가능
//        validProductPostOwner(user, productPost);
//
//        // 상품 게시물 소프트 딜리트
//        productPost.softDelete();
//
//        // 하위 옵션 리스트들 비활성화
//        productPost.getProductPostOptionList()
//                .forEach(ProductPostOption::optionInactive);
//    }

    /**
     * 본인의 상품 게시물인지 검증
     */
    private static void validProductPostOwner(User user, ProductPost productPost) {
        if (!Objects.equals(user.getId(), productPost.getUser().getId())) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }
    }

    /**
     * 상품 판매 게시물 생성 권한 검증 - 사용자 x
     */
    private User validPermission(Long userId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        if (UserRole.valueOf(user.getRole()) == UserRole.USER) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        return user;
    }

    /**
     * 판매 기간 검증
     */
    private static void validProductPostSaleDate(LocalDateTime startDate, LocalDateTime endDate) {

        // 판매 시작일
        if (startDate.isBefore(LocalDateTime.now())) {
            throw new CustomException(ExceptionCode.PP_INVALID_DATE_RANGE);
        }

        // 판매 종료일
        if (endDate.isBefore(startDate)) {
            throw new CustomException(ExceptionCode.PP_INVALID_DATE_RANGE);
        }
    }
}
