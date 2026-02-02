package com.zero9platform.domain.auth;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.jwt.JwtUtil;
import com.zero9platform.domain.auth.refresh_token.RefreshToken;
import com.zero9platform.domain.auth.refresh_token.RefreshTokenRepository;
import com.zero9platform.domain.auth.model.request.AuthLoginRequest;
import com.zero9platform.domain.auth.model.response.AuthLoginResponse;
import com.zero9platform.domain.user.entity.Influencer;
import com.zero9platform.domain.user.repository.InfluencerRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final InfluencerRepository influencerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    /**
     * 로그인
     */
    @Transactional
    public AuthLoginResponse login(AuthLoginRequest request, HttpServletResponse response) {

        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        // 탈퇴한 회원 검사
        if (user.getDeletedAt() != null) {
            throw new CustomException(ExceptionCode.USER_WITHDRAWN);
        }

        // 비밀번호 검사
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ExceptionCode.PASSWORD_NOT_MATCH);
        }

        // role 변환
        UserRole role = UserRole.valueOf(user.getRole());

        // 인플루언서 승인 확인
        if (role == UserRole.INFLUENCER) {
            boolean approved = influencerRepository.findByUserId(user.getId())
                    .map(Influencer::getInfluencerApprovalStatus)
                    .orElse(false);

            if (!approved) {
                throw new CustomException(ExceptionCode.INFLUENCER_NOT_APPROVED);
            }
        }

        // 기존 Refresh Token 전부 제거
        refreshTokenRepository.deleteAllByUserId(user.getId());

        // Access Token 생성
        String accessToken = jwtUtil.createToken(
                user.getId(),
                user.getNickname(),
                role
        );

        // Refresh Token 생성
        String refreshTokenValue = jwtUtil.createRefreshToken();

        RefreshToken refreshToken = new RefreshToken(
                refreshTokenValue,
                user.getId(),
                LocalDateTime.now().plusDays(14)
        );

        refreshTokenRepository.save(refreshToken);

        // 쿠키 생성
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshTokenValue)
                .httpOnly(true)
                .secure(false) // https = true
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .sameSite("Lax") // 개발 환경 = Lax
                .build();

        // 브라우저는 이 헤더를 보고 쿠키를 저장 이후 요청 시 자동으로 서버에 쿠키를 포함해서 보냄
        response.addHeader("Set-Cookie", cookie.toString());

        return new AuthLoginResponse(accessToken);
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(Long userId, HttpServletResponse response) {

        refreshTokenRepository.deleteAllByUserId(userId);

        // 쿠키 지우기
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // https = true
                .path("/")
                .maxAge(0)
                .sameSite("Lax") // 개발환경 = Lax
                .build();

        // 브라우저는 이 헤더를 보고 쿠키를 저장 이후 요청 시 자동으로 서버에 쿠키를 포함해서 보냄
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Refresh Token 재발급
     * 기존 Refresh Token 삭제
     */
    @Transactional
    public AuthLoginResponse reissue(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new CustomException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String refreshTokenValue = Arrays.stream(cookies)
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .orElseThrow(() -> new CustomException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND))
                .getValue();

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenValue)
                .orElseThrow(() -> new CustomException(ExceptionCode.REFRESH_TOKEN_INVALID));

        // 만료 체크
        if (refreshToken.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ExceptionCode.REFRESH_TOKEN_EXPIRED);
        }

        // 재사용 감지 (탈취)
        if (refreshToken.isUsed()) {
            refreshTokenRepository.deleteAllByUserId(refreshToken.getUserId());
            throw new CustomException(ExceptionCode.REFRESH_TOKEN_REUSED);
        }

        // 기존 토큰 사용 처리
        refreshToken.updateUsed();

        // 새 Refresh Token 발급
        String newRefreshTokenValue = jwtUtil.createRefreshToken();

        RefreshToken newRefreshToken = new RefreshToken(
                newRefreshTokenValue,
                refreshToken.getUserId(),
                LocalDateTime.now().plusDays(14) // 14일 동안 유지
        );
        refreshTokenRepository.save(newRefreshToken);

        // Access Token 재발급
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        String newAccessToken = jwtUtil.createToken(
                user.getId(),
                user.getNickname(),
                UserRole.valueOf(user.getRole())
        );

        // 쿠키 재설정
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshTokenValue)
                .httpOnly(true)
                .secure(false) // https = true
                .sameSite("Lax") // 개발 환경 = Lax
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .build();

        // 브라우저는 이 헤더를 보고 쿠키를 저장 이후 요청 시 자동으로 서버에 쿠키를 포함해서 보냄
        response.addHeader("Set-Cookie", cookie.toString());

        return new AuthLoginResponse(newAccessToken);
    }
}
