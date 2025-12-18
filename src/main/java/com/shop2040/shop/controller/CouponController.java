package com.shop2040.shop.controller;

import com.shop2040.shop.entity.Coupon;
import com.shop2040.shop.entity.Member;
import com.shop2040.shop.entity.MemberCoupon;
import com.shop2040.shop.repository.CouponRepository;
import com.shop2040.shop.repository.MemberCouponRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class CouponController {

    @Autowired private CouponRepository couponRepository;
    @Autowired private MemberCouponRepository memberCouponRepository;

    @GetMapping("/coupon")
    public String couponPage(Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        List<Coupon> allCoupons = couponRepository.findAll();

        List<MemberCoupon> myCoupons = memberCouponRepository.findByMemberOrderByIdDesc(member);

        model.addAttribute("coupons", allCoupons);
        model.addAttribute("myCoupons", myCoupons);
        model.addAttribute("userName", member.getName());

        return "coupon";
    }

    @PostMapping("/coupon/download/{id}")
    public String downloadCoupon(@PathVariable Long id, HttpSession session) {
        Member member = (Member) session.getAttribute("user");
        if (member == null) return "redirect:/login";

        Coupon coupon = couponRepository.findById(id).orElse(null);
        if (coupon != null) {
            Optional<MemberCoupon> existing = memberCouponRepository.findByMemberAndCoupon(member, coupon);

            if (existing.isEmpty()) {
                MemberCoupon mc = new MemberCoupon();
                mc.setMember(member);
                mc.setCoupon(coupon);
                mc.setUsed(false);
                memberCouponRepository.save(mc);
            }
        }
        return "redirect:/coupon";
    }
}