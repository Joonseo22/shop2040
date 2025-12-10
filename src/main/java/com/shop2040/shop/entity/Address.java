package com.shop2040.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    private String addressName; // 배송지명 (예: 우리집, 회사)
    private String fullAddress; // 주소
    private String detailAddress; // 상세주소
}