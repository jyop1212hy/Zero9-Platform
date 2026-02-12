package com.zero9platform.domain.product_post_option.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.ProgressStatus;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionCreateRequest;
import com.zero9platform.domain.product_post_option.model.response.*;
import com.zero9platform.domain.product_post_option.repository.ProductPostOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductPostOptionService {

    private final ProductPostOptionRepository optionRepository;
    private final ProductPostRepository productPostRepository;

    /**
     * 옵션 추가 생성
     */
    @Transactional
    public ProductPostOptionCreateResponse optionCreate(Long userId, UserRole userRole, Long productPostId, ProductPostOptionCreateRequest request) {

        // 상품 게시물 존재여부 검증
        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        // 상품 게시물 "READY" 상태 검증
        validateOptionChangeAllowed(productPost);

        // 관리자이거나 인플루언서 본인인지 확인
        validInfluencerOwnerOrAdmin(productPost, userId, userRole);

        ProductPostOption option = new ProductPostOption(productPost, request.getName(), request.getSalePrice(), request.getStockQuantity());

        ProductPostOption savedOption = optionRepository.save(option);

        return ProductPostOptionCreateResponse.from(savedOption);
    }

    /**
     * 옵션 상세 조회
     */
    @Transactional(readOnly = true)
    public ProductPostOptionGetDetailResponse optionGetDetail(Long optionId) {

        ProductPostOption option = optionRepository.findById(optionId)
                .orElseThrow(() -> new CustomException(ExceptionCode.OPTION_NOT_FOUND));

        return ProductPostOptionGetDetailResponse.from(option);
    }

    /**
     * 옵션 삭제
     */
    @Transactional
    public void optionDelete(Long userId, UserRole userRole, Long productPostId, Long optionId) {

        // 상품 게시물 존재여부 검증
        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        // 관리자이거나 인플루언서 본인인지 확인
        validInfluencerOwnerOrAdmin(productPost, userId, userRole);

        // 해당하는 상품게시물에 속한 옵션인지 확인
        ProductPostOption option = optionRepository.findByIdAndProductPost_IdAndStockStatus(optionId, productPostId, "IN_STOCK")
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_OPTION_NOT_FOUND));

        // 상품 게시물 "READY" 상태 검증
        validateOptionChangeAllowed(productPost);

        // 옵션의 개수가 1일 때, 삭제 불가 예외 처리
        if (productPost.getProductPostOptionList().size() == 1) {
            throw new CustomException(ExceptionCode.OPTION_CANNOT_DELETE_LAST);
        }

        // 옵션 삭제
        productPost.getProductPostOptionList().remove(option);

        // 양방향
        option.setProductPost(null);
    }

    /**
     * 인플루언서 본인여부 검증 및 관리자인지 검증
     */
    private void validInfluencerOwnerOrAdmin(ProductPost productPost, Long userId, UserRole userRole) {

        Long ownerId = productPost.getUser().getId();

        if (userRole == UserRole.ADMIN) {
            return;
        }

        if (userRole != UserRole.INFLUENCER) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }

        if (!ownerId.equals(userId)) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }
    }

    /**
     * 옵션 추가 생성 & 삭제: 상품판메 게시물 "READY" 상태일 때만 가능
     */
    private static void validateOptionChangeAllowed(ProductPost productPost) {

        if (!productPost.getProgressStatus().equals(ProgressStatus.READY.name())) {
            throw new CustomException(ExceptionCode.OPTION_CHANGE_NOT_ALLOWED_AFTER_SALE_START);
        }
    }
}