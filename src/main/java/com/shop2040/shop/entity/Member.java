package com.shop2040.shop.entity;

import jakarta.persistence.*; // *로 퉁쳐도 됩니다
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [수정] unique = true : 똑같은 이메일이 들어오면 DB가 알아서 튕겨냄!
    @Column(unique = true)
    private String email;

    private String password;
    private String name;
}