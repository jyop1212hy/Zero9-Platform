//package com.zero9platform.domain.searchLog.repository;
//
//import com.zero9platform.domain.searchLog.elasticsearch.SearchDocument;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//
//import java.util.List;
//
//public interface SearchLogElasticsearchRepository extends ElasticsearchRepository<SearchDocument, String> {
//
//    // keyword 필드에서 검색어(name)와 일치하는 것들을 찾아서 리스트로 반환
//    List<SearchDocument> findByKeywordStartingWithOrderByCreatedAtDesc(String name);
//
//    // 유저별 최근 검색어 조회용
//    List<SearchDocument> findByUserIdAndKeywordStartingWith(Long userId, String keyword);
//}
