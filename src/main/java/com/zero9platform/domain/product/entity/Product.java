package com.zero9platform.domain.product.entity;

import com.zero9platform.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private Long productPrice; // 정가

    @Column
    private LocalDateTime deletedAt;

    public Product(String name, String description, Long productPrice) {
        this.name = name;
        this.description = description;
        this.productPrice = productPrice;
    }

    public void update(String name, String description, Long price) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (price != null) this.productPrice = price;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}