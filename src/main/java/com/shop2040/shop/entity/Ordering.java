package com.shop2040.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Ordering { // Order는 DB 예약어라 겹칠 수 있어 Ordering으로 함

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누가 샀는지 (Member와 연결)
    @ManyToOne
    private Member member;

    // 무엇을 샀는지 (Item과 연결)
    @ManyToOne
    private Item item;

    // 언제 샀는지
    private LocalDateTime orderDate;
}