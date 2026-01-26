package com.zero9platform.domain.product.service;

import com.zero9platform.common.enums.ExceptionCode;
import com.zero9platform.common.enums.UserRole;
import com.zero9platform.common.exception.CustomException;
import com.zero9platform.common.model.PageResponse;
import com.zero9platform.domain.product.entity.Product;
import com.zero9platform.domain.product.model.request.ProductCreateRequest;
import com.zero9platform.domain.product.model.request.ProductUpdateRequest;
import com.zero9platform.domain.product.model.response.ProductCreateResponse;
import com.zero9platform.domain.product.model.response.ProductGetDetailResponse;
import com.zero9platform.domain.product.model.response.ProductUpdateResponse;
import com.zero9platform.domain.product.repository.ProductRepository;
import com.zero9platform.domain.user.entity.User;
import com.zero9platform.domain.user.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * 상품 생성
     */
    @Transactional
    public ProductCreateResponse productCreate(Long userId, ProductCreateRequest request) {

        // 사용자는 상품 생성 권한 없음
        validPermission(userId);

        // 상품 생성
        Product product = new Product(request.getName(), request.getDescription(), request.getProductPrice());

        productRepository.save(product);

        return ProductCreateResponse.from(product);
    }

    /**
     * 상품 상세 조회
     */
    @Transactional(readOnly = true)
    public ProductGetDetailResponse productGetDetail(Long userId, Long productId) {

        validPermission(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_NOT_FOUND));

        return ProductGetDetailResponse.from(product);
    }

    /**
     * 상품 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductGetDetailResponse> productGetList(Long userId, Pageable pageable) {

        validPermission(userId);

        Page<ProductGetDetailResponse> productPage = productRepository.findAll(pageable)
                        .map(ProductGetDetailResponse::from);

        return PageResponse.from(productPage);
    }

    /**
     * 상품 수정
     */
    @Transactional
    public ProductUpdateResponse productUpdate(Long userId, Long productId, ProductUpdateRequest request) {

        validPermission(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_NOT_FOUND));

        product.update(request.getName(), request.getDescription(), request.getProductPrice());

        productRepository.save(product);

        return ProductUpdateResponse.from(product);
    }

//    /**
//     * 상품 삭제
//     */
//    @Transactional
//    public void productDelete(Long userId, Long productId) {
//
//        validPermission(userId);
//
//        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
//                .orElseThrow(() -> new CustomException(ExceptionCode.PRODUCT_NOT_FOUND));
//
//        product.delete();
//    }

    /**
     * 상품 생성 권한 검증 - 사용자 x
     */
    private void validPermission(Long userId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        if (UserRole.valueOf(user.getRole()) == UserRole.USER) {
            throw new CustomException(ExceptionCode.NO_PERMISSION);
        }
    }
}