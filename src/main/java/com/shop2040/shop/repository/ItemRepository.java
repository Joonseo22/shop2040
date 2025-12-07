package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // 텅 비워둬도 저장(save), 조회(findAll) 기능이 다 들어있습니다.
    List<Item> findByNameContaining(String keyword);
}