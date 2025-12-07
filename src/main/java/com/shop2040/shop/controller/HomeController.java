package com.shop2040.shop.controller;

import com.shop2040.shop.entity.Item;
import com.shop2040.shop.entity.Member;
import com.shop2040.shop.entity.Ordering;
import com.shop2040.shop.repository.ItemRepository;
import com.shop2040.shop.repository.OrderingRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderingRepository orderingRepository; // 주문 저장소 추가

    // 메인 페이지
    @GetMapping("/")
    public String home(Model model, HttpSession session,
                       @RequestParam(value = "keyword", required = false) String keyword) {

        List<Item> items;

        // 검색어가 있으면 검색, 없으면 전체 조회
        if (keyword != null && !keyword.isEmpty()) {
            items = itemRepository.findByNameContaining(keyword);
        } else {
            items = itemRepository.findAll();
        }

        model.addAttribute("items", items);

        // 로그인 정보 처리
        Member user = (Member) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }

        return "home";
    }

    // 상품 상세 페이지
    @GetMapping("/item/{id}")
    public String itemDetail(@PathVariable Long id, Model model) {
        Optional<Item> itemOptional = itemRepository.findById(id);

        if (itemOptional.isPresent()) {
            model.addAttribute("item", itemOptional.get());
            return "item-detail";
        } else {
            return "redirect:/";
        }
    }

    // [추가된 기능] 주문 처리
    @PostMapping("/order")
    public String order(@RequestParam Long itemId, HttpSession session) {
        // 1. 로그인 체크
        Member member = (Member) session.getAttribute("user");
        if (member == null) {
            return "redirect:/login";
        }

        // 2. 상품 조회
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            return "redirect:/"; // 상품이 없으면 홈으로
        }

        // 3. 주문 정보 생성 및 저장
        Ordering order = new Ordering();
        order.setMember(member);
        order.setItem(itemOptional.get());
        order.setOrderDate(LocalDateTime.now());

        orderingRepository.save(order);
        System.out.println("주문 성공! 구매자: " + member.getName() + ", 상품: " + itemOptional.get().getName());

        // 4. 주문 후 메인으로 이동
        return "redirect:/";
    }

    @GetMapping("/my-orders")
    public String myOrders(Model model, HttpSession session) {
        // 1. 로그인 체크
        Member user = (Member) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 2. 내 주문 내역 가져오기
        List<Ordering> orders = orderingRepository.findByMemberOrderByIdDesc(user);

        // 3. 화면에 전달
        model.addAttribute("orders", orders);
        model.addAttribute("userName", user.getName()); // 상단바 이름 표시용

        return "my-orders"; // my-orders.mustache 파일 보여줘라
    }
}