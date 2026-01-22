package com.zero9platform.domain.gpp_favorite;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.gpp_favorite.model.response.GppFavoriteCreateResponse;
import com.zero9platform.domain.gpp_favorite.model.GppFavoriteGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9")
public class GppFavoriteController {

    private final GppFavoriteService gppFavoriteService;

    /**
     * 찜 등록
     */
    @PostMapping("/gp-posts/{gppId}/favorites")
    public ResponseEntity<CommonResponse<GppFavoriteCreateResponse>> gppFavoriteCreateHandler(@PathVariable Long gppId, @AuthenticationPrincipal AuthUser authUser) {

        GppFavoriteCreateResponse createResponse = gppFavoriteService.gppFavoriteCreate(gppId, authUser);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("찜 등록 성공", createResponse));
    }

    /**
     * 찜 등록 취소
     */
    @DeleteMapping("/gp-posts/{gppId}/favorites")
    public ResponseEntity<CommonResponse<Void>> gppFavoriteCancellationHandler(@PathVariable Long gppId, @AuthenticationPrincipal AuthUser authUser) {

        gppFavoriteService.gppFavoriteCancellation(gppId, authUser);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("찜 취소 성공", null));
    }

    /**
     * 찜 목록 조회
     */
    @GetMapping("/favorites")
    public ResponseEntity<CommonResponse<PageResponse<GppFavoriteGetResponse>>> gppFavoriteGetPageHandler(@AuthenticationPrincipal AuthUser authUser, Pageable pageable) {

        PageResponse<GppFavoriteGetResponse> favoriteList = gppFavoriteService.gppFavoritePage(authUser, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("찜 목록 조회 성공", favoriteList));
    }

}

