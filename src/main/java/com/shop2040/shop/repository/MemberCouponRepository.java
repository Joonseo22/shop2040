package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Member;
import com.shop2040.shop.entity.MemberCoupon;
import com.shop2040.shop.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
    List<MemberCoupon> findByMemberAndIsUsedFalse(Member member);

    List<MemberCoupon> findByMemberOrderByIdDesc(Member member);

    Optional<MemberCoupon> findByMemberAndCoupon(Member member, Coupon coupon);
}