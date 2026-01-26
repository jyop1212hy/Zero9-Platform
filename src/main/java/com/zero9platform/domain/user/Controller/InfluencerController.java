package com.zero9platform.domain.user.Controller;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.gpp_follow.model.response.InfluencerGppGetListResponse;
import com.zero9platform.domain.user.Service.InfluencerService;
import com.zero9platform.domain.user.model.influencer.InfluencerDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/zero9")
@RequiredArgsConstructor
public class InfluencerController {

    private final InfluencerService influencerService;

    /**
     * 인플루언서 목록 조회
     */
    @GetMapping("/influencers")
    public ResponseEntity<CommonResponse<PageResponse<InfluencerDetailResponse>>> InfluencersListHandler(@RequestParam(required = false) Boolean approved, Pageable pageable)  {

        PageResponse<InfluencerDetailResponse> response = PageResponse.from(influencerService.influencerList(approved, pageable));

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("인플루언서 목록 조회 성공", response));
    }

}
