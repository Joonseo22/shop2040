package com.shop2040.shop.service;

import com.shop2040.shop.entity.Item;
import com.shop2040.shop.entity.ItemCategory;
import com.shop2040.shop.repository.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
public class SeleniumService {

    @Autowired private ItemRepository itemRepository;
    @Autowired private OrderingRepository orderingRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private WishRepository wishRepository;
    @Autowired private CartItemRepository cartItemRepository;

    public void crawl4910() {
        System.out.println("=== 🧹 데이터 초기화 중... ===");
        reviewRepository.deleteAll();
        wishRepository.deleteAll();
        cartItemRepository.deleteAll();
        orderingRepository.deleteAll();
        itemRepository.deleteAll();

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--start-maximized");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        try {
            System.out.println("=== 🕷️ 4대 카테고리 크롤링 (30개 제한) ===");
            driver.get("https://4910.kr/");
            Thread.sleep(3000);

            Actions actions = new Actions(driver);
            for (int i = 0; i < 5; i++) {
                try {
                    driver.findElement(By.tagName("body")).click();
                    actions.sendKeys(Keys.END).perform();
                    Thread.sleep(1000);
                } catch (Exception e) {}
            }

            List<WebElement> productLinks = driver.findElements(By.tagName("a"));
            int count = 0;
            Random random = new Random();

            for (WebElement link : productLinks) {
                if (count >= 30) break;

                try {
                    List<WebElement> imgs = link.findElements(By.tagName("img"));
                    if (imgs.isEmpty()) continue;

                    WebElement imgElement = imgs.get(0);
                    String imgUrl = imgElement.getAttribute("src");
                    if (imgUrl == null || !imgUrl.startsWith("http") || imgUrl.contains("logo") || imgUrl.contains("icon")) continue;

                    String rawText = link.getText();
                    if (rawText == null || rawText.trim().isEmpty()) continue;

                    String[] lines = rawText.split("\n");
                    String realName = "";
                    String realPrice = "";

                    for (String line : lines) {
                        line = line.trim();
                        if ((line.contains(",") || line.contains("원")) && line.matches(".*\\d.*") && !line.contains("%")) {
                            realPrice = line.replaceAll("[^0-9]", "");
                        }
                        else if (!line.contains("%") && line.length() > 5) {
                            if (realName.isEmpty()) realName = line;
                        }
                    }

                    if (realPrice.isEmpty()) realPrice = String.valueOf((random.nextInt(190) + 10) * 1000);
                    if (realName.isEmpty()) realName = imgElement.getAttribute("alt");
                    if (realName == null) realName = "Item " + (count+1);

                    realName = realName.replaceAll("\\[.*?\\]", "").trim();
                    if (realName.length() > 60) realName = realName.substring(0, 60);

                    // [카테고리 분석]
                    ItemCategory category = analyzeCategory(realName);

                    // 잡화나 뷰티 등 4개에 속하지 않는 건 저장 안 함 (엄격 모드)
                    if (category == null) {
                        // 혹은 그냥 상의로 넣고 싶으면: category = ItemCategory.TOP;
                        continue;
                    }

                    Item item = new Item();
                    item.setName(realName);
                    item.setBrand("4910 Partners");
                    item.setImgUrl(imgUrl);
                    item.setPrice(String.format("%,d", Integer.parseInt(realPrice)));
                    item.setCategory(category);

                    if (random.nextInt(5) == 0) {
                        item.setEvent(true);
                        item.setDiscountRate((random.nextInt(4) + 1) * 10);
                    } else {
                        item.setEvent(false);
                        item.setDiscountRate(0);
                    }

                    itemRepository.save(item);
                    System.out.println("✅ 저장 [" + (count+1) + "]: " + category + " / " + realName);
                    count++;

                } catch (Exception e) {
                    continue;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { driver.quit(); } catch (Exception e) {}
            System.out.println("=== 🎉 크롤링 완료 ===");
        }
    }

    // [수정] 4개 카테고리로만 분류
    private ItemCategory analyzeCategory(String name) {
        String n = name.toLowerCase().replaceAll(" ", "");

        // 1. 아우터
        if (n.contains("패딩") || n.contains("코트") || n.contains("자켓") || n.contains("재킷") ||
                n.contains("점퍼") || n.contains("가디건") || n.contains("후리스") || n.contains("아우터") ||
                n.contains("집업") || n.contains("바람막이") || n.contains("베스트") || n.contains("조끼")) {
            return ItemCategory.OUTER;
        }
        // 2. 하의
        else if (n.contains("팬츠") || n.contains("바지") || n.contains("슬랙스") || n.contains("데님") ||
                n.contains("청바지") || n.contains("진") || n.contains("조거") || n.contains("레깅스") ||
                n.contains("스커트") || n.contains("트레이닝") || n.contains("쇼츠")) {
            return ItemCategory.BOTTOM;
        }
        // 3. 신발 (ACC 대신 SHOES)
        else if (n.contains("신발") || n.contains("운동화") || n.contains("부츠") || n.contains("슈즈") ||
                n.contains("스니커즈") || n.contains("워커") || n.contains("샌들") || n.contains("슬리퍼")) {
            return ItemCategory.SHOES;
        }
        // 4. 상의 (나머지 대부분)
        else if (n.contains("티셔츠") || n.contains("맨투맨") || n.contains("후드") || n.contains("니트") ||
                n.contains("스웨터") || n.contains("셔츠") || n.contains("블라우스") || n.contains("나시") || n.contains("탑")) {
            return ItemCategory.TOP;
        }
        else {
            return ItemCategory.TOP; // 애매하면 상의로
        }
    }
}