package com.shop2040.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 하나의 장바구니에는 여러 상품이 담김
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne // 하나의 상품은 여러 장바구니에 담길 수 있음
    @JoinColumn(name = "item_id")
    private Item item;

    private int count; // 담은 개수

    // 장바구니 아이템 생성 메서드
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    // 개수 증가 (이미 담긴 상품 또 담을 때)
    public void addCount(int count) {
        this.count += count;
    }
}