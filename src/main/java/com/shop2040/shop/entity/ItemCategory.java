package com.shop2040.shop.entity;

public enum ItemCategory {
    OUTER("아우터"),
    TOP("상의"),
    BOTTOM("하의"),
    SHOES("신발");

    private final String description;

    ItemCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}