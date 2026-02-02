package com.zero9platform.domain.user.repository;

import com.zero9platform.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    /**
     * 회원 목록 조회
     */
    @Query("""
        SELECT u
        FROM User u
        WHERE u.role IN ('USER', 'INFLUENCER') AND (:nickname IS NULL OR u.nickname LIKE CONCAT('%', :nickname, '%'))
        ORDER BY u.createdAt DESC
    """)
    Page<User> findAllUser(@Param("nickname") String nickname, Pageable pageable);

    /**
     * 로그인 확인
     */
    Optional<User> findByLoginId(String loginId);

    /**
     * 중복되는 아이디 조회
     */
    boolean existsByLoginId(String loginId);

    /**
     * 중복되는 이메일 조회
     */
    boolean existsByEmail(String email);

    /**
     * 중복되는 핸드폰
     */
    boolean existsByPhone(String phone);

    /**
     * 중복되는 닉네임
     */
    boolean existsByNickname(String nickname);

    /**
     * 나를 제외한 중복 이메일
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * 나를 제외한 중복 핸드폰
     */
    boolean existsByPhoneAndIdNot(String phone, Long id);

    /**
     * 나를 제외한 중복 닉네임
     */
    boolean existsByNicknameAndIdNot(String nickname, Long id);

    Optional<User> findByIdAndDeletedAtIsNull(Long userId);

}
