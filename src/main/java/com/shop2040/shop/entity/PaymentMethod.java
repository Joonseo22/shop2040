package com.shop2040.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    private String cardCompany;
    private String cardNumber;
    private String cardNickname;

    // 카드 배경색 스타일 (디자인용)
    private String colorStyle;
}