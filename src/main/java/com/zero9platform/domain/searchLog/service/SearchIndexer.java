package com.zero9platform.domain.searchLog.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post.repository.ProductPostRepository;
import com.zero9platform.domain.searchLog.elasticsearch.SearchDocument;
import com.zero9platform.domain.searchLog.repository.SearchDocumentRepository;
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
    private final SearchDocumentRepository searchDocumentRepository;

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

            List<SearchDocument> productDocs = slice.getContent().stream()
                    .filter(product -> product.getUser().getDeletedAt() == null)
                    .filter(product -> product.getId() != null)
                    .map(product -> SearchDocument.builder()
                            .id("PRODUCT_POST_" + product.getId())
                            .postType("PRODUCT_POST")
                            .title(product.getTitle())
                            .content(product.getContent())
                            .nickname(product.getUser().getNickname())
                            .price(product.getOriginalPrice())
                            .image(product.getImage())
                            .startDate(product.getStartDate())
                            .endDate(product.getEndDate())
                            .userId(product.getUser().getId())
                            .build())
                    .toList();

            saveDocs(productDocs, "ProductPost", productPage++);
        }

        // GroupPurchasePost 페이징 처리
        int gppPage = 0;
        long totalGpps = 0;

        while (true) {

            Page<GroupPurchasePost> slice = (modifiedAfter == null) ? groupPurchasePostRepository.findAll(PageRequest.of(gppPage, pageSize)) : groupPurchasePostRepository.findAllByUpdatedAtAfter(modifiedAfter, PageRequest.of(gppPage, pageSize));

            if (slice.isEmpty()) {
                break;
            }

            List<SearchDocument> gppDocs = slice.getContent().stream()
                    .filter(gpp -> gpp.getUser() != null && gpp.getUser().getDeletedAt() == null)
                    .filter(gpp -> gpp.getId() != null && gpp.getDeletedAt() == null)
                    .map(gpp -> SearchDocument.builder()
                            .id("GROUP_PURCHASE_POST_" + gpp.getId())
                            .postType("GROUP_PURCHASE_POST")
                            .title(gpp.getProductName())
                            .content(gpp.getContent())
                            .nickname(gpp.getUser().getNickname())
                            .price(gpp.getPrice())
                            .image(gpp.getImage())
                            .startDate(gpp.getStartDate())
                            .endDate(gpp.getEndDate())
                            .userId(gpp.getUser().getId())
                            .build())
                    .toList();

            saveDocs(gppDocs, "GroupPurchasePost", gppPage++);
        }

        log.info("[Bulk Indexing] 완료! (총합: {} 건)", (totalProducts + totalGpps));
    }

    /**
     * 인덱싱 결과 저장
     */
    private void saveDocs(List<SearchDocument> docs, String type, int page) {
        try {

            if (!docs.isEmpty()) {
                searchDocumentRepository.saveAll(docs);

                log.info("[{}] {} 건 인덱싱 중... (Page: {})", type, docs.size(), page);
            }
        } catch (Exception e) {
            log.error("[{}] {} {}번 페이지 저장 실패: {}", type.equals("ProductPost") ? ExceptionCode.SEARCH_LOGS_BULK_INDEXING_PRODUCT_FAILED.name() : ExceptionCode.SEARCH_LOGS_BULK_INDEXING_GPP_FAILED.name(), type, page, e.getMessage());
        }
    }
}