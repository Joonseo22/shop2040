package com.shop2040.shop.entity;

public enum ItemCategory {
    OUTER("아우터"),
    TOP("상의"),
    BOTTOM("하의"),
    SHOES("신발"); // [변경] ACC 제거, SHOES 추가

    private final String description;

    ItemCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}