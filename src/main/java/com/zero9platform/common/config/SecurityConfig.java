package com.zero9platform.common.config;

import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.jwt.JwtAccessDeniedHandler;
import com.zero9platform.common.jwt.JwtAuthenticationEntryPoint;
import com.zero9platform.common.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler) throws Exception {

        return httpSecurity
                // CORS 활성화
                .cors(Customizer.withDefaults())

                // CSRF, BASIC, FORM 로그인 비활성화 (JWT 사용)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // JWT 인증 필터 등록
                .addFilterBefore(jwtFilter, SecurityContextHolderAwareRequestFilter.class)

                // 세션 설정: STATELESS (JWT 기반 인증)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Security 예외 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패 (401)
                        .accessDeniedHandler(jwtAccessDeniedHandler) // 인가 실패 (403)
                )

                // 인가(Authorization) 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll() // CORS Preflight 허용
                        .requestMatchers(
                                "/", "/index.html", "/main.html",
                                "/**/*.css", "/**/*.js",
                                "/**/*.png", "/**/*.jpg", "/**/*.jpeg",
                                "/**/*.svg", "/**/*.ico"
                        ).permitAll()
                        .requestMatchers(
                                "/img/**",
                                "/goods/**",
                                "/mypage/**",
                                "/payment/**",
                                "/joinpage/**",
                                "/frontend/**",
                                "/auth/**"
                        ).permitAll()
                        .requestMatchers(
                                "/zero9/auth/**",
                                "/zero9/test/**"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/zero9/users/normal",
                                "/zero9/users/influencer"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/zero9/options/*",
                                "/zero9/product-posts",
                                "/zero9/product-posts/*",
                                "/zero9/gp-posts/**",
                                "/zero9/posts/**",
                                "/zero9/search-logs/**",
                                "/zero9/gpp-comments/**",
                                "/zero9/comments",
                                "/zero9/influencers/*/follows",
                                "/zero9/feeds/all",
                                "/zero9/ranking/**"
                        ).permitAll()
                        .requestMatchers("/zero9/admin/**").hasRole(UserRole.ADMIN.name())
                        .anyRequest().authenticated() // 그 외 요청은 인증 필수
                )
                .build();
    }
}