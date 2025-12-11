package com.shop2040.shop.controller;

import com.shop2040.shop.entity.Item;
import com.shop2040.shop.entity.ItemCategory; // 추가됨
import com.shop2040.shop.entity.OrderStatus;
import com.shop2040.shop.entity.Ordering;
import com.shop2040.shop.repository.ItemRepository;
import com.shop2040.shop.repository.OrderingRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {

    @Autowired private OrderingRepository orderingRepository;
    @Autowired private ItemRepository itemRepository;

    @GetMapping("/admin")
    public String adminPage(Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "redirect:/";
        List<Ordering> orders = orderingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("orders", orders);
        return "admin";
    }

    @PostMapping("/admin/order/status")
    public String updateStatus(@RequestParam Long orderId, @RequestParam String status, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "redirect:/";
        Ordering order = orderingRepository.findById(orderId).get();
        order.setStatus(OrderStatus.valueOf(status));
        orderingRepository.save(order);
        return "redirect:/admin";
    }

    @PostMapping("/admin/item/update")
    public String updateItem(@RequestParam Long itemId,
                             @RequestParam String name,
                             @RequestParam String price,
                             @RequestParam String imgUrl,
                             @RequestParam String category, // 카테고리 받기
                             HttpSession session) {

        if (session.getAttribute("isAdmin") == null) return "redirect:/";

        Item item = itemRepository.findById(itemId).orElse(null);
        if (item != null) {
            item.setName(name);
            item.setPrice(price);
            item.setImgUrl(imgUrl);

            item.setCategory(ItemCategory.valueOf(category));

            itemRepository.save(item);
        }

        return "redirect:/item/" + itemId;
    }
}