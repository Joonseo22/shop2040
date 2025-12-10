package com.shop2040.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Item item;

    @ManyToOne
    private Member member;

    private String content;
    private int score; // 1~5 점수

    private LocalDateTime createdDate;

    // --- Mustache 화면용 도우미 ---
    // 점수만큼 별 이모지를 반복해서 반환 (예: score가 5면 ⭐⭐⭐⭐⭐)
    public String getStarDisplay() {
        return "⭐".repeat(score);
    }
}