package com.zero9platform.common.jwt;

import com.zero9platform.domain.auth.model.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UserIdAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthUser principal;

    public UserIdAuthenticationToken(AuthUser principal) {
        super(principal.getAuthorities());
        this.principal = principal;
        setAuthenticated(true); // JWT 검증 완료 상태
    }

    @Override
    public Object getCredentials() {
        return null; // 비밀번호 없음 (JWT)
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}