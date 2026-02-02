package com.zero9platform.domain.admin;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.admin.model.request.influencer.InfluencerApproveRequest;
import com.zero9platform.domain.admin.model.response.influencer.InfluencerDetailResponse;
import com.zero9platform.domain.admin.model.response.influencer.InfluencerApproveResponse;
import com.zero9platform.domain.admin.model.response.user.UserDetailResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/zero9/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 인플루언서 목록 조회
     */
    @GetMapping("/influencers")
    public ResponseEntity<CommonResponse<PageResponse<InfluencerDetailResponse>>> InfluencersListHandler(@RequestParam(required = false) Boolean approved, @RequestParam(required = false) String nickname, Pageable pageable)  {

        PageResponse<InfluencerDetailResponse> response = PageResponse.from(adminService.influencerList(approved, nickname, pageable));

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("인플루언서 목록 조회 성공", response));
    }

    /**
     * 인플루언서 가입 승인
     */
    @PutMapping("/influencers/{userId}/approval")
    public ResponseEntity<CommonResponse<InfluencerApproveResponse>> influencerApproveHandler(@PathVariable Long userId, @Valid @RequestBody InfluencerApproveRequest request) {

        InfluencerApproveResponse response = adminService.influencerApprove(userId, request);

        String approvedMessage = Boolean.TRUE.equals(response.getStatus()) ? "인플루언서 승인 성공" : "인플루언서 승인 보류";

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success(approvedMessage, response));
    }

    /**
     * 사용자 목록 조회
     */
    @GetMapping("/users")
    public ResponseEntity<CommonResponse<PageResponse<UserDetailResponse>>> userListHandler(@RequestParam(required = false) String nickname, Pageable pageable) {

        PageResponse<UserDetailResponse> response = PageResponse.from(adminService.userList(nickname, pageable));

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("사용자 목록 조회 성공", response));
    }
}