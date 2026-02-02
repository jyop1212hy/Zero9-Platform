package com.zero9platform.domain.user.Service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.aws.s3.S3Service;
import com.zero9platform.domain.auth.refresh_token.RefreshTokenRepository;
import com.zero9platform.domain.user.entity.Influencer;
import com.zero9platform.domain.user.repository.InfluencerRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.model.request.*;
import com.zero9platform.domain.user.model.response.*;
import com.zero9platform.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final InfluencerRepository influencerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Service s3Service;

    private static final String ADMIN_EN = "admin";
    private static final String ADMIN_KR = "관리자";

    /**
     * 회원가입
     */
    @Transactional
    public UserCreateResponse createUser(UserCreateCommonRequest request) {

        // 관리자 관련데이터 검증
        validateNotAdmin(request);

        // 중복검사 아이디
        checkDuplicate(userRepository.existsByLoginId(request.getLoginId()), ExceptionCode.LOGINID_EXIST);

        // 중복검사 이메일
        checkDuplicate(userRepository.existsByEmail(request.getEmail()), ExceptionCode.EMAIL_EXIST);

        // 중복검사 핸드폰
        checkDuplicate(userRepository.existsByPhone(request.getPhone()), ExceptionCode.PHONE_EXIST);

        // 중복검사 닉네임
        checkDuplicate(userRepository.existsByNickname(request.getNickname()), ExceptionCode.NICKNAME_EXIST);

        User user = new User(request.getLoginId(), passwordEncoder.encode(request.getPassword()), request.getEmail(), request.getName(), request.getRole().name(), request.getPhone(), request.getNickname());

        User userCreated = userRepository.save(user);

        if (request.getRole() == UserRole.USER) {
            return UserCreateResponse.from(userCreated);
        }

        // UserInfluencerCreateRequest형태로 다운 캐스팅
        UserInfluencerCreateRequest influencerRequest = (UserInfluencerCreateRequest) request;

        // 인플루언서 승인 상태 저장
        influencerRepository.save(new Influencer(userCreated, influencerRequest.getInfluencerSocialLink()));

        return UserInfluencerCreateResponse.from(userCreated, influencerRequest.getInfluencerSocialLink());
    }

    /**
     * 사용자 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserDetailResponse userDetail(Long userId, boolean isAdmin, boolean isMy) {

        User user = findById(userId);

        // 관리자 프로필 보호 조건
        if (!isAdmin) {
            if (UserRole.valueOf(user.getRole()) == UserRole.ADMIN) {
                throw new CustomException(ExceptionCode.NO_PERMISSION);
            }
        }

        // 승인되지 않은 인플루언서 예외 처리
        boolean notProvedInfluencer = influencerRepository.existsByUserIdAndInfluencerApprovalStatusFalse(userId);

        if (notProvedInfluencer) {
            throw new CustomException(ExceptionCode.INFLUENCER_NOT_APPROVED);
        }

        // 자기 자신을 조회할때의 데이터는 다르게 나옴
        return isMy ? UserMyDetailResponse.from(user, user.getPhone(), user.getEmail()) : UserDetailResponse.from(user);
    }

    /**
     * 사용자 프로필 수정
     */
    @Transactional
    public UserUpdateResponse userUpdate(Long userId, UserUpdateRequest request, MultipartFile profileImage) {

        User user = findById(userId);

        // 나를 제외한 중복되는 이메일 조회
        checkDuplicate(userRepository.existsByEmailAndIdNot(request.getEmail(), userId), ExceptionCode.EMAIL_EXIST);

        // 나를 제외한 중복되는 닉네임 조회
        checkDuplicate(userRepository.existsByNicknameAndIdNot(request.getNickname(), userId), ExceptionCode.NICKNAME_EXIST);

        // 나를 제외한 중복되는 핸드폰번호 조회
        checkDuplicate(userRepository.existsByPhoneAndIdNot(request.getPhone(), userId), ExceptionCode.PHONE_EXIST);

        String profileImageUrl = profileImage != null && !profileImage.isEmpty() ? s3Service.upload(profileImage) : "";

        user.userUpdate(request.getEmail(), request.getNickname(), request.getPhone(), profileImageUrl);

        return UserUpdateResponse.from(user);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void userDelete(Long userId, UserDeleteRequest request) {

        User user = findById(userId);

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new CustomException(ExceptionCode.PASSWORD_NOT_MATCH);
        }

        user.userDelete();
    }

    /**
     * 공통 검증 메서드
     */
    private void checkDuplicate(boolean exists, ExceptionCode code) {

        if (exists) {
            throw new CustomException(code);
        }
    }

    /**
     * 회원 조회 메서드
     */
    private User findById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        return user;
    }

    /**
     * 관리자 데이터 검증 메서드
     */
    private void validateNotAdmin(UserCreateCommonRequest request) {

        if (ADMIN_EN.equals(request.getLoginId()) || ADMIN_KR.equals(request.getLoginId()) || ADMIN_KR.equals(request.getNickname())) {
            throw new CustomException(ExceptionCode.ADMIN_DATA_NOT_ALLOWED);
        }
    }
}
