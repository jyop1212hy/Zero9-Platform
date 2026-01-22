package com.zero9platform.domain.gpp_favorite;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.gpp_favorite.entity.GppFavorite;
import com.zero9platform.domain.gpp_favorite.model.response.GppFavoriteCreateResponse;
import com.zero9platform.domain.gpp_favorite.model.GppFavoriteGetResponse;
import com.zero9platform.domain.gpp_favorite.repository.GppFavoriteRepository;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GppFavoriteService {

    private final GppFavoriteRepository gppFavoriteRepository;
    private final UserRepository userRepository;
    private final GroupPurchasePostRepository groupPurchasePostRepository;

    /**
     * 찜 등록
     */
    @Transactional
    public GppFavoriteCreateResponse gppFavoriteCreate(Long gppId, AuthUser authUser) {

        //유저 아이디정보 추출
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER));

        //게시물 존재 여부 확인
        GroupPurchasePost gpPost = groupPurchasePostRepository.findByIdAndDeletedAtIsNull(gppId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        //중복 등록 방지
        boolean existence = gppFavoriteRepository.existsByUserIdAndGroupPurchasePostId(user.getId(), gpPost.getId());
        if (existence) {
            throw new CustomException(ExceptionCode.ALREADY_FAVORITE);
        }

        //게시물이 있으면 로그인 되있는 유저 아이디를 추가
        GppFavorite gppFavorite = new GppFavorite(user, gpPost);

        gppFavoriteRepository.save(gppFavorite);

        return GppFavoriteCreateResponse.from(gppFavorite);
    }

    /**
     * 찜 등록 취소
     */
    @Transactional
    public void gppFavoriteCancellation(Long gppId, AuthUser authUser) {

        //유저 아이디정보 추출
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER));

        //게시물 존재 여부 확인
        GroupPurchasePost gpPost = groupPurchasePostRepository.findByIdAndDeletedAtIsNull(gppId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_POST));

        // 찜 존재 여부 확인
        Optional<GppFavorite> gppFavorite = gppFavoriteRepository.findByUserIdAndGroupPurchasePostId(user.getId(), gpPost.getId());

        if (gppFavorite.isEmpty()) {
            throw new CustomException(ExceptionCode.NOT_FOUND_FAVORITE);
        }

        // 삭제
        gppFavoriteRepository.deleteById(gppFavorite.get().getId());
    }

    /**
     * 찜 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<GppFavoriteGetResponse> gppFavoritePage(AuthUser authUser, Pageable pageable) {

        //유저 아이디정보 추출
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_FOUND_USER));

        //유저 아이디가 찜 등록한 리스트 조회하기
        Page<GppFavorite> gppFavoritePage = gppFavoriteRepository.findByUserId(user.getId(), pageable);

        //있으면 리스트를 리스폰스에 담는다.
        Page<GppFavoriteGetResponse> GppFavoriteGetDtoPage = gppFavoritePage.map(GppFavoriteGetResponse::from);

        return PageResponse.from(GppFavoriteGetDtoPage);
    }

}
