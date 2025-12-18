package com.shop2040.shop.controller;

import com.shop2040.shop.entity.*;
import com.shop2040.shop.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class CartController {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private OrderingRepository orderingRepository;
    @Autowired private MemberCouponRepository memberCouponRepository;

    // [추가] 주소와 카드 정보를 가져오기 위해 필요
    @Autowired private AddressRepository addressRepository;
    @Autowired private PaymentMethodRepository paymentMethodRepository;

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long itemId, @RequestParam int count, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin != null && isAdmin) return "redirect:/";

        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        Item item = itemRepository.findById(itemId).orElseThrow();
        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        if (cartItem == null) {
            cartItem = CartItem.createCartItem(cart, item, count);
            cartItemRepository.save(cartItem);
        } else {
            cartItem.addCount(count);
            cartItemRepository.save(cartItem);
        }

        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        Cart cart = cartRepository.findByMemberId(member.getId());

        List<CartItem> cartItems = new ArrayList<>();
        int totalPrice = 0;

        if (cart != null) {
            cartItems = cartItemRepository.findByCartId(cart.getId());
            for (CartItem ci : cartItems) {
                String priceStr = ci.getItem().getPrice().replace(",", "");
                totalPrice += Integer.parseInt(priceStr) * ci.getCount();
            }
        }

        List<MemberCoupon> myCoupons = memberCouponRepository.findByMemberAndIsUsedFalse(member);

        List<Address> addresses = addressRepository.findByMember(member);
        List<PaymentMethod> cards = paymentMethodRepository.findByMember(member);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("myCoupons", myCoupons);
        model.addAttribute("addresses", addresses); // 화면 전달
        model.addAttribute("cards", cards);         // 화면 전달
        model.addAttribute("userName", member.getName());

        return "cart";
    }

    @PostMapping("/cart/delete")
    public String deleteCartItem(@RequestParam Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout(HttpSession session,
                           @RequestParam(required = false) Long memberCouponId,
                           @RequestParam(required = false) Long addressId,  // 배송지 ID 받기
                           @RequestParam(required = false) Long cardId      // 카드 ID 받기
    ) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin != null && isAdmin) return "redirect:/";

        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) return "redirect:/cart";

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) return "redirect:/cart";

        String shippingInfo = "배송지 미지정";
        if (addressId != null) {
            Optional<Address> addr = addressRepository.findById(addressId);
            if (addr.isPresent()) {
                shippingInfo = addr.get().getAddressName() + " (" + addr.get().getFullAddress() + " " + addr.get().getDetailAddress() + ")";
            }
        }

        String paymentInfo = "결제수단 미지정";
        if (cardId != null) {
            Optional<PaymentMethod> card = paymentMethodRepository.findById(cardId);
            if (card.isPresent()) {
                paymentInfo = card.get().getCardCompany() + " (" + card.get().getCardNickname() + ")";
            }
        }

        int discountAmount = 0;
        if (memberCouponId != null) {
            Optional<MemberCoupon> mcOptional = memberCouponRepository.findById(memberCouponId);
            if (mcOptional.isPresent()) {
                MemberCoupon mc = mcOptional.get();
                if(mc.getMember().getId().equals(member.getId()) && !mc.isUsed()) {
                    mc.setUsed(true);
                    mc.setUsedDate(LocalDateTime.now());
                    memberCouponRepository.save(mc);
                    discountAmount = mc.getCoupon().getDiscountPrice();
                }
            }
        }

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem cartItem = cartItems.get(i);
            Ordering order = new Ordering();
            order.setMember(member);
            order.setItem(cartItem.getItem());
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(OrderStatus.PREPARING);

            order.setShippingAddress(shippingInfo);
            order.setPaymentInfo(paymentInfo);

            if (i == 0) {
                order.setDiscountPrice(discountAmount);
            } else {
                order.setDiscountPrice(0);
            }

            orderingRepository.save(order);
        }

        cartItemRepository.deleteAll(cartItems);
        return "redirect:/my-orders";
    }
}