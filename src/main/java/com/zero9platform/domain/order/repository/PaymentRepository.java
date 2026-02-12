package com.zero9platform.domain.order.repository;

import com.zero9platform.domain.order.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findPaymentKeyByOrder_Id(Long orderId);
}
