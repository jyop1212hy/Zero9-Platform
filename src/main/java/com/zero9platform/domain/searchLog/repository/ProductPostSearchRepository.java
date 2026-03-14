package com.zero9platform.domain.searchLog.repository;

import com.zero9platform.domain.searchLog.elasticsearch.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductPostSearchRepository extends ElasticsearchRepository<ProductDocument, String> {

    // 자동완성 쿼리 (keyword 필드에 ngram 적용되어 있음)
    List<ProductDocument> findByKeywordStartingWith(String keyword);
}
