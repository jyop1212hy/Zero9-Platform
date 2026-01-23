package com.zero9platform.domain.product_post.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.product.entity.Product;
import com.zero9platform.domain.product.repository.ProductRepository;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.model.request.ProductPostCreateRequest;
import com.zero9platform.domain.product_post.model.request.ProductPostUpdateRequest;
import com.zero9platform.domain.product_post.model.response.ProductPostCreateResponse;
import com.zero9platform.domain.product_post.model.response.ProductPostGetDetailResponse;
import com.zero9platform.domain.product_post.model.response.ProductPostUpdateResponse;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionCreateRequest;
import com.zero9platform.domain.product_post_option.repository.ProductPostOptionRepository;
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
    private final ProductRepository productRepository;
    private final ProductPostRepository productPostRepository;
    private final ProductPostOptionRepository productPostOptionRepository;

    /**
     * 상품 판매 게시물 생성
     */
    @Transactional
    public ProductPostCreateResponse productPostCreate(Long userId, Long productId, ProductPostCreateRequest request) {

        User user = validPermission(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_NOT_FOUND));

        // 판매일 검증
        validProductPostSaleDate(request.getStartDate(), request.getEndDate());

        // + 옵션의 가격 (=할인가, 추후 리팩토링) -> 옵션을 여러 개 생성해서 new Option() -> option을 상품 게시물에 포함 -> (선택한 옵션의 가격 * 수량 = 주문 가격 / 금액 ?) "주문 상품 생성"
        // 제목, 내용, 재고, 이미지, 카테고리, 판매 진행 상태, 진행 시작일, 진행 마감일

        ProductPost productPost = new ProductPost(user, product, request.getTitle(), request.getContent(), request.getStock(), request.getImage(), request.getCategory().name(), request.getProductPostProgressStatus().name(), request.getStartDate(), request.getEndDate());

        // 옵션 생성
        for (ProductPostOptionCreateRequest optionRequest: request.getOptionList()) {
            ProductPostOption option = new ProductPostOption(productPost, optionRequest.getName(), optionRequest.getOptionPrice(), optionRequest.getCapacity());
            productPost.addOption(option);
        }

        ProductPost savedProductPost = productPostRepository.save(productPost);

        // 응답 DTO에 상품의 정가 및 옵션가 추가

        return ProductPostCreateResponse.from(user, product, savedProductPost);
    }

    /**
     * 상품 게시물 상세 조회
     */
    @Transactional(readOnly = true)
    public ProductPostGetDetailResponse productPostGetDetail(Long productpostId) {

        ProductPost productPost = productPostRepository.findByIdAndDeletedAtIsNull(productpostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        return ProductPostGetDetailResponse.from(productPost);
    }

    /**
     * 상품 게시물 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductPostGetDetailResponse> productPostGetList(Pageable pageable) {

        Page<ProductPost> productPostsPage = productPostRepository.findAllByDeletedAtIsNull(pageable);

        Page<ProductPostGetDetailResponse> responsePage = productPostsPage.map(ProductPostGetDetailResponse::from);

        return PageResponse.from(responsePage);
    }

    /**
     * 상품 게시물 수정
     */
    @Transactional
    public ProductPostUpdateResponse productPostUpdate(Long userId, Long productpostId, ProductPostUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        ProductPost productPost = productPostRepository.findByIdAndDeletedAtIsNull(productpostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        // 본인만 수정 가능
        validProductPostOwner(user, productPost);

        // 판매일 검증
        validProductPostSaleDate(request.getStartDate(), request.getEndDate());

        productPost.update(request.getTitle(), request.getContent(), request.getStock(), request.getImage(), request.getCategory().name(), request.getProductPostProgressStatus().name(), request.getStartDate(), request.getEndDate());

        return ProductPostUpdateResponse.from(productPost);
    }

    /**
     * 상품 게시물 삭제
     */
    @Transactional
    public void productPostDelete(Long userId, Long productpostId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        ProductPost productPost = productPostRepository.findByIdAndDeletedAtIsNull(productpostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        if (!user.getId().equals(productPost.getUser().getId())) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        productPostRepository.deleteById(productpostId);
    }

    /**
     * 본인의 상품 게시물인지 검증
     */
    private static void validProductPostOwner(User user, ProductPost productPost) {
        if (!Objects.equals(user.getId(), productPost.getUser().getId())) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }
    }

    /**
     * 상품 판매 게시물 생성 권한 검증 - 사용자, 관리자 x
     */
    private User validPermission(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        if (UserRole.valueOf(user.getRole()) == UserRole.USER) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        return user;
    }

    /**
     * 판매일 검증
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
