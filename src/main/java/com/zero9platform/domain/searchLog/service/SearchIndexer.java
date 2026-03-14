package com.zero9platform.domain.searchLog.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.searchLog.elasticsearch.ProductDocument;
import com.zero9platform.domain.searchLog.repository.ProductPostSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchIndexer {

    private final ProductPostRepository productPostRepository;
    private final GroupPurchasePostRepository groupPurchasePostRepository;
    private final ProductPostSearchRepository productPostSearchRepository;

    /**
     * [수동 실행용] DB의 모든 데이터를 ES로 전송 (Full Indexing)
     */
    @Async("SEARCH_LOG")
    @Transactional(readOnly = true)
    public void bulkIndexingAll() {

        log.info("[Bulk Indexing - FULL] 전체 인덱싱 시작");

        performIndexing(null); // 날짜 제한 없이 전체 조회

        log.info("[Bulk Indexing - FULL] 전체 인덱싱 완료");
    }

    /**
     * [스케줄러용] 최근 24시간 내 변경된 데이터만 전송 (Incremental Indexing)
     */
    @Async("SEARCH_LOG")
    @Transactional(readOnly = true)
    public void bulkIndexingIncremental() {

        LocalDateTime targetTime = LocalDateTime.now().minusDays(1);

        log.info("[Bulk Indexing - INCREMENTAL] {} 이후 변경분 인덱싱 시작", targetTime);

        performIndexing(targetTime);

        log.info("[Bulk Indexing - INCREMENTAL] 변경분 인덱싱 완료");
    }


    /**
     * 공통 역 벌크 인덱싱 (중복 제거)
     */
    @Transactional(readOnly = true)
    public void performIndexing(LocalDateTime modifiedAfter) {

        log.info("[Bulk Indexing] 시작");

        int pageSize = 1000;

        // ProductPost 인덱싱 처리
        int productPage = 0;
        long totalProducts = 0;

        while (true) {

            Page<ProductPost> slice = (modifiedAfter == null) ? productPostRepository.findAll(PageRequest.of(productPage, pageSize)) : productPostRepository.findAllByUpdatedAtAfter(modifiedAfter, PageRequest.of(productPage, pageSize));

            if (slice.isEmpty()) {
                break;
            }

            // ProductDocument.from() 사용
            List<ProductDocument> productDocs = slice.getContent().stream()
                    .filter(product -> product.getUser().getDeletedAt() == null)
                    .map(product -> ProductDocument.from(product)) // 변환기 호출
                    .toList();

            saveDocs(productDocs, "ProductPost", productPage++);
            totalProducts += productDocs.size();
        }


        // GroupPurchasePost 페이징 처리
        int gppPage = 0;
        long totalGpps = 0;

        while (true) {

            Page<GroupPurchasePost> slice = (modifiedAfter == null) ? groupPurchasePostRepository.findAll(PageRequest.of(gppPage, pageSize)) : groupPurchasePostRepository.findAllByUpdatedAtAfter(modifiedAfter, PageRequest.of(gppPage, pageSize));

            if (slice.isEmpty()) {
                break;
            }

            List<ProductDocument> gppDocs = slice.getContent().stream()
                    .filter(gpp -> gpp.getUser() != null && gpp.getUser().getDeletedAt() == null)
                    .map(ProductDocument::from) // 변환기 호출
                    .toList();

            saveDocs(gppDocs, "GroupPurchasePost", gppPage++);
            totalGpps += gppDocs.size();
        }

        log.info("[Bulk Indexing] 완료! (총합: {} 건)", (totalProducts + totalGpps));
    }

    /**
     * 인덱싱 결과 저장
     */
    private void saveDocs(List<ProductDocument> docs, String type, int page) {
        try {

            if (!docs.isEmpty()) {
                productPostSearchRepository.saveAll(docs);

                log.info("[{}] {} 건 인덱싱 중... (Page: {})", type, docs.size(), page);
            }
        } catch (Exception e) {
            log.error("[{}] {} {}번 페이지 저장 실패: {}", type.equals("ProductPost") ? ExceptionCode.SEARCH_LOGS_BULK_INDEXING_PRODUCT_FAILED.name() : ExceptionCode.SEARCH_LOGS_BULK_INDEXING_GPP_FAILED.name(), type, page, e.getMessage());
        }
    }
}