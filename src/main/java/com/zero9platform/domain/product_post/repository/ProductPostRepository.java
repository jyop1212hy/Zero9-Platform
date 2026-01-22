package com.zero9platform.domain.product_post.repository;

import com.zero9platform.domain.product_post.entity.ProductPost;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductPostRepository  extends JpaRepository<ProductPost, Long> {
    Optional<ProductPost> findByIdAndDeletedAtIsNull(Long id);
}
