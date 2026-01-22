package com.zero9platform.domain.searchLog;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.searchLog.model.SearchLogItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9")
public class SearchLogController {

    private final SearchLogService searchLogService;

    /**
     * 통합 검색 API
     * 검색 대상
     * - 공동구매 상품명
     * - 인플루언서 활동 닉네임
     */
    @GetMapping("/searches")
    public ResponseEntity<CommonResponse<PageResponse<SearchLogItemResponse>>> searchLogGetPageHandler(@RequestParam(required = false) String keyword, @RequestParam(required = false) String searchCondition, Pageable pageable) {

        // 검색 서비스 호출
        PageResponse<SearchLogItemResponse> pageResponse = searchLogService.search(keyword, searchCondition, pageable);

        // 공통 응답 포맷으로 반환
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("통합 검색 결과 조회 성공", pageResponse));
    }

}
