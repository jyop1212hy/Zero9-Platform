package com.zero9platform.common.util;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.exception.CustomException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SearchProfanityFilter {

    private final Set<String> badWords = new HashSet<>();
    private final String FILE_PATH = "/src/main/resources/bad-words.txt";

    /**
     * 비속어 단어 업로드
     */
    @PostConstruct
    public void init() {
        refresh();
    }

    /**
     * 비속어 단어 재업로드 (동기화)
     */
    public synchronized void refresh() {
        try {
            // 기존 메모리 데이터 초기화
            badWords.clear();

            ClassPathResource resource = new ClassPathResource(FILE_PATH);

            if (resource.exists()) {

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                    // 파일 읽기 -> 공백 제거 -> 빈 줄 제외 -> HashSet에 저장
                    badWords.addAll(reader.lines()
                            .map(String::trim) // 앞뒤 공백 제거
                            .filter(line -> !line.isEmpty())  // 빈 라인 제외
                            .collect(Collectors.toSet()));
                }
            }
            log.info("비속어 사전 동기화 완료: {}개 단어 로드됨", badWords.size());
        } catch (IOException e) {
            log.error("비속어 파일 읽기 실패: {}", e.getMessage());

            throw new CustomException(ExceptionCode.SEARCH_LOGS_PROFANITY_FILE_IO_ERROR);
        }
    }

    /**
     * 단어 추가 + 파일 저장
     */
    public synchronized void addWord(String word) {

        String trimmedWord = word.trim();

        if (trimmedWord.isEmpty()) {
            return;
        }

        // 중복 체크
        if (badWords.contains(trimmedWord)) {
            log.warn("이미 존재하는 단어 추가 시도: {}", trimmedWord);

            throw new CustomException(ExceptionCode.SEARCH_LOGS_PROFANITY_ALREADY_EXISTS, trimmedWord);
        }

        badWords.add(trimmedWord);

        try {
            Files.writeString(Paths.get(FILE_PATH),
                System.lineSeparator() + trimmedWord,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);

            log.info("단어 추가 및 저장 완료: {}", trimmedWord);
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.SEARCH_LOGS_PROFANITY_FILE_IO_ERROR);
        }
    }

    /**
     * 단어 삭제 + 파일 갱신
     */
    public synchronized void removeWord(String word) {

        String trimmedWord = word.trim();

        // 중복 체크
        if (!badWords.contains(trimmedWord)) {
            log.warn("존재하지 않는 단어 삭제 시도: {}", trimmedWord);

            throw new CustomException(ExceptionCode.SEARCH_LOGS_PROFANITY_NOT_FOUND, trimmedWord);
        }

        // 삭제 및 파일 갱신
        if (badWords.remove(word.trim())) {
            try {
                Files.write(Paths.get(FILE_PATH), badWords, StandardCharsets.UTF_8);

                log.info("단어 삭제 및 파일 갱신 완료: {}", word);
            } catch (IOException e) {
                throw new CustomException(ExceptionCode.SEARCH_LOGS_PROFANITY_FILE_IO_ERROR);
            }
        }
    }

    /**
     * 정규식으로 비속어 방어
     */
    public boolean isBadWord(String text) {

        if (text == null || text.isBlank()) {
            return false;
        }

        // 공백 및 특수문자 제거 (변조된 비속어 방어)
        String cleanText = text.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]", "");

        // 포함 여부 검사
        for (String badWord : badWords) {
            if (cleanText.contains(badWord)) {
                return true;
            }
        }

        return false;
    }
}