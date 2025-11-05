package com.group01.aurora_demo.checkout;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class CheckoutTest {
    WebDriver driver;
    WebDriverWait wait;

    // ----- Thông tin tài khoản -----
    final String BASE_URL = "http://localhost:8080/home";
    final String USER_EMAIL = "dvhaoce190204@gmail.com";
    final String USER_PASSWORD = "Hao190204#";

    // ----- Thông tin địa chỉ nhận hàng -----
    final String ADDR_FULL_NAME = "Đinh Việt Hào";
    final String ADDR_PHONE = "0946290204";
    final String ADDR_PROVINCE = "Hậu Giang";
    final String ADDR_DISTRICT = "Thành phố Ngã Bảy";
    final String ADDR_WARD = "Phường Hiệp Thành";
    final String ADDR_DETAIL = "Số 155, đường 1-5";

    // ----- Thông tin thanh toán VNPAY -----
    final String VNP_CARD_NUMBER = "9704198526191432198";
    final String VNP_CARD_NAME = "NGUYEN VAN A";
    final String VNP_CARD_DATE = "07/15";
    final String VNP_OTP = "123456";

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Tăng thời gian chờ tối đa lên 30 giây cho các giao dịch phức tạp
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test
    void testSuccessfulCheckout_WithNewAddress_AndVoucher() throws InterruptedException {
        try {
            // ----- Bước 1: Đăng nhập -----
            driver.get(BASE_URL);
            Thread.sleep(1000);
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[data-open='login']"))).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-email"))).sendKeys(USER_EMAIL);
            Thread.sleep(1000);
            driver.findElement(By.id("login-password")).sendKeys(USER_PASSWORD);
            Thread.sleep(1000);
            driver.findElement(By.cssSelector("#form-login .button-three")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("header-user")));
            System.out.println("BƯỚC 1: Đăng nhập thành công.");
            Thread.sleep(1000);

            // ----- Bước 2: Tương tác với chatbot -----
            System.out.println("BƯỚC 2: Bắt đầu tương tác với Chatbot.");
            wait.until(ExpectedConditions.elementToBeClickable(By.id("chatbot-btn"))).click();
            WebElement chatBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("chatbot-box")));
            Thread.sleep(1500);
            WebElement chatInput = chatBox.findElement(By.id("chatbot-input"));
            chatInput.sendKeys("Tôi muốn đọc thể loại trinh thám");
            Thread.sleep(1500);
            chatBox.findElement(By.id("chatbot-send")).click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".bubble.loading")));
            Thread.sleep(1500);
            WebElement lastBotMessage = driver
                    .findElement(By.cssSelector("#chatbot-messages .message-bot:last-child .bubble"));
            String botReply = lastBotMessage.getText();
            System.out.println("Chatbot trả lời: " + botReply);
            Thread.sleep(1500);

            String extractedKeyword;
            try {
                extractedKeyword = botReply.split("'")[1].trim();
            } catch (Exception e) {
                System.out.println("Không thể trích xuất từ khóa, dùng từ khóa dự phòng 'Sherlock Holmes'.");
                extractedKeyword = "Sherlock Holmes";
            }
            System.out.println("Đã trích xuất từ khóa: " + extractedKeyword);

            chatBox.findElement(By.id("chatbot-close")).click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("chatbot-box")));
            System.out.println("BƯỚC 2: Tương tác Chatbot hoàn tất.");

            // ----- Bước 3 & 4: Tìm kiếm và chọn sản phẩm -----
            driver.findElement(By.name("keyword")).sendKeys(extractedKeyword);
            Thread.sleep(1000);
            driver.findElement(By.cssSelector("div.header-search button")).click();
            Thread.sleep(1000);
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.product a"))).click();
            System.out.println("BƯỚC 3 & 4: Tìm kiếm và chọn SP thành công.");
            Thread.sleep(1000);

            // ----- Bước 5: Xem chi tiết sách và thêm sản phẩm vào giỏ (book_detail.jsp)
            WebElement addToCartButton = wait
                    .until(ExpectedConditions.visibilityOfElementLocated(By.id("add-to-cart")));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    addToCartButton);
            Thread.sleep(500); // Đợi 0.5 giây cho cuộn xong
            wait.until(ExpectedConditions.elementToBeClickable(addToCartButton)).click();
            WebElement toastElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.className("notify-toast")));
            String toastMessage = toastElement.getText();
            System.out.println("Toast message: " + toastMessage);
            String toastClasses = toastElement.getAttribute("class");
            boolean isSuccess = toastClasses.contains("toast--success");
            Assertions.assertTrue(isSuccess, "Thêm vào giỏ hàng thất bại. Thông báo lỗi: " + toastMessage);
            System.out.println("BƯỚC 5: Thêm vào giỏ hàng thành công.");
            wait.until(ExpectedConditions.invisibilityOf(toastElement));

            // ----- Bước 6 & 7: Vào giỏ hàng & mua hàng (cart.jsp) -----
            driver.findElement(By.className("header-cart")).click();

            WebElement firstCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input.cart-checkbox:not(:disabled)")));
            if (!firstCheckbox.isSelected()) {
                System.out.println("Checkbox chưa được tick. Đang tiến hành tick...");
                firstCheckbox.click();
            } else {
                System.out.println("Checkbox đã được tick. Bỏ qua bước tick.");
            }
            Thread.sleep(1000);

            driver.findElement(By.id("cart-pay-button")).click();
            System.out.println("BƯỚC 6 & 7: Vào giỏ hàng và nhấn 'Mua Hàng'.");

            // ----- Bước 8: Thanh toán (checkout.jsp) -----
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

            try {
                WebElement addressModal = shortWait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.id("addAddressModal")));
                // Kịch bản 8A_1: "Chưa có địa chỉ"
                System.out.println("BƯỚC 8A: Không tìm thấy địa chỉ. Bắt đầu điền modal 'Thêm địa chỉ'...");
                addressModal.findElement(By.id("fullName")).sendKeys(ADDR_FULL_NAME);
                addressModal.findElement(By.id("phone")).sendKeys(ADDR_PHONE);
                Thread.sleep(1000);

                // Xử lý Dropdown Tỉnh/Thành
                new Select(addressModal.findElement(By.id("addProvince"))).selectByVisibleText(ADDR_PROVINCE);
                Thread.sleep(1000);

                // Xử lý Dropdown Quận/Huyện
                new Select(addressModal.findElement(By.id("addDistrict"))).selectByVisibleText(ADDR_DISTRICT);
                Thread.sleep(1000);

                // Xử lý Dropdown Phường/Xã
                new Select(addressModal.findElement(By.id("addWard"))).selectByVisibleText(ADDR_WARD);
                Thread.sleep(1000);

                // Điền địa chỉ chi tiết
                addressModal.findElement(By.id("address")).sendKeys(ADDR_DETAIL);
                Thread.sleep(1000);

                // Nhấn "Hoàn thành" để lưu địa chỉ
                addressModal.findElement(By.cssSelector("#form-create-address button[type='submit']")).click();
                System.out.println("BƯỚC 8A_1: Đã điền và lưu địa chỉ mới.");

                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addAddressModal")));
            } catch (Exception e) {
                System.out.println("BƯỚC 8A_2: Đã có địa chỉ. Bỏ qua bước thêm địa chỉ.");
            }

            // Kịch bản 8B: Áp dụng voucher
            System.out.println("BƯỚC 8B: Áp dụng voucher...");
            // Chờ modal địa chỉ đóng lại và nút voucher có thể click
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addAddressModal")));
            WebElement voucherButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("a[data-bs-target='#voucherModal']")));
            voucherButton.click();

            // Chờ modal voucher xuất hiện
            WebElement voucherModal = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("voucherModal")));

            // Chọn voucher giảm giá đầu tiên (nếu có)
            try {
                voucherModal.findElement(By.cssSelector("input[name='voucherDiscount']")).click();
                Thread.sleep(200);
            } catch (Exception e) {
                System.out.println("Không tìm thấy voucher giảm giá.");
            }

            // Chọn voucher freeship đầu tiên (nếu có)
            try {
                voucherModal.findElement(By.cssSelector("input[name='voucherShip']")).click();
                Thread.sleep(200);
            } catch (Exception e) {
                System.out.println("Không tìm thấy voucher freeship.");
            }

            // Nhấn OK
            Thread.sleep(1000);
            voucherModal.findElement(By.id("confirmVoucher")).click();
            System.out.println("BƯỚC 8B: Đã chọn voucher.");

            // Kịch bản 8C: Đặt hàng
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("voucherModal")));
            WebElement placeOrderButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("btnPlaceOrder")));
            Thread.sleep(500); // Dừng 1s xem lại trang checkout
            placeOrderButton.click();
            System.out.println("BƯỚC 8C: Đã nhấp 'Đặt hàng'.");

            // ----- Bước 9: Chuyển hướng VNPay -----
            wait.until(ExpectedConditions.urlContains("sandbox.vnpayment.vn"));
            System.out.println("BƯỚC 9.1: Đã chuyển hướng đến VNPAY - Trang 1 (Chọn phương thức).");

            // Nhấp vào "Thẻ nội địa"
            Thread.sleep(1000);
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("div[data-bs-target='#accordionList2']"))).click();

            // Chờ accordion mở ra và nhấp vào "NCB"
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accordionList2")));
            Thread.sleep(1000);
            driver.findElement(By.id("NCB")).click();
            System.out.println("BƯỚC 9.2: Đã chọn ngân hàng NCB.");

            // ----- Bước 10: Điền thông tin thẻ thanh toán -----
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("card_number_mask")));
            System.out.println("BƯỚC 10.1: Đã chuyển đến VNPAY - Trang 2 (Điền thông tin thẻ).");

            driver.findElement(By.id("card_number_mask")).sendKeys(VNP_CARD_NUMBER);
            Thread.sleep(1000);
            driver.findElement(By.id("cardHolder")).sendKeys(VNP_CARD_NAME);
            Thread.sleep(1000);
            driver.findElement(By.id("cardDate")).sendKeys(VNP_CARD_DATE);
            Thread.sleep(1000);

            // Nhấn "Tiếp tục"
            driver.findElement(By.id("btnContinue")).click();

            // Chờ modal điều khoản và nhấn "Đồng ý"
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalDKSD")));
            Thread.sleep(1000);
            driver.findElement(By.id("btnAgree")).click();
            System.out.println("BƯỚC 10.2: Đã điền thông tin thẻ và đồng ý điều khoản.");

            // ----- Bước 11: Xác thực OTP -----
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("otpvalue")));
            System.out.println("BƯỚC 11.1: Đã chuyển đến VNPAY - Trang 3 (Nhập OTP).");

            driver.findElement(By.id("otpvalue")).sendKeys(VNP_OTP);
            Thread.sleep(1000);
            driver.findElement(By.id("btnConfirm")).click();
            System.out.println("BƯỚC 11.2: Đã nhập OTP và nhấn 'Thanh toán'.");

            // ----- Bước 12: Xác nhận đơn đặt hàng thành công -----
            wait.until(ExpectedConditions.urlContains("/order"));
            System.out.println("BƯỚC 12: Đã quay về trang Quản lý đơn hàng.");

            toastElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.className("notify-toast")));
            toastMessage = toastElement.getText();
            System.out.println("Toast message: " + toastMessage);
            toastClasses = toastElement.getAttribute("class");
            isSuccess = toastClasses.contains("toast--success");
            Assertions.assertTrue(isSuccess, "Đặt hàng thất bại. Thông báo lỗi: " + toastMessage);

            Thread.sleep(3000); // Dừng 3s xem kết quả cuối cùng
        } catch (Exception e) {
            e.printStackTrace();
            Thread.sleep(5000);
            Assertions.fail("Kịch bản test thất bại với ngoại lệ: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}