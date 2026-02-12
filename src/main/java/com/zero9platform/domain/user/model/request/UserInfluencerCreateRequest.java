package com.zero9platform.domain.user.model.request;

import com.zero9platform.common.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfluencerCreateRequest implements UserCreateCommonRequest {

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-z](?:[a-z0-9_]{2,}[a-z0-9])$", message = "아이디는 4자 이상이며, 영문 소문자로 시작하고 소문자/숫자/_ 만 사용할 수 있으며 '_'로 끝날 수 없습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*_+-]{8,}$", message = "비밀번호는 8자 이상이며 영문 대소문자, 숫자, 특수문자(!@#$%^&*_+-)만 사용할 수 있습니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(max = 20, message = "이름은 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[가-힣A-Za-z]{2,}$", message = "이름은 공백 없이 한글 또는 영문만 입력 가능하며 2자 이상이어야 합니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "휴대폰 번호는 필수 입력값입니다.")
    @Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{4}$", message = "휴대폰 번호는 010-0000-0000 형식으로 입력해주세요.")
    private String phone;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[가-힣A-Za-z]{2,}$", message = "닉네임은 공백 없이 한글 또는 영문만 사용 가능하며 2자 이상이어야 합니다.")
    private String nickname;

    @NotBlank(message = "소셜 링크는 필수 입력값입니다. (인플루언서 확인을 위한 정보입니다.)")
    private String influencerSocialLink;

    @Override
    public UserRole getRole() {
        return UserRole.INFLUENCER;
    }
}
