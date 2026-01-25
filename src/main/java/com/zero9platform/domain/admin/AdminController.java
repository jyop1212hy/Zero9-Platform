package com.zero9platform.domain.admin;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.admin.model.request.influencer.InfluencerApproveRequest;
import com.zero9platform.domain.admin.model.response.influencer.InfluencerApproveResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/zero9/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 인플루언서 가입 승인
     */
    @PutMapping("/influencers/{userId}/approval")
    public ResponseEntity<CommonResponse<InfluencerApproveResponse>> influencerApproveHandler(@PathVariable Long userId, @Valid @RequestBody InfluencerApproveRequest request) {

        InfluencerApproveResponse response = adminService.influencerApprove(userId, request);

        String approvedMessage = Boolean.TRUE.equals(response.getStatus()) ? "인플루언서 승인 성공" : "인플루언서 승인 보류";

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success(approvedMessage, response));
    }
}