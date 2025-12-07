package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Member;
import com.shop2040.shop.entity.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderingRepository extends JpaRepository<Ordering, Long> {
    // 내(Member)가 주문한 리스트를 최신순(Id 내림차순)으로 찾아줘!
    List<Ordering> findByMemberOrderByIdDesc(Member member);
}