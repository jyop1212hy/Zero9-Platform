package com.zero9platform.domain.auth;

import com.zero9platform.common.model.CommonResponse;
import com.zero9platform.domain.auth.model.AuthUser;
import com.zero9platform.domain.auth.model.request.AuthLoginRequest;
import com.zero9platform.domain.auth.model.response.AuthLoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zero9/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthLoginResponse>> loginHandler(@Valid @RequestBody AuthLoginRequest request, HttpServletResponse httpServletResponse) {

        AuthLoginResponse response = authService.login(request, httpServletResponse);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("로그인 성공", response));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthUser authUser, HttpServletResponse httpServletResponse) {

        authService.logout(authUser.getId(), httpServletResponse);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Refresh Token 재발급
     * 기존 Refresh Token 삭제
     */
    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<AuthLoginResponse>> reissueHandler(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        AuthLoginResponse response = authService.reissue(httpServletRequest, httpServletResponse);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.success("Refresh Token 재발급", response));
    }
}
