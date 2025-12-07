package com.shop2040.shop.controller;

import com.shop2040.shop.entity.Member;
import com.shop2040.shop.repository.MemberRepository;
import jakarta.servlet.http.HttpSession; // 세션 사용을 위한 임포트
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/join")
    public String joinForm() {
        return "join";
    }

    // 회원가입 처리
    @PostMapping("/join")
    public String createMember(Member member) {
        memberRepository.save(member);
        return "redirect:/login";
    }

    // --- [로그인 처리 핵심 로직] ---
    @PostMapping("/login")
    public String login(String email, String password, HttpSession session) {
        // 1. DB에서 이메일로 회원 조회
        Member member = memberRepository.findByEmail(email);

        // 2. 회원이 있고, 비밀번호가 일치하는지 확인
        if (member != null && member.getPassword().equals(password)) {
            // 3. 로그인 성공! 세션에 회원 정보 저장 ("user"라는 이름표로 저장)
            session.setAttribute("user", member);
            return "redirect:/"; // 메인 페이지로 이동
        } else {
            // 4. 로그인 실패 시 다시 로그인 페이지로
            System.out.println("로그인 실패: 아이디나 비번이 틀림");
            return "redirect:/login";
        }
    }

    // --- [로그아웃 처리] ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 세션에 저장된 정보 삭제 (로그아웃)
        session.invalidate();
        return "redirect:/";
    }
}