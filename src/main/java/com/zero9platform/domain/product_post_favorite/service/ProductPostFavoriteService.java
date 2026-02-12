package com.zero9platform.domain.product_post_favorite.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.FeedType;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.activity_feed.event.FeedCreateEvent;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.product_post_favorite.entity.ProductPostFavorite;
import com.zero9platform.domain.product_post_favorite.model.response.ProductPostFavoriteCreateResponse;
import com.zero9platform.domain.product_post_favorite.model.response.ProductPostFavoriteGetResponse;
import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
import com.zero9platform.domain.ranking.service.RankingCounter;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductPostFavoriteService {

    private final ProductPostFavoriteRepository productPostFavoriteRepository;
    private final UserRepository userRepository;
    private final ProductPostRepository productPostRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RankingCounter rankingCounter;

    /**
     * 찜 등록
     */
    @Transactional
    public ProductPostFavoriteCreateResponse favoriteCreate(Long productPostId, AuthUser authUser) {

        //유저 아이디정보 추출
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        //게시물 존재 여부 확인
        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        //중복 등록 방지
        boolean existence = productPostFavoriteRepository.existsByUser_IdAndProductPost_Id(user.getId(), productPost.getId());

        if (existence) {
            throw new CustomException(ExceptionCode.FAVORITE_ALREADY);
        }

        //상품 게시물에 찜등록이 되어있지 않다면 상찜등록 하기
        ProductPostFavorite productPostFavorite = new ProductPostFavorite(user, productPost);

        //DB 저장
        productPostFavoriteRepository.save(productPostFavorite);

        // 캐시 저장
        rankingCounter.increaseFavorite(productPostFavorite.getProductPost().getId());

        // 현재 게시물의 찜 개수 확인
        long favoriteCount = productPostFavoriteRepository.countByProductPost_Id(productPost.getId());

        // 초기 서비스 규모에 최적화된 3건을 기준으로 인기 상품 피드 생성
        if (favoriteCount >= 3) {
            eventPublisher.publishEvent(new FeedCreateEvent(FeedType.POPULAR, productPost.getId(), productPost.getTitle(), null));
        }

        return ProductPostFavoriteCreateResponse.from(productPostFavorite);
    }

    /**
     * 찜 등록 취소
     */
    @Transactional
    public void favoriteCancellation(Long productPostId, AuthUser authUser) {

        //유저 아이디정보 추출
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        //게시물 존재 여부 확인
        ProductPost productPost = productPostRepository.findById(productPostId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_FOUND));

        // 찜 등록 확인
        Optional<ProductPostFavorite> productPostFavorite = productPostFavoriteRepository.findByUserAndProductPost(user, productPost);

        if (productPostFavorite.isEmpty()) {
            throw new CustomException(ExceptionCode.FAVORITE_NOT_FOUND);
        }

        // DB 삭제
        productPostFavoriteRepository.deleteById(productPostFavorite.get().getId());

        // 캐시 삭제
        rankingCounter.decreaseFavorite(productPost.getId());
    }

    /**
     * 찜 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ProductPostFavoriteGetResponse> favoriteGetPage(AuthUser authUser, Pageable pageable) {

        //유저 아이디정보 추출
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        //본인 찜 등록 리스트 조회하기
        Page<ProductPostFavorite> gppFavoritePage = productPostFavoriteRepository.findByUser_Id(user.getId(), pageable);

        // ProductPostFavorite Page -> ProductPostFavoriteGetResponse Page 변환
        return gppFavoritePage.map(ProductPostFavoriteGetResponse::from);
    }
}