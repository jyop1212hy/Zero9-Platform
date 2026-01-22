package com.zero9platform.domain.grouppurchase_post.service;

import com.zero9platform.common.enums.Category;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.GppApprovalStatus;
import com.zero9platform.common.enums.GppProgressStatus;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.model.request.GroupPurchasePostCreateRequest;
import com.zero9platform.domain.grouppurchase_post.model.request.GroupPurchasePostUpdateRequest;
import com.zero9platform.domain.grouppurchase_post.model.response.GroupPurchasePostDetailResponse;
import com.zero9platform.domain.grouppurchase_post.model.response.GroupPurchasePostListResponse;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.zero9platform.common.enums.UserRole.ADMIN;

@Service
@RequiredArgsConstructor
public class GroupPurchasePostService {

    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final UserRepository userRepository;
    private final GroupPurchasePostViewCountService groupPurchasePostViewCountService;

    /**
     * 공동구매 게시물 작성
     */
    @Transactional
    public GroupPurchasePostDetailResponse gpPostCreate(GroupPurchasePostCreateRequest request, AuthUser authUser) {

        Long userId = authUser.getId();
        // 1️. User 조회 (AuthUser)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 2️. 유효성 검증 - 시작일/종료일 타당성
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

        // 3️. Enum 변환 - 카테고리, 승인상태(작성시점 = 대기중), 진행상태
        Category category = Category.from(request.getCategory());
        GppProgressStatus gppProgressStatus = GppProgressStatus.from(request.getGppProgressStatus());
        //GppApprovalStatus gppApprovalStatus = GppApprovalStatus.APPROVED; // 테스트 시 approved

        // 4️. Entity 생성
        GroupPurchasePost gpp = new GroupPurchasePost(
                user,
                request.getProductName(),
                request.getContent(),
                request.getImage(),
                request.getPrice(),
                request.getLinkUrl(),
                category,
                GppApprovalStatus.PENDING,
                gppProgressStatus,
                request.getStartDate().atStartOfDay(),
                request.getEndDate().atStartOfDay()
        );

        // 5️. 데이터 저장
        GroupPurchasePost savedGpp = groupPurchasePostRepository.save(gpp);

        // 6️. Response 변환
        return GroupPurchasePostDetailResponse.from(savedGpp);
    }

    /**
     * 공동구매 게시물 전체 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<GroupPurchasePostListResponse> gpPostReadAll(Pageable pageable) {

        // 1. 공동구매 게시물 페이징 조회 [삭제처리 제외 + 승인된 공동구매 게시물]
        Page<GroupPurchasePost> page = groupPurchasePostRepository.findAllByDeletedAtIsNullAndGppApprovalStatus(pageable, GppApprovalStatus.APPROVED);

        // 2. Entity -> ListResponse DTO 변환
        Page<GroupPurchasePostListResponse> responsePage = page.map(GroupPurchasePostListResponse::from);

        // 3. PageResponse 공용 응답 객체로 변환
        return PageResponse.from(responsePage);
    }
    
    /**
     * 공동구매 게시물 상세 조회
     */
    @Transactional(readOnly = true)
    public GroupPurchasePostDetailResponse gpPostReadDetail(Long gppId) {

        // 1. 공동구매 게시물 조회 [삭제처리 제외 + 유효성 검사 + 승인된 공동구매 게시물]
        GroupPurchasePost gpp = groupPurchasePostRepository.findByIdAndDeletedAtIsNullAndGppApprovalStatus(gppId, GppApprovalStatus.APPROVED)
                .orElseThrow(() -> new CustomException(ExceptionCode.GPP_NOT_FOUND));

        // 2. 조회수 증가 (아직 동시성 문제 고려 안했음, 추후 고민할 것)
        groupPurchasePostViewCountService.increaseViewCount(gppId);

        // 3. 저장 (영속 상태라 사실상 생략 가능)
//        groupPurchasePostRepository.save(gpp);

        // 4. Response 변환
        return GroupPurchasePostDetailResponse.from(gpp);
    }

    /**
     * 공동구매 게시물 수정
     */
    @Transactional
    public GroupPurchasePostDetailResponse gpPostUpdate(Long gppId, GroupPurchasePostUpdateRequest request, AuthUser authUser) {

        Long userId = authUser.getId();

        // 1. 공동구매 게시물 조회 [삭제처리 제외 + 유효성 검사 + 승인된 공동구매 게시물]
        GroupPurchasePost gpp = groupPurchasePostRepository.findByIdAndDeletedAtIsNullAndGppApprovalStatus(gppId, GppApprovalStatus.APPROVED)
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

        // 4. Enum 변환 - 카테고리, 승인상태, 진행상태
        Category category = Category.from(request.getCategory());
        GppProgressStatus gppProgressStatus = GppProgressStatus.from(request.getGppProgressStatus());
//        GppApprovalStatus gppApprovalStatus = gpp.getGppApprovalStatus();

        // 5. 엔티티 수정
        gpp.update(
                request.getProductName(),
                request.getContent(),
                request.getImage(),
                request.getPrice(),
                request.getLinkUrl(),
                category,
                gppProgressStatus,
                request.getStartDate().atStartOfDay(),
                request.getEndDate().atStartOfDay()
        );

        // 6. 응답 변환
        return GroupPurchasePostDetailResponse.from(gpp);
    }


    /**
     * 공동구매 게시물 삭제
     */
    @Transactional
    public void gpPostDelete(Long gppId, AuthUser authUser) {

        Long userId = authUser.getId();

        // 1. 공동구매 게시물 조회(삭제처리 제외) + 유효성 검사
        GroupPurchasePost gpp = groupPurchasePostRepository.findByIdAndDeletedAtIsNull(gppId)
                .orElseThrow(() -> new CustomException(ExceptionCode.GPP_NOT_FOUND));

        // 2. 요청 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 3. 권한 검증 - 본인 or 관리자
        boolean isOwner = gpp.getUser().getId().equals(user.getId());
        boolean isAdmin = authUser.getUserRole() == ADMIN;

        if (!isOwner && !isAdmin) {
            throw new CustomException(ExceptionCode.GPP_NO_PERMISSION);
        }

        // 4. Soft Delete
        gpp.softDelete();
    }

}
