package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 특정 상품(ItemId)에 달린 리뷰를 최신순으로 가져오기
    List<Review> findByItemIdOrderByIdDesc(Long itemId);
}