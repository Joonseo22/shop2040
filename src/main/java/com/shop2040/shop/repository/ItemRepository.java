package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Item;
import com.shop2040.shop.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // 1. 이름으로 검색
    List<Item> findByNameContaining(String keyword);

    // 2. [추가] 카테고리별 조회
    List<Item> findByCategory(ItemCategory category);

    // 3. [추가] 이벤트 상품만 조회
    List<Item> findByIsEventTrue();
}