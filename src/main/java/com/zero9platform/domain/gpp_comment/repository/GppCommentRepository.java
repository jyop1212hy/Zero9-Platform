package com.zero9platform.domain.gpp_comment.repository;

import com.zero9platform.domain.gpp_comment.entity.GppComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GppCommentRepository extends JpaRepository<GppComment, Long> {

    /**
     * gppId에 해당하는 모든 공동구매 게시물의 댓글을 조회
     */
    Page<GppComment> findAllByGroupPurchasePost_Id(Long id, Pageable pageable);
}
