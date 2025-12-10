package com.shop2040.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Ordering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Item item;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private boolean isReviewed;
    private int discountPrice;

    // [추가됨] 주문 당시의 배송지와 결제수단 정보 (텍스트로 박제)
    private String shippingAddress;
    private String paymentInfo;

    // --- [Mustache 화면 처리를 위한 도우미 메서드들] ---

    public String getFinalPrice() {
        if (item == null) return "0";
        int originPrice = Integer.parseInt(item.getPrice().replace(",", ""));
        int finalPrice = originPrice - discountPrice;
        return String.format("%,d", finalPrice);
    }

    public boolean hasDiscount() {
        return discountPrice > 0;
    }

    public boolean isPreparing() { return this.status == OrderStatus.PREPARING; }
    public boolean isShipping() { return this.status == OrderStatus.SHIPPING; }
    public boolean isDelivered() { return this.status == OrderStatus.DELIVERED; }
    public boolean isCanceled() { return this.status == OrderStatus.CANCELED; }
}