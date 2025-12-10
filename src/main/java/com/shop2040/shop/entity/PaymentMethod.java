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

    private String cardCompany; // 카드사 (예: 신한카드, 현대카드)
    private String cardNumber;  // 카드번호 (예: 1234-5678-****-****)
    private String cardNickname; // 별칭 (예: 내 월급통장)

    // 카드 배경색 스타일 (디자인용)
    private String colorStyle;
}