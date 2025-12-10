package com.shop2040.shop.controller;

import com.shop2040.shop.entity.*;
import com.shop2040.shop.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired private ItemRepository itemRepository;
    @Autowired private OrderingRepository orderingRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private WishRepository wishRepository;

    @GetMapping("/")
    public String home(Model model, HttpSession session,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "category", required = false) String categoryStr) {

        List<Item> items;
        String sectionTitle = "New Arrivals";
        String sectionDesc = "이번 주 새로 들어온 신상품을 만나보세요.";

        if (keyword != null && !keyword.isEmpty()) {
            items = itemRepository.findByNameContaining(keyword);
            sectionTitle = "'" + keyword + "' 검색 결과";
            sectionDesc = "검색하신 상품 목록입니다.";
        } else if (categoryStr != null && !categoryStr.isEmpty()) {
            try {
                ItemCategory category = ItemCategory.valueOf(categoryStr);
                items = itemRepository.findByCategory(category);
                sectionTitle = category.getDescription();
                sectionDesc = category.getDescription() + " 카테고리 인기 상품입니다.";
            } catch (IllegalArgumentException e) {
                items = itemRepository.findAll();
            }
        } else {
            items = itemRepository.findAll();
        }

        prepareModel(model, session, items);
        model.addAttribute("sectionTitle", sectionTitle);
        model.addAttribute("sectionDesc", sectionDesc);

        return "home";
    }

    @GetMapping("/event")
    public String eventPage(Model model, HttpSession session) {
        List<Item> items = itemRepository.findByIsEventTrue();
        prepareModel(model, session, items);
        model.addAttribute("sectionTitle", "🔥 금주의 특가 이벤트");
        model.addAttribute("sectionDesc", "한정수량! 놓치면 후회할 초특가 상품을 만나보세요.");
        return "home";
    }

    private void prepareModel(Model model, HttpSession session, List<Item> items) {
        model.addAttribute("items", items);
        Member user = (Member) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            if (isAdmin != null && isAdmin) model.addAttribute("isAdmin", true);

            List<Wish> myWishes = wishRepository.findByMemberOrderByIdDesc(user);
            Set<Long> wishedItemIds = myWishes.stream().map(w -> w.getItem().getId()).collect(Collectors.toSet());
            for (Item item : items) {
                if (wishedItemIds.contains(item.getId())) item.setWished(true);
            }
        }
    }

    @GetMapping("/item/{id}")
    public String itemDetail(@PathVariable Long id, Model model, HttpSession session) {
        Optional<Item> itemOptional = itemRepository.findById(id);

        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            model.addAttribute("item", item);

            // [리뷰 통계 계산 로직]
            List<Review> reviews = reviewRepository.findByItemIdOrderByIdDesc(id);
            model.addAttribute("reviews", reviews);
            model.addAttribute("reviewCount", reviews.size());

            if (!reviews.isEmpty()) {
                // 1. 평균 별점 계산
                double average = reviews.stream().mapToInt(Review::getScore).average().orElse(0.0);
                model.addAttribute("averageScore", String.format("%.1f", average)); // 소수점 한자리

                // 2. 점수별 개수 및 비율(%) 계산 (막대 그래프용)
                int[] counts = new int[6]; // 0~5 인덱스 사용 (1점~5점 저장)
                for (Review r : reviews) counts[r.getScore()]++;

                model.addAttribute("score5", counts[5]);
                model.addAttribute("score4", counts[4]);
                model.addAttribute("score3", counts[3]);
                model.addAttribute("score2", counts[2]);
                model.addAttribute("score1", counts[1]);

                // 비율 계산 (전체 개수로 나눠서 100 곱함)
                model.addAttribute("per5", counts[5] * 100 / reviews.size());
                model.addAttribute("per4", counts[4] * 100 / reviews.size());
                model.addAttribute("per3", counts[3] * 100 / reviews.size());
                model.addAttribute("per2", counts[2] * 100 / reviews.size());
                model.addAttribute("per1", counts[1] * 100 / reviews.size());
            } else {
                model.addAttribute("averageScore", "0.0");
            }

            // 최근 본 상품
            List<Long> recentItems = (List<Long>) session.getAttribute("recentItems");
            if (recentItems == null) recentItems = new ArrayList<>();
            if (recentItems.contains(id)) recentItems.remove(id);
            recentItems.add(0, id);
            if (recentItems.size() > 5) recentItems.remove(recentItems.size() - 1);
            session.setAttribute("recentItems", recentItems);

            Member user = (Member) session.getAttribute("user");
            if (user != null) {
                model.addAttribute("userName", user.getName());
                Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
                if (isAdmin != null && isAdmin) model.addAttribute("isAdmin", true);
                Optional<Wish> wish = wishRepository.findByMemberAndItem(user, item);
                if (wish.isPresent()) model.addAttribute("isWished", true);
            }

            return "item-detail";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/order")
    public String order(@RequestParam Long itemId, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin != null && isAdmin) return "redirect:/";

        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) return "redirect:/";

        Ordering order = new Ordering();
        order.setMember(member);
        order.setItem(itemOptional.get());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PREPARING);
        orderingRepository.save(order);
        return "redirect:/my-orders";
    }

    @GetMapping("/my-orders")
    public String myOrders(Model model, HttpSession session) {
        Member user = (Member) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        List<Ordering> orders = orderingRepository.findByMemberOrderByIdDesc(user);
        model.addAttribute("orders", orders);
        model.addAttribute("userName", user.getName());
        return "my-orders";
    }

    @PostMapping("/order/cancel")
    public String cancelOrder(@RequestParam Long orderId) {
        Optional<Ordering> orderOptional = orderingRepository.findById(orderId);
        if(orderOptional.isPresent()) {
            Ordering order = orderOptional.get();
            if(order.getStatus() == OrderStatus.PREPARING) {
                order.setStatus(OrderStatus.CANCELED);
                orderingRepository.save(order);
            }
        }
        return "redirect:/my-orders";
    }
}