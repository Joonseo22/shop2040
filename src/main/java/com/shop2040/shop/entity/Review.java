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

    public String getStarDisplay() {
        return "⭐".repeat(score);
    }
}