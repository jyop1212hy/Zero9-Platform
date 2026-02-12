package com.zero9platform.domain.auth.model;

import com.zero9platform.common.enums.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class AuthUser {

    private final Long id;
    private final String nickname;
    private final UserRole userRole;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
    }
}