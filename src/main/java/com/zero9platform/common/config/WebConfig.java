package com.zero9platform.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins("http://127.0.0.1:5500",  // 허용할 프론트엔드 도메인
                                "http://localhost:63342") // SSE 알림 도메인 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용 http 메서드
                .allowedHeaders("*") // 허용할 헤더
                .allowCredentials(true); // 이 서버는 쿠키/Authorization, 인증 정보가 포함된 요청을 허용한다.
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저가 요청하는 그 긴 경로(/Zero9-Platform/...)를
        // 서버의 실제 static 폴더(classpath:/static/)로 매핑해줍니다.
        registry.addResourceHandler("/Zero9-Platform/Zero9-Platform.main/static/**")
                .addResourceLocations("classpath:/static/");

        // 혹시 모르니 기본 경로도 한 번 더 잡아줍니다.
        registry.addResourceHandler("/img/**").addResourceLocations("classpath:/static/img/");
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}