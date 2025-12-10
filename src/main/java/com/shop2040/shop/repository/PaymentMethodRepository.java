package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Member;
import com.shop2040.shop.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByMember(Member member);
}