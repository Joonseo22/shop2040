package com.shop2040.shop.service;

import com.shop2040.shop.entity.Item;
import com.shop2040.shop.repository.ItemRepository;
import com.shop2040.shop.repository.OrderingRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class SeleniumService {

    @Autowired private ItemRepository itemRepository;
    @Autowired private OrderingRepository orderingRepository;

    public void crawl4910() {
        // 기존 데이터 삭제 (초기화)
        orderingRepository.deleteAll();
        itemRepository.deleteAll();

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER); // 고속 모드
        // options.addArguments("--headless"); // 필요하면 주석 해제

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        try {
            System.out.println("=== 4910 대량 크롤링 시작 ===");

            try {
                driver.get("https://4910.kr/");
            } catch (Exception e) {
                System.out.println("로딩 지연 무시하고 진행");
            }

            Thread.sleep(5000); // 이미지 로딩 대기

            List<WebElement> images = driver.findElements(By.tagName("img"));
            System.out.println("발견된 이미지: " + images.size());

            int count = 0;
            Random random = new Random(); // 랜덤 가격 생성을 위해

            for (WebElement img : images) {
                if(count >= 40) break; // [변경] 상품 40개까지 저장!

                try {
                    String imgUrl = img.getAttribute("src");
                    if (imgUrl == null || !imgUrl.startsWith("http")) continue;
                    if (imgUrl.contains("logo") || imgUrl.contains("icon")) continue;

                    Item item = new Item();
                    item.setBrand("4910 Official");

                    // 이름도 약간씩 다르게 (번호 붙이기)
                    item.setName("4910 프리미엄 컬렉션 No." + (count + 1));

                    // [변경] 가격을 10,000원 ~ 99,000원 사이 랜덤으로 생성
                    int randomPrice = (random.nextInt(90) + 10) * 1000;
                    item.setPrice(String.format("%,d", randomPrice)); // 35,000 형식으로 저장

                    item.setImgUrl(imgUrl);

                    itemRepository.save(item);
                    System.out.println("저장 완료 [" + (count+1) + "]: " + item.getPrice() + "원");
                    count++;
                } catch (Exception e) {
                    continue;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { driver.quit(); } catch (Exception e) {}
            System.out.println("=== 총 " + itemRepository.count() + "개 상품 입고 완료 ===");
        }
    }
}