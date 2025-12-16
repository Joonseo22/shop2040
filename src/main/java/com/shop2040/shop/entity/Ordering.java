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

    private String shippingAddress;
    private String paymentInfo;


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