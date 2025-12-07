package com.shop2040.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne // 회원 1명당 장바구니 1개
    @JoinColumn(name = "member_id")
    private Member member;

    // 장바구니 안에 몇 개가 담겼는지 숫자를 셀 변수 (편의상 추가)
    private int count;

    // 회원을 받아서 장바구니를 만드는 편의 메서드
    public static Cart createCart(Member member) {
        Cart cart = new Cart();
        cart.setMember(member);
        cart.setCount(0);
        return cart;
    }
}