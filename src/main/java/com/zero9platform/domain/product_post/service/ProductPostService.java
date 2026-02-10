package com.zero9platform.domain.product_post.service;

import com.amazonaws.services.s3.AmazonS3;
import com.zero9platform.common.aws.s3.S3Service;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.StockStatus;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductPostService {

    private final UserRepository userRepository;
    private final ProductPostRepository productPostRepository;
    private final ActivityFeedService activityFeedService;
    private final S3Service s3Service;
    private final AmazonS3 amazonS3;
    private final ApplicationEventPublisher eventPublisher;

    private static final String S3_FOLDER = "product_post";

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

        // 상품판매 게시물 생성
        ProductPost productPost = new ProductPost(user, request.getTitle(), request.getName(), request.getContent(), request.getOriginalPrice(), contentImageKey, request.getCategory().name(), request.getProgressStatus().name(), request.getStartDate(), request.getEndDate());

        // 옵션 생성
        for (ProductPostOptionCreateRequest optionRequest: request.getOptionList()) {
            ProductPostOption option = new ProductPostOption(productPost, optionRequest.getName(), optionRequest.getSalePrice(), optionRequest.getStockQuantity());
            option.setStockStatus(StockStatus.IN_STOCK.name());
            productPost.addOption(option);
        }

        ProductPost savedProductPost = productPostRepository.save(productPost);

        //엘라스틱서치 비동기 업데이트
        eventPublisher.publishEvent(SearchEvent.from(savedProductPost, false));

        // 피드 생성 호출
        //activityFeedService.feedCreate("SOON", savedProductPost.getId(), savedProductPost.getTitle());

        return ProductPostCreateResponse.from(savedProductPost);
    }

    /**
     * 상품 게시물 상세 조회
     */
    @Transactional(readOnly = true)
    public ProductPostGetDetailResponse productPostGetDetail(Long productPostId) {

        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        String productImage = productPost.getImage() != null ? amazonS3.getUrl(bucket, productPost.getImage()).toString() : null;

        return ProductPostGetDetailResponse.from(productPost, productImage);
    }

    @Transactional(readOnly = true)
    public Page<ProductPostGetListResponse> productPostGetList(Pageable pageable) {

        Page<ProductPost> productPostsPage = productPostRepository.findAllByOrderByUpdatedAtDesc(pageable);

        return productPostsPage.map(productPost -> ProductPostGetListResponse.from(
                        productPost,
                        productPost.getImage() != null ? amazonS3.getUrl(bucket, productPost.getImage()).toString() : null
                )
        );
    }

    /**
     * 상품 게시물 수정
     */
    @Transactional
    public ProductPostUpdateResponse productPostUpdate(Long userId, Long productPostId, ProductPostUpdateRequest request, MultipartFile file) {

        // 인가 확인 (사용자 제외)
        User user = validPermission(userId);

        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_POST_NOT_FOUND));

        // 본인만 수정 가능
        validProductPostOwner(user, productPost);

        // 이미지 파일 업로드 S3 서비스 호출
        String newImageKey = null;
        String oldImageKey = productPost.getImage();

        if (file != null && !file.isEmpty()) {
            newImageKey = s3Service.upload(file, S3_FOLDER);
        }

        String category = request.getCategory() != null ? request.getCategory().name() : null;
        String progressStatus = request.getProgressStatus() != null ? request.getProgressStatus().name() : null;

        // 이미지 교체 로직
        String finalImageKey = newImageKey != null ? newImageKey : oldImageKey;

        productPost.update(category, progressStatus, request.getTitle(), request.getName(), request.getContent(), request.getOriginalPrice(), finalImageKey, request.getStartDate(), request.getEndDate());

        //엘라스틱서치 비동기 업데이트
        eventPublisher.publishEvent(SearchEvent.from(productPost, false));

        // 기존 이미지 삭제 (새 이미지가 있을 때만)
        if (newImageKey != null && oldImageKey != null) {
            s3Service.s3Delete(oldImageKey);
        }

        return ProductPostUpdateResponse.from(productPost);
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
