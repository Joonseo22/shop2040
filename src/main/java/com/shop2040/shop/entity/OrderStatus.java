package com.shop2040.shop.entity;

public enum OrderStatus {
    PREPARING, // 배송 준비 중
    SHIPPING,  // 배송 중
    DELIVERED, // 배송 완료
    CANCELED   // 주문 취소
}