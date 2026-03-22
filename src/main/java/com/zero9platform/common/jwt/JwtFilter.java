package com.zero9platform.common.jwt;

import com.zero9platform.common.enums.UserRole;
import com.zero9platform.domain.auth.model.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j(topic = "jwtfilter")
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // [Step 1] 무조건 통과해야 하는 것들 (화면 파일, 정적 리소스)
        if (uri.contains("/Zero9-Platform/") ||
                uri.contains("/static/") ||
                uri.endsWith(".html") ||
                uri.endsWith(".js") ||
                uri.endsWith(".css") ||
                uri.endsWith(".png") ||
                uri.endsWith(".jpg") ||
                uri.equals("/favicon.ico") ||
                uri.equals("/error")) {
            log.info("[JWT] 정적 리소스/화면 통과: uri={}, skip=true", uri);
            return true;
        }

        // [Step 2] API 중에서도 로그인이 필요 없는 '공개' API들
        boolean isPublic = uri.contains("/zero9/auth/") ||           // 인증/로그인/회원가입
                uri.contains("/zero9/product-posts") ||    // 상품 목록
                uri.contains("/zero9/feeds/") ||           // 피드
                uri.contains("/zero9/ranking/") ||         // 랭킹
                uri.contains("/zero9/search-logs/") ||     // 검색어
                uri.startsWith("/oauth2/") ||
                uri.startsWith("/login/oauth2/") ||
                uri.equals("/login/") ||
                uri.startsWith("/zero9/auth/oauth/callback");

            if (isPublic) {
                log.info("[JWT] 인증/공개 API 통과: uri={}, skip=true", uri);
                return true;
            }


//        boolean skip = uri.startsWith("/oauth2/")
//                || uri.startsWith("/login/oauth2/")
//                || uri.equals("/login")
//                || uri.startsWith("/login/")
//                || uri.startsWith("/zero9/auth/oauth/callback");

        // 3. [보안 API] 위에서 안 걸러진 나머지 모든 데이터 요청(/zero9/...)은 JWT 검사 진행!
        if (uri.contains("/zero9/")) {
            log.info("[JWT] 보안 API 검사 진행: uri={}, skip=false", uri);
            return false;
        }

        // 4. 그 외 (favicon 등)
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("[JWT] doFilterInternal uri={}", request.getRequestURI());

        String bearerJwt = request.getHeader("Authorization");

        if (bearerJwt == null || bearerJwt.isBlank()) {
            // 토큰이 없는 경우 시큐리티에게 위임
            filterChain.doFilter(request, response);

            return;
        }

        String jwt = jwtUtil.substringToken(bearerJwt);

        try {
            // JWT 유효성 검사와 claims 추출
            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims == null) {
                filterChain.doFilter(request, response);

                return;
            }

            String subject = claims.getSubject();

            if (subject == null) {
                throw new BadCredentialsException("JWT에 userId(sub)가 없습니다.");
            }

            Long userId = Long.parseLong(subject);
            String nickname = (String) claims.get("nickname");
            UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

            AuthUser authUser = new AuthUser(userId, nickname, userRole);

            Authentication authentication = new UserIdAuthenticationToken(authUser);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);

            throw new BadCredentialsException("유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);

            throw new CredentialsExpiredException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);

            throw new BadCredentialsException("지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("Internal server error", e);

            throw new AuthenticationServiceException("인증 처리 중 서버 오류가 발생했습니다.", e);
        }
    }
}