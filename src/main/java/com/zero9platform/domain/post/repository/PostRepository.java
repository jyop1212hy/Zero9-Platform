package com.zero9platform.domain.post.repository;

import com.zero9platform.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndDeletedAtIsNull(Long id);

    Page<Post> findAllByDeletedAtIsNull(Pageable pageable);

    /**
     * 조회수 증가 - 삭제되지 않은 게시물만
     * DB에서 직접 증가, 영속성 컨텍스트를 거치지 않음
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Post p
           set p.viewCount = p.viewCount + 1
         where p.id = :postId
           and p.deletedAt is null
    """)
    int increaseViewCount(@Param("postId") Long postId);
}