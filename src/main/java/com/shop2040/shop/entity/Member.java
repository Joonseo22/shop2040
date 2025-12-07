package com.shop2040.shop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity  // DB에 테이블을 만들어라!
@Getter @Setter // 데이터를 넣고 빼는 기능을 자동으로 만들어라!
public class Member {

    @Id // 주민등록번호 같은 고유 ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;    // 아이디로 쓸 이메일
    private String password; // 비밀번호
    private String name;     // 사용자 이름
}