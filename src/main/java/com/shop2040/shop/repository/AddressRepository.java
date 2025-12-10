package com.shop2040.shop.repository;

import com.shop2040.shop.entity.Address;
import com.shop2040.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByMember(Member member);
}