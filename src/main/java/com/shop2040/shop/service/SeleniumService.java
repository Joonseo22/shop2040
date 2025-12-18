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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class SeleniumService {

    @Autowired private ItemRepository itemRepository;
    @Autowired private OrderingRepository orderingRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private WishRepository wishRepository;
    @Autowired private CartItemRepository cartItemRepository;

    private Map<ItemCategory, Integer> categoryCount = new HashMap<>();

    public void crawl4910() {
        System.out.println("===  [1단계] 기존 데이터 삭제 중... ===");
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

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

        categoryCount.put(ItemCategory.OUTER, 0);
        categoryCount.put(ItemCategory.TOP, 0);
        categoryCount.put(ItemCategory.BOTTOM, 0);
        categoryCount.put(ItemCategory.SHOES, 0);

        try {
            System.out.println("=== 🕷️ [2단계] 4910.kr 접속 (신발 집중 탐색) ===");
            driver.get("https://4910.kr/");
            Thread.sleep(3000);

            System.out.println(" 물량 확보를 위해 스크롤 다운 중... (잠시 대기)");
            Actions actions = new Actions(driver);
            for (int i = 0; i < 50; i++) {
                try {
                    driver.findElement(By.tagName("body")).click();
                    actions.sendKeys(Keys.END).perform();
                    Thread.sleep(500);
                } catch (Exception e) {}
            }

            List<WebElement> productLinks = driver.findElements(By.tagName("a"));
            System.out.println(">>> 분석할 상품 개수: " + productLinks.size() + "개");

            int targetPerCategory = 10; // 각 10개씩 수집
            Random random = new Random();

            for (WebElement link : productLinks) {
                if (categoryCount.values().stream().allMatch(c -> c >= targetPerCategory)) break;

                try {
                    List<WebElement> imgs = link.findElements(By.tagName("img"));
                    if (imgs.isEmpty()) continue;

                    WebElement imgElement = imgs.get(0);
                    String imgUrl = imgElement.getAttribute("src");
                    if (imgUrl == null || !imgUrl.startsWith("http")) continue;

                    String rawText = link.getText();
                    String realName = "";
                    String realPrice = "";

                    if (rawText != null && !rawText.isEmpty()) {
                        String[] lines = rawText.split("\n");
                        for (String line : lines) {
                            line = line.trim();
                            if ((line.contains(",") || line.contains("원")) && line.matches(".*\\d.*") && !line.contains("%")) {
                                realPrice = line.replaceAll("[^0-9]", "");
                            }
                            else if (!line.contains("%") && line.length() > 5) {
                                if (realName.isEmpty()) realName = line;
                            }
                        }
                    }

                    if (realName.isEmpty()) realName = imgElement.getAttribute("alt");
                    if (realName == null || realName.length() < 2) continue;

                    realName = realName.replaceAll("\\[.*?\\]", "").trim();
                    if (realName.length() > 60) realName = realName.substring(0, 60);

                    if (realPrice.isEmpty()) realPrice = String.valueOf((random.nextInt(190) + 10) * 1000);

                    ItemCategory category = analyzeCategory(realName);

                    if (category == null) continue;

                    if (categoryCount.get(category) >= targetPerCategory) continue;

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
                    categoryCount.put(category, categoryCount.get(category) + 1);
                    System.out.println("저장 [" + category + "]: " + categoryCount.get(category) + "/10 - " + realName);

                } catch (Exception e) {
                    continue;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { driver.quit(); } catch (Exception e) {}
            System.out.println("=== 크롤링 완료 ===");
            System.out.println("최종 결과: " + categoryCount);
        }
    }

    private ItemCategory analyzeCategory(String name) {
        String n = name.toLowerCase().replaceAll(" ", "");

        if (n.contains("신발") || n.contains("운동화") || n.contains("부츠") || n.contains("슈즈") ||
                n.contains("스니커즈") || n.contains("워커") || n.contains("구두") || n.contains("로퍼") ||
                n.contains("더비") || n.contains("모카신") || n.contains("샌들") || n.contains("슬리퍼") ||
                n.contains("나이키") || n.contains("nike") ||
                n.contains("아디다스") || n.contains("adidas") ||
                n.contains("뉴발") || n.contains("newbalance") ||
                n.contains("아식스") || n.contains("asics") ||
                n.contains("살로몬") || n.contains("salomon") ||
                n.contains("크록스") || n.contains("crocs") ||
                n.contains("닥터마틴") || n.contains("어그") || n.contains("ugg") ||
                n.contains("반스") || n.contains("vans") ||
                n.contains("컨버스") || n.contains("converse")) {
            return ItemCategory.SHOES;
        }

        if (n.contains("팬츠") || n.contains("바지") || n.contains("슬랙스") || n.contains("데님") ||
                n.contains("청바지") || n.contains("진") || n.contains("조거") || n.contains("레깅스") ||
                n.contains("스커트") || n.contains("트레이닝") || n.contains("쇼츠") || n.contains("카고") ||
                n.contains("와이드") || n.contains("버뮤다") || n.contains("sweatpants")) {
            return ItemCategory.BOTTOM;
        }

        if (n.contains("패딩") || n.contains("코트") || n.contains("자켓") || n.contains("재킷") ||
                n.contains("점퍼") || n.contains("가디건") || n.contains("후리스") || n.contains("플리스") ||
                n.contains("아우터") || n.contains("집업") || n.contains("바람막이") || n.contains("베스트") ||
                n.contains("조끼") || n.contains("파카") || n.contains("무스탕") || n.contains("블레이저") ||
                n.contains("푸퍼") || n.contains("다운")) {
            return ItemCategory.OUTER;
        }

        if (n.contains("티셔츠") || n.contains("맨투맨") || n.contains("후드") || n.contains("니트") ||
                n.contains("스웨터") || n.contains("셔츠") || n.contains("블라우스") || n.contains("나시") ||
                n.contains("탑") || n.contains("긴팔") || n.contains("반팔") || n.contains("pk") ||
                n.contains("카라") || n.contains("sweatshirt")) {
            return ItemCategory.TOP;
        }

        return null;
    }
}