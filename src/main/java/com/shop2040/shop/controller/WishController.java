package com.shop2040.shop.controller;

import com.shop2040.shop.entity.Item;
import com.shop2040.shop.entity.Member;
import com.shop2040.shop.entity.Wish;
import com.shop2040.shop.repository.ItemRepository;
import com.shop2040.shop.repository.WishRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody; // 추가됨

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class WishController {

    @Autowired private WishRepository wishRepository;
    @Autowired private ItemRepository itemRepository;

    // 1. [기존 방식] 찜하기 (상세 페이지용 - 페이지 새로고침 됨)
    @PostMapping("/wish/{itemId}")
    public String toggleWish(@PathVariable Long itemId, HttpSession session, HttpServletRequest request) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        processWish(member, itemId); // 로직 분리

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    // 2. [추가된 방식] 찜하기 API (메인 화면용 - 새로고침 없음!)
    @PostMapping("/api/wish/{itemId}")
    @ResponseBody // [중요] HTML파일이 아니라 데이터(true/false)만 보낸다는 뜻
    public boolean toggleWishApi(@PathVariable Long itemId, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return false; // 로그인 안했으면 실패

        return processWish(member, itemId); // 찜 됐으면 true, 취소면 false 리턴
    }

    // 찜하기 로직 (중복 제거를 위해 따로 뺌)
    private boolean processWish(Member member, Long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) return false;

        Optional<Wish> wishOptional = wishRepository.findByMemberAndItem(member, item);
        if (wishOptional.isPresent()) {
            wishRepository.delete(wishOptional.get());
            return false; // 찜 취소됨 (빈 하트)
        } else {
            Wish wish = new Wish();
            wish.setMember(member);
            wish.setItem(item);
            wish.setCreatedDate(LocalDateTime.now());
            wishRepository.save(wish);
            return true; // 찜 됨 (빨간 하트)
        }
    }

    // 3. 찜한 상품 모아보기
    @GetMapping("/wishlist")
    public String wishlist(Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        List<Wish> wishlist = wishRepository.findByMemberOrderByIdDesc(member);
        model.addAttribute("wishlist", wishlist);
        model.addAttribute("userName", member.getName());

        return "wishlist";
    }
}