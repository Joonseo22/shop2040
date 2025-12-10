package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Member;
import com.shop2040.shop.entity.Item;
import com.shop2040.shop.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {
    // 1. 내가 찜한 목록 다 가져오기 (최신순)
    List<Wish> findByMemberOrderByIdDesc(Member member);

    // 2. 내가 이 상품을 찜했는지 확인하기
    Optional<Wish> findByMemberAndItem(Member member, Item item);
}