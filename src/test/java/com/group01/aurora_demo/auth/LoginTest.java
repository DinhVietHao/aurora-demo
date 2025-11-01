package com.group01.aurora_demo.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class LoginTest {

    WebDriver driver;
    WebDriverWait wait; // Biến "wait" để xử lý các hành động bất đồng bộ (modal, ajax)

    final String BASE_URL = "http://localhost:8080/home";
    final String USER_EMAIL = "dvhaoce190204@gmail.com";
    final String USER_PASSWORD = "Hao190204#";
    final String EXPECTED_USER_NAME = "Đinh Việt Hào";

    @BeforeEach
    void setUp() {
        // Tự động tải và cài đặt chromedriver.exe
        WebDriverManager.chromedriver().setup();

        // Khởi tạo trình duyệt Chrome
        driver = new ChromeDriver();

        // Thiết lập một đối tượng "wait" chung, chờ tối đa 15 giây
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    void testSuccessfulLoginFlow() {
        try {
            // 1. Mở trang chủ (home.jsp)
            driver.get(BASE_URL);

            // 2. Tìm và nhấp vào nút "Tài khoản" để mở modal login
            WebElement accountButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("a[data-open='login']")));
            Thread.sleep(500);
            accountButton.click();

            // 3. Chờ cho modal login xuất hiện
            WebElement emailField = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("login-email")));

            // 4. Tìm các phần tử trong modal
            WebElement passwordField = driver.findElement(By.id("login-password"));
            WebElement loginButton = driver.findElement(By.cssSelector("#form-login .button-three"));

            // 5. Điền thông tin và nhấp đăng nhập
            emailField.sendKeys(USER_EMAIL);
            Thread.sleep(500);

            passwordField.sendKeys(USER_PASSWORD);
            Thread.sleep(500);

            loginButton.click();
            Thread.sleep(1000);

            // 6. CHỜ ĐỢI QUAN TRỌNG NHẤT:
            // File login.js của bạn sẽ điều hướng trang (redirect) sau khi thành công.
            // Chúng ta phải chờ cho trang tải lại và phần tử xác nhận (tên user) xuất hiện.
            WebElement userDropdown = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.className("header-user")));

            // 7. Xác nhận (Assertion)
            // Kiểm tra xem phần tử dropdown có tồn tại không
            Assertions.assertNotNull(userDropdown, "Đăng nhập thất bại, không tìm thấy dropdown người dùng.");

            // Kiểm tra xem tên người dùng có đúng như mong đợi không
            String actualUserName = userDropdown.getText();
            Assertions.assertTrue(actualUserName.contains(EXPECTED_USER_NAME),
                    "Tên người dùng hiển thị không chính xác. Hiển thị: " + actualUserName);
            Thread.sleep(3000);
        } catch (Exception e) {
            // Nếu có bất kỳ lỗi nào (timeout, không tìm thấy phần tử), test sẽ thất bại
            e.printStackTrace();
            Assertions.fail("Kịch bản test thất bại với ngoại lệ: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        // Đóng trình duyệt sau khi test xong
        if (driver != null) {
            driver.quit();
        }
    }
}