package com.zero9platform.domain.product_post_option.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface ProductPostOptionRepository  extends JpaRepository<ProductPostOption, Long> {

    Optional<ProductPostOption> findByIdAndProductPost_IdAndStockStatus(Long optionId, Long productPostId, String inStock);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM ProductPostOption o WHERE o.id = :optionId")
    Optional<ProductPostOption> findByIdWithLock(@Param("optionId") Long optionId);
}