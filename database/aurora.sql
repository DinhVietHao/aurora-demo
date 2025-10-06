CREATE TABLE Roles (
  RoleCode  NVARCHAR(20)  NOT NULL PRIMARY KEY,
  RoleName  NVARCHAR(100) NOT NULL
);

CREATE TABLE Users (
  UserID       BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  Email        NVARCHAR(255) NOT NULL,
  [Password]   NVARCHAR(255) NOT NULL,
  FullName     NVARCHAR(150) NOT NULL,
  Phone        NVARCHAR(20)  NULL,
  NationalID   NVARCHAR(20)  NULL,
  AvatarUrl    NVARCHAR(2000) NULL,
  [Status]     NVARCHAR(20),
  CreatedAt    DATETIME2(6)  NOT NULL DEFAULT SYSUTCDATETIME(),
  AuthProvider NVARCHAR(20)  NOT NULL
);

-- Cân nhắc
CREATE TABLE RememberMeTokens (
  TokenID       BIGINT IDENTITY(1,1) PRIMARY KEY,
  UserID        BIGINT NOT NULL,
  Selector      CHAR(18) NOT NULL,
  ValidatorHash VARBINARY(32) NOT NULL,
  ExpiresAt     DATETIME2(6) NOT NULL,
  CreatedAt     DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
  LastUsedAt    DATETIME2(6) NULL,
  CONSTRAINT FK_RM_User FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

CREATE TABLE UserRoles (
  UserID   BIGINT       NOT NULL,
  RoleCode NVARCHAR(20) NOT NULL,
  CONSTRAINT PK_UserRoles PRIMARY KEY (UserID, RoleCode),
  CONSTRAINT FK_UserRoles_Users FOREIGN KEY (UserID) REFERENCES Users(UserID),
  CONSTRAINT FK_UserRoles_Roles FOREIGN KEY (RoleCode) REFERENCES Roles(RoleCode)
);

CREATE TABLE Addresses (
  AddressID     BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  RecipientName NVARCHAR(150) NOT NULL,
  Phone         NVARCHAR(20)  NOT NULL,
  City          NVARCHAR(100) NOT NULL,
  Ward          NVARCHAR(100) NOT NULL,
  Description	NVARCHAR(255) NOT NULL,
  CreatedAt     DATETIME2(6)  NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE Users_Addresses (
  UserID    BIGINT NOT NULL,
  AddressID BIGINT NOT NULL,
  IsDefault BIT    NOT NULL,
  CONSTRAINT PK_Users_Addresses PRIMARY KEY (UserID, AddressID),
  CONSTRAINT FK_UsersAddr_Users   FOREIGN KEY (UserID)    REFERENCES Users(UserID),
  CONSTRAINT FK_UsersAddr_Address FOREIGN KEY (AddressID) REFERENCES Addresses(AddressID)
);

CREATE TABLE Shops (
  ShopID          BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  Name            NVARCHAR(150) NOT NULL,
  Description     NVARCHAR(255) NULL,
  RatingAvg       DECIMAL(3,2) NOT NULL,
  [Status]        NVARCHAR(20) NOT NULL,
  OwnerUserID     BIGINT NOT NULL,
  CreatedAt       DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
  PickupAddressID BIGINT NOT NULL,
  InvoiceEmail    NVARCHAR(255) NOT NULL,
  AvatarUrl       NVARCHAR(2000) NULL,
  RejectReason    NVARCHAR(255) NULL,
  CONSTRAINT FK_Shops_Owner      FOREIGN KEY (OwnerUserID)     REFERENCES Users(UserID),
  CONSTRAINT FK_Shops_PickupAddr FOREIGN KEY (PickupAddressID) REFERENCES Addresses(AddressID)
);

CREATE TABLE VAT (
  VATCode     NVARCHAR(50) PRIMARY KEY,
  VATRate     DECIMAL(5,2) NOT NULL,
  Description NVARCHAR(255) NULL
);

CREATE TABLE Category (
  CategoryID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  Name       NVARCHAR(120) NOT NULL,
  VATCode    NVARCHAR(50)  NOT NULL,
  CONSTRAINT FK_Categories_VAT FOREIGN KEY (VATCode) REFERENCES VAT(VATCode)
);

CREATE TABLE Publishers (
  PublisherID BIGINT IDENTITY(1,1) PRIMARY KEY,
  Name        NVARCHAR(150) NOT NULL
);

CREATE TABLE Products (
  ProductID     BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  ShopID        BIGINT NOT NULL,
  Title         NVARCHAR(255) NOT NULL,
  Description   NVARCHAR(MAX) NULL,
  OriginalPrice DECIMAL(12,2) NOT NULL,
  SalePrice     DECIMAL(12,2) NOT NULL,
  SoldCount     BIGINT NOT NULL DEFAULT 0,
  Stock         INT NOT NULL,
  PublisherID   BIGINT NULL,
  [Status]      NVARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PublishedDate DATE NULL,
  Weight        DECIMAL(10,2) NOT NULL,
  CreatedAt     DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
  CONSTRAINT FK_Products_Shop      FOREIGN KEY (ShopID)      REFERENCES Shops(ShopID),
  CONSTRAINT FK_Products_Publisher FOREIGN KEY (PublisherID) REFERENCES Publishers(PublisherID)
);

CREATE TABLE ProductCategory (
  ProductID  BIGINT NOT NULL,
  CategoryID BIGINT NOT NULL,
  CONSTRAINT PK_ProductCategory PRIMARY KEY (ProductID, CategoryID),
  CONSTRAINT FK_ProductCategory_Product  FOREIGN KEY (ProductID)  REFERENCES Products(ProductID),
  CONSTRAINT FK_ProductCategory_Category FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID)
);

CREATE TABLE ProductImages (
  ImageID   BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  ProductID BIGINT NOT NULL,
  Url       NVARCHAR(2000) NOT NULL,
  IsPrimary BIT NOT NULL,
  CONSTRAINT FK_ProductImages_Product FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

CREATE TABLE Authors (
  AuthorID   BIGINT IDENTITY(1,1) PRIMARY KEY,
  AuthorName NVARCHAR(200) NOT NULL
);

CREATE TABLE BookAuthors (
  ProductID BIGINT NOT NULL,
  AuthorID  BIGINT NOT NULL,
  CONSTRAINT PK_BookAuthors PRIMARY KEY (ProductID, AuthorID),
  CONSTRAINT FK_BookAuthors_Product FOREIGN KEY (ProductID) REFERENCES Products(ProductID),
  CONSTRAINT FK_BookAuthors_Author  FOREIGN KEY (AuthorID)  REFERENCES Authors(AuthorID)
);

CREATE TABLE Languages (
  LanguageCode NVARCHAR(20)  NOT NULL PRIMARY KEY,
  LanguageName NVARCHAR(100) NOT NULL
);

CREATE TABLE BookDetails (
  ProductID    BIGINT NOT NULL PRIMARY KEY,
  Translator   NVARCHAR(200) NULL,
  [Version]    NVARCHAR(50)  NOT NULL,
  CoverType    NVARCHAR(50)  NOT NULL,
  Pages        INT NOT NULL,
  LanguageCode NVARCHAR(20) NOT NULL,
  [Size]       NVARCHAR(50)  NOT NULL,
  ISBN         NVARCHAR(20)  NOT NULL,
  CONSTRAINT FK_BookDetails_Product 
      FOREIGN KEY (ProductID)    REFERENCES Products(ProductID) ON DELETE CASCADE,
  CONSTRAINT FK_BookDetails_Language 
      FOREIGN KEY (LanguageCode) REFERENCES Languages(LanguageCode)
);

INSERT INTO Languages (LanguageCode, LanguageName)
VALUES (N'vi', N'Tiếng Việt'), (N'en', N'Tiếng Anh');

CREATE TABLE CartItems (
  CartItemID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  UserID     BIGINT NOT NULL,
  ProductID  BIGINT NOT NULL,
  Quantity   INT    NOT NULL,
  UnitPrice  DECIMAL(12,2) NOT NULL,
  Subtotal   AS (CAST(Quantity AS DECIMAL(12,2)) * UnitPrice) PERSISTED,
  IsChecked  BIT NOT NULL DEFAULT 0,
  CreatedAt  DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
  CONSTRAINT FK_CartItems_User    FOREIGN KEY (UserID)    REFERENCES Users(UserID),
  CONSTRAINT FK_CartItems_Product FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

CREATE TABLE Vouchers (
  VoucherID      BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  Code           NVARCHAR(40)  NOT NULL,
  DiscountType   NVARCHAR(20)  NOT NULL,
  Value          DECIMAL(12,2) NOT NULL,
  MaxAmount      DECIMAL(12,2) NULL,
  MinOrderAmount DECIMAL(12,2) NOT NULL,
  StartAt        DATETIME2(6) NOT NULL,
  EndAt          DATETIME2(6) NOT NULL,
  UsageLimit     INT NULL,
  PerUserLimit   INT NULL,
  [Status]       NVARCHAR(20) NOT NULL,
  UsageCount     INT NOT NULL DEFAULT 0,
  IsShopVoucher  BIT NOT NULL DEFAULT 0,
  ShopID         BIGINT NULL,
  CONSTRAINT FK_Vouchers_Shop  FOREIGN KEY (ShopID) REFERENCES Shops(ShopID),
  CONSTRAINT UQ_Vouchers_Code UNIQUE (Code)
);

CREATE TABLE UserVouchers (
  UserVoucherID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  VoucherID     BIGINT NOT NULL,
  UserID        BIGINT NOT NULL,
  CONSTRAINT FK_UserVouchers_Voucher FOREIGN KEY (VoucherID) REFERENCES Vouchers(VoucherID),
  CONSTRAINT FK_UserVouchers_User    FOREIGN KEY (UserID)    REFERENCES Users(UserID)
);

CREATE TABLE Orders (
  OrderID        BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  UserID         BIGINT NOT NULL,
  AddressID      BIGINT NOT NULL,
  VoucherID      BIGINT NULL,
  TotalAmount    DECIMAL(12,2) NOT NULL,
  DiscountAmount DECIMAL(12,2) NOT NULL,
  FinalAmount    AS (TotalAmount - DiscountAmount) PERSISTED,
  PaymentMethod  NVARCHAR(20) NOT NULL,
  PaymentStatus  NVARCHAR(20) NOT NULL,
  OrderStatus    NVARCHAR(20) NOT NULL,
  CreatedAt      DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
  DeliveredAt    DATETIME2(6) NULL,
  CancelReason   NVARCHAR(255) NULL,
  CancelledAt    DATETIME2(6) NULL,
  CONSTRAINT FK_Orders_User    FOREIGN KEY (UserID)    REFERENCES Users(UserID),
  CONSTRAINT FK_Orders_Address FOREIGN KEY (AddressID) REFERENCES Addresses(AddressID),
  CONSTRAINT FK_Orders_Voucher FOREIGN KEY (VoucherID) REFERENCES Vouchers(VoucherID)
);

CREATE TABLE OrderShops (
  OrderShopID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  OrderID     BIGINT NOT NULL,
  ShopID      BIGINT NOT NULL,
  ShippingFee DECIMAL(12,2) NOT NULL,
  [Status]    NVARCHAR(20) NOT NULL,
  CreatedAt   DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
  VoucherID   BIGINT NULL,
  CONSTRAINT FK_OrderShops_Order   FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
  CONSTRAINT FK_OrderShops_Shop    FOREIGN KEY (ShopID)  REFERENCES Shops(ShopID),
  CONSTRAINT FK_OrderShops_Voucher FOREIGN KEY (VoucherID) REFERENCES Vouchers(VoucherID)
);

CREATE TABLE FlashSales (
  FlashSaleID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  Name        NVARCHAR(150) NOT NULL,
  ShopID      BIGINT NOT NULL,
  StartAt     DATETIME2(6) NOT NULL,
  EndAt       DATETIME2(6) NOT NULL,
  [Status]    NVARCHAR(20) NOT NULL,
  CreatedAt   DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
  CONSTRAINT FK_FlashSales_Shop FOREIGN KEY (ShopID) REFERENCES Shops(ShopID)
);

CREATE TABLE FlashSaleItems (
  FlashSaleItemID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  FlashSaleID     BIGINT NOT NULL,
  ProductID       BIGINT NOT NULL,
  FlashPrice      DECIMAL(12,2) NOT NULL,
  FsStock         INT NOT NULL,
  PerUserLimit    INT NULL,
  CreatedAt       DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
  CONSTRAINT FK_FSI_FlashSale FOREIGN KEY (FlashSaleID) REFERENCES FlashSales(FlashSaleID),
  CONSTRAINT FK_FSI_Product   FOREIGN KEY (ProductID)   REFERENCES Products(ProductID)
);

CREATE TABLE OrderItems (
  OrderItemID     BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  OrderShopID     BIGINT NOT NULL,
  ProductID       BIGINT NOT NULL,
  FlashSaleItemID BIGINT NULL,
  Quantity        INT    NOT NULL,
  UnitPrice       DECIMAL(12,2) NOT NULL,
  Subtotal        AS (CAST(Quantity AS DECIMAL(12,2)) * UnitPrice) PERSISTED,
  VATRate         DECIMAL(5,2) NOT NULL DEFAULT 0,
  CONSTRAINT FK_OrderItems_OrderShop FOREIGN KEY (OrderShopID) REFERENCES OrderShops(OrderShopID),
  CONSTRAINT FK_OrderItems_Product   FOREIGN KEY (ProductID)   REFERENCES Products(ProductID),
  CONSTRAINT FK_OrderItems_Flash     FOREIGN KEY (FlashSaleItemID) REFERENCES FlashSaleItems(FlashSaleItemID)
);

CREATE TABLE Payments (
  PaymentID      BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  OrderID        BIGINT NOT NULL UNIQUE,
  Amount         DECIMAL(12,2) NOT NULL,
  Method         NVARCHAR(20) NOT NULL,
  TransactionRef NVARCHAR(100) NOT NULL,
  [Status]       NVARCHAR(20) NOT NULL,
  CreatedAt      DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
  CONSTRAINT FK_Payments_Order FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)
);

CREATE TABLE Reviews (
  ReviewID    BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  OrderItemID BIGINT NOT NULL,
  UserID      BIGINT NOT NULL,
  Rating      TINYINT NOT NULL,
  Comment     NVARCHAR(255) NULL,
  CreatedAt   DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
  CONSTRAINT FK_Reviews_OrderItem FOREIGN KEY (OrderItemID) REFERENCES OrderItems(OrderItemID),
  CONSTRAINT FK_Reviews_User      FOREIGN KEY (UserID)      REFERENCES Users(UserID)
);

CREATE TABLE ReviewImages (
  ReviewImageID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  ReviewID      BIGINT NOT NULL,
  Url           NVARCHAR(2000) NOT NULL,
  Caption       NVARCHAR(255) NULL,
  IsPrimary     BIT NOT NULL,
  CreatedAt     DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
  CONSTRAINT FK_ReviewImages_Review FOREIGN KEY (ReviewID) REFERENCES Reviews(ReviewID)
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

    DECLARE @Blocked TABLE (ProductID BIGINT, Reason NVARCHAR(255));

    -- 1️⃣ Kiểm tra sản phẩm đang tham gia Flash Sale
    INSERT INTO @Blocked (ProductID, Reason)
    SELECT d.ProductID, N'Sản phẩm đang tham gia Flash Sale'
    FROM deleted d
    WHERE EXISTS (
        SELECT 1 FROM FlashSaleItems f
        WHERE f.ProductID = d.ProductID
    );

    -- 2️⃣ Kiểm tra sản phẩm nằm trong các đơn hàng đang xử lý (đã xác nhận, đã đóng gói, đang giao)
    INSERT INTO @Blocked (ProductID, Reason)
    SELECT DISTINCT d.ProductID, N'Sản phẩm đang nằm trong đơn hàng đang xử lý'
    FROM deleted d
    JOIN OrderItems oi ON oi.ProductID = d.ProductID
    JOIN OrderShops os ON os.OrderShopID = oi.OrderShopID
    JOIN Orders o ON o.OrderID = os.OrderID
    WHERE o.OrderStatus IN (N'Đã xác nhận', N'Đã đóng gói', N'Đang giao');

    -- 3️⃣ Nếu có sản phẩm bị chặn xóa thì báo lỗi, không xóa
    IF EXISTS (SELECT 1 FROM @Blocked)
    BEGIN
        DECLARE @msg NVARCHAR(MAX) = N'Không thể xóa các sản phẩm sau do còn ràng buộc:' + CHAR(13);
        SELECT @msg = @msg + N'• ProductID: ' + CAST(ProductID AS NVARCHAR) + N' – ' + Reason + CHAR(13)
        FROM @Blocked;

        RAISERROR(@msg, 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END;

    -- 4️⃣ Nếu hợp lệ → Xóa dữ liệu liên quan trước
    DELETE FROM BookDetails WHERE ProductID IN (SELECT ProductID FROM deleted);
    DELETE FROM BookAuthors WHERE ProductID IN (SELECT ProductID FROM deleted);
    DELETE FROM ProductImages WHERE ProductID IN (SELECT ProductID FROM deleted);
    DELETE FROM ProductCategory WHERE ProductID IN (SELECT ProductID FROM deleted);
    DELETE FROM CartItems WHERE ProductID IN (SELECT ProductID FROM deleted);
    DELETE FROM FlashSaleItems WHERE ProductID IN (SELECT ProductID FROM deleted);
    DELETE FROM OrderItems WHERE ProductID IN (SELECT ProductID FROM deleted);
    DELETE FROM Reviews WHERE OrderItemID IN (
        SELECT OrderItemID FROM OrderItems WHERE ProductID IN (SELECT ProductID FROM deleted)
    );

    -- Cuối cùng xóa Product
    DELETE FROM Products WHERE ProductID IN (SELECT ProductID FROM deleted);
END;
GO

INSERT INTO Roles (RoleCode, RoleName) VALUES 
(N'CUSTOMER', N'Khách hàng'),
(N'SELLER', N'Người bán'),
(N'ADMIN', N'Quản trị');

INSERT INTO VAT (VATCode, VATRate, Description)
VALUES (N'VAT5', 5.00, N'Thuế VAT 5%');
INSERT INTO VAT (VATCode, VATRate, Description)
VALUES (N'VAT10', 10.00, N'Thuế VAT 10%');

INSERT INTO Category (Name, VATCode)
VALUES
(N'Tiểu thuyết',           N'VAT5'),
(N'Truyện ngắn',           N'VAT5'),
(N'Thơ ca',                N'VAT5'),
(N'Văn học',               N'VAT5'),
(N'Truyện tranh',          N'VAT5'),
(N'Light Novel',           N'VAT5'),
(N'Sách giáo khoa',        N'VAT5'),
(N'Sách tham khảo',        N'VAT5'),
(N'Kinh tế',               N'VAT10'),
(N'Tài chính',             N'VAT10'),
(N'Phát triển bản thân',   N'VAT10'),
(N'Lịch sử',               N'VAT5'),
(N'Chính trị',             N'VAT5'),
(N'Pháp luật',             N'VAT5'),
(N'Khoa học',              N'VAT5'),
(N'Tâm lý',                N'VAT5'),
(N'Y học',                 N'VAT5'),
(N'Ẩm thực',               N'VAT10'),
(N'Nuôi dạy con',          N'VAT10'),
(N'Du lịch',               N'VAT10'),
(N'Thời trang',            N'VAT10'),
(N'Nhà cửa',               N'VAT10'),
(N'Nghệ thuật',            N'VAT10'),
(N'Tôn giáo',              N'VAT5'),
(N'Trinh Thám',            N'VAT5');