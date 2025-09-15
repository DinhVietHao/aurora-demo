CREATE TABLE Roles (
  RoleCode  NVARCHAR(20)  NOT NULL PRIMARY KEY,
  RoleName  NVARCHAR(100) NOT NULL
);

CREATE TABLE ShopStatus (
  StatusCode NVARCHAR(20) NOT NULL PRIMARY KEY
);

CREATE TABLE ShipperStatus (
  StatusCode NVARCHAR(20) NOT NULL PRIMARY KEY
);

CREATE TABLE ShipmentStatus (
  StatusCode NVARCHAR(20) NOT NULL PRIMARY KEY
);

CREATE TABLE OrderStatus (
  StatusCode NVARCHAR(20) NOT NULL PRIMARY KEY
);

CREATE TABLE PaymentMethod (
  MethodCode NVARCHAR(20)  NOT NULL PRIMARY KEY,
  MethodName NVARCHAR(100) NOT NULL,
  Description NVARCHAR(255) NULL
);

CREATE TABLE PaymentStatus (
  StatusCode NVARCHAR(20) NOT NULL PRIMARY KEY
);

CREATE TABLE DiscountType (
  TypeCode NVARCHAR(20) NOT NULL PRIMARY KEY
);

CREATE TABLE VoucherStatus (
  StatusCode NVARCHAR(20) NOT NULL PRIMARY KEY
);

CREATE TABLE FlashSaleStatus (
  StatusCode NVARCHAR(20) NOT NULL PRIMARY KEY
);

CREATE TABLE ShippingMethods (
  MethodCode  NVARCHAR(20)  NOT NULL PRIMARY KEY,
  MethodName  NVARCHAR(100) NOT NULL,
  Description NVARCHAR(255) NULL
);

CREATE TABLE Users (
  UserID     BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  Email      NVARCHAR(255) NOT NULL,
  Password   NVARCHAR(255) NOT NULL,
  FullName   NVARCHAR(150) NOT NULL,
  Phone      NVARCHAR(20)  NULL,
  Points     INT NOT NULL DEFAULT (0),
  NationalID NVARCHAR(20) NULL,
  AvatarUrl  NVARCHAR(2000) NULL,
  CreatedAt  DATETIME2(7) NOT NULL DEFAULT (SYSUTCDATETIME()),
  AuthProvider  NVARCHAR(20) NOT NULL
);

CREATE TABLE RememberMeTokens (
    TokenID        BIGINT IDENTITY(1,1) PRIMARY KEY,
    UserID         BIGINT NOT NULL,
    Selector       CHAR(18) NOT NULL,
    ValidatorHash  VARBINARY(32) NOT NULL,
    ExpiresAt      DATETIME2(3) NOT NULL,
    CreatedAt      DATETIME2(3) NOT NULL,
    LastUsedAt     DATETIME2(3) NULL,
    CONSTRAINT FK_RM_User FOREIGN KEY (UserID) REFERENCES dbo.Users(UserID) ON DELETE CASCADE
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
  Line          NVARCHAR(255) NOT NULL,
  City          NVARCHAR(100) NOT NULL,
  District      NVARCHAR(100) NOT NULL,
  Ward          NVARCHAR(100) NOT NULL,
  PostalCode    NVARCHAR(20)  NOT NULL,
  CreatedAt     DATETIME2(7)  NOT NULL
);

CREATE TABLE Users_Addresses (
  UserID    BIGINT NOT NULL,
  AddressID BIGINT NOT NULL,
  IsDefault BIT    NOT NULL,
  CONSTRAINT PK_Users_Addresses PRIMARY KEY (UserID, AddressID),
  CONSTRAINT FK_UsersAddr_Users    FOREIGN KEY (UserID)    REFERENCES Users(UserID),
  CONSTRAINT FK_UsersAddr_Address  FOREIGN KEY (AddressID) REFERENCES Addresses(AddressID)
);

CREATE TABLE Shops (
  ShopID          BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  Name            NVARCHAR(150) NOT NULL,
  Description     NVARCHAR(255) NULL,
  RatingAvg       DECIMAL(3,2) NOT NULL,
  Status          NVARCHAR(20) NOT NULL,
  OwnerUserID     BIGINT NOT NULL,
  CreatedAt       DATETIME2(7) NOT NULL,
  PickupAddressID BIGINT NOT NULL,
  InvoiceEmail    NVARCHAR(255) NOT NULL,
  AvatarUrl       NVARCHAR(2000) NULL,
  CONSTRAINT FK_Shops_Status      FOREIGN KEY (Status)          REFERENCES ShopStatus(StatusCode),
  CONSTRAINT FK_Shops_Owner       FOREIGN KEY (OwnerUserID)     REFERENCES Users(UserID),
  CONSTRAINT FK_Shops_PickupAddr  FOREIGN KEY (PickupAddressID) REFERENCES Addresses(AddressID)
);

CREATE TABLE Shops_ShippingMethods (
  ShopID     BIGINT       NOT NULL,
  MethodCode NVARCHAR(20) NOT NULL,
  CONSTRAINT PK_Shops_ShippingMethods PRIMARY KEY (ShopID, MethodCode),
  CONSTRAINT FK_SSM_Shops            FOREIGN KEY (ShopID)     REFERENCES Shops(ShopID),
  CONSTRAINT FK_SSM_ShippingMethods  FOREIGN KEY (MethodCode) REFERENCES ShippingMethods(MethodCode)
);

CREATE TABLE ProductCategories (
  CategoryID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  Name       NVARCHAR(120) NOT NULL,
  ParentID   BIGINT NULL
);

CREATE TABLE Publishers (
  PublisherID BIGINT IDENTITY(1,1) PRIMARY KEY,
  Name        NVARCHAR(150) NOT NULL
);

CREATE TABLE Products (
  ProductID      BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  ShopID         BIGINT NOT NULL,
  Title          NVARCHAR(255) NOT NULL,
  Description    NVARCHAR(MAX) NULL,
  OriginalPrice  DECIMAL(12,2) NOT NULL,
  SalePrice      DECIMAL(12,2) NOT NULL,
  SoldCount      BIGINT NOT NULL DEFAULT 0,
  Stock          INT NOT NULL,
  IsBundle       BIT NOT NULL DEFAULT 0,
  CategoryID     BIGINT NOT NULL,
  PublisherID    BIGINT NULL,
  PublishedDate  DATE NULL,
  CONSTRAINT FK_Products_Shop 
      FOREIGN KEY (ShopID) REFERENCES Shops(ShopID),
  CONSTRAINT FK_Products_Publisher 
      FOREIGN KEY (PublisherID) REFERENCES Publishers(PublisherID),
  CONSTRAINT FK_Products_Category 
      FOREIGN KEY (CategoryID) REFERENCES ProductCategories(CategoryID)
);

CREATE TABLE BundleItems (
  BundleProductID BIGINT NOT NULL,
  ProductID       BIGINT NOT NULL,
  Quantity        INT    NOT NULL,
  CONSTRAINT PK_BundleItems PRIMARY KEY (BundleProductID, ProductID),
  CONSTRAINT FK_BundleItems_Bundle  FOREIGN KEY (BundleProductID) REFERENCES Products(ProductID),
  CONSTRAINT FK_BundleItems_Product FOREIGN KEY (ProductID)       REFERENCES Products(ProductID)
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
  LanguageCode NVARCHAR(20) NOT NULL PRIMARY KEY,
  LanguageName NVARCHAR(100) NOT NULL
);

CREATE TABLE BookDetails (
  ProductID    BIGINT NOT NULL PRIMARY KEY,
  Translator   NVARCHAR(200) NULL,
  Version      NVARCHAR(50)  NOT NULL,
  CoverType    NVARCHAR(50)  NOT NULL,
  Pages        INT NOT NULL,
  LanguageCode NVARCHAR(20) NOT NULL,
  [Size]       NVARCHAR(50)  NOT NULL,
  ISBN         NVARCHAR(20)  NOT NULL,
  CONSTRAINT FK_BookDetails_Product 
      FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE,
  CONSTRAINT FK_BookDetails_Language 
      FOREIGN KEY (LanguageCode) REFERENCES Languages(LanguageCode)
);

INSERT INTO Languages (LanguageCode, LanguageName)
VALUES 
  (N'vi', N'Tiếng Việt'),
  (N'en', N'Tiếng Anh');

CREATE TABLE Carts (
  CartID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  UserID BIGINT NOT NULL UNIQUE,
  CONSTRAINT FK_Carts_User FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

CREATE TABLE CartItems (
  CartItemID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  CartID     BIGINT NOT NULL,
  ProductID  BIGINT NOT NULL,
  Quantity   INT    NOT NULL,
  IsChecked BIT NOT NULL DEFAULT 1, -- THÊM MỚI THUỘC TÍNH IS CHECKED
  UnitPrice  DECIMAL(12,2) NOT NULL,
  Subtotal   DECIMAL(12,2) NOT NULL,
  CONSTRAINT FK_CartItems_Cart    FOREIGN KEY (CartID)    REFERENCES Carts(CartID),
  CONSTRAINT FK_CartItems_Product FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

CREATE TABLE Vouchers (
  VoucherID      BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  Code           NVARCHAR(40) NOT NULL,
  DiscountType   NVARCHAR(20) NOT NULL,
  Value          DECIMAL(12,2) NOT NULL,
  MaxAmount		 DECIMAL(12,2) NULL, -- THÊM MỚI THUỘC TÍNH MAX AMOUNT 
  MinOrderAmount DECIMAL(12,2) NOT NULL,
  StartAt        DATETIME2(7) NOT NULL,
  EndAt          DATETIME2(7) NOT NULL,
  UsageLimit     INT NULL,
  PerUserLimit   INT NULL,
  Status         NVARCHAR(20) NOT NULL,
  UsageCount     INT NOT NULL,
  CONSTRAINT FK_Vouchers_Status       FOREIGN KEY (Status)       REFERENCES VoucherStatus(StatusCode),
  CONSTRAINT FK_Vouchers_DiscountType FOREIGN KEY (DiscountType) REFERENCES DiscountType(TypeCode)
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
  CreatedAt      DATETIME2(7) NOT NULL,
  DeliveredAt    DATETIME2(7) NULL,
  CONSTRAINT FK_Orders_User          FOREIGN KEY (UserID)        REFERENCES Users(UserID),
  CONSTRAINT FK_Orders_Address       FOREIGN KEY (AddressID)     REFERENCES Addresses(AddressID),
  CONSTRAINT FK_Orders_Voucher       FOREIGN KEY (VoucherID)     REFERENCES Vouchers(VoucherID),
  CONSTRAINT FK_Orders_PaymentMethod FOREIGN KEY (PaymentMethod) REFERENCES PaymentMethod(MethodCode),
  CONSTRAINT FK_Orders_PaymentStatus FOREIGN KEY (PaymentStatus) REFERENCES PaymentStatus(StatusCode),
  CONSTRAINT FK_Orders_OrderStatus   FOREIGN KEY (OrderStatus)   REFERENCES OrderStatus(StatusCode)
);

CREATE TABLE OrderShops (
  OrderShopID BIGINT IDENTITY(1,1) NOT NULL,
  OrderID     BIGINT NOT NULL,
  ShopID      BIGINT NOT NULL,
  ShippingFee DECIMAL(12,2) NOT NULL,
  Status      NVARCHAR(20) NOT NULL,
  CreatedAt   DATETIME2(7) NOT NULL,
  CONSTRAINT PK_OrderShops PRIMARY KEY (OrderShopID),
  CONSTRAINT FK_OrderShops_Order FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
  CONSTRAINT FK_OrderShops_Shop  FOREIGN KEY (ShopID)  REFERENCES Shops(ShopID)
);

CREATE TABLE FlashSales (
  FlashSaleID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  Name        NVARCHAR(150) NOT NULL,
  StartAt     DATETIME2(7) NOT NULL,
  EndAt       DATETIME2(7) NOT NULL,
  Status      NVARCHAR(20) NOT NULL,
  CreatedAt   DATETIME2(7) NOT NULL,
  CONSTRAINT FK_FlashSales_Status FOREIGN KEY (Status) REFERENCES FlashSaleStatus(StatusCode)
);

CREATE TABLE FlashSaleItems (
  FlashSaleItemID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  FlashSaleID     BIGINT NOT NULL,
  ProductID       BIGINT NOT NULL,
  FlashPrice      DECIMAL(12,2) NOT NULL,
  FsStock         INT NOT NULL,
  PerUserLimit    INT NULL,
  CreatedAt       DATETIME2(7) NOT NULL,
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
  Subtotal        AS (Quantity * UnitPrice) PERSISTED,
  CONSTRAINT FK_OrderItems_OrderShop     FOREIGN KEY (OrderShopID)     REFERENCES OrderShops(OrderShopID),
  CONSTRAINT FK_OrderItems_Product       FOREIGN KEY (ProductID)       REFERENCES Products(ProductID),
  CONSTRAINT FK_OrderItems_FlashSaleItem FOREIGN KEY (FlashSaleItemID) REFERENCES FlashSaleItems(FlashSaleItemID)
);

CREATE TABLE Payments (
  PaymentID      BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  OrderID        BIGINT NOT NULL UNIQUE,
  Amount         DECIMAL(12,2) NOT NULL,
  Method         NVARCHAR(20) NOT NULL,
  TransactionRef NVARCHAR(100) NOT NULL,
  Status         NVARCHAR(20) NOT NULL,
  CreatedAt      DATETIME2(7) NOT NULL,
  CONSTRAINT FK_Payments_Order   FOREIGN KEY (OrderID)  REFERENCES Orders(OrderID),
  CONSTRAINT FK_Payments_Method  FOREIGN KEY (Method)   REFERENCES PaymentMethod(MethodCode),
  CONSTRAINT FK_Payments_Status  FOREIGN KEY (Status)   REFERENCES PaymentStatus(StatusCode)
);

CREATE TABLE Shippers (
  ShipperID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  UserID    BIGINT NOT NULL UNIQUE,
  Status    NVARCHAR(20) NOT NULL,
  Area      NVARCHAR(150) NOT NULL,
  HireDate  DATE NOT NULL,
  CONSTRAINT FK_Shippers_User   FOREIGN KEY (UserID) REFERENCES Users(UserID),
  CONSTRAINT FK_Shippers_Status FOREIGN KEY (Status) REFERENCES ShipperStatus(StatusCode)
);

CREATE TABLE Shipments (
  ShipmentID   BIGINT IDENTITY(1,1) NOT NULL,
  OrderShopID  BIGINT NOT NULL,
  ShipperID    BIGINT NOT NULL,
  TrackingCode NVARCHAR(64) NOT NULL,
  Status       NVARCHAR(20) NOT NULL,
  PickedAt     DATETIME2(7) NULL,
  DeliveredAt  DATETIME2(7) NULL,
  CODCollected BIT NOT NULL,
  CONSTRAINT PK_Shipments PRIMARY KEY (ShipmentID),
  CONSTRAINT FK_Shipments_OrderShop FOREIGN KEY (OrderShopID) REFERENCES OrderShops(OrderShopID),
  CONSTRAINT FK_Shipments_Shipper   FOREIGN KEY (ShipperID)   REFERENCES Shippers(ShipperID),
  CONSTRAINT FK_Shipments_Status    FOREIGN KEY (Status)      REFERENCES ShipmentStatus(StatusCode)
);

CREATE TABLE ShipmentEvents (
  EventID    BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  ShipmentID BIGINT NOT NULL,
  Status     NVARCHAR(20) NOT NULL,
  EventAt    DATETIME2(7) NOT NULL,
  Note       NVARCHAR(255) NULL,
  CONSTRAINT FK_ShipmentEvents_Shipment FOREIGN KEY (ShipmentID) REFERENCES Shipments(ShipmentID),
  CONSTRAINT FK_ShipmentEvents_Status   FOREIGN KEY (Status)    REFERENCES ShipmentStatus(StatusCode)
);

CREATE TABLE DeliveryFailureReasons (
  ReasonCode NVARCHAR(50) NOT NULL PRIMARY KEY,  -- Mã lý do (VD: NO_ONE_HOME, WRONG_ADDRESS)
  ReasonText NVARCHAR(255) NOT NULL              -- Mô tả lý do (VD: "Khách không có ở nhà")
);

INSERT INTO DeliveryFailureReasons (ReasonCode, ReasonText) VALUES
('NO_ONE_HOME',        N'Khách không có ở nhà'),
('WRONG_ADDRESS',      N'Địa chỉ giao hàng không đúng'),
('CUSTOMER_REFUSED',   N'Khách hàng từ chối nhận'),
('DAMAGED_ITEM',       N'Sản phẩm bị hư hỏng'),
('PHONE_NOT_REACHABLE',N'Không liên hệ được với khách hàng'),
('RESCHEDULED',        N'Khách hẹn giao lại vào thời gian khác'),
('SECURITY_DENIED',    N'Bảo vệ hoặc khu vực không cho vào'),
('BAD_WEATHER',        N'Thời tiết xấu, không thể giao hàng'),
('TRAFFIC_ISSUE',      N'Kẹt xe hoặc sự cố giao thông');

CREATE TABLE DeliveryAttempts (
  AttemptID     BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  ShipmentID    BIGINT NOT NULL,
  Status        NVARCHAR(20) NOT NULL,         -- Kết quả (DELIVERED, FAILED, v.v.)
  ReasonCode    NVARCHAR(50) NULL,             -- Lý do thất bại (nếu Status = FAILED)
  PhotoUrl      NVARCHAR(2000) NULL,           -- Ảnh bằng chứng giao hàng
  SignatureName NVARCHAR(120) NULL,            -- Người ký nhận
  CreatedAt     DATETIME2(7) NOT NULL,

  CONSTRAINT FK_DeliveryAttempts_Shipment FOREIGN KEY (ShipmentID) REFERENCES Shipments(ShipmentID),
  CONSTRAINT FK_DeliveryAttempts_Status   FOREIGN KEY (Status)    REFERENCES ShipmentStatus(StatusCode),
  CONSTRAINT FK_DeliveryAttempts_Reason   FOREIGN KEY (ReasonCode) REFERENCES DeliveryFailureReasons(ReasonCode)
);

CREATE TABLE ShipperRatings (
  RatingID   BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  ShipmentID BIGINT NOT NULL,
  ShipperID  BIGINT NOT NULL,
  UserID     BIGINT NOT NULL,
  Rating     TINYINT NOT NULL,
  Comment    NVARCHAR(500) NULL,
  CreatedAt  DATETIME2(7) NOT NULL,
  CONSTRAINT FK_ShipperRatings_Shipment FOREIGN KEY (ShipmentID) REFERENCES Shipments(ShipmentID),
  CONSTRAINT FK_ShipperRatings_Shipper  FOREIGN KEY (ShipperID)  REFERENCES Shippers(ShipperID),
  CONSTRAINT FK_ShipperRatings_User     FOREIGN KEY (UserID)     REFERENCES Users(UserID)
);

CREATE TABLE Reviews (
  ReviewID    BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  OrderItemID BIGINT NOT NULL,
  UserID      BIGINT NOT NULL,
  Rating      TINYINT NOT NULL,
  Comment     NVARCHAR(255) NULL,
  CreatedAt   DATETIME2(7) NOT NULL,
  CONSTRAINT FK_Reviews_OrderItem FOREIGN KEY (OrderItemID) REFERENCES OrderItems(OrderItemID),
  CONSTRAINT FK_Reviews_User      FOREIGN KEY (UserID)      REFERENCES Users(UserID)
);

CREATE TABLE ReviewImages (
  ReviewImageID BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  ReviewID      BIGINT NOT NULL,
  Url           NVARCHAR(2000) NOT NULL,
  Caption       NVARCHAR(255) NULL,
  IsPrimary     BIT NOT NULL,
  CreatedAt     DATETIME2(7) NOT NULL,
  CONSTRAINT FK_ReviewImages_Review FOREIGN KEY (ReviewID) REFERENCES Reviews(ReviewID)
);

INSERT INTO Roles (RoleCode, RoleName) VALUES
 (N'CUSTOMER',  N'Khách hàng'),
 (N'SHOP_OWNER',N'Chủ cửa hàng'),
 (N'SHIPPER',   N'Người giao hàng'),
 (N'ADMIN',     N'Quản trị viên');

INSERT INTO ShopStatus (StatusCode) VALUES
 (N'PENDING'),
 (N'APPROVED'),
 (N'SUSPENDED'),
 (N'BANNED');

INSERT INTO ShipperStatus (StatusCode) VALUES
 (N'ACTIVE'),
 (N'INACTIVE'),
 (N'SUSPENDED'),
 (N'OFFBOARD');

INSERT INTO ShipmentStatus (StatusCode) VALUES
 (N'AWAIT_PICKUP'),
 (N'PICKED'),
 (N'IN_TRANSIT'),
 (N'OUT_FOR_DELIVERY'),
 (N'DELIVERED'),
 (N'FAILED_ATTEMPT'),
 (N'RETURN_TO_SENDER'),
 (N'CANCELED');

INSERT INTO OrderStatus (StatusCode) VALUES
 (N'PLACED'),
 (N'CONFIRMED'),
 (N'PACKED'),
 (N'SHIPPING'),
 (N'DELIVERED'),
 (N'FAILED'),
 (N'RETURNED'),
 (N'CANCELED');

INSERT INTO PaymentMethod (MethodCode, MethodName, Description) VALUES
 (N'COD',   N'Thanh toán khi nhận hàng (COD)', N'Thu hộ tiền mặt khi giao'),
 (N'VNPAY', N'VNPAY',                          N'Thanh toán qua cổng VNPAY');


INSERT INTO PaymentStatus (StatusCode) VALUES
 (N'PENDING'),
 (N'AUTHORIZED'),
 (N'CAPTURED'),
 (N'PARTIALLY_CAPTURED'),
 (N'REFUNDED'),
 (N'PARTIALLY_REFUNDED'),
 (N'FAILED'),
 (N'CANCELED');

INSERT INTO DiscountType (TypeCode) VALUES
 (N'PERCENT'),
 (N'AMOUNT');

INSERT INTO VoucherStatus (StatusCode) VALUES
 (N'ACTIVE'),
 (N'INACTIVE'),
 (N'EXPIRED'),
 (N'SCHEDULED');

INSERT INTO FlashSaleStatus (StatusCode) VALUES
 (N'SCHEDULED'),
 (N'ACTIVE'),
 (N'PAUSED'),
 (N'ENDED'),
 (N'CANCELED');

INSERT INTO ShippingMethods (MethodCode, MethodName, Description) VALUES
 (N'STANDARD',  N'Tiêu chuẩn', N'Giao trong 2–4 ngày làm việc, phí thấp'),
 (N'EXPRESS',   N'Nhanh',      N'Giao trong 24–48 giờ, ưu tiên tuyến nhanh'),
 (N'SAME_DAY',  N'Giao trong ngày', N'Áp dụng nội thành, đặt trước cut-off');

 -- Users
INSERT INTO Users (Email, Password, FullName, Phone, Points, NationalID, AvatarUrl, CreatedAt, AuthProvider) VALUES
 (N'customer1@example.com', N'123456', N'Nguyễn Văn A', N'0909123456', 100, N'123456789', NULL, GETDATE(), N'LOCAL'),
 (N'shopowner1@example.com', N'123456', N'Lê Thị B',    N'0912123456', 200, N'234567891', NULL, GETDATE(), N'LOCAL'),
 (N'shipper1@example.com',   N'123456', N'Trần Văn C',  N'0933123456', 50,  N'345678912', NULL, GETDATE(), N'LOCAL');

-- Gán Roles cho Users
INSERT INTO UserRoles (UserID, RoleCode) VALUES
 (1, N'CUSTOMER'),
 (2, N'SHOP_OWNER'),
 (3, N'SHIPPER');

-- Addresses
INSERT INTO Addresses (RecipientName, Phone, Line, City, District, Ward, PostalCode, CreatedAt) VALUES
 (N'Nguyễn Văn A', N'0909123456', N'123 Lý Thường Kiệt', N'Hà Nội', N'Hoàn Kiếm', N'Phường Tràng Tiền', N'100000', GETDATE()),
 (N'Lê Thị B',    N'0912123456', N'456 Nguyễn Trãi',     N'Hà Nội', N'Thanh Xuân', N'Phường Thượng Đình', N'100001', GETDATE()),
 (N'Trần Văn C',  N'0933123456', N'789 Lê Lợi',          N'Hà Nội', N'Hai Bà Trưng', N'Phường Bạch Mai', N'100002', GETDATE());

-- Liên kết Users với Addresses
INSERT INTO Users_Addresses (UserID, AddressID, IsDefault) VALUES
 (1, 1, 1),
 (2, 2, 1),
 (3, 3, 1);

-- Shops
INSERT INTO Shops (Name, Description, RatingAvg, Status, OwnerUserID, CreatedAt, PickupAddressID, InvoiceEmail, AvatarUrl) VALUES
 (N'Nhà sách ABC', N'Chuyên bán sách giáo khoa và tham khảo', 4.5, N'APPROVED', 2, GETDATE(), 2, N'shop.abc@example.com', NULL);

-- Shop Shipping Methods
INSERT INTO Shops_ShippingMethods (ShopID, MethodCode) VALUES
 (1, N'STANDARD'),
 (1, N'EXPRESS');

-- Product Categories
INSERT INTO ProductCategories (Name, ParentID) VALUES
 (N'Sách giáo khoa', NULL),
 (N'Sách tham khảo', NULL);

-- Publishers
INSERT INTO Publishers (Name) VALUES
 (N'NXB Giáo dục'),
 (N'NXB Kim Đồng');

-- Products
INSERT INTO Products 
(ShopID, Title, Description, OriginalPrice, SalePrice, SoldCount, Stock, IsBundle, CategoryID, PublisherID, PublishedDate) 
VALUES
(1, N'Sách Toán lớp 1', N'Sách giáo khoa Toán lớp 1', 20000, 20000, 100, 500, 0, 1, 1, '2020-09-01'),
(1, N'Sách Văn lớp 1', N'Sách giáo khoa Văn lớp 1', 18000, 18000, 80, 400, 0, 1, 1, '2020-09-01'),
(1, N'Truyện tranh Doremon tập 1', N'Truyện thiếu nhi nổi tiếng', 25000, 20000, 300, 200, 0, 2, 2, '2018-01-01');


-- Product Images
INSERT INTO ProductImages (ProductID, Url, IsPrimary) VALUES
(1, N'thumb-main.jpg', 1),
(1, N'thumb1.jpg', 0),
(1, N'thumb2.webp', 0),
(1, N'thumb3.webp', 0),
(1, N'thumb4.png', 0);

INSERT INTO ProductImages (ProductID, Url, IsPrimary) VALUES
(2, N'thumb-main.jpg', 0),
(2, N'thumb1.jpg', 0),
(2, N'thumb2.webp', 1),
(2, N'thumb3.webp', 0),
(2, N'thumb4.png', 0);

INSERT INTO ProductImages (ProductID, Url, IsPrimary) VALUES
(3, N'thumb-main.jpg', 0),
(3, N'thumb1.jpg', 1),
(3, N'thumb2.webp', 0),
(3, N'thumb3.webp', 0),
(3, N'thumb4.png', 0);

-- Cart cho customer
INSERT INTO Carts (UserID) VALUES
 (1);

INSERT INTO CartItems (CartID, ProductID, Quantity, UnitPrice, Subtotal) VALUES
 (1, 1, 2, 20000, 40000),
 (1, 3, 1, 20000, 20000);

-- Vouchers
INSERT INTO Vouchers (Code, DiscountType, Value, MinOrderAmount, StartAt, EndAt, UsageLimit, PerUserLimit, Status, UsageCount) VALUES
 (N'GIAM10K', N'AMOUNT', 10000, 50000, GETDATE(), DATEADD(DAY, 30, GETDATE()), 100, 1, N'ACTIVE', 0);

-- Orders
INSERT INTO Orders (UserID, AddressID, VoucherID, TotalAmount, DiscountAmount, PaymentMethod, PaymentStatus, OrderStatus, CreatedAt, DeliveredAt) VALUES
 (1, 1, 1, 60000, 10000, N'COD', N'PENDING', N'PLACED', GETDATE(), NULL);

-- OrderShops
INSERT INTO OrderShops (OrderID, ShopID, ShippingFee, Status, CreatedAt) VALUES
 (1, 1, 15000, N'PLACED', GETDATE());

-- OrderItems
INSERT INTO OrderItems (OrderShopID, ProductID, FlashSaleItemID, Quantity, UnitPrice) VALUES
 (1, 1, NULL, 2, 20000),
 (1, 3, NULL, 1, 20000);

-- Payment
INSERT INTO Payments (OrderID, Amount, Method, TransactionRef, Status, CreatedAt) VALUES
 (1, 50000, N'COD', N'TRANS123456', N'PENDING', GETDATE());

-- Shipper
INSERT INTO Shippers (UserID, Status, Area, HireDate) VALUES
 (3, N'ACTIVE', N'Hà Nội', '2024-01-01');

-- Shipment
INSERT INTO Shipments (OrderShopID, ShipperID, TrackingCode, Status, PickedAt, DeliveredAt, CODCollected) VALUES
 (1, 1, N'TRACK123', N'AWAIT_PICKUP', NULL, NULL, 0);

 SELECT * FROM Authors