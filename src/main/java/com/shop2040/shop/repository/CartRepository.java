package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // 회원 ID로 장바구니 찾기
    Cart findByMemberId(Long memberId);
}