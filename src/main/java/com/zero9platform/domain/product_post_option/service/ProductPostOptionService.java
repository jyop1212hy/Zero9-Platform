package com.zero9platform.domain.product_post_option.service;


import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionCreateRequest;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionUpdateRequest;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionCreateResponse;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionGetDetailResponse;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionGetListResponse;
import com.zero9platform.domain.product_post_option.model.response.ProductPostOptionUpdateResponse;
import com.zero9platform.domain.product_post_option.repository.ProductPostOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        ProductPost productPost = productPostRepository.findByIdAndDeletedAtIsNull(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        // 관리자이거나 인플루언서 본인인지 확인
        validInfluencerOwnerOrAdmin(productPost, userId, userRole);

        ProductPostOption option = new ProductPostOption(productPost, request.getName(), request.getOptionPrice(), request.getCapacity());

        ProductPostOption saved = optionRepository.save(option);

        return ProductPostOptionCreateResponse.from(saved);
    }

    /**
     * 옵션 상세 조회
     */
    @Transactional(readOnly = true)
    public ProductPostOptionGetDetailResponse optionGetDetail(Long productPostId, Long optionId) {

        // 상품 게시물 존재여부 검증
        productPostRepository.findByIdAndDeletedAtIsNull(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        // 해당하는 상품게시물에 속한 옵션인지 확인
        ProductPostOption option = optionRepository.findByIdAndProductPost_Id(optionId, productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_OPTION_NOT_FOUND));

        return ProductPostOptionGetDetailResponse.from(option);
    }

    /**
     *  상품 게시물별 옵션 전체 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductPostOptionGetListResponse> optionGetPage(Long productPostId, Pageable pageable) {

        // 상품 게시물 존재여부 검증
        productPostRepository.findByIdAndDeletedAtIsNull(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        Page<ProductPostOptionGetListResponse> page = optionRepository.findAllByProductPost_Id(productPostId, pageable)
                .map(ProductPostOptionGetListResponse::from);

        return PageResponse.from(page);
    }

    /**
     * 옵션 수정
     */
    @Transactional
    public ProductPostOptionUpdateResponse optionUpdate(Long userId, UserRole userRole, Long productPostId, Long optionId, ProductPostOptionUpdateRequest request) {

        // 상품 게시물 존재여부 검증
        ProductPost productPost = productPostRepository.findByIdAndDeletedAtIsNull(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        // 관리자이거나 인플루언서 본인인지 확인
        validInfluencerOwnerOrAdmin(productPost, userId, userRole);

        // 해당하는 상품게시물에 속한 옵션인지 확인
        ProductPostOption option = optionRepository.findByIdAndProductPost_Id(optionId, productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_OPTION_NOT_FOUND));

        option.update(request.getName(), request.getOptionPrice(), request.getCapacity());

        return ProductPostOptionUpdateResponse.from(option);
    }

    /**
     * 옵션 삭제
     */
    @Transactional
    public void optionDelete(Long userId, UserRole userRole, Long productPostId, Long optionId) {

        // 상품 게시물 존재여부 검증
        ProductPost productPost = productPostRepository.findByIdAndDeletedAtIsNull(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        // 관리자이거나 인플루언서 본인인지 확인
        validInfluencerOwnerOrAdmin(productPost, userId, userRole);

        // 해당하는 상품게시물에 속한 옵션인지 확인
        ProductPostOption option = optionRepository.findByIdAndProductPost_Id(optionId, productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_OPTION_NOT_FOUND));

        optionRepository.delete(option);
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
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }

        if (!ownerId.equals(userId)) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }
    }
}