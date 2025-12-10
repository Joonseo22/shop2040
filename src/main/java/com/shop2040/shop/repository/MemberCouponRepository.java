package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Member;
import com.shop2040.shop.entity.MemberCoupon;
import com.shop2040.shop.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {
    // 1. 내 쿠폰 중 사용 안 한 것만 가져오기 (장바구니용)
    List<MemberCoupon> findByMemberAndIsUsedFalse(Member member);

    // 2. 내 쿠폰 전체 가져오기 (마이페이지용)
    List<MemberCoupon> findByMemberOrderByIdDesc(Member member);

    // 3. 이미 받은 쿠폰인지 확인용
    Optional<MemberCoupon> findByMemberAndCoupon(Member member, Coupon coupon);
}