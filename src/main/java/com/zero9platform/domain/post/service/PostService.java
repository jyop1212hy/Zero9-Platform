package com.zero9platform.domain.post.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.FeedType;
import com.zero9platform.common.enums.PostType;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.activity_feed.event.FeedCreateEvent;
import com.zero9platform.domain.post.entity.Post;
import com.zero9platform.domain.post.model.request.PostCreateRequest;
import com.zero9platform.domain.post.model.request.PostUpdateRequest;
import com.zero9platform.domain.post.model.response.*;
import com.zero9platform.domain.post.repository.PostRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 공지 및 문의사항 생성
     */
    @Transactional
    public PostCreateResponse postCreate(Long userId, PostCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 공지사항은 관리자만 작성 가능
        if (request.getPostType() == PostType.NOTICE && !UserRole.ADMIN.name().equals(user.getRole())) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }

        String encodedPassword = (request.isSecret() && request.getPassword() != null)
                ? passwordEncoder.encode(request.getPassword()) : null;


        Post saved = postRepository.save(new Post(user, request.getPostType().name(), request.getTitle(), request.getContent(), request.isSecret(), encodedPassword));

        // 4. 피드 이벤트 발행 (딱 이거면 끝)
        // 공지(NOTICE)면 userId에 null을, 문의(INQUIRY)면 본인 userId를 넘김
        FeedType feedType = (request.getPostType() == PostType.NOTICE) ? FeedType.NOTICE : FeedType.INQUIRY;
        Long targetUserId = (request.getPostType() == PostType.NOTICE) ? null : userId;
        eventPublisher.publishEvent(new FeedCreateEvent(feedType, saved.getId(), saved.getTitle(), targetUserId));
        return PostCreateResponse.from(saved);
    }

    /**
     * 공지 / 문의 상세조회
     */
    @Transactional(readOnly = true)
    public PostGetDetailResponse postGetDetail(Long userId, Long postId, String inputPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        // 해당 글이 비밀글인 경우
        if (post.isSecret()) {
            boolean isAdmin = UserRole.ADMIN.name().equals(user.getRole());
            boolean isOwner = Objects.equals(user.getId(), post.getUser().getId());

            // 관리자도 아니고 작성자도 아닌 경우만 비밀번호 체크 수행
            if (!isAdmin && !isOwner) {
                if (inputPassword == null || !passwordEncoder.matches(inputPassword, post.getPassword())) {
                    throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
                }
            }
        }

        return PostGetDetailResponse.from(post);
    }

    /**
     * 유형별 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<PostGetListResponse> postGetPage(PostType postType, Pageable pageable) {

        return postRepository.findAllByTypeAndDeletedAtIsNull(postType.name(), pageable)
                .map(PostGetListResponse::from);
    }

    /**
     * 공지 및 문의 수정
     */
    @Transactional
    public PostUpdateResponse postUpdate(Long userId, Long id, PostUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        // 권한 체크
        if (PostType.NOTICE.name().equals(post.getType())) {
            if (!UserRole.ADMIN.name().equals(user.getRole())) throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        } else {
            validOwner(post, userId);
        }

        String encodedPw = (request.getIsSecret() != null && request.getIsSecret() && request.getPassword() != null)
                ? passwordEncoder.encode(request.getPassword()) : post.getPassword();

        post.update(request.getTitle(), request.getContent(), request.getIsSecret(), encodedPw);

        return PostUpdateResponse.from(post);
    }

    /**
     * 공지 및 문의 삭제
     */
    @Transactional
    public void postDelete(Long userId, Long id) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        validOwnerOrAdmin(post, userId, UserRole.valueOf(user.getRole()));

        post.delete();
    }

    /**
     * 게시물 작성자 본인 여부 검증
     */
    private void validOwner(Post post, Long userId) {

        Long ownerId = post.getUser().getId();

        if (!ownerId.equals(userId)) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }
    }

    /**
     * 게시물 작성자 본인 여부 or 관리자 여부 검증
     */
    private void validOwnerOrAdmin(Post post, Long userId, UserRole userRole) {

        Long ownerId = post.getUser().getId();

        if (!ownerId.equals(userId) && userRole != UserRole.ADMIN) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }
    }
}