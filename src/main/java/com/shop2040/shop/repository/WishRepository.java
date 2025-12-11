package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Member;
import com.shop2040.shop.entity.Item;
import com.shop2040.shop.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findByMemberOrderByIdDesc(Member member);

    Optional<Wish> findByMemberAndItem(Member member, Item item);
}