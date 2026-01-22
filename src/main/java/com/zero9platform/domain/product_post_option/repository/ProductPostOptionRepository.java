package com.zero9platform.domain.product_post_option.repository;

import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductPostOptionRepository  extends JpaRepository<ProductPostOption, Long> {
    Optional<ProductPostOption> findByIdAndProductPost_Id(Long optionId, Long productPostId);

    Page<ProductPostOption> findAllByProductPost_Id(Long productPostId, Pageable pageable);
}