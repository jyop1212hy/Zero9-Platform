package com.zero9platform.domain.searchLog;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class validateProfanity {

    // 비속어 검증
    private static final List<String> FORBIDDEN_WORDS =
            List.of("개새끼", "시발", "병신", "ㅅㅂ", "fuck", "shit");

    // 검색어 null 또는 공백 입력 방어
    public void validate(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new CustomException(ExceptionCode.INVALID_KEYWORD);
        }

        //검색어에 비속어가 포함되어 있는지 검사
        FORBIDDEN_WORDS.stream()
                .filter(keyword::contains)
                .findAny()
                .ifPresent(word -> {
                    throw new CustomException(ExceptionCode.INVALID_KEYWORD);
                });
    }
}
