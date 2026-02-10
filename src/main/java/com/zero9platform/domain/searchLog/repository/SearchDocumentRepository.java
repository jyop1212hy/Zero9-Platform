package com.zero9platform.domain.searchLog.repository;

import com.zero9platform.domain.searchLog.elasticsearch.SearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchDocumentRepository extends ElasticsearchRepository<SearchDocument, String> {
}
