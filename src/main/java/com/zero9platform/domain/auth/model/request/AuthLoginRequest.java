package com.zero9platform.domain.auth.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginRequest {

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-z](?:[a-z0-9_]{2,}[a-z0-9])$", message = "아이디는 4자 이상이며, 영문 소문자로 시작하고 소문자/숫자/_ 만 사용할 수 있으며 '_'로 끝날 수 없습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*_+-]{8,}$", message = "비밀번호는 8자 이상이며 영문 대소문자, 숫자, 특수문자(!@#$%^&*_+-)만 사용할 수 있습니다.")
    private String password;
}