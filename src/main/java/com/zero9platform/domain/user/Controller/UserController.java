package com.zero9platform.domain.user.Controller;

import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.user.Service.UserService;
import com.zero9platform.domain.user.model.user.request.UserCreateRequest;
import com.zero9platform.domain.user.model.user.request.UserDeleteRequest;
import com.zero9platform.domain.user.model.user.request.UserInfluencerCreateRequest;
import com.zero9platform.domain.user.model.user.request.UserUpdateRequest;
import com.zero9platform.domain.user.model.user.response.UserCreateResponse;
import com.zero9platform.domain.user.model.user.response.UserDetailResponse;
import com.zero9platform.domain.user.model.user.response.UserUpdateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/zero9")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 일반 회원 회원가입
     */
    @PostMapping("/users/normal")
    public ResponseEntity<CommonResponse<UserCreateResponse>> createUserHandler(@Valid @RequestBody UserCreateRequest request) {

        UserCreateResponse response = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("회원가입 성공", response));
    }

    /**
     * 인플루언서 회원가입
     */
    @PostMapping("/users/influencer")
    public ResponseEntity<CommonResponse<UserCreateResponse>> createInfluencerHandler(@Valid @RequestBody UserInfluencerCreateRequest request) {

        UserCreateResponse response = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("인플루언서는 관리자 승인후에 활동 가능합니다.", response));
    }

    /**
     * 사용자 프로필 조회
     */
    @GetMapping("/users/{userId}/profile")
    public ResponseEntity<CommonResponse<UserDetailResponse>> userDetailHandler(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long userId) {

        boolean isAdmin = false, // 관리자 권한 확인
                isMy = false; // 내 자신의 고유 식별자 일때

        // 관리자 권한은 조회 불가능
        if (authUser.getUserRole() == UserRole.ADMIN) {
            isAdmin = true;
        }
    
        // 자기 자신일때의 조회 데이터가 다르게 나오는 조건
        if (authUser.getId().equals(userId)) {
            isMy = true;
        }

        UserDetailResponse response = userService.userDetail(userId, isAdmin, isMy);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("사용자 프로필 조회 성공", response));
    }

    /**
     * 사용자 목록 조회
     */
    @GetMapping("/users")
    public ResponseEntity<CommonResponse<PageResponse<UserDetailResponse>>> userListHandler(Pageable pageable) {

        Page<UserDetailResponse> page = userService.userList(pageable);

        PageResponse<UserDetailResponse> response = PageResponse.from(page);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("사용자 목록 조회 성공", response));
    }

    /**
     * 사용자 프로필 수정
     */
    @PutMapping("/users")
    public ResponseEntity<CommonResponse<UserUpdateResponse>> userUpdateHandler(@AuthenticationPrincipal AuthUser authUser, @Valid  @RequestBody UserUpdateRequest request) {

        UserUpdateResponse response = userService.userUpdate(authUser.getId(), request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("사용자 프로필 수정이 완료 되었습니다.", response));
    }

    /**
     * 회원 삭제 (완전 삭제가 아닌 삭제 날짜 업데이트)
     */
    @DeleteMapping("/users")
    public ResponseEntity<CommonResponse<Void>> userDeleteHandler(@AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UserDeleteRequest request) {

        userService.userDelete(authUser.getId(), request);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("회원탈퇴가 완료 되었습니다.",null));
    }

}
