package com.zero9platform.domain.product_post.service;

import com.amazonaws.services.s3.AmazonS3;
import com.zero9platform.common.aws.s3.S3Service;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.ProgressStatus;
import com.zero9platform.common.enums.StockStatus;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.model.request.ProductPostCreateRequest;
import com.zero9platform.domain.product_post.model.request.ProductPostUpdateRequest;
import com.zero9platform.domain.product_post.model.response.*;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import com.zero9platform.domain.product_post_option.model.request.ProductPostOptionCreateRequest;
import com.zero9platform.domain.searchLog.model.event.SearchEvent;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductPostService {

    private final UserRepository userRepository;
    private final ProductPostRepository productPostRepository;
    private final S3Service s3Service;
    private final AmazonS3 amazonS3;
    private final ApplicationEventPublisher eventPublisher;

    private static final String S3_FOLDER = "product_post";
    private final ProductPostFavoriteRepository productPostFavoriteRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 상품 판매 게시물 생성 - Influencer, Admin
     */
    @Transactional
    public ProductPostCreateResponse productPostCreate(Long userId, ProductPostCreateRequest request, MultipartFile file) {

        // 인가 확인 (사용자 제외)
        User user = validPermission(userId);

        // 판매일 검증
        validProductPostSaleDate(request.getStartDate(), request.getEndDate());

        // 이미지 파일 업로드 S3 서비스 호출
        String contentImageKey = null;
        if (file != null && !file.isEmpty()) {
            contentImageKey = s3Service.upload(file, S3_FOLDER);
        }

        // 현재 시간 생성
        LocalDateTime now = LocalDateTime.now();

        // 상품판매 게시물 생성
        ProductPost productPost = new ProductPost(user, request.getTitle(), request.getName(), request.getContent(), request.getOriginalPrice(), contentImageKey, request.getCategory().name(), request.getStartDate(), request.getEndDate(), now);

        // 옵션 생성
        for (ProductPostOptionCreateRequest optionRequest: request.getOptionList()) {
            ProductPostOption option = new ProductPostOption(productPost, optionRequest.getName(), optionRequest.getSalePrice(), optionRequest.getStockQuantity());
            option.setStockStatus(StockStatus.IN_STOCK.name());
            productPost.addOption(option);
        }

        ProductPost savedProductPost = productPostRepository.save(productPost);

        //엘라스틱서치 비동기 업데이트
        eventPublisher.publishEvent(SearchEvent.from(savedProductPost, false));

        return ProductPostCreateResponse.from(savedProductPost);
    }

    /**
     * 상품 게시물 상세 조회
     */
    @Transactional(readOnly = true)
    public ProductPostGetDetailResponse productPostGetDetail(Long productPostId) {

        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        Long favoriteCount = productPostFavoriteRepository.countByProductPost_Id(productPost.getId());

        String productImage = productPost.getImage() != null ? amazonS3.getUrl(bucket, productPost.getImage()).toString() : null;

        return ProductPostGetDetailResponse.from(productPost, productImage, favoriteCount);
    }

    /**
     * 상품목록 전체 조회
     */
    @Transactional(readOnly = true)
    public Page<ProductPostGetListResponse> productPostGetList(Pageable pageable) {

        Page<ProductPost> productPostsPage = productPostRepository.findAllByOrderByUpdatedAtDesc(pageable);

        return productPostsPage.map(productPost -> {
            // 찜 개수 조회
            Long favoriteCount = productPostFavoriteRepository.countByProductPost_Id(productPost.getId());

            // 이미지 URL 생성
            String imageUrl = (productPost.getImage() != null) ? amazonS3.getUrl(bucket, productPost.getImage()).toString() : null;

            return ProductPostGetListResponse.from(productPost, imageUrl, favoriteCount);
        });
    }

    /**
     * 내가 등록한 판매 게시물 보기
     * limit version
     */
    @Transactional(readOnly = true)
    public List<ProductPostGetMyListResponse> myProductPostGetLimitList(Long userId, Pageable pageable) {

        return productPostRepository.findMyPostsWithFavoriteCount(userId, pageable);
    }

    /**
     * 상품 게시물 수정
     */
    @Transactional
    public ProductPostUpdateResponse productPostUpdate(Long userId, Long productPostId, ProductPostUpdateRequest request, MultipartFile file) {

        User user = validPermission(userId);

        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        if (!ProgressStatus.READY.name().equals(productPost.getProgressStatus())) {
            throw new CustomException(ExceptionCode.PRODUCT_POST_CANNOT_UPDATE_ALREADY_STARTED);
        }

        validProductPostOwner(user, productPost);

        String oldImageKey = productPost.getImage();
        String newImageKey = null;

        if (file != null && !file.isEmpty()) {
            newImageKey = s3Service.upload(file, S3_FOLDER);
        }

        String finalImageKey = (newImageKey != null) ? newImageKey : oldImageKey;

        String category = (request.getCategory() != null) ? request.getCategory().name() : productPost.getCategory(); // 기존 유지

        productPost.update(
                category,
                request.getTitle(),
                request.getName(),
                request.getContent(),
                request.getOriginalPrice(),
                finalImageKey,
                request.getStartDate(),
                request.getEndDate(),
                LocalDateTime.now()
        );

        if (newImageKey != null && oldImageKey != null) {
            s3Service.s3Delete(oldImageKey);
        }

        eventPublisher.publishEvent(SearchEvent.from(productPost, false));

        return ProductPostUpdateResponse.from(productPost);
    }

    /**
     * 본인의 상품 게시물인지 검증
     */
    private static void validProductPostOwner(User user, ProductPost productPost) {
        if (!Objects.equals(user.getId(), productPost.getUser().getId())) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }
    }

    /**
     * 상품 판매 게시물 생성 권한 검증 - 사용자 x
     */
    private User validPermission(Long userId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        if (UserRole.valueOf(user.getRole()) == UserRole.USER) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }

        return user;
    }

    /**
     * 판매 기간 검증
     */
    private static void validProductPostSaleDate(LocalDateTime startDate, LocalDateTime endDate) {

        // 판매 시작일
        if (startDate.isBefore(LocalDateTime.now())) {
            throw new CustomException(ExceptionCode.PRODUCT_POST_INVALID_DATE_RANGE);
        }

        // 판매 종료일
        if (endDate.isBefore(startDate)) {
            throw new CustomException(ExceptionCode.PRODUCT_POST_INVALID_DATE_RANGE);
        }
    }
}