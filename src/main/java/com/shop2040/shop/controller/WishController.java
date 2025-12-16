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

    // 찜하기
    @PostMapping("/wish/{itemId}")
    public String toggleWish(@PathVariable Long itemId, HttpSession session, HttpServletRequest request) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        processWish(member, itemId); // 로직 분리

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping("/api/wish/{itemId}")
    @ResponseBody
    public boolean toggleWishApi(@PathVariable Long itemId, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return false;

        return processWish(member, itemId);
    }


    private boolean processWish(Member member, Long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) return false;

        Optional<Wish> wishOptional = wishRepository.findByMemberAndItem(member, item);
        if (wishOptional.isPresent()) {
            wishRepository.delete(wishOptional.get());
            return false; // 찜 취소
        } else {
            Wish wish = new Wish();
            wish.setMember(member);
            wish.setItem(item);
            wish.setCreatedDate(LocalDateTime.now());
            wishRepository.save(wish);
            return true; // 찜
        }
    }


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