-- Trigger 

CREATE OR ALTER TRIGGER trg_DeleteProductCascade
ON Products
INSTEAD OF DELETE
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @Blocked TABLE (
        ProductID BIGINT,
        Reason NVARCHAR(255)
    );

    -- 1️⃣ Sản phẩm đang tham gia Flash Sale
    INSERT INTO @Blocked
        (ProductID, Reason)
    SELECT d.ProductID, N'Sản phẩm đang tham gia Flash Sale'
    FROM deleted d
    WHERE EXISTS (
        SELECT 1
    FROM FlashSaleItems f
    WHERE f.ProductID = d.ProductID
    );

    -- 2️⃣ Sản phẩm nằm trong đơn hàng đang xử lý
    INSERT INTO @Blocked
        (ProductID, Reason)
    SELECT DISTINCT d.ProductID, N'Sản phẩm đang nằm trong đơn hàng đang xử lý'
    FROM deleted d
        JOIN OrderItems oi ON oi.ProductID = d.ProductID
        JOIN OrderShops os ON os.OrderShopID = oi.OrderShopID
    WHERE os.[Status] IN (
        N'PENDING', N'SHIPPING', N'WAITING_SHIP',
        N'CONFIRM', N'COMPLETED', N'CANCELLED',
        N'RETURNED', N'RETURNED_REJECTED', N'RETURNED_REQUESTED'
    );

    -- 3️⃣ Kiểm tra trạng thái sản phẩm
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

    -- 4️⃣ Sản phẩm PENDING nhưng đã có SoldCount > 0
    INSERT INTO @Blocked
        (ProductID, Reason)
    SELECT d.ProductID,
        CASE p.Status
        WHEN N'PENDING' THEN N'Sản phẩm đang ở trạng thái chờ duyệt (PENDING) nhưng đã được bán'
        WHEN N'REJECTED' THEN N'Sản phẩm đã bị từ chối (REJECTED) nhưng vẫn có đơn hàng bán'
    END
    FROM deleted d
        JOIN Products p ON p.ProductID = d.ProductID
    WHERE p.Status IN (N'PENDING', N'REJECTED') AND p.SoldCount > 0;

    -- 5️⃣ Nếu có sản phẩm bị chặn xóa → báo lỗi
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

    -- 6️⃣ Xóa dữ liệu liên quan
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
    DELETE FROM Products WHERE ProductID IN (SELECT ProductID
    FROM deleted);
END;
GO


CREATE OR ALTER TRIGGER trg_OrderShopStatusNotification
ON OrderShops
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- 1️⃣ Đơn hàng mới (INSERT)
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
        LEFT JOIN Users u ON u.UserID = i.UserID
    WHERE d.OrderShopID IS NULL;

    -- 2️⃣ Giao hàng thành công
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
    WHERE d.Status <> i.Status AND i.Status = 'COMPLETED';

    -- 3️⃣ Yêu cầu trả hàng
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
    WHERE d.Status <> i.Status AND i.Status = 'RETURNED_REQUESTED';

    -- 4️⃣ Đơn hàng bị hủy
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
    WHERE d.Status <> i.Status AND i.Status = 'CANCELLED';
END;
GO




---------------------------------------------------
-- TRIGGER 2:(Products)
---------------------------------------------------

CREATE OR ALTER TRIGGER trg_ProductNotifications
ON Products
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    --------------------------------------------------------
    -- 1️⃣  THÔNG BÁO: SẢN PHẨM HẾT HÀNG
    --------------------------------------------------------
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
        INNER JOIN deleted d ON i.ProductID = d.ProductID
    WHERE d.Quantity > 0 AND i.Quantity <= 0;


    --------------------------------------------------------
    -- 2️⃣  THÔNG BÁO: SẢN PHẨM ĐƯỢC DUYỆT (PENDING → ACTIVE)
    --------------------------------------------------------
    INSERT INTO Notifications
        (RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt)
    SELECT
        'SELLER',
        i.ShopID,
        'PRODUCT_ACTIVE',
        N'Sản phẩm đã được duyệt',
        CONCAT(N'Sản phẩm "', i.Title, N'" đã được duyệt và đang bán.'),
        'PRODUCT',
        i.ProductID,
        DATEADD(HOUR, 7, SYSDATETIME())
    FROM inserted i
        INNER JOIN deleted d ON i.ProductID = d.ProductID
    WHERE i.Status = 'ACTIVE';


    --------------------------------------------------------
    -- 3️⃣  THÔNG BÁO: SẢN PHẨM BỊ TỪ CHỐI (PENDING → REJECTED)
    --------------------------------------------------------
    INSERT INTO Notifications
        (RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt)
    SELECT
        'SELLER',
        i.ShopID,
        'PRODUCT_REJECTED',
        N'Sản phẩm bị từ chối',
        CONCAT(N'Sản phẩm "', i.Title, N'" đã bị từ chối.'),
        'PRODUCT',
        i.ProductID,
        DATEADD(HOUR, 7, SYSDATETIME())
    FROM inserted i
        INNER JOIN deleted d ON i.ProductID = d.ProductID
    WHERE i.Status = 'REJECTED';
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
    WHERE i.IsShopVoucher = 1
        AND (d.Status IS NULL OR d.Status <> i.Status)
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
    WHERE i.IsShopVoucher = 1
        AND (d.Status IS NULL OR d.Status <> i.Status)
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
    WHERE i.IsShopVoucher = 1
        AND (d.Status IS NULL OR d.Status <> i.Status)
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

CREATE OR ALTER TRIGGER trg_OrderShopStatusNotification_Customer
ON OrderShops
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    ---------------------------------------------------
    -- Tạo thông báo cho KHÁCH HÀNG dựa trên trạng thái
    ---------------------------------------------------
    INSERT INTO Notifications
        (RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt)
    SELECT
        'CUSTOMER',
        i.UserID,
        CASE i.[Status]
            WHEN 'SHIPPING' THEN 'ORDER_SHIPPING'
            WHEN 'CANCELLED' THEN 'ORDER_CANCELLED'
            WHEN 'CONFIRM' THEN 'ORDER_CONFIRM'
            WHEN 'RETURNED' THEN 'ORDER_RETURNED'
            WHEN 'RETURNED_REJECTED' THEN 'ORDER_RETURNED_REJECTED'
        END AS Type,
        CASE i.[Status]
            WHEN 'SHIPPING' THEN N'Đơn hàng đang được giao'
            WHEN 'CANCELLED' THEN N'Đơn hàng đã bị hủy'
            WHEN 'CONFIRM' THEN N'Đơn hàng đã giao – chờ bạn xác nhận'
            WHEN 'RETURNED' THEN N'Yêu cầu trả hàng được chấp nhận'
            WHEN 'RETURNED_REJECTED' THEN N'Yêu cầu trả hàng bị từ chối'
        END AS Title,
        CASE i.[Status]
            WHEN 'SHIPPING' THEN CONCAT(N'Đơn hàng #', i.OrderShopID, N' của bạn đã được người bán xác nhận và đang được giao.')
            WHEN 'CANCELLED' THEN CONCAT(N'Đơn hàng #', i.OrderShopID, N' của bạn đã bị hủy.')
            WHEN 'CONFIRM' THEN CONCAT(N'Đơn hàng #', i.OrderShopID, N' của bạn đã được giao. Vui lòng xác nhận nếu bạn đã nhận hàng.')
            WHEN 'RETURNED' THEN CONCAT(N'Đơn hàng #', i.OrderShopID, N' của bạn đã được xác nhận trả hàng thành công.')
            WHEN 'RETURNED_REJECTED' THEN CONCAT(N'Yêu cầu trả hàng của đơn #', i.OrderShopID, N' đã bị từ chối.')
        END AS Message,
        'ORDER',
        i.OrderShopID,
        DATEADD(HOUR, 7, SYSDATETIME())
    FROM inserted i
        JOIN deleted d ON i.OrderShopID = d.OrderShopID
    WHERE 
        i.[Status] IN ('SHIPPING', 'CANCELLED', 'CONFIRM', 'RETURNED', 'RETURNED_REJECTED')
        AND ISNULL(d.[Status], '') <> i.[Status]
        AND NOT EXISTS (
            SELECT 1
        FROM Notifications n
        WHERE n.RecipientType = 'CUSTOMER'
            AND n.RecipientID = i.UserID
            AND n.ReferenceType = 'ORDER'
            AND n.ReferenceID = i.OrderShopID
            AND n.Type = 
                    CASE i.[Status]
                        WHEN 'SHIPPING' THEN 'ORDER_SHIPPING'
                        WHEN 'CANCELLED' THEN 'ORDER_CANCELLED'
                        WHEN 'CONFIRM' THEN 'ORDER_CONFIRM'
                        WHEN 'RETURNED' THEN 'ORDER_RETURNED'
                        WHEN 'RETURNED_REJECTED' THEN 'ORDER_RETURNED_REJECTED'
                    END
            AND n.CreatedAt >= DATEADD(HOUR, -1, SYSDATETIME())
        );
END;
GO

-----------------------------------------------------------
-- TRIGGER: Khi Flash Sale thay đổi trạng thái (chỉ xử lý khi kết thúc)
-----------------------------------------------------------
CREATE OR ALTER TRIGGER trg_UpdateProduct_OnFlashSaleEnd
ON FlashSales
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -------------------------------------------------------
    -- 🔹 Khi Flash Sale kết thúc → hoàn hàng tồn và cộng dồn SoldCount
    -------------------------------------------------------

    -- Tạo bảng tạm lưu lại các item cần xử lý
    DECLARE @ToRestore TABLE (
        ProductID BIGINT,
        FlashSaleItemID BIGINT,
        fsStock INT,
        soldCount BIGINT
    );

    INSERT INTO @ToRestore
        (ProductID, FlashSaleItemID, fsStock, soldCount)
    SELECT fsi.ProductID, fsi.FlashSaleItemID, fsi.fsStock, fsi.SoldCount
    FROM FlashSaleItems fsi
        INNER JOIN inserted i ON fsi.FlashSaleID = i.FlashSaleID
        INNER JOIN deleted d ON i.FlashSaleID = d.FlashSaleID
    WHERE 
        i.[Status] = 'ENDED'
        AND d.[Status] <> 'ENDED'
        AND fsi.fsStock > 0
        AND fsi.ApprovalStatus = 'APPROVED';

    -- ✅ Cập nhật sản phẩm: hoàn kho và cộng dồn soldCount
    UPDATE p
    SET 
        p.Quantity = p.Quantity + t.fsStock,
        p.SoldCount = p.SoldCount + t.soldCount
    FROM Products p
        INNER JOIN @ToRestore t ON p.ProductID = t.ProductID;

    -- ✅ Sau khi hoàn tất, reset lại fsStock = 0
    UPDATE fsi
    SET fsi.fsStock = 0
    FROM FlashSaleItems fsi
        INNER JOIN @ToRestore t ON fsi.FlashSaleItemID = t.FlashSaleItemID;
END;
GO

 
