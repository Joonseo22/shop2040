package com.shop2040.shop.repository;

import com.shop2040.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 특정 장바구니의 아이템 다 찾아오기
    List<CartItem> findByCartId(Long cartId);

    // 이미 장바구니에 담겨있는 상품인지 확인하기 위함
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);
}