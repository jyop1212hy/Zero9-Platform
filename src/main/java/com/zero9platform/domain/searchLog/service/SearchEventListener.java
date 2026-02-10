package com.zero9platform.domain.searchLog.service;

import com.zero9platform.domain.searchLog.elasticsearch.SearchDocument;
import com.zero9platform.domain.searchLog.model.event.SearchEvent;
import com.zero9platform.domain.searchLog.repository.SearchDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Log4j2
@Component
@RequiredArgsConstructor
public class SearchEventListener {

    private final SearchDocumentRepository searchDocumentRepository;

    /**
     * 엘라스틱서치 비동기 이벤트 리스너
     */
    @Async("SEARCH_LOG")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // DB 저장 성공 시에만 실행
    public void searchEventHandle(SearchEvent event){

        try {
            if (event.isDeleted()) {
                searchDocumentRepository.deleteById(event.getId());
                log.info("[ES Sync] 문서 삭제 완료: {}", event.getId());
                return;
            }
            SearchDocument doc = SearchDocument.builder()
                    .id(event.getId())
                    .postType(event.getPostType())
                    .title(event.getTitle())
                    .content(event.getContent())
                    .nickname(event.getNickname())
                    .price(event.getPrice())
                    .image(event.getImage())
                    .startDate(event.getStartDate())
                    .endDate(event.getEndDate())
                    .userId(event.getUserId())
                    .build();
            searchDocumentRepository.save(doc);

            log.info("[DB Sync] 문서 저장 완료: {}", event.getId());
        } catch (Exception e) {
            log.error("[DB Sync] 동기화 중 에러 발생 - ID: {}", event.getId(), e);
        }
    }
}
