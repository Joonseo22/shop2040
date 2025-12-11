package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByItemIdOrderByIdDesc(Long itemId);
}