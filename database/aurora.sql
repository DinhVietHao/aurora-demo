CREATE TABLE Roles
(
    RoleCode NVARCHAR(20) NOT NULL PRIMARY KEY,
    RoleName NVARCHAR(100) NOT NULL
);

CREATE TABLE Users
(
    UserID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    Email NVARCHAR(255) NOT NULL,
    [Password] NVARCHAR(255) NOT NULL,
    FullName NVARCHAR(150) NOT NULL,
    AvatarUrl NVARCHAR(2000) NULL,
    [Status] NVARCHAR(20),
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    AuthProvider NVARCHAR(20) NOT NULL
);

CREATE TABLE UserRoles
(
    UserID BIGINT NOT NULL,
    RoleCode NVARCHAR(20) NOT NULL,
    CONSTRAINT PK_UserRoles PRIMARY KEY (UserID, RoleCode),
    CONSTRAINT FK_UserRoles_Users FOREIGN KEY (UserID) REFERENCES Users(UserID),
    CONSTRAINT FK_UserRoles_Roles FOREIGN KEY (RoleCode) REFERENCES Roles(RoleCode)
);

CREATE TABLE Addresses
(
    AddressID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    RecipientName NVARCHAR(150) NOT NULL,
    Phone NVARCHAR(20) NOT NULL,
    City NVARCHAR(100) NOT NULL,
    ProvinceID INT NULL,
    District NVARCHAR(100) NOT NULL,
    DistrictID INT NULL,
    Ward NVARCHAR(100) NOT NULL,
    WardCode NVARCHAR(20) NULL,
    Description NVARCHAR(255) NOT NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE Users_Addresses
(
    UserID BIGINT NOT NULL,
    AddressID BIGINT NOT NULL,
    IsDefault BIT NOT NULL,
    CONSTRAINT PK_Users_Addresses PRIMARY KEY (UserID, AddressID),
    CONSTRAINT FK_UsersAddr_Users   FOREIGN KEY (UserID)    REFERENCES Users(UserID),
    CONSTRAINT FK_UsersAddr_Address FOREIGN KEY (AddressID) REFERENCES Addresses(AddressID)
);

CREATE TABLE Shops
(
    ShopID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    Name NVARCHAR(150) NOT NULL UNIQUE,
    Description NVARCHAR(255) NULL,
    RatingAvg DECIMAL(3,2) NOT NULL,
    [Status] NVARCHAR(20) NOT NULL,
    OwnerUserID BIGINT NOT NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    PickupAddressID BIGINT NOT NULL UNIQUE,
    InvoiceEmail NVARCHAR(255) NOT NULL,
    AvatarUrl NVARCHAR(2000) NULL,
    RejectReason NVARCHAR(255) NULL,
    CONSTRAINT FK_Shops_Owner      FOREIGN KEY (OwnerUserID)     REFERENCES Users(UserID),
    CONSTRAINT FK_Shops_PickupAddr FOREIGN KEY (PickupAddressID) REFERENCES Addresses(AddressID)
);

CREATE TABLE VAT
(
    VATCode NVARCHAR(50) PRIMARY KEY,
    VATRate DECIMAL(5,2) NOT NULL,
    Description NVARCHAR(255) NULL
);

CREATE TABLE Category
(
    CategoryID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    Name NVARCHAR(120) NOT NULL,
    VATCode NVARCHAR(50) NOT NULL,
    CONSTRAINT FK_Categories_VAT FOREIGN KEY (VATCode) REFERENCES VAT(VATCode)
);

CREATE TABLE Publishers
(
    PublisherID BIGINT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(150) NOT NULL
);

CREATE TABLE Products
(
    ProductID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    ShopID BIGINT NOT NULL,
    Title NVARCHAR(255) NOT NULL,
    Description NVARCHAR(MAX) NULL,
    OriginalPrice DECIMAL(12,2) NOT NULL,
    SalePrice DECIMAL(12,2) NOT NULL,
    SoldCount BIGINT NOT NULL DEFAULT 0,
    Quantity INT NOT NULL,
    PublisherID BIGINT NULL,
    [Status] NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    PublishedDate DATE NULL,
    Weight DECIMAL(10,2) NOT NULL,
    RejectReason NVARCHAR(255) NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_Products_Shop      FOREIGN KEY (ShopID)      REFERENCES Shops(ShopID),
    CONSTRAINT FK_Products_Publisher FOREIGN KEY (PublisherID) REFERENCES Publishers(PublisherID)
);

CREATE TABLE ProductCategory
(
    ProductID BIGINT NOT NULL,
    CategoryID BIGINT NOT NULL,
    CONSTRAINT PK_ProductCategory PRIMARY KEY (ProductID, CategoryID),
    CONSTRAINT FK_ProductCategory_Product  FOREIGN KEY (ProductID)  REFERENCES Products(ProductID),
    CONSTRAINT FK_ProductCategory_Category FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID)
);

CREATE TABLE ProductImages
(
    ImageID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    ProductID BIGINT NOT NULL,
    Url NVARCHAR(2000) NOT NULL,
    IsPrimary BIT NOT NULL,
    CONSTRAINT FK_ProductImages_Product FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

CREATE TABLE Authors
(
    AuthorID BIGINT IDENTITY(1,1) PRIMARY KEY,
    AuthorName NVARCHAR(200) NOT NULL
);

CREATE TABLE BookAuthors
(
    ProductID BIGINT NOT NULL,
    AuthorID BIGINT NOT NULL,
    CONSTRAINT PK_BookAuthors PRIMARY KEY (ProductID, AuthorID),
    CONSTRAINT FK_BookAuthors_Product FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
    CONSTRAINT FK_BookAuthors_Author  FOREIGN KEY (AuthorID)  REFERENCES Authors(AuthorID)
);

CREATE TABLE Languages
(
    LanguageCode NVARCHAR(20) NOT NULL PRIMARY KEY,
    LanguageName NVARCHAR(100) NOT NULL
);

CREATE TABLE BookDetails
(
    ProductID BIGINT NOT NULL PRIMARY KEY,
    Translator NVARCHAR(200) NULL,
    [Version] NVARCHAR(50) NOT NULL,
    CoverType NVARCHAR(50) NOT NULL,
    Pages INT NOT NULL,
    LanguageCode NVARCHAR(20) NOT NULL,
    [Size] NVARCHAR(50) NOT NULL,
    ISBN NVARCHAR(20) NOT NULL,
    CONSTRAINT FK_BookDetails_Product 
      FOREIGN KEY (ProductID)    REFERENCES Products(ProductID) ON DELETE CASCADE,
    CONSTRAINT FK_BookDetails_Language 
      FOREIGN KEY (LanguageCode) REFERENCES Languages(LanguageCode)
);

INSERT INTO Languages
    (LanguageCode, LanguageName)
VALUES
    (N'vi', N'Tiếng Việt'),
    (N'en', N'Tiếng Anh');

CREATE TABLE CartItems
(
    CartItemID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    UserID BIGINT NOT NULL,
    ProductID BIGINT NOT NULL,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(12,2) NOT NULL,
    Subtotal   AS (CAST(Quantity AS DECIMAL(12,2)) * UnitPrice) PERSISTED,
    IsChecked BIT NOT NULL DEFAULT 0,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_CartItems_User    FOREIGN KEY (UserID)    REFERENCES Users(UserID),
    CONSTRAINT FK_CartItems_Product FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

CREATE TABLE Vouchers
(
    VoucherID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    Code NVARCHAR(40) NOT NULL,
    DiscountType NVARCHAR(20) NOT NULL,
    Value DECIMAL(12,2) NOT NULL,
    MaxAmount DECIMAL(12,2) NULL,
    MinOrderAmount DECIMAL(12,2) NOT NULL,
    StartAt DATETIME2(6) NOT NULL,
    EndAt DATETIME2(6) NOT NULL,
    UsageLimit INT NOT NULL,
    PerUserLimit INT NULL,
    [Status] NVARCHAR(20) NOT NULL,
    UsageCount INT NOT NULL DEFAULT 0,
    IsShopVoucher BIT NOT NULL DEFAULT 0,
    ShopID BIGINT NULL,
    [Description] NVARCHAR(255) NULL,
    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Vouchers_Shop  FOREIGN KEY (ShopID) REFERENCES Shops(ShopID),
    CONSTRAINT UQ_Vouchers_Code UNIQUE (Code)
);

CREATE TABLE UserVouchers
(
    UserVoucherID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    VoucherID BIGINT NOT NULL,
    UserID BIGINT NOT NULL,
    Status VARCHAR(20) DEFAULT 'USED',
    CONSTRAINT FK_UserVouchers_Voucher FOREIGN KEY (VoucherID) REFERENCES Vouchers(VoucherID),
    CONSTRAINT FK_UserVouchers_User    FOREIGN KEY (UserID)    REFERENCES Users(UserID)
);

CREATE TABLE Orders
(
    OrderID BIGINT IDENTITY(1,1) PRIMARY KEY,
    UserID BIGINT NOT NULL,
    Address NVARCHAR(255) NOT NULL DEFAULT N'',
    VoucherDiscountID BIGINT NULL,
    -- voucher giảm giá hệ thống
    VoucherShipID BIGINT NULL,
    -- voucher freeship hệ thống
    TotalAmount DECIMAL(12,2) NOT NULL,
    -- tổng tiền hàng
    DiscountAmount DECIMAL(12,2) NOT NULL DEFAULT 0,
    -- giảm giá từ voucher/khuyến mãi
    TotalShippingFee DECIMAL(12,2) NOT NULL DEFAULT 0,
    -- phí giao hàng gốc
    ShippingDiscount DECIMAL(12,2) NOT NULL DEFAULT 0,
    -- giảm phí ship (voucher freeship)
    FinalAmount DECIMAL(12,2) NOT NULL,
    -- tổng tiền cuối cùng, backend tự tính
    OrderStatus NVARCHAR(20) NOT NULL,
    --  PENDING,SHIPPING, WAITING_SHIP,  COMPLETED, CANCELLED , RETURNED
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    DeliveredAt DATETIME2(6) NULL,
    CancelledAt DATETIME2(6) NULL,
    CONSTRAINT FK_Orders_User FOREIGN KEY (UserID) REFERENCES Users(UserID),
    CONSTRAINT FK_Orders_VoucherDiscount FOREIGN KEY (VoucherDiscountID) REFERENCES Vouchers(VoucherID),
    CONSTRAINT FK_Orders_VoucherShip FOREIGN KEY (VoucherShipID) REFERENCES Vouchers(VoucherID)
);

CREATE TABLE OrderShops
(
    OrderShopID BIGINT IDENTITY(1,1) PRIMARY KEY,
    OrderID BIGINT NOT NULL,
    ShopID BIGINT NOT NULL,
    VoucherID BIGINT NULL,
    -- voucher của shop (nếu có)
    Subtotal DECIMAL(12,2) NOT NULL,
    -- tổng tiền hàng của shop
    Discount DECIMAL(12,2) NOT NULL DEFAULT 0,
    -- giảm giá của shop
    TotalShippingFee DECIMAL(12,2) NOT NULL DEFAULT 0,
    -- phí ship riêng (nếu cần)
    FinalAmount DECIMAL(12,2) NOT NULL,
    -- backend tự tính
    [Status] NVARCHAR(20) NOT NULL,
    --  PENDING,SHIPPING, WAITING_SHIP,  COMPLETED, CANCELLED , RETURNED
    SystemShippingDiscount DECIMAL(12,2) NOT NULL DEFAULT 0,
    SystemVoucherDiscount DECIMAL(12,2) NOT NULL DEFAULT 0,
    UpdateAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    CancelReason NVARCHAR(255) NULL,
    ReturnReason NVARCHAR(255) NULL,
    CONSTRAINT FK_OrderShops_Order FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
    CONSTRAINT FK_OrderShops_Shop FOREIGN KEY (ShopID) REFERENCES Shops(ShopID),
    CONSTRAINT FK_OrderShops_Voucher FOREIGN KEY (VoucherID) REFERENCES Vouchers(VoucherID)
);

CREATE TABLE OrderItems
(
    OrderItemID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    OrderShopID BIGINT NOT NULL,
    ProductID BIGINT NOT NULL,
    FlashSaleItemID BIGINT NULL,
    Quantity INT NOT NULL,
    OriginalPrice DECIMAL(12,2) NOT NULL,
    SalePrice DECIMAL(12,2) NOT NULL,
    Subtotal DECIMAL(12,2),
    VATRate DECIMAL(5,2) NOT NULL DEFAULT 0,
    CONSTRAINT FK_OrderItems_OrderShop FOREIGN KEY (OrderShopID) REFERENCES OrderShops(OrderShopID),
    CONSTRAINT FK_OrderItems_Product   FOREIGN KEY (ProductID)   REFERENCES Products(ProductID),
    CONSTRAINT FK_OrderItems_Flash     FOREIGN KEY (FlashSaleItemID) REFERENCES FlashSaleItems(FlashSaleItemID)
);
CREATE TABLE FlashSales
(
    FlashSaleID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    Name NVARCHAR(150) NOT NULL,
    StartAt DATETIME2(6) NOT NULL,
    EndAt DATETIME2(6) NOT NULL,
    [Status] NVARCHAR(20) NOT NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
);

CREATE TABLE FlashSaleItems
(
    FlashSaleItemID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    FlashSaleID BIGINT NOT NULL,
    ProductID BIGINT NOT NULL,
    ShopID BIGINT NOT NULL,
    FlashPrice DECIMAL(12,2) NOT NULL,
    FsStock INT NOT NULL,
    PerUserLimit INT NULL,
    ApprovalStatus NVARCHAR(20) NOT NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_FSI_FlashSale FOREIGN KEY (FlashSaleID) REFERENCES FlashSales(FlashSaleID),
    CONSTRAINT FK_FSI_Product   FOREIGN KEY (ProductID)   REFERENCES Products(ProductID)
);


CREATE TABLE Payments
(
    PaymentID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    OrderID BIGINT NOT NULL UNIQUE,
    Amount DECIMAL(12,2) NOT NULL,
    RefundedAmount DECIMAL(12,2) NOT NULL DEFAULT 0,
    TransactionRef NVARCHAR(100) NOT NULL,
    Status NVARCHAR(20) NOT NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_Payments_Order FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
);



-- Bảng lưu số dư của admin/shop
CREATE TABLE AccountBalances
(
    AccountID BIGINT IDENTITY(1,1) PRIMARY KEY,
    OwnerType NVARCHAR(20) NOT NULL CHECK (OwnerType IN ('ADMIN', 'SHOP')),
    OwnerID BIGINT NULL,
    -- NULL nếu là ADMIN, chứa ShopID nếu là SHOP
    Balance DECIMAL(18,2) NOT NULL DEFAULT 0,
    CONSTRAINT FK_AccountBalances_Shop FOREIGN KEY (OwnerID) REFERENCES Shops(ShopID)
);

-- Bảng ghi nhận thay đổi số dư
CREATE TABLE BalanceChanges
(
    ChangeID BIGINT IDENTITY(1,1) PRIMARY KEY,
    AccountID BIGINT NOT NULL,
    ChangeAmount DECIMAL(18,2) NOT NULL,
    ActionType NVARCHAR(50) NOT NULL,
    -- ví dụ: ORDER_PAYMENT, REFUND, BONUS
    OrderID BIGINT NULL,
    -- tham chiếu tới đơn hàng (nếu có)
    TransactionRef NVARCHAR(100) NOT NULL,
    -- mã giao dịch duy nhất
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_BalanceChanges_Account FOREIGN KEY (AccountID) REFERENCES AccountBalances(AccountID),
    CONSTRAINT FK_BalanceChanges_Order FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
);

CREATE TABLE Reviews
(
    ReviewID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    OrderItemID BIGINT NOT NULL,
    UserID BIGINT NOT NULL,
    Rating TINYINT NOT NULL,
    Comment NVARCHAR(255) NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_Reviews_OrderItem FOREIGN KEY (OrderItemID) REFERENCES OrderItems(OrderItemID),
    CONSTRAINT FK_Reviews_User      FOREIGN KEY (UserID)      REFERENCES Users(UserID)
);

CREATE TABLE ReviewImages
(
    ReviewImageID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    ReviewID BIGINT NOT NULL,
    Url NVARCHAR(2000) NOT NULL,
    Caption NVARCHAR(255) NULL,
    IsPrimary BIT NOT NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_ReviewImages_Review FOREIGN KEY (ReviewID) REFERENCES Reviews(ReviewID)
);

CREATE TABLE Notifications
(
    NotificationID BIGINT IDENTITY(1,1) PRIMARY KEY,
    RecipientType NVARCHAR(20) NOT NULL CHECK (RecipientType IN ('CUSTOMER', 'SELLER', 'ADMIN')),
    RecipientID BIGINT NOT NULL,
    Type NVARCHAR(50) NOT NULL,
    Title NVARCHAR(255) NOT NULL,
    Message NVARCHAR(1000) NOT NULL,
    ReferenceType NVARCHAR(50) NULL,
    ReferenceID BIGINT NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME()
);

-- Trigger 
DROP TRIGGER IF EXISTS trg_DeleteProductCascade;
GO

CREATE TRIGGER trg_DeleteProductCascade
ON Products
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @Blocked TABLE (
        ProductID BIGINT,
        Reason NVARCHAR(255)
    );

    -- 1️⃣ Kiểm tra sản phẩm đang tham gia Flash Sale
    INSERT INTO @Blocked
        (ProductID, Reason)
    SELECT d.ProductID, N'Sản phẩm đang tham gia Flash Sale'
    FROM deleted d
    WHERE EXISTS (
        SELECT 1
    FROM FlashSaleItems f
    WHERE f.ProductID = d.ProductID
    );

    -- 2️⃣ Kiểm tra sản phẩm nằm trong các đơn hàng đang xử lý
    INSERT INTO @Blocked
        (ProductID, Reason)
    SELECT DISTINCT d.ProductID, N'Sản phẩm đang nằm trong đơn hàng đang xử lý'
    FROM deleted d
        JOIN OrderItems oi ON oi.ProductID = d.ProductID
        JOIN OrderShops os ON os.OrderShopID = oi.OrderShopID
        JOIN Orders o ON o.OrderID = os.OrderID
    WHERE os.[Status] IN (
        N'PENDING', N'SHIPPING', N'WAITING_SHIP',
        N'CONFIRM', N'COMPLETED', N'CANCELLED',
        N'RETURNED', N'RETURNED_REJECTED', N'RETURNED_REQUESTED'
    );

    -- 3️⃣ Kiểm tra sản phẩm có trạng thái không được phép xóa
    INSERT INTO @Blocked
        (ProductID, Reason)
    SELECT d.ProductID,
        CASE p.Status
            WHEN N'ACTIVE' THEN N'Sản phẩm đang hoạt động'
            WHEN N'INACTIVE' THEN N'Sản phẩm đang ngừng kinh doanh'
            WHEN N'OUT_OF_STOCK' THEN N'Sản phẩm đang hết hàng'
        END
    FROM deleted d
        JOIN Products p ON p.ProductID = d.ProductID
    WHERE p.Status IN (N'ACTIVE', N'INACTIVE', N'OUT_OF_STOCK');

    -- 4️⃣ Kiểm tra sản phẩm PENDING nhưng đã có SoldCount > 0
    INSERT INTO @Blocked
        (ProductID, Reason)
    SELECT d.ProductID, N'Sản phẩm đang ở trạng thái chờ duyệt (PENDING) nhưng đã được bán'
    FROM deleted d
        JOIN Products p ON p.ProductID = d.ProductID
    WHERE p.Status = N'PENDING'
        AND p.SoldCount > 0;

    -- 5️⃣ Nếu có sản phẩm bị chặn xóa thì báo lỗi
    IF EXISTS (SELECT 1
    FROM @Blocked)
    BEGIN
        DECLARE @msg NVARCHAR(MAX) = N'Không thể xóa các sản phẩm sau do còn ràng buộc:' + CHAR(13);

        SELECT @msg = @msg + N'• ProductID: ' + CAST(ProductID AS NVARCHAR) + N' – ' + Reason + CHAR(13)
        FROM @Blocked;

        RAISERROR(@msg, 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    -- 6️⃣ Nếu hợp lệ → Xóa dữ liệu liên quan trước
    DELETE FROM BookDetails WHERE ProductID IN (SELECT ProductID
    FROM deleted);
    DELETE FROM BookAuthors WHERE ProductID IN (SELECT ProductID
    FROM deleted);
    DELETE FROM ProductImages WHERE ProductID IN (SELECT ProductID
    FROM deleted);
    DELETE FROM ProductCategory WHERE ProductID IN (SELECT ProductID
    FROM deleted);
    DELETE FROM CartItems WHERE ProductID IN (SELECT ProductID
    FROM deleted);
    DELETE FROM FlashSaleItems WHERE ProductID IN (SELECT ProductID
    FROM deleted);
    DELETE FROM OrderItems WHERE ProductID IN (SELECT ProductID
    FROM deleted);
    DELETE FROM Reviews WHERE OrderItemID IN (
        SELECT OrderItemID
    FROM OrderItems
    WHERE ProductID IN (SELECT ProductID
    FROM deleted)
    );

    -- 7️⃣ Cuối cùng xóa Product
    DELETE FROM Products WHERE ProductID IN (SELECT ProductID
    FROM deleted);
END;
GO

DROP TRIGGER IF EXISTS trg_OrderShopStatusNotification
GO

CREATE OR ALTER TRIGGER trg_OrderShopStatusNotification
ON OrderShops
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    ---------------------------------------------------
    -- 1) Đơn hàng mới (Chỉ khi INSERT)
    ---------------------------------------------------
    INSERT INTO Notifications
        (RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt)
    SELECT
        'SELLER',
        i.ShopID,
        'ORDER_NEW',
        N'Đơn hàng mới',
        CONCAT(N'Bạn có đơn hàng mới #', i.OrderShopID, N' từ khách hàng ', ISNULL(u.FullName, N'Khách hàng')),
        'ORDER',
        i.OrderShopID,
        DATEADD(HOUR, 7, SYSDATETIME())
    FROM inserted i
        LEFT JOIN deleted d ON d.OrderShopID = i.OrderShopID
        JOIN Orders o ON o.OrderID = i.OrderID
        LEFT JOIN Users u ON u.UserID = o.UserID
    WHERE d.OrderShopID IS NULL
        AND NOT EXISTS (
            SELECT 1
        FROM Notifications n
        WHERE n.RecipientType = 'SELLER'
            AND n.RecipientID = i.ShopID
            AND n.Type = 'ORDER_NEW'
            AND n.ReferenceType = 'ORDER'
            AND n.ReferenceID = i.OrderShopID
            AND n.CreatedAt >= DATEADD(HOUR, 7, SYSDATETIME())
        );

    ---------------------------------------------------
    -- 2) Giao hàng thành công (Customer nhận)
    ---------------------------------------------------
    INSERT INTO Notifications
        (RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt)
    SELECT
        'SELLER',
        i.ShopID,
        'ORDER_DELIVERED',
        N'Đơn hàng đã giao thành công',
        CONCAT(N'Đơn hàng #', i.OrderShopID, N' của bạn đã được giao thành công.'),
        'ORDER',
        i.OrderShopID,
        DATEADD(HOUR, 7, SYSDATETIME())
    FROM inserted i
        JOIN deleted d ON d.OrderShopID = i.OrderShopID
        JOIN Orders o ON o.OrderID = i.OrderID
    WHERE ((d.Status IS NULL OR d.Status <> i.Status))
        AND i.Status IN ('COMPLETED')
        AND NOT EXISTS (
            SELECT 1
        FROM Notifications n
        WHERE n.RecipientType = 'SELLER'
            AND n.RecipientID = o.UserID
            AND n.Type = 'ORDER_DELIVERED'
            AND n.ReferenceType = 'ORDER'
            AND n.ReferenceID = i.OrderShopID
            AND n.CreatedAt >= DATEADD(HOUR, 7, SYSDATETIME())
        );

    ---------------------------------------------------
    -- 3) Yêu cầu trả hàng (Seller nhận)
    ---------------------------------------------------
    INSERT INTO Notifications
        (RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt)
    SELECT
        'SELLER',
        i.ShopID,
        'RETURN_REQUESTED',
        N'Yêu cầu trả hàng mới',
        CONCAT(N'Khách hàng đã yêu cầu trả hàng cho đơn #', i.OrderShopID),
        'ORDER',
        i.OrderShopID,
        DATEADD(HOUR, 7, SYSDATETIME())
    FROM inserted i
        JOIN deleted d ON d.OrderShopID = i.OrderShopID
    WHERE (d.Status IS NULL OR d.Status <> i.Status)
        AND i.Status IN ('RETURNED_REQUESTED')
        AND NOT EXISTS (
            SELECT 1
        FROM Notifications n
        WHERE n.RecipientType = 'SELLER'
            AND n.RecipientID = i.ShopID
            AND n.Type = 'RETURN_REQUESTED'
            AND n.ReferenceType = 'ORDER'
            AND n.ReferenceID = i.OrderShopID
            AND n.CreatedAt >= DATEADD(HOUR, 7, SYSDATETIME())
        );

    INSERT INTO Notifications
        (RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt)
    SELECT
        'SELLER',
        i.ShopID,
        'ORDER_CANCELLED',
        N'Đơn hàng đã bị hủy',
        CONCAT(N'Đơn hàng #', i.OrderShopID, N' đã bị hủy.'),
        'ORDER',
        i.OrderShopID,
        DATEADD(HOUR, 7, SYSDATETIME())
    FROM inserted i
        JOIN deleted d ON d.OrderShopID = i.OrderShopID
    WHERE (d.Status IS NULL OR d.Status <> i.Status)
        AND i.Status IN ('CANCELLED')
        AND NOT EXISTS (
            SELECT 1
        FROM Notifications n
        WHERE n.RecipientType = 'SELLER'
            AND n.RecipientID = i.ShopID
            AND n.Type = 'ORDER_CANCELLED'
            AND n.ReferenceType = 'ORDER'
            AND n.ReferenceID = i.OrderShopID
            AND n.CreatedAt >= DATEADD(HOUR, 7, SYSDATETIME())
        );
END;
GO



---------------------------------------------------
-- TRIGGER 2: Hết hàng (Products)
---------------------------------------------------
DROP TRIGGER IF EXISTS trg_ProductOutOfStockNotification
GO

CREATE OR ALTER TRIGGER trg_ProductOutOfStockNotification
ON Products
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO Notifications
        (RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt)
    SELECT
        'SELLER',
        i.ShopID,
        'OUT_OF_STOCK',
        N'Sản phẩm đã hết hàng',
        CONCAT(N'Sản phẩm "', i.Title, N'" hiện đã hết hàng.'),
        'PRODUCT',
        i.ProductID,
        DATEADD(HOUR, 7, SYSDATETIME())
    FROM inserted i
        JOIN deleted d ON d.ProductID = i.ProductID
    WHERE d.Quantity > 0 AND i.Quantity <= 0
        AND NOT EXISTS (
            SELECT 1
        FROM Notifications n
        WHERE n.Type = 'OUT_OF_STOCK'
            AND n.ReferenceID = i.ProductID
            AND n.RecipientID = i.ShopID
            AND n.CreatedAt >= DATEADD(HOUR, 7, SYSDATETIME())
        );
END;
GO


---------------------------------------------------
-- TRIGGER 3: Voucher
---------------------------------------------------
CREATE OR ALTER TRIGGER trg_VoucherStatusNotification
ON Vouchers
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    ---------------------------------------------------
    -- 1) Voucher vào hoạt động
    ---------------------------------------------------
    INSERT INTO Notifications
        (RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt)
    SELECT
        'SELLER',
        i.ShopID,
        'VOUCHER_ACTIVE',
        N'Voucher đã được kích hoạt',
        CONCAT(N'Voucher "', i.Code, N'" đã được kích hoạt và sẵn sàng sử dụng.'),
        'VOUCHER',
        i.VoucherID,
        DATEADD(HOUR, 7, SYSDATETIME())
    FROM inserted i
        LEFT JOIN deleted d ON d.VoucherID = i.VoucherID
    WHERE (d.Status IS NULL OR d.Status <> i.Status)
        AND i.Status = 'ACTIVE'
        AND NOT EXISTS (
            SELECT 1
        FROM Notifications n
        WHERE n.RecipientType = 'SELLER'
            AND n.RecipientID = i.ShopID
            AND n.Type = 'VOUCHER_ACTIVE'
            AND n.ReferenceType = 'VOUCHER'
            AND n.ReferenceID = i.VoucherID
            AND n.CreatedAt >= DATEADD(HOUR, 7, SYSDATETIME())
        );

    ---------------------------------------------------
    -- 2) Voucher hết lượt / hết số lượng
    ---------------------------------------------------
    INSERT INTO Notifications
        (RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt)
    SELECT
        'SELLER',
        i.ShopID,
        'VOUCHER_OUT_OF_STOCK',
        N'Voucher đã hết lượt sử dụng',
        CONCAT(N'Voucher "', i.Code, N'" đã hết lượt hoặc hết số lượng khả dụng.'),
        'VOUCHER',
        i.VoucherID,
        DATEADD(HOUR, 7, SYSDATETIME())
    FROM inserted i
        JOIN deleted d ON d.VoucherID = i.VoucherID
    WHERE (d.Status IS NULL OR d.Status <> i.Status)
        AND i.Status = 'OUT_OF_STOCK'
        AND NOT EXISTS (
            SELECT 1
        FROM Notifications n
        WHERE n.RecipientType = 'SELLER'
            AND n.RecipientID = i.ShopID
            AND n.Type = 'VOUCHER_OUT_OF_STOCK'
            AND n.ReferenceType = 'VOUCHER'
            AND n.ReferenceID = i.VoucherID
            AND n.CreatedAt >= DATEADD(HOUR, 7, SYSDATETIME())
        );

    ---------------------------------------------------
    -- 3) Voucher hết hạn sử dụng
    ---------------------------------------------------
    INSERT INTO Notifications
        (RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt)
    SELECT
        'SELLER',
        i.ShopID,
        'VOUCHER_EXPIRED',
        N'Voucher đã hết hạn',
        CONCAT(N'Voucher "', i.Code, N'" đã hết hạn vào ngày ', FORMAT(i.EndAt, 'dd/MM/yyyy'), N'.'),
        'VOUCHER',
        i.VoucherID,
        DATEADD(HOUR, 7, SYSDATETIME())
    FROM inserted i
        JOIN deleted d ON d.VoucherID = i.VoucherID
    WHERE (d.Status IS NULL OR d.Status <> i.Status)
        AND i.Status = 'EXPIRED'
        AND NOT EXISTS (
            SELECT 1
        FROM Notifications n
        WHERE n.RecipientType = 'SELLER'
            AND n.RecipientID = i.ShopID
            AND n.Type = 'VOUCHER_EXPIRED'
            AND n.ReferenceType = 'VOUCHER'
            AND n.ReferenceID = i.VoucherID
            AND n.CreatedAt >= DATEADD(HOUR, 7, SYSDATETIME())
        );
END;
GO


------------------------------------------------------------------------------------------------------------------------------------------------------------------

INSERT INTO Roles
    (RoleCode, RoleName)
VALUES
    (N'CUSTOMER', N'Khách hàng'),
    (N'SELLER', N'Người bán'),
    (N'ADMIN', N'Quản trị');

INSERT INTO VAT
    (VATCode, VATRate, Description)
VALUES
    (N'VAT5', 5.00, N'Thuế VAT 5%');
INSERT INTO VAT
    (VATCode, VATRate, Description)
VALUES
    (N'VAT10', 10.00, N'Thuế VAT 10%');

INSERT INTO Category
    (Name, VATCode)
VALUES
    (N'Tiểu thuyết', N'VAT5'),
    (N'Truyện ngắn', N'VAT5'),
    (N'Thơ ca', N'VAT5'),
    (N'Văn học', N'VAT5'),
    (N'Truyện tranh', N'VAT5'),
    (N'Light Novel', N'VAT5'),
    (N'Sách giáo khoa', N'VAT5'),
    (N'Sách tham khảo', N'VAT5'),
    (N'Kinh tế', N'VAT10'),
    (N'Tài chính', N'VAT10'),
    (N'Phát triển bản thân', N'VAT10'),
    (N'Lịch sử', N'VAT5'),
    (N'Chính trị', N'VAT5'),
    (N'Pháp luật', N'VAT5'),
    (N'Khoa học', N'VAT5'),
    (N'Tâm lý', N'VAT5'),
    (N'Y học', N'VAT5'),
    (N'Ẩm thực', N'VAT10'),
    (N'Nuôi dạy con', N'VAT10'),
    (N'Du lịch', N'VAT10'),
    (N'Thời trang', N'VAT10'),
    (N'Nhà cửa', N'VAT10'),
    (N'Nghệ thuật', N'VAT10'),
    (N'Tôn giáo', N'VAT5'),
    (N'Trinh Thám', N'VAT5');

INSERT INTO FlashSales
    (Name, StartAt, EndAt, [Status])
VALUES
    -- 1️⃣ Đang diễn ra
    (N'Flash Sale Halloween 2025',
        '2025-10-20 00:00:00',
        '2025-10-25 23:59:59',
        'ACTIVE'),

    -- 2️⃣ Đã lên lịch (chưa bắt đầu)
    (N'Black Friday 2025',
        '2025-11-29 00:00:00',
        '2025-11-30 23:59:59',
        'SCHEDULED'),

    -- 3️⃣ Đã kết thúc
    (N'Flash Sale Trung Thu 2025',
        '2025-09-01 00:00:00',
        '2025-09-03 23:59:59',
        'ENDED');