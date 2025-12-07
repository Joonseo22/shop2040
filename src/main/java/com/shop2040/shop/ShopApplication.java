package com.shop2040.shop; // 이 줄이 없으면 에러가 납니다!

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
    public CommandLineRunner demo(SeleniumService seleniumService) {
        return (args) -> {
            //seleniumService.crawl4910();
        };
    }
}