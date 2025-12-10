package com.shop2040.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class MemberCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;      // 쿠폰 주인

    @ManyToOne
    private Coupon coupon;      // 어떤 쿠폰인지

    private boolean isUsed;     // 사용 여부 (true면 사용완료)
    private LocalDateTime usedDate; // 사용 날짜
}