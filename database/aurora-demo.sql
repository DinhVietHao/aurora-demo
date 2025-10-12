USE [master]
GO
/****** Object:  Database [AuroraDemo]    Script Date: 10/2/2025 11:36:50 AM ******/
--CREATE DATABASE [AuroraDemo]

USE [AuroraDemo]
GO
/****** Object:  Table [dbo].[Addresses]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Addresses](
	[AddressID] [bigint] IDENTITY(1,1) NOT NULL,
	[RecipientName] [nvarchar](150) NOT NULL,
	[Phone] [nvarchar](20) NOT NULL,
	[Line] [nvarchar](255) NOT NULL,
	[City] [nvarchar](100) NOT NULL,
	[District] [nvarchar](100) NOT NULL,
	[Ward] [nvarchar](100) NOT NULL,
	[PostalCode] [nvarchar](20) NOT NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[AddressID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Authors]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Authors](
	[AuthorID] [bigint] IDENTITY(1,1) NOT NULL,
	[AuthorName] [nvarchar](200) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[AuthorID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BookAuthors]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BookAuthors](
	[ProductID] [bigint] NOT NULL,
	[AuthorID] [bigint] NOT NULL,
 CONSTRAINT [PK_BookAuthors] PRIMARY KEY CLUSTERED 
(
	[ProductID] ASC,
	[AuthorID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BookDetails]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BookDetails](
	[ProductID] [bigint] NOT NULL,
	[Translator] [nvarchar](200) NULL,
	[Version] [nvarchar](50) NOT NULL,
	[CoverType] [nvarchar](50) NOT NULL,
	[Pages] [int] NOT NULL,
	[LanguageCode] [nvarchar](20) NOT NULL,
	[Size] [nvarchar](50) NOT NULL,
	[ISBN] [nvarchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ProductID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BundleItems]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BundleItems](
	[BundleProductID] [bigint] NOT NULL,
	[ProductID] [bigint] NOT NULL,
	[Quantity] [int] NOT NULL,
 CONSTRAINT [PK_BundleItems] PRIMARY KEY CLUSTERED 
(
	[BundleProductID] ASC,
	[ProductID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[CartItems]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[CartItems](
	[CartItemID] [bigint] IDENTITY(1,1) NOT NULL,
	[CartID] [bigint] NOT NULL,
	[ProductID] [bigint] NOT NULL,
	[Quantity] [int] NOT NULL,
	[IsChecked] [bit] NOT NULL,
	[UnitPrice] [decimal](12, 2) NOT NULL,
	[Subtotal] [decimal](12, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[CartItemID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Carts]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Carts](
	[CartID] [bigint] IDENTITY(1,1) NOT NULL,
	[UserID] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[CartID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DeliveryAttempts]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DeliveryAttempts](
	[AttemptID] [bigint] IDENTITY(1,1) NOT NULL,
	[ShipmentID] [bigint] NOT NULL,
	[Status] [nvarchar](20) NOT NULL,
	[ReasonCode] [nvarchar](50) NULL,
	[PhotoUrl] [nvarchar](2000) NULL,
	[SignatureName] [nvarchar](120) NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[AttemptID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DeliveryFailureReasons]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DeliveryFailureReasons](
	[ReasonCode] [nvarchar](50) NOT NULL,
	[ReasonText] [nvarchar](255) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ReasonCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DiscountType]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DiscountType](
	[TypeCode] [nvarchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[TypeCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FlashSaleItems]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FlashSaleItems](
    [FlashSaleItemID] [bigint] IDENTITY(1,1) NOT NULL,
    [FlashSaleID] [bigint] NOT NULL,
    [ProductID] [bigint] NOT NULL,
    [ShopID] [bigint] NOT NULL,
    [FlashPrice] [decimal](12, 2) NOT NULL,
    [FsStock] [int] NOT NULL,
    [PerUserLimit] [int] NULL,
    [ApprovalStatus] [nvarchar](20) NOT NULL,
    [CreatedAt] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
    [FlashSaleItemID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FlashSales]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FlashSales](
	[FlashSaleID] [bigint] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](150) NOT NULL,
	[StartAt] [datetime2](7) NOT NULL,
	[EndAt] [datetime2](7) NOT NULL,
	[Status] [nvarchar](20) NOT NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[FlashSaleID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FlashSaleStatus]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FlashSaleStatus](
	[StatusCode] [nvarchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[StatusCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FlashSaleItemApprovalStatus]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FlashSaleItemApprovalStatus](
	[StatusCode] [nvarchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[StatusCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Languages]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Languages](
	[LanguageCode] [nvarchar](20) NOT NULL,
	[LanguageName] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[LanguageCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[OrderItems]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OrderItems](
	[OrderItemID] [bigint] IDENTITY(1,1) NOT NULL,
	[OrderShopID] [bigint] NOT NULL,
	[ProductID] [bigint] NOT NULL,
	[FlashSaleItemID] [bigint] NULL,
	[Quantity] [int] NOT NULL,
	[UnitPrice] [decimal](12, 2) NOT NULL,
	[Subtotal]  AS ([Quantity]*[UnitPrice]) PERSISTED,
PRIMARY KEY CLUSTERED 
(
	[OrderItemID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Orders]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Orders](
	[OrderID] [bigint] IDENTITY(1,1) NOT NULL,
	[UserID] [bigint] NOT NULL,
	[AddressID] [bigint] NOT NULL,
	[VoucherID] [bigint] NULL,
	[TotalAmount] [decimal](12, 2) NOT NULL,
	[DiscountAmount] [decimal](12, 2) NOT NULL,
	[FinalAmount]  AS ([TotalAmount]-[DiscountAmount]) PERSISTED,
	[PaymentMethod] [nvarchar](20) NOT NULL,
	[PaymentStatus] [nvarchar](20) NOT NULL,
	[OrderStatus] [nvarchar](20) NOT NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
	[DeliveredAt] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[OrderID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[OrderShops]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OrderShops](
	[OrderShopID] [bigint] IDENTITY(1,1) NOT NULL,
	[OrderID] [bigint] NOT NULL,
	[ShopID] [bigint] NOT NULL,
	[ShippingFee] [decimal](12, 2) NOT NULL,
	[Status] [nvarchar](20) NOT NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
 CONSTRAINT [PK_OrderShops] PRIMARY KEY CLUSTERED 
(
	[OrderShopID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[OrderStatus]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OrderStatus](
	[StatusCode] [nvarchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[StatusCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PaymentMethod]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PaymentMethod](
	[MethodCode] [nvarchar](20) NOT NULL,
	[MethodName] [nvarchar](100) NOT NULL,
	[Description] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[MethodCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Payments]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Payments](
	[PaymentID] [bigint] IDENTITY(1,1) NOT NULL,
	[OrderID] [bigint] NOT NULL,
	[Amount] [decimal](12, 2) NOT NULL,
	[Method] [nvarchar](20) NOT NULL,
	[TransactionRef] [nvarchar](100) NOT NULL,
	[Status] [nvarchar](20) NOT NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[PaymentID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PaymentStatus]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PaymentStatus](
	[StatusCode] [nvarchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[StatusCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ProductCategories]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ProductCategories](
	[CategoryID] [bigint] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](120) NOT NULL,
	[ParentID] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[CategoryID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ProductImages]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ProductImages](
	[ImageID] [bigint] IDENTITY(1,1) NOT NULL,
	[ProductID] [bigint] NOT NULL,
	[Url] [nvarchar](2000) NOT NULL,
	[IsPrimary] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ImageID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Products]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Products](
	[ProductID] [bigint] IDENTITY(1,1) NOT NULL,
	[ShopID] [bigint] NOT NULL,
	[Title] [nvarchar](255) NOT NULL,
	[Description] [nvarchar](max) NULL,
	[OriginalPrice] [decimal](12, 2) NOT NULL,
	[SalePrice] [decimal](12, 2) NOT NULL,
	[SoldCount] [bigint] NOT NULL,
	[Stock] [int] NOT NULL,
	[IsBundle] [bit] NOT NULL,
	[CategoryID] [bigint] NOT NULL,
	[PublisherID] [bigint] NULL,
	[PublishedDate] [date] NULL,
PRIMARY KEY CLUSTERED 
(
	[ProductID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Publishers]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Publishers](
	[PublisherID] [bigint] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](150) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[PublisherID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[RememberMeTokens]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RememberMeTokens](
	[TokenID] [bigint] IDENTITY(1,1) NOT NULL,
	[UserID] [bigint] NOT NULL,
	[Selector] [char](18) NOT NULL,
	[ValidatorHash] [varbinary](32) NOT NULL,
	[ExpiresAt] [datetime2](3) NOT NULL,
	[CreatedAt] [datetime2](3) NOT NULL,
	[LastUsedAt] [datetime2](3) NULL,
PRIMARY KEY CLUSTERED 
(
	[TokenID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ReviewImages]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ReviewImages](
	[ReviewImageID] [bigint] IDENTITY(1,1) NOT NULL,
	[ReviewID] [bigint] NOT NULL,
	[Url] [nvarchar](2000) NOT NULL,
	[Caption] [nvarchar](255) NULL,
	[IsPrimary] [bit] NOT NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ReviewImageID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Reviews]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Reviews](
	[ReviewID] [bigint] IDENTITY(1,1) NOT NULL,
	[OrderItemID] [bigint] NOT NULL,
	[UserID] [bigint] NOT NULL,
	[Rating] [tinyint] NOT NULL,
	[Comment] [nvarchar](255) NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ReviewID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Roles]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Roles](
	[RoleCode] [nvarchar](20) NOT NULL,
	[RoleName] [nvarchar](100) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[RoleCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ShipmentEvents]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ShipmentEvents](
	[EventID] [bigint] IDENTITY(1,1) NOT NULL,
	[ShipmentID] [bigint] NOT NULL,
	[Status] [nvarchar](20) NOT NULL,
	[EventAt] [datetime2](7) NOT NULL,
	[Note] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[EventID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Shipments]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Shipments](
	[ShipmentID] [bigint] IDENTITY(1,1) NOT NULL,
	[OrderShopID] [bigint] NOT NULL,
	[ShipperID] [bigint] NOT NULL,
	[TrackingCode] [nvarchar](64) NOT NULL,
	[Status] [nvarchar](20) NOT NULL,
	[PickedAt] [datetime2](7) NULL,
	[DeliveredAt] [datetime2](7) NULL,
	[CODCollected] [bit] NOT NULL,
 CONSTRAINT [PK_Shipments] PRIMARY KEY CLUSTERED 
(
	[ShipmentID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ShipmentStatus]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ShipmentStatus](
	[StatusCode] [nvarchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[StatusCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ShipperRatings]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ShipperRatings](
	[RatingID] [bigint] IDENTITY(1,1) NOT NULL,
	[ShipmentID] [bigint] NOT NULL,
	[ShipperID] [bigint] NOT NULL,
	[UserID] [bigint] NOT NULL,
	[Rating] [tinyint] NOT NULL,
	[Comment] [nvarchar](500) NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[RatingID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Shippers]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Shippers](
	[ShipperID] [bigint] IDENTITY(1,1) NOT NULL,
	[UserID] [bigint] NOT NULL,
	[Status] [nvarchar](20) NOT NULL,
	[Area] [nvarchar](150) NOT NULL,
	[HireDate] [date] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ShipperID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ShipperStatus]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ShipperStatus](
	[StatusCode] [nvarchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[StatusCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ShippingMethods]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ShippingMethods](
	[MethodCode] [nvarchar](20) NOT NULL,
	[MethodName] [nvarchar](100) NOT NULL,
	[Description] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[MethodCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Shops]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Shops](
	[ShopID] [bigint] IDENTITY(1,1) NOT NULL,
	[Name] [nvarchar](150) NOT NULL,
	[Description] [nvarchar](255) NULL,
	[RatingAvg] [decimal](3, 2) NOT NULL,
	[Status] [nvarchar](20) NOT NULL,
	[OwnerUserID] [bigint] NOT NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
	[PickupAddressID] [bigint] NOT NULL,
	[InvoiceEmail] [nvarchar](255) NOT NULL,
	[AvatarUrl] [nvarchar](2000) NULL,
PRIMARY KEY CLUSTERED 
(
	[ShopID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Shops_ShippingMethods]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Shops_ShippingMethods](
	[ShopID] [bigint] NOT NULL,
	[MethodCode] [nvarchar](20) NOT NULL,
 CONSTRAINT [PK_Shops_ShippingMethods] PRIMARY KEY CLUSTERED 
(
	[ShopID] ASC,
	[MethodCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[ShopStatus]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ShopStatus](
	[StatusCode] [nvarchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[StatusCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[UserRoles]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[UserRoles](
	[UserID] [bigint] NOT NULL,
	[RoleCode] [nvarchar](20) NOT NULL,
 CONSTRAINT [PK_UserRoles] PRIMARY KEY CLUSTERED 
(
	[UserID] ASC,
	[RoleCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Users]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[UserID] [bigint] IDENTITY(1,1) NOT NULL,
	[Email] [nvarchar](255) NOT NULL,
	[Password] [nvarchar](255) NOT NULL,
	[FullName] [nvarchar](150) NOT NULL,
	[Phone] [nvarchar](20) NULL,
	[Points] [int] NOT NULL,
	[NationalID] [nvarchar](20) NULL,
	[AvatarUrl] [nvarchar](2000) NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
	[AuthProvider] [nvarchar](20) NOT NULL,
	[Status] [nchar](10) NULL,
 CONSTRAINT [PK__Users__1788CCAC898C8C23] PRIMARY KEY CLUSTERED 
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Users_Addresses]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users_Addresses](
	[UserID] [bigint] NOT NULL,
	[AddressID] [bigint] NOT NULL,
	[IsDefault] [bit] NOT NULL,
 CONSTRAINT [PK_Users_Addresses] PRIMARY KEY CLUSTERED 
(
	[UserID] ASC,
	[AddressID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[UserVouchers]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[UserVouchers](
	[UserVoucherID] [bigint] IDENTITY(1,1) NOT NULL,
	[VoucherID] [bigint] NOT NULL,
	[UserID] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[UserVoucherID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Vouchers]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Vouchers](
	[VoucherID] [bigint] IDENTITY(1,1) NOT NULL,
	[Code] [nvarchar](40) NOT NULL,
	[DiscountType] [nvarchar](20) NOT NULL,
	[Value] [decimal](12, 2) NOT NULL,
	[MaxAmount] [decimal](12, 2) NULL,
	[MinOrderAmount] [decimal](12, 2) NOT NULL,
	[StartAt] [datetime2](7) NOT NULL,
	[EndAt] [datetime2](7) NOT NULL,
	[UsageLimit] [int] NULL,
	[PerUserLimit] [int] NULL,
	[Status] [nvarchar](20) NOT NULL,
	[UsageCount] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[VoucherID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[VoucherStatus]    Script Date: 10/2/2025 11:36:50 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[VoucherStatus](
	[StatusCode] [nvarchar](20) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[StatusCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[Addresses] ON 

INSERT [dbo].[Addresses] ([AddressID], [RecipientName], [Phone], [Line], [City], [District], [Ward], [PostalCode], [CreatedAt]) VALUES (1, N'Nguyễn Văn A', N'0909123456', N'123 Lý Thường Kiệt', N'Hà Nội', N'Hoàn Kiếm', N'Phường Tràng Tiền', N'100000', CAST(N'2025-09-30T16:18:43.7200000' AS DateTime2))
INSERT [dbo].[Addresses] ([AddressID], [RecipientName], [Phone], [Line], [City], [District], [Ward], [PostalCode], [CreatedAt]) VALUES (2, N'Lê Thị B', N'0912123456', N'456 Nguyễn Trãi', N'Hà Nội', N'Thanh Xuân', N'Phường Thượng Đình', N'100001', CAST(N'2025-09-30T16:18:43.7200000' AS DateTime2))
INSERT [dbo].[Addresses] ([AddressID], [RecipientName], [Phone], [Line], [City], [District], [Ward], [PostalCode], [CreatedAt]) VALUES (3, N'Trần Văn C', N'0933123456', N'789 Lê Lợi', N'Hà Nội', N'Hai Bà Trưng', N'Phường Bạch Mai', N'100002', CAST(N'2025-09-30T16:18:43.7200000' AS DateTime2))
SET IDENTITY_INSERT [dbo].[Addresses] OFF
GO
INSERT [dbo].[BookDetails] ([ProductID], [Translator], [Version], [CoverType], [Pages], [LanguageCode], [Size], [ISBN]) VALUES (1, N'ABC', N'1', N'1', 2000, N'en', N'M', N'NA')
GO
SET IDENTITY_INSERT [dbo].[CartItems] ON 

INSERT [dbo].[CartItems] ([CartItemID], [CartID], [ProductID], [Quantity], [IsChecked], [UnitPrice], [Subtotal]) VALUES (1, 1, 1, 2, 1, CAST(20000.00 AS Decimal(12, 2)), CAST(40000.00 AS Decimal(12, 2)))
INSERT [dbo].[CartItems] ([CartItemID], [CartID], [ProductID], [Quantity], [IsChecked], [UnitPrice], [Subtotal]) VALUES (2, 1, 3, 1, 1, CAST(20000.00 AS Decimal(12, 2)), CAST(20000.00 AS Decimal(12, 2)))
SET IDENTITY_INSERT [dbo].[CartItems] OFF
GO
SET IDENTITY_INSERT [dbo].[Carts] ON 

INSERT [dbo].[Carts] ([CartID], [UserID]) VALUES (1, 1)
SET IDENTITY_INSERT [dbo].[Carts] OFF
GO
INSERT [dbo].[DeliveryFailureReasons] ([ReasonCode], [ReasonText]) VALUES (N'BAD_WEATHER', N'Thời tiết xấu, không thể giao hàng')
INSERT [dbo].[DeliveryFailureReasons] ([ReasonCode], [ReasonText]) VALUES (N'CUSTOMER_REFUSED', N'Khách hàng từ chối nhận')
INSERT [dbo].[DeliveryFailureReasons] ([ReasonCode], [ReasonText]) VALUES (N'DAMAGED_ITEM', N'Sản phẩm bị hư hỏng')
INSERT [dbo].[DeliveryFailureReasons] ([ReasonCode], [ReasonText]) VALUES (N'NO_ONE_HOME', N'Khách không có ở nhà')
INSERT [dbo].[DeliveryFailureReasons] ([ReasonCode], [ReasonText]) VALUES (N'PHONE_NOT_REACHABLE', N'Không liên hệ được với khách hàng')
INSERT [dbo].[DeliveryFailureReasons] ([ReasonCode], [ReasonText]) VALUES (N'RESCHEDULED', N'Khách hẹn giao lại vào thời gian khác')
INSERT [dbo].[DeliveryFailureReasons] ([ReasonCode], [ReasonText]) VALUES (N'SECURITY_DENIED', N'Bảo vệ hoặc khu vực không cho vào')
INSERT [dbo].[DeliveryFailureReasons] ([ReasonCode], [ReasonText]) VALUES (N'TRAFFIC_ISSUE', N'Kẹt xe hoặc sự cố giao thông')
INSERT [dbo].[DeliveryFailureReasons] ([ReasonCode], [ReasonText]) VALUES (N'WRONG_ADDRESS', N'Địa chỉ giao hàng không đúng')
GO
INSERT [dbo].[DiscountType] ([TypeCode]) VALUES (N'AMOUNT')
INSERT [dbo].[DiscountType] ([TypeCode]) VALUES (N'PERCENT')
GO
SET IDENTITY_INSERT [dbo].[FlashSaleItems] ON 

INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (1, 1, 1, 1, CAST(15000.00 AS Decimal(12, 2)), 100, 2, N'APPROVED', CAST(N'2025-09-30T20:35:24.4866667' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (2, 1, 2, 1, CAST(12000.00 AS Decimal(12, 2)), 80, 2, N'APPROVED', CAST(N'2025-09-30T20:35:24.4866667' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (3, 1, 3, 1, CAST(15000.00 AS Decimal(12, 2)), 150, 3, N'APPROVED', CAST(N'2025-09-30T20:35:24.4866667' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (4, 2, 1, 1, CAST(10000.00 AS Decimal(12, 2)), 50, 1, N'PENDING', CAST(N'2025-09-30T20:35:24.4866667' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (5, 2, 2, 1, CAST(9000.00 AS Decimal(12, 2)), 50, 1, N'PENDING', CAST(N'2025-09-30T20:35:24.4866667' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (6, 1, 1, 1, CAST(15000.00 AS Decimal(12, 2)), 100, 2, N'APPROVED', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (7, 1, 2, 1, CAST(12000.00 AS Decimal(12, 2)), 80, 2, N'APPROVED', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (8, 1, 3, 1, CAST(15000.00 AS Decimal(12, 2)), 150, 3, N'APPROVED', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (9, 2, 1, 1, CAST(10000.00 AS Decimal(12, 2)), 200, 1, N'PENDING', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (10, 2, 2, 1, CAST(9000.00 AS Decimal(12, 2)), 200, 1, N'REJECTED', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (11, 2, 3, 1, CAST(15000.00 AS Decimal(12, 2)), 250, 2, N'PENDING', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (12, 3, 1, 1, CAST(8000.00 AS Decimal(12, 2)), 500, 2, N'APPROVED', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (13, 3, 2, 1, CAST(7000.00 AS Decimal(12, 2)), 500, 2, N'PENDING', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (14, 3, 3, 1, CAST(10000.00 AS Decimal(12, 2)), 600, 3, N'REJECTED', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (15, 4, 1, 1, CAST(12000.00 AS Decimal(12, 2)), 100, 1, N'PENDING', CAST(N'2025-09-30T20:44:40.0533333' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (16, 4, 2, 1, CAST(11000.00 AS Decimal(12, 2)), 100, 1, N'PENDING', CAST(N'2025-09-30T20:44:40.0533333' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (17, 4, 3, 1, CAST(16000.00 AS Decimal(12, 2)), 120, 2, N'APPROVED', CAST(N'2025-09-30T20:44:40.0533333' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (18, 5, 1, 1, CAST(9000.00 AS Decimal(12, 2)), 300, 2, N'PENDING', CAST(N'2025-09-30T20:44:40.0533333' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (19, 5, 2, 1, CAST(8000.00 AS Decimal(12, 2)), 300, 2, N'PENDING', CAST(N'2025-09-30T20:44:40.0533333' AS DateTime2))
INSERT [dbo].[FlashSaleItems] ([FlashSaleItemID], [FlashSaleID], [ProductID], [ShopID], [FlashPrice], [FsStock], [PerUserLimit], [ApprovalStatus], [CreatedAt]) VALUES (20, 5, 3, 1, CAST(12000.00 AS Decimal(12, 2)), 400, 3, N'PENDING', CAST(N'2025-09-30T20:44:40.0533333' AS DateTime2))
SET IDENTITY_INSERT [dbo].[FlashSaleItems] OFF
GO
SET IDENTITY_INSERT [dbo].[FlashSales] ON 

INSERT [dbo].[FlashSales] ([FlashSaleID], [Name], [StartAt], [EndAt], [Status], [CreatedAt]) VALUES (1, N'Giảm giá hè 2024', CAST(N'2024-07-01T00:00:00.0000000' AS DateTime2), CAST(N'2024-07-07T23:59:59.0000000' AS DateTime2), N'ACTIVE', CAST(N'2025-09-30T20:35:24.4866667' AS DateTime2))
INSERT [dbo].[FlashSales] ([FlashSaleID], [Name], [StartAt], [EndAt], [Status], [CreatedAt]) VALUES (2, N'Back to School 2024', CAST(N'2024-08-15T00:00:00.0000000' AS DateTime2), CAST(N'2024-08-20T23:59:59.0000000' AS DateTime2), N'SCHEDULED', CAST(N'2025-09-30T20:35:24.4866667' AS DateTime2))
INSERT [dbo].[FlashSales] ([FlashSaleID], [Name], [StartAt], [EndAt], [Status], [CreatedAt]) VALUES (3, N'Giảm giá hè 2025', CAST(N'2024-07-01T00:00:00.0000000' AS DateTime2), CAST(N'2024-07-07T23:59:00.0000000' AS DateTime2), N'ACTIVE', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSales] ([FlashSaleID], [Name], [StartAt], [EndAt], [Status], [CreatedAt]) VALUES (4, N'Back to School 2024', CAST(N'2024-08-15T00:00:00.0000000' AS DateTime2), CAST(N'2024-08-20T23:59:59.0000000' AS DateTime2), N'SCHEDULED', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSales] ([FlashSaleID], [Name], [StartAt], [EndAt], [Status], [CreatedAt]) VALUES (5, N'Black Friday 2024', CAST(N'2024-11-29T00:00:00.0000000' AS DateTime2), CAST(N'2024-11-29T23:59:59.0000000' AS DateTime2), N'SCHEDULED', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSales] ([FlashSaleID], [Name], [StartAt], [EndAt], [Status], [CreatedAt]) VALUES (6, N'Giáng sinh 2024', CAST(N'2024-12-20T00:00:00.0000000' AS DateTime2), CAST(N'2024-12-25T23:59:59.0000000' AS DateTime2), N'SCHEDULED', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSales] ([FlashSaleID], [Name], [StartAt], [EndAt], [Status], [CreatedAt]) VALUES (7, N'Tết Nguyên Đán 2025', CAST(N'2025-01-15T00:00:00.0000000' AS DateTime2), CAST(N'2025-01-31T23:59:59.0000000' AS DateTime2), N'SCHEDULED', CAST(N'2025-09-30T20:44:40.0500000' AS DateTime2))
INSERT [dbo].[FlashSales] ([FlashSaleID], [Name], [StartAt], [EndAt], [Status], [CreatedAt]) VALUES (8, N'Balcjk myth wukong', CAST(N'2025-10-03T22:20:00.0000000' AS DateTime2), CAST(N'2025-10-08T22:21:00.0000000' AS DateTime2), N'ACTIVE', CAST(N'2025-09-30T15:21:11.9822363' AS DateTime2))
SET IDENTITY_INSERT [dbo].[FlashSales] OFF
GO
INSERT [dbo].[FlashSaleStatus] ([StatusCode]) VALUES (N'ACTIVE')
INSERT [dbo].[FlashSaleStatus] ([StatusCode]) VALUES (N'CANCELED')
INSERT [dbo].[FlashSaleStatus] ([StatusCode]) VALUES (N'ENDED')
INSERT [dbo].[FlashSaleStatus] ([StatusCode]) VALUES (N'PAUSED')
INSERT [dbo].[FlashSaleStatus] ([StatusCode]) VALUES (N'SCHEDULED')
GO
INSERT [dbo].[FlashSaleItemApprovalStatus] ([StatusCode]) VALUES (N'PENDING')
INSERT [dbo].[FlashSaleItemApprovalStatus] ([StatusCode]) VALUES (N'APPROVED')
INSERT [dbo].[FlashSaleItemApprovalStatus] ([StatusCode]) VALUES (N'REJECTED')
GO
INSERT [dbo].[Languages] ([LanguageCode], [LanguageName]) VALUES (N'en', N'Tiếng Anh')
INSERT [dbo].[Languages] ([LanguageCode], [LanguageName]) VALUES (N'vi', N'Tiếng Việt')
GO
SET IDENTITY_INSERT [dbo].[OrderItems] ON 

INSERT [dbo].[OrderItems] ([OrderItemID], [OrderShopID], [ProductID], [FlashSaleItemID], [Quantity], [UnitPrice]) VALUES (1, 1, 1, NULL, 2, CAST(20000.00 AS Decimal(12, 2)))
INSERT [dbo].[OrderItems] ([OrderItemID], [OrderShopID], [ProductID], [FlashSaleItemID], [Quantity], [UnitPrice]) VALUES (2, 1, 3, NULL, 1, CAST(20000.00 AS Decimal(12, 2)))
SET IDENTITY_INSERT [dbo].[OrderItems] OFF
GO
SET IDENTITY_INSERT [dbo].[Orders] ON 

INSERT [dbo].[Orders] ([OrderID], [UserID], [AddressID], [VoucherID], [TotalAmount], [DiscountAmount], [PaymentMethod], [PaymentStatus], [OrderStatus], [CreatedAt], [DeliveredAt]) VALUES (1, 1, 1, 1, CAST(60000.00 AS Decimal(12, 2)), CAST(10000.00 AS Decimal(12, 2)), N'COD', N'PENDING', N'PLACED', CAST(N'2025-09-30T16:18:43.7666667' AS DateTime2), NULL)
SET IDENTITY_INSERT [dbo].[Orders] OFF
GO
SET IDENTITY_INSERT [dbo].[OrderShops] ON 

INSERT [dbo].[OrderShops] ([OrderShopID], [OrderID], [ShopID], [ShippingFee], [Status], [CreatedAt]) VALUES (1, 1, 1, CAST(15000.00 AS Decimal(12, 2)), N'PLACED', CAST(N'2025-09-30T16:18:43.7700000' AS DateTime2))
SET IDENTITY_INSERT [dbo].[OrderShops] OFF
GO
INSERT [dbo].[OrderStatus] ([StatusCode]) VALUES (N'CANCELED')
INSERT [dbo].[OrderStatus] ([StatusCode]) VALUES (N'CONFIRMED')
INSERT [dbo].[OrderStatus] ([StatusCode]) VALUES (N'DELIVERED')
INSERT [dbo].[OrderStatus] ([StatusCode]) VALUES (N'FAILED')
INSERT [dbo].[OrderStatus] ([StatusCode]) VALUES (N'PACKED')
INSERT [dbo].[OrderStatus] ([StatusCode]) VALUES (N'PLACED')
INSERT [dbo].[OrderStatus] ([StatusCode]) VALUES (N'RETURNED')
INSERT [dbo].[OrderStatus] ([StatusCode]) VALUES (N'SHIPPING')
GO
INSERT [dbo].[PaymentMethod] ([MethodCode], [MethodName], [Description]) VALUES (N'COD', N'Thanh toán khi nhận hàng (COD)', N'Thu hộ tiền mặt khi giao')
INSERT [dbo].[PaymentMethod] ([MethodCode], [MethodName], [Description]) VALUES (N'VNPAY', N'VNPAY', N'Thanh toán qua cổng VNPAY')
GO
SET IDENTITY_INSERT [dbo].[Payments] ON 

INSERT [dbo].[Payments] ([PaymentID], [OrderID], [Amount], [Method], [TransactionRef], [Status], [CreatedAt]) VALUES (1, 1, CAST(50000.00 AS Decimal(12, 2)), N'COD', N'TRANS123456', N'PENDING', CAST(N'2025-09-30T16:18:43.7766667' AS DateTime2))
SET IDENTITY_INSERT [dbo].[Payments] OFF
GO
INSERT [dbo].[PaymentStatus] ([StatusCode]) VALUES (N'AUTHORIZED')
INSERT [dbo].[PaymentStatus] ([StatusCode]) VALUES (N'CANCELED')
INSERT [dbo].[PaymentStatus] ([StatusCode]) VALUES (N'CAPTURED')
INSERT [dbo].[PaymentStatus] ([StatusCode]) VALUES (N'FAILED')
INSERT [dbo].[PaymentStatus] ([StatusCode]) VALUES (N'PARTIALLY_CAPTURED')
INSERT [dbo].[PaymentStatus] ([StatusCode]) VALUES (N'PARTIALLY_REFUNDED')
INSERT [dbo].[PaymentStatus] ([StatusCode]) VALUES (N'PENDING')
INSERT [dbo].[PaymentStatus] ([StatusCode]) VALUES (N'REFUNDED')
GO
SET IDENTITY_INSERT [dbo].[ProductCategories] ON 

INSERT [dbo].[ProductCategories] ([CategoryID], [Name], [ParentID]) VALUES (1, N'Sách giáo khoa', NULL)
INSERT [dbo].[ProductCategories] ([CategoryID], [Name], [ParentID]) VALUES (2, N'Sách tham khảo', NULL)
SET IDENTITY_INSERT [dbo].[ProductCategories] OFF
GO
SET IDENTITY_INSERT [dbo].[ProductImages] ON 

INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (1, 1, N'thumb-main.jpg', 1)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (2, 1, N'thumb1.jpg', 0)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (3, 1, N'thumb2.webp', 0)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (4, 1, N'thumb3.webp', 0)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (5, 1, N'thumb4.png', 0)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (6, 2, N'thumb-main.jpg', 0)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (7, 2, N'thumb1.jpg', 0)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (8, 2, N'thumb2.webp', 1)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (9, 2, N'thumb3.webp', 0)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (10, 2, N'thumb4.png', 0)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (11, 3, N'thumb-main.jpg', 0)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (12, 3, N'thumb1.jpg', 1)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (13, 3, N'thumb2.webp', 0)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (14, 3, N'thumb3.webp', 0)
INSERT [dbo].[ProductImages] ([ImageID], [ProductID], [Url], [IsPrimary]) VALUES (15, 3, N'thumb4.png', 0)
SET IDENTITY_INSERT [dbo].[ProductImages] OFF
GO
SET IDENTITY_INSERT [dbo].[Products] ON 

INSERT [dbo].[Products] ([ProductID], [ShopID], [Title], [Description], [OriginalPrice], [SalePrice], [SoldCount], [Stock], [IsBundle], [CategoryID], [PublisherID], [PublishedDate]) VALUES (1, 1, N'Sách Toán lớp 1', N'Sách giáo khoa Toán lớp 1', CAST(20000.00 AS Decimal(12, 2)), CAST(20000.00 AS Decimal(12, 2)), 100, 500, 0, 1, 1, CAST(N'2020-09-01' AS Date))
INSERT [dbo].[Products] ([ProductID], [ShopID], [Title], [Description], [OriginalPrice], [SalePrice], [SoldCount], [Stock], [IsBundle], [CategoryID], [PublisherID], [PublishedDate]) VALUES (2, 1, N'Sách Văn lớp 1', N'Sách giáo khoa Văn lớp 1', CAST(18000.00 AS Decimal(12, 2)), CAST(18000.00 AS Decimal(12, 2)), 80, 400, 0, 1, 1, CAST(N'2020-09-01' AS Date))
INSERT [dbo].[Products] ([ProductID], [ShopID], [Title], [Description], [OriginalPrice], [SalePrice], [SoldCount], [Stock], [IsBundle], [CategoryID], [PublisherID], [PublishedDate]) VALUES (3, 1, N'Truyện tranh Doremon tập 1', N'Truyện thiếu nhi nổi tiếng', CAST(25000.00 AS Decimal(12, 2)), CAST(20000.00 AS Decimal(12, 2)), 300, 200, 0, 2, 2, CAST(N'2018-01-01' AS Date))
SET IDENTITY_INSERT [dbo].[Products] OFF
GO
SET IDENTITY_INSERT [dbo].[Publishers] ON 

INSERT [dbo].[Publishers] ([PublisherID], [Name]) VALUES (1, N'NXB Giáo dục')
INSERT [dbo].[Publishers] ([PublisherID], [Name]) VALUES (2, N'NXB Kim Đồng')
SET IDENTITY_INSERT [dbo].[Publishers] OFF
GO
INSERT [dbo].[Roles] ([RoleCode], [RoleName]) VALUES (N'ADMIN', N'Quản trị viên')
INSERT [dbo].[Roles] ([RoleCode], [RoleName]) VALUES (N'CUSTOMER', N'Khách hàng')
INSERT [dbo].[Roles] ([RoleCode], [RoleName]) VALUES (N'SHIPPER', N'Người giao hàng')
INSERT [dbo].[Roles] ([RoleCode], [RoleName]) VALUES (N'SHOP_OWNER', N'Chủ cửa hàng')
GO
SET IDENTITY_INSERT [dbo].[Shipments] ON 

INSERT [dbo].[Shipments] ([ShipmentID], [OrderShopID], [ShipperID], [TrackingCode], [Status], [PickedAt], [DeliveredAt], [CODCollected]) VALUES (1, 1, 1, N'TRACK123', N'AWAIT_PICKUP', NULL, NULL, 0)
SET IDENTITY_INSERT [dbo].[Shipments] OFF
GO
INSERT [dbo].[ShipmentStatus] ([StatusCode]) VALUES (N'AWAIT_PICKUP')
INSERT [dbo].[ShipmentStatus] ([StatusCode]) VALUES (N'CANCELED')
INSERT [dbo].[ShipmentStatus] ([StatusCode]) VALUES (N'DELIVERED')
INSERT [dbo].[ShipmentStatus] ([StatusCode]) VALUES (N'FAILED_ATTEMPT')
INSERT [dbo].[ShipmentStatus] ([StatusCode]) VALUES (N'IN_TRANSIT')
INSERT [dbo].[ShipmentStatus] ([StatusCode]) VALUES (N'OUT_FOR_DELIVERY')
INSERT [dbo].[ShipmentStatus] ([StatusCode]) VALUES (N'PICKED')
INSERT [dbo].[ShipmentStatus] ([StatusCode]) VALUES (N'RETURN_TO_SENDER')
GO
SET IDENTITY_INSERT [dbo].[Shippers] ON 

INSERT [dbo].[Shippers] ([ShipperID], [UserID], [Status], [Area], [HireDate]) VALUES (1, 3, N'ACTIVE', N'Hà Nội', CAST(N'2024-01-01' AS Date))
SET IDENTITY_INSERT [dbo].[Shippers] OFF
GO
INSERT [dbo].[ShipperStatus] ([StatusCode]) VALUES (N'ACTIVE')
INSERT [dbo].[ShipperStatus] ([StatusCode]) VALUES (N'INACTIVE')
INSERT [dbo].[ShipperStatus] ([StatusCode]) VALUES (N'OFFBOARD')
INSERT [dbo].[ShipperStatus] ([StatusCode]) VALUES (N'SUSPENDED')
GO
INSERT [dbo].[ShippingMethods] ([MethodCode], [MethodName], [Description]) VALUES (N'EXPRESS', N'Nhanh', N'Giao trong 24–48 giờ, ưu tiên tuyến nhanh')
INSERT [dbo].[ShippingMethods] ([MethodCode], [MethodName], [Description]) VALUES (N'SAME_DAY', N'Giao trong ngày', N'Áp dụng nội thành, đặt trước cut-off')
INSERT [dbo].[ShippingMethods] ([MethodCode], [MethodName], [Description]) VALUES (N'STANDARD', N'Tiêu chuẩn', N'Giao trong 2–4 ngày làm việc, phí thấp')
GO
SET IDENTITY_INSERT [dbo].[Shops] ON 

INSERT [dbo].[Shops] ([ShopID], [Name], [Description], [RatingAvg], [Status], [OwnerUserID], [CreatedAt], [PickupAddressID], [InvoiceEmail], [AvatarUrl]) VALUES (1, N'Nhà sách ABC', N'Chuyên bán sách giáo khoa và tham khảo', CAST(4.50 AS Decimal(3, 2)), N'APPROVED', 2, CAST(N'2025-09-30T16:18:43.7266667' AS DateTime2), 2, N'shop.abc@example.com', N'/assets/images/shops/shop_07f65cef-766e-422d-967b-b26bc74688a9.png')
SET IDENTITY_INSERT [dbo].[Shops] OFF
GO
INSERT [dbo].[Shops_ShippingMethods] ([ShopID], [MethodCode]) VALUES (1, N'EXPRESS')
INSERT [dbo].[Shops_ShippingMethods] ([ShopID], [MethodCode]) VALUES (1, N'STANDARD')
GO
INSERT [dbo].[ShopStatus] ([StatusCode]) VALUES (N'APPROVED')
INSERT [dbo].[ShopStatus] ([StatusCode]) VALUES (N'BANNED')
INSERT [dbo].[ShopStatus] ([StatusCode]) VALUES (N'PENDING')
INSERT [dbo].[ShopStatus] ([StatusCode]) VALUES (N'SUSPENDED')
GO
INSERT [dbo].[UserRoles] ([UserID], [RoleCode]) VALUES (1, N'CUSTOMER')
INSERT [dbo].[UserRoles] ([UserID], [RoleCode]) VALUES (2, N'SHOP_OWNER')
INSERT [dbo].[UserRoles] ([UserID], [RoleCode]) VALUES (3, N'SHIPPER')
INSERT [dbo].[UserRoles] ([UserID], [RoleCode]) VALUES (4, N'ADMIN')
GO
SET IDENTITY_INSERT [dbo].[Users] ON 

INSERT [dbo].[Users] ([UserID], [Email], [Password], [FullName], [Phone], [Points], [NationalID], [AvatarUrl], [CreatedAt], [AuthProvider], [Status]) VALUES (1, N'customer1@example.com', N'123456', N'Nguyễn Văn A', N'0909123456', 100, N'123456789', NULL, CAST(N'2025-09-30T16:18:43.7100000' AS DateTime2), N'LOCAL', N'active    ')
INSERT [dbo].[Users] ([UserID], [Email], [Password], [FullName], [Phone], [Points], [NationalID], [AvatarUrl], [CreatedAt], [AuthProvider], [Status]) VALUES (2, N'shopowner1@example.com', N'123456', N'Lê Thị B', N'0912123456', 200, N'234567891', NULL, CAST(N'2025-09-30T16:18:43.7100000' AS DateTime2), N'LOCAL', N'active    ')
INSERT [dbo].[Users] ([UserID], [Email], [Password], [FullName], [Phone], [Points], [NationalID], [AvatarUrl], [CreatedAt], [AuthProvider], [Status]) VALUES (3, N'shipper1@example.com', N'123456', N'Trần Văn C', N'0933123456', 50, N'345678912', NULL, CAST(N'2025-09-30T16:18:43.7100000' AS DateTime2), N'LOCAL', N'active    ')
INSERT [dbo].[Users] ([UserID], [Email], [Password], [FullName], [Phone], [Points], [NationalID], [AvatarUrl], [CreatedAt], [AuthProvider], [Status]) VALUES (4, N'admin@example.com', N'123456', N'Nguyễn Quản Trị', N'0909999999', 0, NULL, NULL, CAST(N'2025-09-30T17:27:28.5833333' AS DateTime2), N'LOCAL', N'active    ')
SET IDENTITY_INSERT [dbo].[Users] OFF
GO
INSERT [dbo].[Users_Addresses] ([UserID], [AddressID], [IsDefault]) VALUES (1, 1, 1)
INSERT [dbo].[Users_Addresses] ([UserID], [AddressID], [IsDefault]) VALUES (2, 2, 1)
INSERT [dbo].[Users_Addresses] ([UserID], [AddressID], [IsDefault]) VALUES (3, 3, 1)
GO
SET IDENTITY_INSERT [dbo].[Vouchers] ON 

INSERT [dbo].[Vouchers] ([VoucherID], [Code], [DiscountType], [Value], [MaxAmount], [MinOrderAmount], [StartAt], [EndAt], [UsageLimit], [PerUserLimit], [Status], [UsageCount]) VALUES (1, N'GIAM10K', N'AMOUNT', CAST(10000.00 AS Decimal(12, 2)), NULL, CAST(50000.00 AS Decimal(12, 2)), CAST(N'2025-09-30T16:18:43.7600000' AS DateTime2), CAST(N'2025-10-30T16:18:43.7600000' AS DateTime2), 100, 1, N'ACTIVE', 0)
SET IDENTITY_INSERT [dbo].[Vouchers] OFF
GO
INSERT [dbo].[VoucherStatus] ([StatusCode]) VALUES (N'ACTIVE')
INSERT [dbo].[VoucherStatus] ([StatusCode]) VALUES (N'EXPIRED')
INSERT [dbo].[VoucherStatus] ([StatusCode]) VALUES (N'INACTIVE')
INSERT [dbo].[VoucherStatus] ([StatusCode]) VALUES (N'SCHEDULED')
GO
/****** Object:  Index [UQ__Carts__1788CCADD07962E2]    Script Date: 10/2/2025 11:36:50 AM ******/
ALTER TABLE [dbo].[Carts] ADD UNIQUE NONCLUSTERED 
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [UQ__Payments__C3905BAE9A892F5B]    Script Date: 10/2/2025 11:36:50 AM ******/
ALTER TABLE [dbo].[Payments] ADD UNIQUE NONCLUSTERED 
(
	[OrderID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [UQ__Shippers__1788CCADCB1D1F47]    Script Date: 10/2/2025 11:36:50 AM ******/
ALTER TABLE [dbo].[Shippers] ADD UNIQUE NONCLUSTERED 
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[CartItems] ADD  DEFAULT ((1)) FOR [IsChecked]
GO
ALTER TABLE [dbo].[Products] ADD  DEFAULT ((0)) FOR [SoldCount]
GO
ALTER TABLE [dbo].[Products] ADD  DEFAULT ((0)) FOR [IsBundle]
GO
ALTER TABLE [dbo].[Users] ADD  CONSTRAINT [DF__Users__Points__114A936A]  DEFAULT ((0)) FOR [Points]
GO
ALTER TABLE [dbo].[Users] ADD  CONSTRAINT [DF__Users__CreatedAt__123EB7A3]  DEFAULT (sysutcdatetime()) FOR [CreatedAt]
GO
ALTER TABLE [dbo].[BookAuthors]  WITH CHECK ADD  CONSTRAINT [FK_BookAuthors_Author] FOREIGN KEY([AuthorID])
REFERENCES [dbo].[Authors] ([AuthorID])
GO
ALTER TABLE [dbo].[BookAuthors] CHECK CONSTRAINT [FK_BookAuthors_Author]
GO
ALTER TABLE [dbo].[BookAuthors]  WITH CHECK ADD  CONSTRAINT [FK_BookAuthors_Product] FOREIGN KEY([ProductID])
REFERENCES [dbo].[Products] ([ProductID])
GO
ALTER TABLE [dbo].[BookAuthors] CHECK CONSTRAINT [FK_BookAuthors_Product]
GO
ALTER TABLE [dbo].[BookDetails]  WITH CHECK ADD  CONSTRAINT [FK_BookDetails_Language] FOREIGN KEY([LanguageCode])
REFERENCES [dbo].[Languages] ([LanguageCode])
GO
ALTER TABLE [dbo].[BookDetails] CHECK CONSTRAINT [FK_BookDetails_Language]
GO
ALTER TABLE [dbo].[BookDetails]  WITH CHECK ADD  CONSTRAINT [FK_BookDetails_Product] FOREIGN KEY([ProductID])
REFERENCES [dbo].[Products] ([ProductID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[BookDetails] CHECK CONSTRAINT [FK_BookDetails_Product]
GO
ALTER TABLE [dbo].[BundleItems]  WITH CHECK ADD  CONSTRAINT [FK_BundleItems_Bundle] FOREIGN KEY([BundleProductID])
REFERENCES [dbo].[Products] ([ProductID])
GO
ALTER TABLE [dbo].[BundleItems] CHECK CONSTRAINT [FK_BundleItems_Bundle]
GO
ALTER TABLE [dbo].[BundleItems]  WITH CHECK ADD  CONSTRAINT [FK_BundleItems_Product] FOREIGN KEY([ProductID])
REFERENCES [dbo].[Products] ([ProductID])
GO
ALTER TABLE [dbo].[BundleItems] CHECK CONSTRAINT [FK_BundleItems_Product]
GO
ALTER TABLE [dbo].[CartItems]  WITH CHECK ADD  CONSTRAINT [FK_CartItems_Cart] FOREIGN KEY([CartID])
REFERENCES [dbo].[Carts] ([CartID])
GO
ALTER TABLE [dbo].[CartItems] CHECK CONSTRAINT [FK_CartItems_Cart]
GO
ALTER TABLE [dbo].[CartItems]  WITH CHECK ADD  CONSTRAINT [FK_CartItems_Product] FOREIGN KEY([ProductID])
REFERENCES [dbo].[Products] ([ProductID])
GO
ALTER TABLE [dbo].[CartItems] CHECK CONSTRAINT [FK_CartItems_Product]
GO
ALTER TABLE [dbo].[Carts]  WITH CHECK ADD  CONSTRAINT [FK_Carts_User] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Carts] CHECK CONSTRAINT [FK_Carts_User]
GO
ALTER TABLE [dbo].[DeliveryAttempts]  WITH CHECK ADD  CONSTRAINT [FK_DeliveryAttempts_Reason] FOREIGN KEY([ReasonCode])
REFERENCES [dbo].[DeliveryFailureReasons] ([ReasonCode])
GO
ALTER TABLE [dbo].[DeliveryAttempts] CHECK CONSTRAINT [FK_DeliveryAttempts_Reason]
GO
ALTER TABLE [dbo].[DeliveryAttempts]  WITH CHECK ADD  CONSTRAINT [FK_DeliveryAttempts_Shipment] FOREIGN KEY([ShipmentID])
REFERENCES [dbo].[Shipments] ([ShipmentID])
GO
ALTER TABLE [dbo].[DeliveryAttempts] CHECK CONSTRAINT [FK_DeliveryAttempts_Shipment]
GO
ALTER TABLE [dbo].[DeliveryAttempts]  WITH CHECK ADD  CONSTRAINT [FK_DeliveryAttempts_Status] FOREIGN KEY([Status])
REFERENCES [dbo].[ShipmentStatus] ([StatusCode])
GO
ALTER TABLE [dbo].[DeliveryAttempts] CHECK CONSTRAINT [FK_DeliveryAttempts_Status]
GO
ALTER TABLE [dbo].[FlashSaleItems]  WITH CHECK ADD  CONSTRAINT [FK_FSI_FlashSale] FOREIGN KEY([FlashSaleID])
REFERENCES [dbo].[FlashSales] ([FlashSaleID])
GO
ALTER TABLE [dbo].[FlashSaleItems] CHECK CONSTRAINT [FK_FSI_FlashSale]
GO
ALTER TABLE [dbo].[FlashSaleItems]  WITH CHECK ADD  CONSTRAINT [FK_FSI_Product] FOREIGN KEY([ProductID])
REFERENCES [dbo].[Products] ([ProductID])
GO
ALTER TABLE [dbo].[FlashSaleItems]  WITH CHECK ADD  CONSTRAINT [FK_FSI_ApprovalStatus] FOREIGN KEY([ApprovalStatus])
REFERENCES [dbo].[FlashSaleItemApprovalStatus] ([StatusCode])
GO
ALTER TABLE [dbo].[FlashSaleItems] CHECK CONSTRAINT [FK_FSI_ApprovalStatus]
GO
ALTER TABLE [dbo].[FlashSaleItems] CHECK CONSTRAINT [FK_FSI_Product]
GO
ALTER TABLE [dbo].[FlashSaleItems]  WITH CHECK ADD  CONSTRAINT [FK_FSI_Shop] FOREIGN KEY([ShopID])
REFERENCES [dbo].[Shops] ([ShopID])
GO
ALTER TABLE [dbo].[FlashSaleItems] CHECK CONSTRAINT [FK_FSI_Shop]
GO
ALTER TABLE [dbo].[FlashSales]  WITH CHECK ADD  CONSTRAINT [FK_FlashSales_Status] FOREIGN KEY([Status])
REFERENCES [dbo].[FlashSaleStatus] ([StatusCode])
GO
ALTER TABLE [dbo].[FlashSales] CHECK CONSTRAINT [FK_FlashSales_Status]
GO
ALTER TABLE [dbo].[OrderItems]  WITH CHECK ADD  CONSTRAINT [FK_OrderItems_FlashSaleItem] FOREIGN KEY([FlashSaleItemID])
REFERENCES [dbo].[FlashSaleItems] ([FlashSaleItemID])
GO
ALTER TABLE [dbo].[OrderItems] CHECK CONSTRAINT [FK_OrderItems_FlashSaleItem]
GO
ALTER TABLE [dbo].[OrderItems]  WITH CHECK ADD  CONSTRAINT [FK_OrderItems_OrderShop] FOREIGN KEY([OrderShopID])
REFERENCES [dbo].[OrderShops] ([OrderShopID])
GO
ALTER TABLE [dbo].[OrderItems] CHECK CONSTRAINT [FK_OrderItems_OrderShop]
GO
ALTER TABLE [dbo].[OrderItems]  WITH CHECK ADD  CONSTRAINT [FK_OrderItems_Product] FOREIGN KEY([ProductID])
REFERENCES [dbo].[Products] ([ProductID])
GO
ALTER TABLE [dbo].[OrderItems] CHECK CONSTRAINT [FK_OrderItems_Product]
GO
ALTER TABLE [dbo].[Orders]  WITH CHECK ADD  CONSTRAINT [FK_Orders_Address] FOREIGN KEY([AddressID])
REFERENCES [dbo].[Addresses] ([AddressID])
GO
ALTER TABLE [dbo].[Orders] CHECK CONSTRAINT [FK_Orders_Address]
GO
ALTER TABLE [dbo].[Orders]  WITH CHECK ADD  CONSTRAINT [FK_Orders_OrderStatus] FOREIGN KEY([OrderStatus])
REFERENCES [dbo].[OrderStatus] ([StatusCode])
GO
ALTER TABLE [dbo].[Orders] CHECK CONSTRAINT [FK_Orders_OrderStatus]
GO
ALTER TABLE [dbo].[Orders]  WITH CHECK ADD  CONSTRAINT [FK_Orders_PaymentMethod] FOREIGN KEY([PaymentMethod])
REFERENCES [dbo].[PaymentMethod] ([MethodCode])
GO
ALTER TABLE [dbo].[Orders] CHECK CONSTRAINT [FK_Orders_PaymentMethod]
GO
ALTER TABLE [dbo].[Orders]  WITH CHECK ADD  CONSTRAINT [FK_Orders_PaymentStatus] FOREIGN KEY([PaymentStatus])
REFERENCES [dbo].[PaymentStatus] ([StatusCode])
GO
ALTER TABLE [dbo].[Orders] CHECK CONSTRAINT [FK_Orders_PaymentStatus]
GO
ALTER TABLE [dbo].[Orders]  WITH CHECK ADD  CONSTRAINT [FK_Orders_User] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Orders] CHECK CONSTRAINT [FK_Orders_User]
GO
ALTER TABLE [dbo].[Orders]  WITH CHECK ADD  CONSTRAINT [FK_Orders_Voucher] FOREIGN KEY([VoucherID])
REFERENCES [dbo].[Vouchers] ([VoucherID])
GO
ALTER TABLE [dbo].[Orders] CHECK CONSTRAINT [FK_Orders_Voucher]
GO
ALTER TABLE [dbo].[OrderShops]  WITH CHECK ADD  CONSTRAINT [FK_OrderShops_Order] FOREIGN KEY([OrderID])
REFERENCES [dbo].[Orders] ([OrderID])
GO
ALTER TABLE [dbo].[OrderShops] CHECK CONSTRAINT [FK_OrderShops_Order]
GO
ALTER TABLE [dbo].[OrderShops]  WITH CHECK ADD  CONSTRAINT [FK_OrderShops_Shop] FOREIGN KEY([ShopID])
REFERENCES [dbo].[Shops] ([ShopID])
GO
ALTER TABLE [dbo].[OrderShops] CHECK CONSTRAINT [FK_OrderShops_Shop]
GO
ALTER TABLE [dbo].[Payments]  WITH CHECK ADD  CONSTRAINT [FK_Payments_Method] FOREIGN KEY([Method])
REFERENCES [dbo].[PaymentMethod] ([MethodCode])
GO
ALTER TABLE [dbo].[Payments] CHECK CONSTRAINT [FK_Payments_Method]
GO
ALTER TABLE [dbo].[Payments]  WITH CHECK ADD  CONSTRAINT [FK_Payments_Order] FOREIGN KEY([OrderID])
REFERENCES [dbo].[Orders] ([OrderID])
GO
ALTER TABLE [dbo].[Payments] CHECK CONSTRAINT [FK_Payments_Order]
GO
ALTER TABLE [dbo].[Payments]  WITH CHECK ADD  CONSTRAINT [FK_Payments_Status] FOREIGN KEY([Status])
REFERENCES [dbo].[PaymentStatus] ([StatusCode])
GO
ALTER TABLE [dbo].[Payments] CHECK CONSTRAINT [FK_Payments_Status]
GO
ALTER TABLE [dbo].[ProductImages]  WITH CHECK ADD  CONSTRAINT [FK_ProductImages_Product] FOREIGN KEY([ProductID])
REFERENCES [dbo].[Products] ([ProductID])
GO
ALTER TABLE [dbo].[ProductImages] CHECK CONSTRAINT [FK_ProductImages_Product]
GO
ALTER TABLE [dbo].[Products]  WITH CHECK ADD  CONSTRAINT [FK_Products_Category] FOREIGN KEY([CategoryID])
REFERENCES [dbo].[ProductCategories] ([CategoryID])
GO
ALTER TABLE [dbo].[Products] CHECK CONSTRAINT [FK_Products_Category]
GO
ALTER TABLE [dbo].[Products]  WITH CHECK ADD  CONSTRAINT [FK_Products_Publisher] FOREIGN KEY([PublisherID])
REFERENCES [dbo].[Publishers] ([PublisherID])
GO
ALTER TABLE [dbo].[Products] CHECK CONSTRAINT [FK_Products_Publisher]
GO
ALTER TABLE [dbo].[Products]  WITH CHECK ADD  CONSTRAINT [FK_Products_Shop] FOREIGN KEY([ShopID])
REFERENCES [dbo].[Shops] ([ShopID])
GO
ALTER TABLE [dbo].[Products] CHECK CONSTRAINT [FK_Products_Shop]
GO
ALTER TABLE [dbo].[RememberMeTokens]  WITH CHECK ADD  CONSTRAINT [FK_RM_User] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[RememberMeTokens] CHECK CONSTRAINT [FK_RM_User]
GO
ALTER TABLE [dbo].[ReviewImages]  WITH CHECK ADD  CONSTRAINT [FK_ReviewImages_Review] FOREIGN KEY([ReviewID])
REFERENCES [dbo].[Reviews] ([ReviewID])
GO
ALTER TABLE [dbo].[ReviewImages] CHECK CONSTRAINT [FK_ReviewImages_Review]
GO
ALTER TABLE [dbo].[Reviews]  WITH CHECK ADD  CONSTRAINT [FK_Reviews_OrderItem] FOREIGN KEY([OrderItemID])
REFERENCES [dbo].[OrderItems] ([OrderItemID])
GO
ALTER TABLE [dbo].[Reviews] CHECK CONSTRAINT [FK_Reviews_OrderItem]
GO
ALTER TABLE [dbo].[Reviews]  WITH CHECK ADD  CONSTRAINT [FK_Reviews_User] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Reviews] CHECK CONSTRAINT [FK_Reviews_User]
GO
ALTER TABLE [dbo].[ShipmentEvents]  WITH CHECK ADD  CONSTRAINT [FK_ShipmentEvents_Shipment] FOREIGN KEY([ShipmentID])
REFERENCES [dbo].[Shipments] ([ShipmentID])
GO
ALTER TABLE [dbo].[ShipmentEvents] CHECK CONSTRAINT [FK_ShipmentEvents_Shipment]
GO
ALTER TABLE [dbo].[ShipmentEvents]  WITH CHECK ADD  CONSTRAINT [FK_ShipmentEvents_Status] FOREIGN KEY([Status])
REFERENCES [dbo].[ShipmentStatus] ([StatusCode])
GO
ALTER TABLE [dbo].[ShipmentEvents] CHECK CONSTRAINT [FK_ShipmentEvents_Status]
GO
ALTER TABLE [dbo].[Shipments]  WITH CHECK ADD  CONSTRAINT [FK_Shipments_OrderShop] FOREIGN KEY([OrderShopID])
REFERENCES [dbo].[OrderShops] ([OrderShopID])
GO
ALTER TABLE [dbo].[Shipments] CHECK CONSTRAINT [FK_Shipments_OrderShop]
GO
ALTER TABLE [dbo].[Shipments]  WITH CHECK ADD  CONSTRAINT [FK_Shipments_Shipper] FOREIGN KEY([ShipperID])
REFERENCES [dbo].[Shippers] ([ShipperID])
GO
ALTER TABLE [dbo].[Shipments] CHECK CONSTRAINT [FK_Shipments_Shipper]
GO
ALTER TABLE [dbo].[Shipments]  WITH CHECK ADD  CONSTRAINT [FK_Shipments_Status] FOREIGN KEY([Status])
REFERENCES [dbo].[ShipmentStatus] ([StatusCode])
GO
ALTER TABLE [dbo].[Shipments] CHECK CONSTRAINT [FK_Shipments_Status]
GO
ALTER TABLE [dbo].[ShipperRatings]  WITH CHECK ADD  CONSTRAINT [FK_ShipperRatings_Shipment] FOREIGN KEY([ShipmentID])
REFERENCES [dbo].[Shipments] ([ShipmentID])
GO
ALTER TABLE [dbo].[ShipperRatings] CHECK CONSTRAINT [FK_ShipperRatings_Shipment]
GO
ALTER TABLE [dbo].[ShipperRatings]  WITH CHECK ADD  CONSTRAINT [FK_ShipperRatings_Shipper] FOREIGN KEY([ShipperID])
REFERENCES [dbo].[Shippers] ([ShipperID])
GO
ALTER TABLE [dbo].[ShipperRatings] CHECK CONSTRAINT [FK_ShipperRatings_Shipper]
GO
ALTER TABLE [dbo].[ShipperRatings]  WITH CHECK ADD  CONSTRAINT [FK_ShipperRatings_User] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[ShipperRatings] CHECK CONSTRAINT [FK_ShipperRatings_User]
GO
ALTER TABLE [dbo].[Shippers]  WITH CHECK ADD  CONSTRAINT [FK_Shippers_Status] FOREIGN KEY([Status])
REFERENCES [dbo].[ShipperStatus] ([StatusCode])
GO
ALTER TABLE [dbo].[Shippers] CHECK CONSTRAINT [FK_Shippers_Status]
GO
ALTER TABLE [dbo].[Shippers]  WITH CHECK ADD  CONSTRAINT [FK_Shippers_User] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Shippers] CHECK CONSTRAINT [FK_Shippers_User]
GO
ALTER TABLE [dbo].[Shops]  WITH CHECK ADD  CONSTRAINT [FK_Shops_Owner] FOREIGN KEY([OwnerUserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Shops] CHECK CONSTRAINT [FK_Shops_Owner]
GO
ALTER TABLE [dbo].[Shops]  WITH CHECK ADD  CONSTRAINT [FK_Shops_PickupAddr] FOREIGN KEY([PickupAddressID])
REFERENCES [dbo].[Addresses] ([AddressID])
GO
ALTER TABLE [dbo].[Shops] CHECK CONSTRAINT [FK_Shops_PickupAddr]
GO
ALTER TABLE [dbo].[Shops]  WITH CHECK ADD  CONSTRAINT [FK_Shops_Status] FOREIGN KEY([Status])
REFERENCES [dbo].[ShopStatus] ([StatusCode])
GO
ALTER TABLE [dbo].[Shops] CHECK CONSTRAINT [FK_Shops_Status]
GO
ALTER TABLE [dbo].[Shops_ShippingMethods]  WITH CHECK ADD  CONSTRAINT [FK_SSM_ShippingMethods] FOREIGN KEY([MethodCode])
REFERENCES [dbo].[ShippingMethods] ([MethodCode])
GO
ALTER TABLE [dbo].[Shops_ShippingMethods] CHECK CONSTRAINT [FK_SSM_ShippingMethods]
GO
ALTER TABLE [dbo].[Shops_ShippingMethods]  WITH CHECK ADD  CONSTRAINT [FK_SSM_Shops] FOREIGN KEY([ShopID])
REFERENCES [dbo].[Shops] ([ShopID])
GO
ALTER TABLE [dbo].[Shops_ShippingMethods] CHECK CONSTRAINT [FK_SSM_Shops]
GO
ALTER TABLE [dbo].[UserRoles]  WITH CHECK ADD  CONSTRAINT [FK_UserRoles_Roles] FOREIGN KEY([RoleCode])
REFERENCES [dbo].[Roles] ([RoleCode])
GO
ALTER TABLE [dbo].[UserRoles] CHECK CONSTRAINT [FK_UserRoles_Roles]
GO
ALTER TABLE [dbo].[UserRoles]  WITH CHECK ADD  CONSTRAINT [FK_UserRoles_Users] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[UserRoles] CHECK CONSTRAINT [FK_UserRoles_Users]
GO
ALTER TABLE [dbo].[Users_Addresses]  WITH CHECK ADD  CONSTRAINT [FK_UsersAddr_Address] FOREIGN KEY([AddressID])
REFERENCES [dbo].[Addresses] ([AddressID])
GO
ALTER TABLE [dbo].[Users_Addresses] CHECK CONSTRAINT [FK_UsersAddr_Address]
GO
ALTER TABLE [dbo].[Users_Addresses]  WITH CHECK ADD  CONSTRAINT [FK_UsersAddr_Users] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Users_Addresses] CHECK CONSTRAINT [FK_UsersAddr_Users]
GO
ALTER TABLE [dbo].[UserVouchers]  WITH CHECK ADD  CONSTRAINT [FK_UserVouchers_User] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[UserVouchers] CHECK CONSTRAINT [FK_UserVouchers_User]
GO
ALTER TABLE [dbo].[UserVouchers]  WITH CHECK ADD  CONSTRAINT [FK_UserVouchers_Voucher] FOREIGN KEY([VoucherID])
REFERENCES [dbo].[Vouchers] ([VoucherID])
GO
ALTER TABLE [dbo].[UserVouchers] CHECK CONSTRAINT [FK_UserVouchers_Voucher]
GO
ALTER TABLE [dbo].[Vouchers]  WITH CHECK ADD  CONSTRAINT [FK_Vouchers_DiscountType] FOREIGN KEY([DiscountType])
REFERENCES [dbo].[DiscountType] ([TypeCode])
GO
ALTER TABLE [dbo].[Vouchers] CHECK CONSTRAINT [FK_Vouchers_DiscountType]
GO
ALTER TABLE [dbo].[Vouchers]  WITH CHECK ADD  CONSTRAINT [FK_Vouchers_Status] FOREIGN KEY([Status])
REFERENCES [dbo].[VoucherStatus] ([StatusCode])
GO
ALTER TABLE [dbo].[Vouchers] CHECK CONSTRAINT [FK_Vouchers_Status]
GO
USE [master]
GO
ALTER DATABASE [AuroraDemo] SET  READ_WRITE 
GO
