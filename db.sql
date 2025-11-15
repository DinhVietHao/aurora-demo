

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
    RatingAvg DECIMAL(3,2) NOT NULL DEFAULT 0,
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
    [Status] NVARCHAR
    (20) NOT NULL DEFAULT 'ACTIVE',
    PublishedDate DATE NULL,
    Weight DECIMAL
    (10,2) NOT NULL,
    RejectReason NVARCHAR
    (255) NULL,
    CreatedAt DATETIME2
    (6) NOT NULL DEFAULT SYSUTCDATETIME
    (),
    CONSTRAINT FK_Products_Shop      FOREIGN KEY
    (ShopID)      REFERENCES Shops
    (ShopID),
    CONSTRAINT FK_Products_Publisher FOREIGN KEY
    (PublisherID) REFERENCES Publishers
    (PublisherID)
);

CREATE TABLE ProductCategory
(
    ProductID BIGINT NOT NULL,
    CategoryID BIGINT NOT NULL,
    IsPrimary BIT NOT NULL DEFAULT 0,
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

CREATE TABLE Payments
(
    PaymentID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    Amount DECIMAL(12,2) NOT NULL,
    RefundedAmount DECIMAL(12,2) NOT NULL DEFAULT 0,
    TransactionRef NVARCHAR(100) NOT NULL,
    Status NVARCHAR(20) NOT NULL,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt DATETIME2(6) NULL
);

CREATE TABLE OrderShops
(
    OrderShopID BIGINT IDENTITY(1,1) PRIMARY KEY,
    UserID BIGINT NOT NULL,
    ShopID BIGINT NOT NULL,
    PaymentID BIGINT NOT NULL,
    Address NVARCHAR(255) NOT NULL DEFAULT N'',
    VoucherShopID BIGINT NULL,
    VoucherDiscountID BIGINT NULL,
    VoucherShipID BIGINT NULL,
    Subtotal DECIMAL(12,2) NOT NULL,
    ShopDiscount DECIMAL(12,2) NOT NULL DEFAULT 0,
    SystemDiscount DECIMAL(12,2) NOT NULL DEFAULT 0,
    ShippingFee DECIMAL(12,2) NOT NULL DEFAULT 0,
    SystemShippingDiscount DECIMAL(12,2) NOT NULL DEFAULT 0,
    FinalAmount DECIMAL(12,2) NOT NULL,
    [Status] NVARCHAR(20) NOT NULL,
    PlatformFee DECIMAL(12,2) NOT NULL DEFAULT 0,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt DATETIME2(6) NULL,
    CancelReason NVARCHAR(255) NULL,
    ReturnReason NVARCHAR(255) NULL,
    CONSTRAINT FK_Orders_User FOREIGN KEY (UserID) REFERENCES Users(UserID),
    CONSTRAINT FK_Orders_Shop FOREIGN KEY (ShopID) REFERENCES Shops(ShopID),
    CONSTRAINT FK_Orders_VoucherShop FOREIGN KEY (VoucherShopID) REFERENCES Vouchers(VoucherID),
    CONSTRAINT FK_Orders_VoucherDiscount FOREIGN KEY (VoucherDiscountID) REFERENCES Vouchers(VoucherID),
    CONSTRAINT FK_Orders_VoucherShip FOREIGN KEY (VoucherShipID) REFERENCES Vouchers(VoucherID),
    CONSTRAINT FK_OrderShops_Payment FOREIGN KEY (PaymentID) REFERENCES Payments(PaymentID)
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
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME()
);

CREATE TABLE FlashSaleItems
(
    FlashSaleItemID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    FlashSaleID BIGINT NOT NULL,
    ProductID BIGINT NOT NULL,
    ShopID BIGINT NOT NULL,
    FlashPrice DECIMAL(12,2) NOT NULL,
    FsStock INT NOT NULL,
    ApprovalStatus NVARCHAR(20) NOT NULL,
    SoldCount BIGINT NOT NULL DEFAULT 0,
    CreatedAt DATETIME2(6) NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_FSI_FlashSale FOREIGN KEY (FlashSaleID) REFERENCES FlashSales(FlashSaleID),
    CONSTRAINT FK_FSI_Product   FOREIGN KEY (ProductID)   REFERENCES Products(ProductID),
    CONSTRAINT FK_FlashSaleItems_Shop FOREIGN KEY (ShopID) REFERENCES Shops(ShopID)
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

CREATE TABLE Documents
(
    DocumentID BIGINT IDENTITY(1,1) PRIMARY KEY,
    Source NVARCHAR(100) NOT NULL,
    SourceID BIGINT NULL,
    Title NVARCHAR(255) NULL,
    Content NVARCHAR(MAX) NOT NULL,
    Embedding NVARCHAR(MAX) NULL,
    CreatedAt DATETIME2 DEFAULT SYSUTCDATETIME(),
    UpdatedAt DATETIME2 DEFAULT SYSUTCDATETIME()
);

CREATE TABLE Setting
(
    SettingID INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    SettingKey NVARCHAR(100) NOT NULL,
    SettingValue NVARCHAR(MAX) NULL,
    Description NVARCHAR(500) NULL,
    CreatedAt DATETIME2(7) NOT NULL DEFAULT SYSUTCDATETIME(),
    UpdatedAt DATETIME2(7) NULL
);

INSERT INTO Languages
    (LanguageCode, LanguageName)
VALUES
    (N'vi', N'Tiếng Việt'),
    (N'en', N'Tiếng Anh');


INSERT INTO Roles
VALUES
    (N'CUSTOMER', N'Khách hàng'),
    (N'SELLER', N'Người bán'),
    (N'ADMIN', N'Quản trị');

INSERT INTO VAT
VALUES
    (N'VAT5', 5.00, N'Thuế VAT 5%'),
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
    (N'Trinh thám', N'VAT5');

INSERT INTO FlashSales
    (Name, StartAt, EndAt, [Status])
VALUES
    (N'Flash Sale Halloween 2025', '2025-10-20 00:00:00', '2025-10-25 23:59:59', 'ACTIVE'),
    (N'Black Friday 2025', '2025-11-29 00:00:00', '2025-11-30 23:59:59', 'SCHEDULED'),
    (N'Flash Sale Trung Thu 2025', '2025-09-01 00:00:00', '2025-09-03 23:59:59', 'ENDED');

INSERT INTO [dbo].[Setting]
    ([SettingKey], [SettingValue], [Description], [CreatedAt])
VALUES
    (N'Platform_fee', N'5', N'Phí sàn theo đơn hàng', SYSUTCDATETIME());

-- =============================================
-- INSERT SAMPLE DATA
-- =============================================

-- Users
INSERT INTO Users (Email, [Password], FullName, AvatarUrl, [Status], AuthProvider)
VALUES
    (N'admin@aurora.vn', N'$2a$10$XQjhF4Z9Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0', N'Admin Aurora', N'https://i.pravatar.cc/150?img=1', N'ACTIVE', N'LOCAL'),
    (N'seller1@gmail.com', N'$2a$10$XQjhF4Z9Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0', N'Nguyễn Văn An', N'https://i.pravatar.cc/150?img=2', N'ACTIVE', N'LOCAL'),
    (N'seller2@gmail.com', N'$2a$10$XQjhF4Z9Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0', N'Trần Thị Bình', N'https://i.pravatar.cc/150?img=3', N'ACTIVE', N'LOCAL'),
    (N'customer1@gmail.com', N'$2a$10$XQjhF4Z9Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0', N'Lê Minh Cường', N'https://i.pravatar.cc/150?img=4', N'ACTIVE', N'LOCAL'),
    (N'customer2@gmail.com', N'$2a$10$XQjhF4Z9Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0', N'Phạm Thu Hà', N'https://i.pravatar.cc/150?img=5', N'ACTIVE', N'GOOGLE'),
    (N'customer3@gmail.com', N'$2a$10$XQjhF4Z9Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0Z0', N'Hoàng Văn Đức', N'https://i.pravatar.cc/150?img=6', N'ACTIVE', N'LOCAL');

-- UserRoles
INSERT INTO UserRoles (UserID, RoleCode)
VALUES
    (1, N'ADMIN'),
    (2, N'SELLER'),
    (2, N'CUSTOMER'),
    (3, N'SELLER'),
    (3, N'CUSTOMER'),
    (4, N'CUSTOMER'),
    (5, N'CUSTOMER'),
    (6, N'CUSTOMER');

-- Addresses
INSERT INTO Addresses (RecipientName, Phone, City, ProvinceID, District, DistrictID, Ward, WardCode, Description)
VALUES
    (N'Nguyễn Văn An', N'0901234567', N'Hồ Chí Minh', 202, N'Quận 1', 3440, N'Phường Bến Nghé', N'20202', N'123 Nguyễn Huệ'),
    (N'Trần Thị Bình', N'0912345678', N'Hà Nội', 201, N'Quận Hoàn Kiếm', 3330, N'Phường Hàng Bạc', N'20101', N'456 Hoàn Kiếm'),
    (N'Lê Minh Cường', N'0923456789', N'Hồ Chí Minh', 202, N'Quận 3', 3460, N'Phường 1', N'20302', N'789 Võ Văn Tần'),
    (N'Phạm Thu Hà', N'0934567890', N'Hà Nội', 201, N'Quận Đống Đa', 3340, N'Phường Láng Hạ', N'20103', N'321 Láng Hạ'),
    (N'Hoàng Văn Đức', N'0945678901', N'Đà Nẵng', 203, N'Quận Hải Châu', 3550, N'Phường Thạch Thang', N'20301', N'654 Trần Phú'),
    (N'Kho Nhà Sách Kim Đồng', N'0281234567', N'Hồ Chí Minh', 202, N'Quận 1', 3440, N'Phường Bến Nghé', N'20202', N'55 Quang Trung, P.10');

-- Users_Addresses
INSERT INTO Users_Addresses (UserID, AddressID, IsDefault)
VALUES
    (2, 1, 1),
    (3, 2, 1),
    (4, 3, 1),
    (5, 4, 1),
    (6, 5, 1);

-- Shops
INSERT INTO Shops (Name, Description, RatingAvg, [Status], OwnerUserID, PickupAddressID, InvoiceEmail, AvatarUrl)
VALUES
    (N'Nhà Sách Kim Đồng', N'Chuyên sách thiếu nhi và văn học', 4.8, N'ACTIVE', 2, 6, N'kimdong@shop.vn', N'https://via.placeholder.com/200?text=Kim+Dong'),
    (N'Fahasa BookStore', N'Cửa hàng sách đa dạng thể loại', 4.9, N'ACTIVE', 3, 2, N'fahasa@shop.vn', N'https://via.placeholder.com/200?text=Fahasa');

-- Publishers
INSERT INTO Publishers (Name)
VALUES
    (N'Nhà Xuất Bản Kim Đồng'),
    (N'Nhà Xuất Bản Trẻ'),
    (N'Nhà Xuất Bản Văn Học'),
    (N'Nhà Xuất Bản Thế Giới'),
    (N'Nhà Xuất Bản Hội Nhà Văn'),
    (N'Alphabooks'),
    (N'First News'),
    (N'Nhà Xuất Bản Tổng Hợp TP.HCM');

-- Authors
INSERT INTO Authors (AuthorName)
VALUES
    (N'Nguyễn Nhật Ánh'),
    (N'Tô Hoài'),
    (N'Vũ Trọng Phụng'),
    (N'Nam Cao'),
    (N'Ngô Tất Tố'),
    (N'Nguyễn Du'),
    (N'Haruki Murakami'),
    (N'Paulo Coelho'),
    (N'Dale Carnegie'),
    (N'Robin Sharma'),
    (N'Yuval Noah Harari'),
    (N'Napoleon Hill'),
    (N'Thích Nhất Hạnh'),
    (N'Rosie Nguyễn');

-- Products
SET IDENTITY_INSERT Products ON;
INSERT INTO Products (ProductID, ShopID, Title, Description, OriginalPrice, SalePrice, SoldCount, Quantity, PublisherID, [Status], PublishedDate, Weight, CreatedAt)
VALUES
    (1, 1, N'Mắt Biếc', N'Tác phẩm nổi tiếng của nhà văn Nguyễn Nhật Ánh, kể về tình yêu trong sáng tuổi học trò', 95000, 85000, 1250, 150, 2, N'ACTIVE', '2020-01-15', 250.00, SYSUTCDATETIME()),
    (2, 1, N'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', N'Câu chuyện về tuổi thơ dữ dội và tinh khôi', 120000, 108000, 890, 200, 2, N'ACTIVE', '2010-11-20', 350.00, SYSUTCDATETIME()),
    (3, 1, N'Dế Mèn Phiêu Lưu Ký', N'Tác phẩm văn học thiếu nhi kinh điển của Việt Nam', 75000, 70000, 2100, 300, 1, N'ACTIVE', '1941-05-10', 200.00, SYSUTCDATETIME()),
    (4, 2, N'Số Đỏ', N'Tiểu thuyết nổi tiếng của Vũ Trọng Phụng về xã hội Hà Nội đầu thế kỷ 20', 85000, 80000, 450, 100, 3, N'ACTIVE', '1936-08-01', 280.00, SYSUTCDATETIME()),
    (5, 2, N'Chí Phèo', N'Truyện ngắn nổi tiếng của Nam Cao', 65000, 60000, 680, 120, 3, N'ACTIVE', '1941-01-01', 150.00, SYSUTCDATETIME()),
    (6, 2, N'Tắt Đèn', N'Tiểu thuyết hiện thực của Ngô Tất Tố', 95000, 90000, 520, 80, 3, N'ACTIVE', '1939-12-01', 320.00, SYSUTCDATETIME()),
    (7, 1, N'Kafka Bên Bờ Biển', N'Tiểu thuyết của Haruki Murakami', 180000, 165000, 750, 100, 4, N'ACTIVE', '2005-03-15', 450.00, SYSUTCDATETIME()),
    (8, 1, N'Nhà Giả Kim', N'Tiểu thuyết nổi tiếng của Paulo Coelho', 79000, 75000, 3200, 500, 4, N'ACTIVE', '2013-05-20', 220.00, SYSUTCDATETIME()),
    (9, 2, N'Đắc Nhân Tâm', N'Sách về kỹ năng sống và giao tiếp', 95000, 86000, 5600, 800, 4, N'ACTIVE', '2015-09-10', 350.00, SYSUTCDATETIME()),
    (10, 2, N'5h Sáng Và Những Kẻ Mộng Mơ', N'Sách về phát triển bản thân của Robin Sharma', 128000, 120000, 420, 150, 7, N'ACTIVE', '2020-06-15', 380.00, SYSUTCDATETIME()),
    (11, 1, N'Sapiens: Lược Sử Loài Người', N'Cuốn sách về lịch sử loài người từ thời tiền sử đến hiện đại', 220000, 198000, 980, 200, 4, N'ACTIVE', '2018-11-20', 600.00, SYSUTCDATETIME()),
    (12, 1, N'13 Nguyên Tắc Nghĩ Giàu Làm Giàu', N'Think and Grow Rich - Napoleon Hill', 115000, 105000, 1400, 250, 4, N'ACTIVE', '2014-07-08', 420.00, SYSUTCDATETIME()),
    (13, 2, N'Thiền Là Sống Trong Giây Phút Hiện Tại', N'Sách về thiền và chánh niệm của Thích Nhất Hạnh', 98000, 92000, 670, 180, 4, N'ACTIVE', '2016-03-12', 280.00, SYSUTCDATETIME()),
    (14, 2, N'Cà Phê Cùng Tony', N'Sách phát triển bản thân của Rosie Nguyễn', 108000, 99000, 890, 200, 8, N'ACTIVE', '2021-08-25', 300.00, SYSUTCDATETIME()),
    (15, 1, N'Truyện Kiều', N'Tác phẩm kinh điển của Nguyễn Du', 125000, 118000, 340, 90, 5, N'ACTIVE', '1820-01-01', 350.00, SYSUTCDATETIME());
SET IDENTITY_INSERT Products OFF;

-- ProductImages
INSERT INTO ProductImages (ProductID, Url, IsPrimary)
VALUES
    (1, N'https://salt.tikicdn.com/cache/750x750/ts/product/5e/18/24/2a6154ba3e93e2a6c5f2e43f8c5f5e99.jpg.webp', 1),
    (2, N'https://salt.tikicdn.com/cache/750x750/ts/product/68/8f/67/01e61d35ac13f5a6bd4d3a6cbaa4dbf1.jpg.webp', 1),
    (3, N'https://salt.tikicdn.com/cache/750x750/ts/product/d7/90/89/20c53ee87e5addfd002e31e1c8a1ad81.jpg.webp', 1),
    (4, N'https://salt.tikicdn.com/cache/750x750/ts/product/3b/5a/42/78a1c0389ff4cc7d524fbae1bd7ad602.jpg.webp', 1),
    (5, N'https://salt.tikicdn.com/cache/750x750/ts/product/87/88/8c/e896e3cd0d303c3f02c69e45e8d2e9f2.jpg.webp', 1),
    (6, N'https://salt.tikicdn.com/cache/750x750/ts/product/23/ce/00/89d63863c6163dba6c021b5e101d7e16.jpg.webp', 1),
    (7, N'https://salt.tikicdn.com/cache/750x750/ts/product/cb/c8/a9/be96452a9b577636a69d1a4fa071e906.jpg.webp', 1),
    (8, N'https://salt.tikicdn.com/cache/750x750/ts/product/88/d8/68/26c7df2427e01cf7494ce886e5f17c85.jpg.webp', 1),
    (9, N'https://salt.tikicdn.com/cache/750x750/ts/product/67/46/5e/80c189fef7e5e31ccfbfa63fd1eaf3e4.jpg.webp', 1),
    (10, N'https://salt.tikicdn.com/cache/750x750/ts/product/8a/24/6e/c423c3c85c29f8477e3de1f0ebdd14e1.jpg.webp', 1),
    (11, N'https://salt.tikicdn.com/cache/750x750/ts/product/6e/24/eb/6fa6f922a80e44136ea4e919c28933c6.jpg.webp', 1),
    (12, N'https://salt.tikicdn.com/cache/750x750/ts/product/67/d8/49/5f3484cc9da3e1e31930a3e09cf0a614.jpg.webp', 1),
    (13, N'https://salt.tikicdn.com/cache/750x750/ts/product/fb/0f/5a/2c5e2f58c821a47d0bd34ecf86c7d0ba.jpg.webp', 1),
    (14, N'https://salt.tikicdn.com/cache/750x750/ts/product/26/13/ba/58f5c41fb4c388ffc7a8deca0f80e3eb.jpg.webp', 1),
    (15, N'https://salt.tikicdn.com/cache/750x750/ts/product/ed/26/04/c983bb0e81f03ea8c0e95f6c7a766478.jpg.webp', 1);

-- BookDetails
INSERT INTO BookDetails (ProductID, Translator, [Version], CoverType, Pages, LanguageCode, [Size], ISBN)
VALUES
    (1, N'', N'Tái bản lần 30', N'Bìa mềm', 250, N'vi', N'14x20.5 cm', N'8934974170808'),
    (2, N'', N'Tái bản lần 25', N'Bìa mềm', 368, N'vi', N'14x20.5 cm', N'8934974179313'),
    (3, N'', N'Tái bản lần 50', N'Bìa mềm', 180, N'vi', N'13x20 cm', N'8935086854815'),
    (4, N'', N'Tái bản lần 10', N'Bìa mềm', 220, N'vi', N'14x20.5 cm', N'8935086853764'),
    (5, N'', N'Tái bản lần 15', N'Bìa mềm', 120, N'vi', N'13x19 cm', N'8935086852798'),
    (6, N'', N'Tái bản lần 12', N'Bìa mềm', 280, N'vi', N'14x20.5 cm', N'8935086851432'),
    (7, N'Dương Tường', N'Tái bản 2024', N'Bìa cứng', 520, N'vi', N'15.5x23 cm', N'8935235228429'),
    (8, N'Lê Chu Cầu', N'Tái bản lần 40', N'Bìa mềm', 227, N'vi', N'14x20.5 cm', N'8935086854082'),
    (9, N'Nguyễn Hiến Lê', N'Tái bản lần 60', N'Bìa mềm', 320, N'vi', N'14.5x20.5 cm', N'8935235221208'),
    (10, N'Ngọc Huyền', N'Lần 1', N'Bìa mềm', 408, N'vi', N'14x20.5 cm', N'8935235230255'),
    (11, N'First News', N'Tái bản 2023', N'Bìa cứng', 540, N'vi', N'16x24 cm', N'8935235223110'),
    (12, N'Nguyễn Phương Hằng', N'Tái bản lần 20', N'Bìa mềm', 380, N'vi', N'14x20.5 cm', N'8935235220713'),
    (13, N'Nguyên Anh', N'Tái bản 2021', N'Bìa mềm', 256, N'vi', N'14x20.5 cm', N'8935235227866'),
    (14, N'', N'Tái bản 2022', N'Bìa mềm', 280, N'vi', N'14x20.5 cm', N'8935235233744'),
    (15, N'', N'Tái bản lần 100', N'Bìa cứng', 350, N'vi', N'17x24 cm', N'8935086850145');

-- BookAuthors
INSERT INTO BookAuthors (ProductID, AuthorID)
VALUES
    (1, 1),
    (2, 1),
    (3, 2),
    (4, 3),
    (5, 4),
    (6, 5),
    (7, 7),
    (8, 8),
    (9, 9),
    (10, 10),
    (11, 11),
    (12, 12),
    (13, 13),
    (14, 14),
    (15, 6);

-- ProductCategory
INSERT INTO ProductCategory (ProductID, CategoryID, IsPrimary)
VALUES
    (1, 4, 1),
    (1, 1, 0),
    (2, 4, 1),
    (2, 1, 0),
    (3, 4, 1),
    (4, 1, 1),
    (4, 4, 0),
    (5, 2, 1),
    (5, 4, 0),
    (6, 1, 1),
    (6, 4, 0),
    (7, 1, 1),
    (7, 4, 0),
    (8, 1, 1),
    (8, 4, 0),
    (9, 11, 1),
    (10, 11, 1),
    (11, 12, 1),
    (11, 15, 0),
    (12, 11, 1),
    (12, 9, 0),
    (13, 16, 1),
    (13, 11, 0),
    (14, 11, 1),
    (15, 4, 1),
    (15, 3, 0);

-- CartItems
INSERT INTO CartItems (UserID, ProductID, Quantity, UnitPrice, IsChecked)
VALUES
    (4, 8, 1, 75000, 1),
    (4, 9, 2, 86000, 1),
    (5, 1, 1, 85000, 1),
    (5, 11, 1, 198000, 0),
    (6, 3, 3, 70000, 1),
    (6, 14, 1, 99000, 1);

-- Vouchers
INSERT INTO Vouchers (Code, DiscountType, Value, MaxAmount, MinOrderAmount, StartAt, EndAt, UsageLimit, PerUserLimit, [Status], UsageCount, IsShopVoucher, ShopID, [Description])
VALUES
    (N'FREESHIP50K', N'FIXED', 50000, 50000, 0, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 10000, 5, N'ACTIVE', 1250, 0, NULL, N'Miễn phí ship đơn hàng từ 0đ'),
    (N'GIAMGIA10', N'PERCENTAGE', 10, 100000, 200000, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 5000, 3, N'ACTIVE', 856, 0, NULL, N'Giảm 10% tối đa 100K cho đơn từ 200K'),
    (N'KIMDONG50K', N'FIXED', 50000, 50000, 300000, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 500, 2, N'ACTIVE', 127, 1, 1, N'Giảm 50K cho đơn hàng từ 300K tại Kim Đồng'),
    (N'FAHASA20', N'PERCENTAGE', 20, 150000, 500000, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 300, 2, N'ACTIVE', 89, 1, 2, N'Giảm 20% tối đa 150K tại Fahasa'),
    (N'NEWCUSTOMER', N'FIXED', 100000, 100000, 0, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 1000, 1, N'ACTIVE', 234, 0, NULL, N'Ưu đãi 100K cho khách hàng mới');

-- Payments
SET IDENTITY_INSERT Payments ON;
INSERT INTO Payments (PaymentID, Amount, RefundedAmount, TransactionRef, Status, CreatedAt)
VALUES
    (1, 795000, 0, N'PAY_20241101_001', N'COMPLETED', '2024-11-01 10:30:00'),
    (2, 580000, 0, N'PAY_20241102_002', N'COMPLETED', '2024-11-02 14:20:00'),
    (3, 1250000, 0, N'PAY_20241103_003', N'COMPLETED', '2024-11-03 09:15:00'),
    (4, 432000, 0, N'PAY_20241104_004', N'COMPLETED', '2024-11-04 16:45:00'),
    (5, 335000, 335000, N'PAY_20241105_005', N'REFUNDED', '2024-11-05 11:20:00'),
    (6, 668000, 0, N'PAY_20241106_006', N'COMPLETED', '2024-11-06 15:30:00'),
    (7, 542000, 0, N'PAY_20241107_007', N'COMPLETED', '2024-11-07 10:10:00'),
    (8, 1890000, 0, N'PAY_20241108_008', N'COMPLETED', '2024-11-08 13:25:00');
SET IDENTITY_INSERT Payments OFF;

-- OrderShops
SET IDENTITY_INSERT OrderShops ON;
INSERT INTO OrderShops (OrderShopID, UserID, ShopID, PaymentID, Address, VoucherShopID, VoucherDiscountID, VoucherShipID, Subtotal, ShopDiscount, SystemDiscount, ShippingFee, SystemShippingDiscount, FinalAmount, [Status], PlatformFee, CreatedAt, UpdatedAt)
VALUES
    (1, 4, 1, 1, N'789 Võ Văn Tần, Phường 1, Quận 3, Hồ Chí Minh', NULL, 2, 1, 768000, 0, 76800, 30000, 30000, 691200, N'DELIVERED', 34560, '2024-11-01 10:30:00', '2024-11-05 10:30:00'),
    (2, 5, 2, 2, N'321 Láng Hạ, Phường Láng Hạ, Quận Đống Đa, Hà Nội', 4, NULL, 1, 580000, 116000, 0, 35000, 35000, 464000, N'DELIVERED', 23200, '2024-11-02 14:20:00', '2024-11-06 14:20:00'),
    (3, 6, 1, 3, N'654 Trần Phú, Phường Thạch Thang, Quận Hải Châu, Đà Nẵng', NULL, NULL, NULL, 1230000, 0, 0, 40000, 0, 1270000, N'SHIPPING', 61500, '2024-11-03 09:15:00', '2024-11-10 09:15:00'),
    (4, 4, 2, 4, N'789 Võ Văn Tần, Phường 1, Quận 3, Hồ Chí Minh', NULL, NULL, 1, 400000, 0, 0, 30000, 30000, 400000, N'DELIVERED', 20000, '2024-11-04 16:45:00', '2024-11-08 16:45:00'),
    (5, 5, 1, 5, N'321 Láng Hạ, Phường Láng Hạ, Quận Đống Đa, Hà Nội', NULL, NULL, NULL, 335000, 0, 0, 35000, 0, 370000, N'CANCELLED', 0, '2024-11-05 11:20:00', '2024-11-05 15:30:00'),
    (6, 6, 2, 6, N'654 Trần Phú, Phường Thạch Thang, Quận Hải Châu, Đà Nẵng', NULL, 2, NULL, 668000, 0, 66800, 35000, 0, 636200, N'DELIVERED', 31810, '2024-11-06 15:30:00', '2024-11-10 15:30:00'),
    (7, 4, 1, 7, N'789 Võ Văn Tần, Phường 1, Quận 3, Hồ Chí Minh', 3, NULL, NULL, 542000, 50000, 0, 30000, 0, 522000, N'PROCESSING', 24600, '2024-11-07 10:10:00', '2024-11-07 10:10:00'),
    (8, 5, 2, 8, N'321 Láng Hạ, Phường Láng Hạ, Quận Đống Đa, Hà Nội', NULL, NULL, NULL, 1890000, 0, 0, 40000, 0, 1930000, N'CONFIRMED', 94500, '2024-11-08 13:25:00', '2024-11-09 08:15:00');
SET IDENTITY_INSERT OrderShops OFF;

-- OrderItems
INSERT INTO OrderItems (OrderShopID, ProductID, FlashSaleItemID, Quantity, OriginalPrice, SalePrice, Subtotal, VATRate)
VALUES
    (1, 1, NULL, 2, 95000, 85000, 170000, 5.00),
    (1, 2, NULL, 2, 120000, 108000, 216000, 5.00),
    (1, 8, NULL, 5, 79000, 75000, 375000, 5.00),
    (2, 9, NULL, 2, 95000, 86000, 172000, 10.00),
    (2, 5, NULL, 4, 65000, 60000, 240000, 5.00),
    (2, 4, NULL, 2, 85000, 80000, 160000, 5.00),
    (3, 11, 1, 5, 220000, 198000, 990000, 5.00),
    (3, 7, NULL, 1, 180000, 165000, 165000, 5.00),
    (3, 1, NULL, 1, 95000, 85000, 85000, 5.00),
    (4, 13, NULL, 2, 98000, 92000, 184000, 5.00),
    (4, 5, NULL, 3, 65000, 60000, 180000, 5.00),
    (5, 3, NULL, 4, 75000, 70000, 280000, 5.00),
    (5, 1, NULL, 1, 95000, 85000, 85000, 5.00),
    (6, 14, NULL, 3, 108000, 99000, 297000, 10.00),
    (6, 10, NULL, 2, 128000, 120000, 240000, 10.00),
    (6, 5, NULL, 2, 65000, 60000, 120000, 5.00),
    (7, 8, NULL, 3, 79000, 75000, 225000, 5.00),
    (7, 12, NULL, 3, 115000, 105000, 315000, 10.00),
    (8, 9, 2, 10, 95000, 86000, 860000, 10.00),
    (8, 6, NULL, 10, 95000, 90000, 900000, 5.00),
    (8, 4, NULL, 2, 85000, 80000, 160000, 5.00);

-- FlashSaleItems
SET IDENTITY_INSERT FlashSaleItems ON;
INSERT INTO FlashSaleItems (FlashSaleItemID, FlashSaleID, ProductID, ShopID, FlashPrice, FsStock, ApprovalStatus, SoldCount, CreatedAt)
VALUES
    (1, 1, 11, 1, 178000, 50, N'APPROVED', 25, '2025-10-15 10:00:00'),
    (2, 2, 9, 2, 77000, 100, N'APPROVED', 45, '2025-11-20 10:00:00'),
    (3, 1, 8, 1, 65000, 200, N'APPROVED', 150, '2025-10-15 10:00:00'),
    (4, 2, 12, 1, 95000, 80, N'APPROVED', 30, '2025-11-20 10:00:00'),
    (5, 3, 1, 1, 75000, 100, N'APPROVED', 100, '2025-08-25 10:00:00');
SET IDENTITY_INSERT FlashSaleItems OFF;

-- UserVouchers (track used vouchers)
INSERT INTO UserVouchers (VoucherID, UserID, Status)
VALUES
    (1, 4, 'USED'),
    (2, 4, 'USED'),
    (4, 5, 'USED'),
    (1, 5, 'USED'),
    (2, 6, 'USED'),
    (3, 4, 'USED');

-- Reviews
INSERT INTO Reviews (OrderItemID, UserID, Rating, Comment, CreatedAt)
VALUES
    (1, 4, 5, N'Sách hay, giao hàng nhanh, đóng gói cẩn thận. Rất hài lòng!', '2024-11-06 10:30:00'),
    (2, 4, 5, N'Tác phẩm kinh điển của Nguyễn Nhật Ánh, đọc đi đọc lại vẫn thấy hay', '2024-11-06 10:35:00'),
    (3, 4, 4, N'Sách đẹp, giá tốt. Tuy nhiên có 1 cuốn bị nhăn nhẹ ở góc', '2024-11-06 10:40:00'),
    (4, 5, 5, N'Cuốn sách quá tuyệt vời! Ai cũng nên đọc một lần', '2024-11-07 14:20:00'),
    (5, 5, 5, N'Chí Phèo - tác phẩm bất hủ của Nam Cao', '2024-11-07 14:25:00'),
    (10, 4, 4, N'Sách về thiền rất hay, giúp tâm trí thanh thản hơn', '2024-11-09 16:45:00'),
    (14, 6, 5, N'Nội dung sách rất bổ ích cho người trẻ', '2024-11-11 15:30:00'),
    (15, 6, 5, N'Robin Sharma viết rất hay và thực tế', '2024-11-11 15:35:00');

-- ReviewImages
INSERT INTO ReviewImages (ReviewID, Url, Caption, IsPrimary, CreatedAt)
VALUES
    (1, N'https://via.placeholder.com/400x400?text=Review+1', N'Sách đóng gói cẩn thận', 1, '2024-11-06 10:30:00'),
    (3, N'https://via.placeholder.com/400x400?text=Review+2', N'Góc sách bị nhăn nhẹ', 1, '2024-11-06 10:40:00'),
    (4, N'https://via.placeholder.com/400x400?text=Review+3', N'Đắc Nhân Tâm - Bìa sách', 1, '2024-11-07 14:20:00'),
    (7, N'https://via.placeholder.com/400x400?text=Review+4', N'Cà Phê Cùng Tony', 1, '2024-11-11 15:30:00');

-- Notifications
INSERT INTO Notifications (RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt)
VALUES
    (N'CUSTOMER', 4, N'ORDER_CONFIRMED', N'Đơn hàng đã được xác nhận', N'Đơn hàng #1 của bạn đã được xác nhận và đang được chuẩn bị', N'ORDER', 1, '2024-11-01 10:35:00'),
    (N'SELLER', 2, N'NEW_ORDER', N'Bạn có đơn hàng mới', N'Đơn hàng #1 từ khách hàng Lê Minh Cường', N'ORDER', 1, '2024-11-01 10:30:00'),
    (N'CUSTOMER', 4, N'ORDER_SHIPPING', N'Đơn hàng đang được giao', N'Đơn hàng #1 của bạn đang trên đường giao đến bạn', N'ORDER', 1, '2024-11-03 08:00:00'),
    (N'CUSTOMER', 4, N'ORDER_DELIVERED', N'Đơn hàng đã được giao', N'Đơn hàng #1 đã được giao thành công', N'ORDER', 1, '2024-11-05 10:30:00'),
    (N'CUSTOMER', 5, N'ORDER_CONFIRMED', N'Đơn hàng đã được xác nhận', N'Đơn hàng #2 của bạn đã được xác nhận', N'ORDER', 2, '2024-11-02 14:25:00'),
    (N'SELLER', 3, N'NEW_ORDER', N'Bạn có đơn hàng mới', N'Đơn hàng #2 từ khách hàng Phạm Thu Hà', N'ORDER', 2, '2024-11-02 14:20:00'),
    (N'CUSTOMER', 6, N'ORDER_CONFIRMED', N'Đơn hàng đã được xác nhận', N'Đơn hàng #3 của bạn đã được xác nhận', N'ORDER', 3, '2024-11-03 09:20:00'),
    (N'ADMIN', 1, N'NEW_SHOP_REQUEST', N'Có yêu cầu mở shop mới', N'Người dùng mới yêu cầu mở shop', N'SHOP', 2, '2024-10-15 10:00:00'),
    (N'SELLER', 2, N'FLASH_SALE_APPROVED', N'Sản phẩm Flash Sale được duyệt', N'Sản phẩm "Sapiens: Lược Sử Loài Người" đã được duyệt tham gia Flash Sale', N'FLASH_SALE', 1, '2025-10-18 14:00:00'),
    (N'CUSTOMER', 4, N'VOUCHER_AVAILABLE', N'Bạn có voucher mới', N'Voucher FREESHIP50K có thể sử dụng cho đơn hàng tiếp theo', N'VOUCHER', 1, '2024-11-01 00:00:00');