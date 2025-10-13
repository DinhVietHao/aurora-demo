CREATE TABLE Orders
(
    OrderID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    UserID BIGINT NOT NULL,
    AddressID BIGINT NOT NULL,
    VoucherID BIGINT NULL,
    TotalAmount DECIMAL(12,2) NOT NULL,
    DiscountAmount DECIMAL(12,2) NOT NULL,
    FinalAmount    AS (TotalAmount - DiscountAmount) PERSISTED,
    PaymentMethod NVARCHAR(20) NOT NULL,
    PaymentStatus NVARCHAR(20) NOT NULL,
    OrderStatus NVARCHAR(20) NOT NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    DeliveredAt DATETIME2(6) NULL,
    CancelReason NVARCHAR(255) NULL,
    CancelledAt DATETIME2(6) NULL,
    CONSTRAINT FK_Orders_User    FOREIGN KEY (UserID)    REFERENCES Users(UserID),
    CONSTRAINT FK_Orders_Address FOREIGN KEY (AddressID) REFERENCES Addresses(AddressID),
    CONSTRAINT FK_Orders_Voucher FOREIGN KEY (VoucherID) REFERENCES Vouchers(VoucherID)
);

CREATE TABLE OrderShops
(
    OrderShopID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    OrderID BIGINT NOT NULL,
    ShopID BIGINT NOT NULL,
    ShippingFee DECIMAL(12,2) NOT NULL,
    [Status] NVARCHAR(20) NOT NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    VoucherID BIGINT NULL,
    CONSTRAINT FK_OrderShops_Order   FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    CONSTRAINT FK_OrderShops_Shop    FOREIGN KEY (ShopID)  REFERENCES Shops(ShopID),
    CONSTRAINT FK_OrderShops_Voucher FOREIGN KEY (VoucherID) REFERENCES Vouchers(VoucherID)
);

CREATE TABLE Wallets
(
    WalletID BIGINT IDENTITY(1,1) PRIMARY KEY,
    OwnerType NVARCHAR(20) NOT NULL CHECK (OwnerType IN ('ADMIN', 'SHOP')),
    OwnerID BIGINT NULL,
    -- NULL nếu là ADMIN, chứa ShopID nếu là SHOP
    Balance DECIMAL(18,2) NOT NULL DEFAULT 0,
    CONSTRAINT FK_Wallets_Shop FOREIGN KEY (OwnerID) REFERENCES Shops(ShopID)
);

CREATE TABLE WalletHistory
(
    HistoryID BIGINT IDENTITY(1,1) PRIMARY KEY,
    WalletID BIGINT NOT NULL,
    ChangeAmount DECIMAL(18,2) NOT NULL,
    ActionType NVARCHAR(50) NOT NULL,
    OrderRef BIGINT NOT NULL UNIQUE,
    TransactionRef NVARCHAR(100) NOT NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_WalletHistory_Wallet FOREIGN KEY (WalletID) REFERENCES Wallets(WalletID)
);

CREATE TABLE Payments
(
    PaymentID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    OrderID BIGINT NOT NULL UNIQUE,
    Amount DECIMAL(12,2) NOT NULL,
    TransactionRef NVARCHAR(100) NOT NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_Payments_Order FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
);

-- Hào thanh toán thành công 1 hóa đơn:
-- Payments
-- payemntId |  orderid   | Amount | TransactionRef     | CreatedAt    |        DeliveryStatus
--    1      |    Hào     |  100k  | 202510090001234567 | 10/09/2025   |   đã nhận hàng (sau 7 ngày)
--    2      |    Phú     |  250k  | 234324343543543543 | 12/09/2025   |   đã nhận hàng (sau 5 ngày -> trả hàng)

-- WalletHistory
--    HistoryID   |     WalletID    |      ChangeAmount     |      ActionType    |   TransactionRef   |   OrderRef  |     CreatedAt
--        1       |        1        |          -80k         |  TRANSFER_TO_SHOP  |  20251001200123343 |     Hào     |     17/09/2025
--        2       |        2        |          +80k         |  ORDER_PAYMENT     |  20251001200123343 |     Hào     |     17/09/2025
--        3       |        1        |          -250k        |  ORDER_REFUND      |  20251001204343434 |     Phú     |     17/09/2025

-- Wallets
-- WalletID  |   OwnerType    |      OwnerID       |       Balance       
--    1      |    Admin       |        Null        |        100k  -sau 7 ngày-> 20k   -(Phú)-> 270k
--    2      |    Seller      |        Kha         |        100k       (Sau 7 ngày)

-- Phân tích chức năng:
-- Chức năng thanh toán đơn hàng qua VNPAY:
-- 1. Khách hàng tiến hành thanh toán, xác nhận đặt hàng.
-- 2. Hệ thống ghi nhận số tiền thu được từ khách hàng kèm mã khách hàng, thời gian và trạng thái.
-- 3. Khách hàng xác nhận nhận được hàng, sau 7 ngày không vấn đề gì.
-- 4. Admin chuyển tiền về shop.
-- Note:
-- +  Dashboard chủ shop cần xem: tổng số tiền (Wallets), doanh thu theo thời gian (WalletHistory), lịch sử giao dịch. (tổng đơn hàng, thất bại, thành công)

-- !Chốt: Chỉ thanh toán online qua VNPAY.