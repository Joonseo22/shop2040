package com.shop2040.shop.controller;

import com.shop2040.shop.entity.Item;
import com.shop2040.shop.entity.Member;
import com.shop2040.shop.entity.Ordering;
import com.shop2040.shop.entity.Review;
import com.shop2040.shop.repository.ItemRepository;
import com.shop2040.shop.repository.OrderingRepository;
import com.shop2040.shop.repository.ReviewRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.multipart.MultipartFile; // 제거됨

import java.time.LocalDateTime;

@Controller
public class ReviewController {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired private OrderingRepository orderingRepository;
    @Autowired private ItemRepository itemRepository;


    @GetMapping("/review/write")
    public String reviewForm(@RequestParam Long orderId, HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        Ordering order = orderingRepository.findById(orderId).orElse(null);
        if (order == null || !order.getMember().getId().equals(member.getId())) {
            return "redirect:/my-orders";
        }

        if (order.isReviewed()) {
            return "redirect:/my-orders";
        }

        model.addAttribute("item", order.getItem());
        model.addAttribute("orderId", orderId);

        return "review-form";
    }

    // 파일 업로드 파라미터 및 로직 제거
    @PostMapping("/review/create")
    public String createReview(@RequestParam Long orderId,
                               @RequestParam String content,
                               @RequestParam int score,
                               HttpSession session) {

        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        Ordering order = orderingRepository.findById(orderId).orElse(null);

        if (order != null && !order.isReviewed()) {
            Review review = new Review();
            review.setMember(member);
            review.setItem(order.getItem());
            review.setContent(content);
            review.setScore(score);
            review.setCreatedDate(LocalDateTime.now());


            reviewRepository.save(review);

            order.setReviewed(true);
            orderingRepository.save(order);

            return "redirect:/item/" + order.getItem().getId();
        }

        return "redirect:/my-orders";
    }
}