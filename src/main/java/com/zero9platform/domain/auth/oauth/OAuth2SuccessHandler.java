package com.zero9platform.domain.auth.oauth;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.domain.auth.model.response.AuthLoginResponse;
import com.zero9platform.domain.auth.service.AuthService;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final AuthService authService;

    // 프론트 콜백 URL (yml로 관리 권장)
//    @Value("${app.oauth2.redirect-uri:http://localhost:3000/oauth/callback}")
    @Value("${app.oauth2.redirect-uri:http://localhost:8080/zero9/auth/oauth/callback}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // CustomOAuth2UserService에서 반환한 Principal
        Object principalObj = authentication.getPrincipal();
        if (!(principalObj instanceof OAuthUserPrincipal principal)) {
            throw new CustomException(ExceptionCode.AUTH_NO_PERMISSION);
        }

        // 필요 시 User 재조회 (탈퇴 여부 등 정책 체크는 AuthService.oauthLogin에서 처리)
//        User user = userRepository.findById(principal.getUserId())
//                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // refreshToken 쿠키 세팅 + accessToken 발급
        AuthLoginResponse login = authService.oauthLogin(principal, response);

        // 프론트로 accessToken 전달(쿼리스트링)
        String accessToken = URLEncoder.encode(login.getToken(), StandardCharsets.UTF_8);
        response.sendRedirect(redirectUri + "?accessToken=" + accessToken);
    }
}
