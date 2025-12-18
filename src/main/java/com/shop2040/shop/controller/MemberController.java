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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class MemberController {

    @Autowired private MemberRepository memberRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private OrderingRepository orderingRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private PaymentMethodRepository paymentMethodRepository;

    // 로그인/회원가입
    @GetMapping("/login")
    public String loginForm() { return "login"; }

    @GetMapping("/join")
    public String joinForm() { return "join"; }

    @PostMapping("/join")
    public String createMember(Member member, @RequestParam String passwordConfirm, Model model) {
        if (!member.getPassword().equals(passwordConfirm)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "join";
        }
        if (memberRepository.findByEmail(member.getEmail()) != null) {
            model.addAttribute("error", "이미 가입된 이메일입니다.");
            return "join";
        }
        memberRepository.save(member);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(String email, String password, HttpSession session) {
        Member member = memberRepository.findByEmail(email);
        if (member != null && member.getPassword().equals(password)) {
            session.setAttribute("user", member);
            if (email.equals("joonseo1595743100@gmail.com")) {
                session.setAttribute("isAdmin", true);
            } else {
                session.removeAttribute("isAdmin");
            }
            return "redirect:/";
        } else {
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/my-page")
    public String myPage(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        model.addAttribute("userName", member.getName());
        model.addAttribute("email", member.getEmail());
        if (session.getAttribute("isAdmin") != null) model.addAttribute("isAdmin", true);

        List<Ordering> orders = orderingRepository.findByMemberOrderByIdDesc(member);
        if (orders.size() > 3) orders = orders.subList(0, 3);
        model.addAttribute("recentOrders", orders);

        List<Long> recentIds = (List<Long>) session.getAttribute("recentItems");
        List<Item> recentItems = new ArrayList<>();
        if (recentIds != null) {
            for (Long id : recentIds) {
                itemRepository.findById(id).ifPresent(recentItems::add);
            }
        }
        model.addAttribute("recentItems", recentItems);

        return "my-page";
    }

    @GetMapping("/my-info")
    public String myInfo(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        model.addAttribute("userName", member.getName());
        if (session.getAttribute("isAdmin") != null) model.addAttribute("isAdmin", true);

        model.addAttribute("member", memberRepository.findById(member.getId()).orElse(member));
        return "my-info";
    }

    @PostMapping("/my-info/update")
    public String updateMember(Member formMember, HttpSession session) {
        Member member = memberRepository.findById(formMember.getId()).get();
        member.setPassword(formMember.getPassword());
        member.setName(formMember.getName());
        memberRepository.save(member);
        session.setAttribute("user", member);
        return "redirect:/my-page";
    }

    @GetMapping("/shipping")
    public String shippingPage(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        model.addAttribute("userName", member.getName());
        if (session.getAttribute("isAdmin") != null) model.addAttribute("isAdmin", true);

        model.addAttribute("addresses", addressRepository.findByMember(member));
        return "shipping";
    }

    @PostMapping("/shipping/add")
    public String addAddress(Address address, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";
        address.setMember(member);
        addressRepository.save(address);
        return "redirect:/shipping";
    }

    @PostMapping("/shipping/delete")
    public String deleteAddress(@RequestParam Long addressId) {
        addressRepository.deleteById(addressId);
        return "redirect:/shipping";
    }

    @GetMapping("/payment")
    public String paymentPage(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        model.addAttribute("userName", member.getName());
        if (session.getAttribute("isAdmin") != null) model.addAttribute("isAdmin", true);

        List<PaymentMethod> cards = paymentMethodRepository.findByMember(member);
        model.addAttribute("cards", cards);

        return "payment";
    }

    @PostMapping("/payment/add")
    public String addCard(PaymentMethod payment, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        payment.setMember(member);
        String[] colors = {"bg-gradient-primary", "bg-gradient-dark", "bg-gradient-success", "bg-gradient-warning"};
        payment.setColorStyle(colors[new Random().nextInt(colors.length)]);

        paymentMethodRepository.save(payment);
        return "redirect:/payment";
    }

    @PostMapping("/payment/delete")
    public String deleteCard(@RequestParam Long cardId) {
        paymentMethodRepository.deleteById(cardId);
        return "redirect:/payment";
    }

    @PostMapping("/withdraw")
    public String withdraw(HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart != null) {
            List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
            cartItemRepository.deleteAll(items);
            cartRepository.delete(cart);
        }

        addressRepository.deleteAll(addressRepository.findByMember(member));
        paymentMethodRepository.deleteAll(paymentMethodRepository.findByMember(member));
        orderingRepository.deleteAll(orderingRepository.findByMemberOrderByIdDesc(member));

        memberRepository.delete(member);
        session.invalidate();
        return "redirect:/";
    }
}