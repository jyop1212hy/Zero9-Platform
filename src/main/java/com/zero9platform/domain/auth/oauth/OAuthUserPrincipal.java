package com.zero9platform.domain.auth.oauth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * OAuth 로그인 사용자(Principal)를 우리 서비스 기준으로 표현하는 객체
 * OAuth2User는 "소셜에서 받은 사용자 정보(attributes)"와 "권한(authorities)"을 SecurityContext에 저장하는 인터페이스
 * 기본 OAuth2User에는 우리 DB의 userId가 없기 때문에,
 * 카카오 로그인 성공 후 upsert된 User의 PK(userId)와 role을 담아서
 * SuccessHandler에서 JWT 발급/권한 처리를 쉽게 하기 위해 사용
 */
@Getter
@RequiredArgsConstructor
public class OAuthUserPrincipal implements OAuth2User {

    private final Long userId;
    private final String role;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    // OAuth 원본 attributes 반환
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 권한 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Spring Security principal 식별값(여기선 userId)
    @Override
    public String getName() {
        return String.valueOf(userId);
    }

}
