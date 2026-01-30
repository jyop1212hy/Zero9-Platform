package com.zero9platform.domain.product_post_option.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductPostOptionRepository  extends JpaRepository<ProductPostOption, Long> {
//    Optional<ProductPostOption> findByIdAndProductPost_Id(Long optionId, Long productPostId);

    Optional<ProductPostOption> findByIdAndProductPost_IdAndStockStatus(Long optionId, Long productPostId, String inStock);

//    Page<ProductPostOption> findAllByProductPost_IdAndOptionStatusOrderByCapacityAsc(Long productPostId, String active, Pageable pageable);
}