package com.zero9platform.domain.auth.oauth;

import com.zero9platform.common.enums.AuthProvider;
import com.zero9platform.domain.user.Service.UserService;
import com.zero9platform.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"kakao".equals(registrationId)) {
            throw new OAuth2AuthenticationException("Unsupported OAuth provider: " + registrationId);
        }

        Object idObj = attributes.get("id");
        if (idObj == null) {
            throw new OAuth2AuthenticationException("Kakao response missing 'id'");
        }
        String providerId = String.valueOf(idObj);

        Map<String, Object> properties = null;
        Object propsObj = attributes.get("properties");
        if (propsObj instanceof Map) {
            properties = (Map<String, Object>) propsObj;
        }

        String nickname = properties != null ? (String) properties.get("nickname") : null;
        String profileImage = properties != null ? (String) properties.get("profile_image") : null;

        // DB upsert (provider + providerId)
        User user = userService.upsertOAuthUser(AuthProvider.KAKAO, providerId, nickname, profileImage);

        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return new OAuthUserPrincipal(user.getId(), user.getRole(), attributes, authorities);
    }
}
