package com.zero9platform.domain.searchLog.service;

import com.zero9platform.domain.searchLog.elasticsearch.ProductDocument;
import com.zero9platform.domain.searchLog.model.event.SearchEvent;
import com.zero9platform.domain.searchLog.repository.ProductPostSearchRepository;
//import com.zero9platform.domain.searchLog.repository.SearchLogElasticsearchRepository;
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

//    private final SearchLogElasticsearchRepository searchLogElasticsearchRepository;
    private final ProductPostSearchRepository productPostSearchRepository;

    /**
     * 엘라스틱서치 비동기 이벤트 리스너
     * 상품(PRODUCT) 및 공구(GPP) 마스터 인덱스 동기화 전용
     */
    @Async("SEARCH_LOG")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // DB 저장 성공 시에만 실행
    public void searchEventHandle(SearchEvent event) {

        try {
            // 1. 삭제 이벤트 처리
            if (event.isDeleted()) {

//                searchLogElasticsearchRepository.deleteById(event.getId());
//                log.info("[ES Sync] 문서 삭제 완료: {}", event.getId());
                productPostSearchRepository.deleteById(event.getId());
                log.info("[Product Master Sync] 문서 삭제 완료: {}", event.getId());
                return;
            }

//            SearchDocument doc = SearchDocument.builder()
//                    .id(event.getId())
//                    .postType(event.getPostType())
//                    .title(event.getTitle())
//                    .content(event.getContent())
//                    .nickname(event.getNickname())
//                    .price(event.getPrice())
//                    .image(event.getImage())
//                    .startDate(event.getStartDate())
//                    .endDate(event.getEndDate())
//                    .userId(event.getUserId())
//                    .build();

            // 2. 통합 창고용 ProductDocument 생성
            // SearchEvent에서 받아온 데이터를 ProductDocument로 옮겨 담습니다.
            ProductDocument productDoc = ProductDocument.builder()
                    .id(event.getId())
                    .postType(event.getPostType())
                    .title(event.getTitle())
                    .keyword(event.getTitle()) // 자동완성 핵심: 제목을 키워드 필드에 저장
                    .price(event.getPrice())
                    .endDate(event.getEndDate())
                    .build();

//            searchLogElasticsearchRepository.save(doc);

            // 3. 통합 인덱스(product_master_V1)에 저장
            productPostSearchRepository.save(productDoc);

//            log.info("[DB Sync] 문서 저장 완료: {}", event.getId());
//        } catch (Exception e) {
//            log.error("[DB Sync] 동기화 중 에러 발생 - ID: {}", event.getId(), e);
//        }

            log.info("[Product Master Sync] 통합 인덱스 저장 완료: ID = {}, Type = {}", event.getId(), event.getPostType());
        } catch (Exception e) {
            log.error("[Product Master Sync] 동기화 중 에러 발생 - ID: {}", event.getId(), e);
        }
    }
}