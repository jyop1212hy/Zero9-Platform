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
    private final String FILE_PATH = System.getProperty("user.dir") + "/bad-words.txt";

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
            badWords.clear();
            Path path = Paths.get(FILE_PATH);

            if (Files.exists(path)) {
                // 1. 서버 폴더에 파일이 있으면 (최신 데이터) 로드
                badWords.addAll(Files.readAllLines(path, StandardCharsets.UTF_8));
            } else {
                // 2. 서버 폴더에 파일이 없으면 (최초 실행) JAR 내부 리소스 로드
                ClassPathResource resource = new ClassPathResource("bad-words.txt");
                if (resource.exists()) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                        Set<String> initialWords = reader.lines().map(String::trim).collect(Collectors.toSet());
                        badWords.addAll(initialWords);

                        // 3. 로드한 데이터를 외부 파일로 복사 (이후부터는 이 파일을 수정함)
                        Files.write(path, initialWords, StandardCharsets.UTF_8);
                    }
                }
            }
            log.info("비속어 사전 동기화 완료: {}개", badWords.size());
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