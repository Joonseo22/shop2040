package com.shop2040.shop;

import com.shop2040.shop.entity.Coupon;
import com.shop2040.shop.repository.CouponRepository;
import com.shop2040.shop.service.SeleniumService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(SeleniumService seleniumService, CouponRepository couponRepository) {
        return (args) -> {
           // seleniumService.crawl4910(); //4910사이트

            // [추가] 기본 쿠폰 생성 (없을 때만)
            if (couponRepository.count() == 0) {
                Coupon c1 = new Coupon();
                c1.setName("신규가입 환영 쿠폰");
                c1.setDiscountPrice(3000);
                couponRepository.save(c1);

                Coupon c2 = new Coupon();
                c2.setName("봄맞이 특별 할인");
                c2.setDiscountPrice(5000);
                couponRepository.save(c2);

                System.out.println("기본 쿠폰이 발급되었습니다.");
            }
        };
    }
}