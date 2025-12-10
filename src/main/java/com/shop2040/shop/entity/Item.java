package com.shop2040.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@ToString
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String price;
    private String imgUrl;
    private String brand;

    @Enumerated(EnumType.STRING)
    private ItemCategory category;

    private boolean isEvent;
    private int discountRate;

    @Transient
    private boolean isWished;

    public String getSalePrice() {
        if (discountRate == 0) return price;
        int origin = Integer.parseInt(price.replace(",", ""));
        int sale = origin * (100 - discountRate) / 100;
        return String.format("%,d", sale);
    }

    public boolean isDiscounted() {
        return discountRate > 0;
    }

    // [수정된 부분] 관리자 페이지 셀렉트 박스용 메서드
    public boolean isOuter() { return this.category == ItemCategory.OUTER; }
    public boolean isTop() { return this.category == ItemCategory.TOP; }
    public boolean isBottom() { return this.category == ItemCategory.BOTTOM; }

    // [변경] isAcc() -> isShoes()로 변경하고 ItemCategory.SHOES와 비교
    public boolean isShoes() { return this.category == ItemCategory.SHOES; }
}