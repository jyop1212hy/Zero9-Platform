package com.zero9platform.domain.searchLog.controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.common.util.SearchProfanityFilter;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.searchLog.model.response.RecentSearchResponse;
import com.zero9platform.domain.searchLog.model.response.SearchLogItemResponse;
import com.zero9platform.domain.searchLog.service.SearchLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9")
public class SearchLogController {

    private final SearchLogService searchLogService;
    private final SearchProfanityFilter profanityFilter;

    /**
     * 통합 검색 API
     * 검색 대상 - 공동구매 상품명, 인플루언서 활동 닉네임
     */
    @GetMapping("/search-logs")
    public ResponseEntity<CommonResponse<PageResponse<SearchLogItemResponse>>> searchLogGetPageHandler(@RequestParam(required = false) String keyword, @RequestParam(required = false) String postType, Pageable pageable, @AuthenticationPrincipal AuthUser authUser, HttpServletRequest request) {

        // keyword가 null이면 빈 문자열로 대체해서 NPE 방지
        String cleanKeyword = (keyword == null) ? "" : keyword.trim();

        // 검색 서비스 호출
        Page<SearchLogItemResponse> page = searchLogService.searchLog(cleanKeyword, postType, pageable, authUser, request);

        // PageResponse로 변환
        PageResponse<SearchLogItemResponse> pageResponse = PageResponse.from(page);

        // 공통 응답 포맷으로 반환
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("통합 검색 결과 조회 성공", pageResponse));
    }

    /**
     * 나의 최근 검색 히스토리 조회 (최근 검색어 리스트)
     */
    @GetMapping("/search-logs/recent")
    public ResponseEntity<CommonResponse<List<RecentSearchResponse>>> getRecentSearchHistory(
            @AuthenticationPrincipal AuthUser authUser,
            HttpServletRequest request) {

        List<RecentSearchResponse> history = searchLogService.getMySearchHistory(authUser, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("최근 검색어 조회 성공", history));
    }

    /**
     * 비속어 업로드
     */
    @PostMapping("/admin/profanity/refresh")
    public ResponseEntity<CommonResponse<String>> refreshFilter() {

        profanityFilter.refresh();

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("비속어 사전이 실시간으로 반영되었습니다.", null));
    }

    /**
     * 비속어 단어 추가
     */
    @PostMapping("/admin/profanities/{word}")
    public ResponseEntity<CommonResponse<String>> addWord(@PathVariable String word) {

        profanityFilter.addWord(word);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("[" + word + "]이(가) 사전에 추가되었으며 파일에도 기록되었습니다.", null));
    }

    /**
     * 비속어 단어 삭제
     */
    @DeleteMapping("/admin/profanities/{word}")
    public ResponseEntity<CommonResponse<String>> removeWord(@PathVariable String word) {

        profanityFilter.removeWord(word);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("[" + word + "]이(가) 사전 및 파일에서 삭제되었습니다.", null));
    }

    /**
     * DB 데이터를 ES로 전송 (데이터 보정용)
     */
    @PostMapping("/admin/search/reindex")
    public ResponseEntity<CommonResponse<String>> reindex() {

        searchLogService.bulkIndexing();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(CommonResponse.success("전체 데이터 재인덱싱 작업을 시작했습니다", null));
    }
}
