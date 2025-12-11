package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일로 회원을 검색하는 기능
    Member findByEmail(String email);
}