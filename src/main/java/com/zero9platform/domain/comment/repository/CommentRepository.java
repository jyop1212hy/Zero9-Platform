package com.zero9platform.domain.comment.repository;

import com.zero9platform.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    @Query("""
        select count(*) from GppComment gc join GroupPurchasePost gpp on gc.groupPurchasePost.id = gpp.id
        where gpp.id = :gppId
    """)
    Long countByGppTotalComments(@Param("gppId") Long gppId);
}
