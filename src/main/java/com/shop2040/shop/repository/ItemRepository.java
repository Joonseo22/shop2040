package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Item;
import com.shop2040.shop.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByNameContaining(String keyword);

    List<Item> findByCategory(ItemCategory category);

    List<Item> findByIsEventTrue();
}