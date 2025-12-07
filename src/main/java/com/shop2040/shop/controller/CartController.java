package com.shop2040.shop.controller;

import com.shop2040.shop.entity.Cart;
import com.shop2040.shop.entity.CartItem;
import com.shop2040.shop.entity.Item;
import com.shop2040.shop.entity.Member;
import com.shop2040.shop.repository.CartItemRepository;
import com.shop2040.shop.repository.CartRepository;
import com.shop2040.shop.repository.ItemRepository;
import com.shop2040.shop.repository.MemberRepository; // 멤버 정보 필요
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private MemberRepository memberRepository;

    // 1. 장바구니에 담기
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long itemId, @RequestParam int count, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        // 1-1. 내 장바구니가 있는지 확인 (없으면 생성)
        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // 1-2. 상품 조회
        Item item = itemRepository.findById(itemId).orElseThrow();

        // 1-3. 이미 장바구니에 있는 상품인지 확인
        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        if (cartItem == null) {
            // 없으면 새로 생성
            cartItem = CartItem.createCartItem(cart, item, count);
            cartItemRepository.save(cartItem);
        } else {
            // 있으면 개수만 증가
            cartItem.addCount(count);
            cartItemRepository.save(cartItem);
        }

        return "redirect:/cart"; // 담고 나서 장바구니 페이지로 이동
    }

    // 2. 장바구니 조회
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        // 내 장바구니 찾기
        Cart cart = cartRepository.findByMemberId(member.getId());

        List<CartItem> cartItems = new ArrayList<>();
        if (cart != null) {
            cartItems = cartItemRepository.findByCartId(cart.getId());
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("userName", member.getName());
        return "cart"; // cart.mustache 보여줘라
    }
    @PostMapping("/cart/delete")
    public String deleteCartItem(@RequestParam Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
        return "redirect:/cart"; // 삭제 후 다시 장바구니 화면으로
    }

    // [추가 2] 장바구니 물건 전체 주문 (결제)
    @Autowired private com.shop2040.shop.repository.OrderingRepository orderingRepository; // 주문 저장소 필요

    @PostMapping("/cart/checkout")
    public String checkout(HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        // 1. 내 장바구니 찾기
        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) return "redirect:/cart";

        // 2. 장바구니에 있는 모든 상품 가져오기
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        // 3. 하나씩 꺼내서 '주문 내역(Ordering)'으로 옮기기
        for (CartItem cartItem : cartItems) {
            // 주문 정보 생성
            com.shop2040.shop.entity.Ordering order = new com.shop2040.shop.entity.Ordering();
            order.setMember(member);
            order.setItem(cartItem.getItem());
            order.setOrderDate(java.time.LocalDateTime.now());

            // 주문 저장!
            orderingRepository.save(order);
        }

        // 4. 장바구니 비우기 (주문했으니까!)
        cartItemRepository.deleteAll(cartItems);

        System.out.println("장바구니 결제 완료! 총 " + cartItems.size() + "건 주문됨.");

        return "redirect:/my-orders"; // 주문 내역 페이지로 이동
    }
}