package com.zero9platform.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.model.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        ExceptionCode code = ExceptionCode.AUTH_LOGIN_REQUIRED;

        response.setStatus(code.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        CommonResponse<Void> body = CommonResponse.exception(code.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}