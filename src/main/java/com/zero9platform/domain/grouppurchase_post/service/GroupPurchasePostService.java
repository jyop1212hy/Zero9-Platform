package com.zero9platform.domain.grouppurchase_post.service;

import com.amazonaws.services.s3.AmazonS3;
import com.zero9platform.common.aws.s3.S3Service;
import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.model.request.GroupPurchasePostCreateRequest;
import com.zero9platform.domain.grouppurchase_post.model.request.GroupPurchasePostUpdateRequest;
import com.zero9platform.domain.grouppurchase_post.model.response.GroupPurchasePostDetailResponse;
import com.zero9platform.domain.grouppurchase_post.model.response.GroupPurchasePostListResponse;
import com.zero9platform.domain.grouppurchase_post.model.response.GroupPurchasePostReadResponse;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GroupPurchasePostService {

    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final UserRepository userRepository;
//    private final GroupPurchasePostViewCountService groupPurchasePostViewCountService;
    private final GroupPurchasePostViewCountRedisService groupPurchasePostViewCountRedisService;
    private final S3Service s3Service;
    private final AmazonS3 amazonS3;
    private final ApplicationEventPublisher eventPublisher;

    private static final String S3_FOLDER = "gp_post";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 공동구매 게시물 작성
     */
    @Transactional
    public GroupPurchasePostDetailResponse gpPostCreate(GroupPurchasePostCreateRequest request, Long userId, MultipartFile file) {

        // 1. User 조회 (AuthUser)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 2. 유효성 검증 - 시작일/종료일 타당성
        // 종료일은 시작일 이전일 수 없음
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new CustomException(ExceptionCode.GPP_INVALID_DATE_RANGE);
        }

        // startDate >= 오늘 && endDate >= 오늘
        // endDate >= 오늘 -> 비교연산 필요없음 (바로 위의 isBefore로 endDate가 무조건 startDate보다 이후임을 검증)
        // 시작일은 오늘 이전일 수 없음
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new CustomException(ExceptionCode.GPP_INVALID_DATE_RANGE);
        }

        // 3. Enum 변환 - 카테고리, 진행상태
//        Category category = request.getCategory();
        String category = request.getCategory().name();
//        GppProgressStatus gppProgressStatus = request.getGppProgressStatus();

        // 4. 이미지 파일 업로드 S3 서비스 호출
        String contentImage = "";
        if (file != null && !file.isEmpty()) {
            contentImage = s3Service.upload(file, S3_FOLDER);
        }

        // 5. Entity 생성
        LocalDateTime now = LocalDateTime.now();
        GroupPurchasePost gpp = new GroupPurchasePost(
                user,
                request.getProductName(),
                request.getContent(),
                contentImage,
                request.getPrice(),
                request.getLinkUrl(),
                category,
//                gppProgressStatus.name(),
                request.getStartDate().atStartOfDay(),
                request.getEndDate().atStartOfDay(),
                now
        );

        // 6. 데이터 저장
        GroupPurchasePost savedGpp = groupPurchasePostRepository.save(gpp);

        // 엘라스틱서치 비동기 데이터 추가
        eventPublisher.publishEvent(SearchEvent.from(savedGpp, false));

        // 7. Response 변환
        return GroupPurchasePostDetailResponse.from(savedGpp, contentImage);
    }

    /**
     * 공동구매 게시물 전체 조회
     */
    @Transactional(readOnly = true)
    public Page<GroupPurchasePostListResponse> gpPostReadAll(Pageable pageable) {

        // 1. 공동구매 게시물 페이징 조회 [삭제처리 제외]
        Page<GroupPurchasePost> page = groupPurchasePostRepository.findAllByDeletedAtIsNull(pageable);

        // 2. 응답객체 매핑 후 반환
        return page.map(gpp -> GroupPurchasePostListResponse.from(
                gpp,
                gpp.getImage() != null ? amazonS3.getUrl(bucket, gpp.getImage()).toString() : null
        ));
    }
    
    /**
     * 공동구매 게시물 상세 조회
     */
    @Transactional(readOnly = true)
    public GroupPurchasePostReadResponse gpPostReadDetail(Long gppId) {

        // 1. 공동구매 게시물 조회 [삭제처리 제외 + 유효성 검사]
        GroupPurchasePost gpp = groupPurchasePostRepository.findByIdAndDeletedAtIsNull(gppId)
                .orElseThrow(() -> new CustomException(ExceptionCode.GPP_NOT_FOUND));

        // 2. 조회수 증가 V1 (트래픽 문제 고려 X, Redis를 사용하지 못하는 환경에서 사용 가능)
//        groupPurchasePostViewCountService.increaseViewCount(gppId);
        // 2. 조회수 증가 V2 (Redis)
        long incrementedValue = groupPurchasePostViewCountRedisService.increaseViewCountRedisCache(gppId);

        // 3. 실시간 조회수 조회 (DB + Redis-delta)
        // DB 조회수 + Redis 조회수 = 실시간 조회수 (사용자가 보는 화면)
        long realtimeViewCount = gpp.getViewCount() + incrementedValue;

        String imgUrl = gpp.getImage() != null ? amazonS3.getUrl(bucket, gpp.getImage()).toString() : null;

        // 4. Response 변환
        return GroupPurchasePostReadResponse.from(gpp, imgUrl, realtimeViewCount);
    }

    /**
     * 공동구매 게시물 수정
     */
    @Transactional
    public GroupPurchasePostDetailResponse gpPostUpdate(Long gppId, GroupPurchasePostUpdateRequest request, Long userId, MultipartFile file) {

        // 1. 공동구매 게시물 조회 [삭제처리 제외 + 유효성 검사]
        GroupPurchasePost gpp = groupPurchasePostRepository.findByIdAndDeletedAtIsNull(gppId)
                .orElseThrow(() -> new CustomException(ExceptionCode.GPP_NOT_FOUND));
        
        // 2. User 조회 (AuthUser)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 3. 유효성 검증 - 시작일/종료일 타당성 + 작성자 검증
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new CustomException(ExceptionCode.GPP_INVALID_DATE_RANGE);
        }
        if (!gpp.getUser().getId().equals(user.getId())) { // 본래의 작성자 - 현재 접속중인 작성자 비교
            throw new CustomException(ExceptionCode.GPP_NO_PERMISSION);
        }

        // 4. 이미지 파일 업로드 S3 서비스 호출
        String oldImageKey = gpp.getImage();
        String newImageKey = null;

        if (file != null && !file.isEmpty()) {
            newImageKey = s3Service.upload(file, S3_FOLDER);
        }

        // 5. Enum 변환 - 카테고리, 진행상태
        Category category = request.getCategory();
//        GppProgressStatus gppProgressStatus = request.getGppProgressStatus();

        // 이미지 교체 로직
        String finalImageKey = newImageKey != null ? newImageKey : oldImageKey;

        // 6. 엔티티 수정
        LocalDateTime now = LocalDateTime.now();
        gpp.update(
                request.getProductName(),
                request.getContent(),
                finalImageKey,
                request.getPrice(),
                request.getLinkUrl(),
                category.name(),
//                gppProgressStatus.name(),
                request.getStartDate().atStartOfDay(),
                request.getEndDate().atStartOfDay(),
                now
        );

        // 기존 이미지 삭제 (새 이미지가 있을 때만)
        if (newImageKey != null && oldImageKey != null) {
            s3Service.s3Delete(oldImageKey);
        }

        // 엘라스틱서치 비동기 데이터 추가
        eventPublisher.publishEvent(SearchEvent.from(gpp, false));

        // 6. 응답 변환
        return GroupPurchasePostDetailResponse.from(gpp, finalImageKey);
    }

    /**
     * 공동구매 게시물 삭제
     */
    @Transactional
    public void gpPostDelete(Long gppId, Long userId, boolean isAdmin) {

//        Long userId = authUser.getId();

        // 1. 공동구매 게시물 조회(삭제처리 제외) + 유효성 검사
        GroupPurchasePost gpp = groupPurchasePostRepository.findByIdAndDeletedAtIsNull(gppId)
                .orElseThrow(() -> new CustomException(ExceptionCode.GPP_NOT_FOUND));

        // 2. 요청 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 3. 권한 검증 - 본인 or 관리자
        boolean isOwner = gpp.getUser().getId().equals(user.getId());

        if (!isOwner && !isAdmin) {
            throw new CustomException(ExceptionCode.GPP_NO_PERMISSION);
        }

        // 4. Soft Delete
        LocalDateTime now = LocalDateTime.now();
        gpp.softDelete(now);

        // 엘라스틱서치 비동기 데이터 삭제
        eventPublisher.publishEvent(SearchEvent.from(gpp, true));
    }
}