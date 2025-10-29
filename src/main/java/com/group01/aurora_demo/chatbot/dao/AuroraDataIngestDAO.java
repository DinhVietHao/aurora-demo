package com.group01.aurora_demo.chatbot.dao;

import java.sql.Date;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.json.JSONArray;
import java.util.ArrayList;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import com.group01.aurora_demo.chatbot.model.Document;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class AuroraDataIngestDAO {
    public void syncAllDocuments() {
        try (Connection conn = DataSourceProvider.get().getConnection()) {
            conn.setAutoCommit(false);
            ingestProducts(conn);
            ingestVouchers(conn);
            ingestReviews(conn);
            conn.commit();
            updateNewOrUpdatedEmbeddings(conn);
            System.out.println("[SYNC] Document ingestion completed successfully.");
        } catch (Exception e) {
            System.out.println("[ERROR] chatbot/dao/AuroraDataIngestDAO in syncAllDocuments: " + e.getMessage());
        }
    }

    private void updateNewOrUpdatedEmbeddings(Connection conn) {
        String selectSql = "SELECT DocumentID, Title, Content FROM Documents WHERE Embedding IS NULL OR UpdatedAt > CreatedAt";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long docId = rs.getLong("DocumentID");
                String title = rs.getString("Title");
                String content = rs.getString("Content");
                String text = (title != null ? title : "") + "\n" + (content != null ? content : "");

                List<Double> vec = generateEmbedding(text);
                if (vec == null || vec.isEmpty())
                    continue;

                JSONArray arr = new JSONArray();
                for (double v : vec)
                    arr.put(v);

                String updateSql = "UPDATE Documents SET Embedding = ? WHERE DocumentID = ?";
                try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                    update.setNString(1, arr.toString());
                    update.setLong(2, docId);
                    update.executeUpdate();
                    System.out.println("[EMBED] Updated embedding for doc ID: " + docId);
                }
            }
            conn.commit();
        } catch (Exception e) {
            System.err.println("[ERROR] updateNewOrUpdatedEmbeddings: " + e.getMessage());
        }
    }

    private void ingestProducts(Connection conn) {
        String sql = """
                    SELECT
                        p.ProductID,
                        p.Title,
                        p.Description,
                        p.OriginalPrice,
                        p.SalePrice,
                        p.Quantity,
                        p.Status,
                        p.CreatedAt,
                        p.Weight,
                        p.PublishedDate,
                        s.Name AS ShopName,
                        s.RatingAvg AS ShopRating,
                        cat.CategoryNames AS CategoryName,
                        pub.Name AS PublisherName,
                        b.Pages,
                        b.Version,
                        b.CoverType,
                        l.LanguageName,
                        revs.AvgRating,
                        revs.ReviewCount,
                        sales.TotalSold,
                        auth.Authors
                    FROM Products p
                    LEFT JOIN Shops s ON p.ShopID = s.ShopID
                    LEFT JOIN (
                        SELECT pc.ProductID, STRING_AGG(c.Name, ', ') AS CategoryNames
                        FROM ProductCategory pc
                        JOIN Category c ON pc.CategoryID = c.CategoryID
                        GROUP BY pc.ProductID
                    ) cat ON cat.ProductID = p.ProductID
                    LEFT JOIN Publishers pub ON p.PublisherID = pub.PublisherID
                    LEFT JOIN BookDetails b ON b.ProductID = p.ProductID
                    LEFT JOIN Languages l ON b.LanguageCode = l.LanguageCode
                    LEFT JOIN (
                        SELECT oi.ProductID, SUM(oi.Quantity) AS TotalSold
                        FROM OrderItems oi
                        JOIN OrderShops os ON oi.OrderShopID = os.OrderShopID
                        WHERE os.Status IN ('COMPLETED', 'DELIVERED')
                        GROUP BY oi.ProductID
                    ) sales ON sales.ProductID = p.ProductID
                    LEFT JOIN (
                        SELECT oi.ProductID,
                               AVG(CAST(r.Rating AS DECIMAL(3,2))) AS AvgRating,
                               COUNT(*) AS ReviewCount
                        FROM Reviews r
                        JOIN OrderItems oi ON r.OrderItemID = oi.OrderItemID
                        WHERE r.Comment IS NOT NULL AND LEN(r.Comment) > 3
                        GROUP BY oi.ProductID
                    ) revs ON revs.ProductID = p.ProductID
                    LEFT JOIN (
                        SELECT ba.ProductID,
                               STRING_AGG(a.AuthorName, ', ') AS Authors
                        FROM BookAuthors ba
                        JOIN Authors a ON ba.AuthorID = a.AuthorID
                        GROUP BY ba.ProductID
                    ) auth ON auth.ProductID = p.ProductID
                    WHERE p.Status = 'ACTIVE';
                """;

        String upsertSql = """
                    MERGE Documents AS target
                    USING (VALUES (?, ?, ?, ?)) AS source (Source, SourceID, Title, Content)
                    ON target.Source = source.Source AND target.SourceID = source.SourceID
                    WHEN MATCHED THEN
                        UPDATE SET Title = source.Title, Content = source.Content, UpdatedAt = SYSUTCDATETIME()
                    WHEN NOT MATCHED THEN
                        INSERT (Source, SourceID, Title, Content, CreatedAt, UpdatedAt)
                        VALUES (source.Source, source.SourceID, source.Title, source.Content, SYSUTCDATETIME(), SYSUTCDATETIME());
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatement ins = conn.prepareStatement(upsertSql)) {

            ResultSet rs = ps.executeQuery();
            int batchCount = 0;

            while (rs.next()) {
                long id = rs.getLong("ProductID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                double originalPrice = rs.getDouble("OriginalPrice");
                double salePrice = rs.getDouble("SalePrice");
                int quantity = rs.getInt("Quantity");
                double weight = rs.getDouble("Weight");
                Date published = rs.getDate("PublishedDate");

                String shopName = rs.getString("ShopName");
                double shopRating = rs.getDouble("ShopRating");
                String categoryName = rs.getString("CategoryName");
                String publisherName = rs.getString("PublisherName");
                String authors = rs.getString("Authors");
                String version = rs.getString("Version");
                String coverType = rs.getString("CoverType");
                String language = rs.getString("LanguageName");
                int pages = rs.getInt("Pages");
                Double avgRating = rs.getObject("AvgRating") != null ? rs.getDouble("AvgRating") : null;
                Long reviewCount = rs.getObject("ReviewCount") != null ? rs.getLong("ReviewCount") : null;
                Long totalSold = rs.getObject("TotalSold") != null ? rs.getLong("TotalSold") : null;

                StringBuilder content = new StringBuilder();
                content.append("Tên sản phẩm: ").append(title).append("\n");
                if (categoryName != null)
                    content.append("Thể loại: ").append(categoryName).append("\n");
                if (authors != null)
                    content.append("Tác giả: ").append(authors).append("\n");
                if (publisherName != null)
                    content.append("Nhà xuất bản: ").append(publisherName).append("\n");
                if (version != null)
                    content.append("Phiên bản: ").append(version).append("\n");
                if (coverType != null)
                    content.append("Loại bìa: ").append(coverType).append("\n");
                if (pages > 0)
                    content.append("Số trang: ").append(pages).append("\n");
                if (language != null)
                    content.append("Ngôn ngữ: ").append(language).append("\n");

                content.append("Giá gốc: ").append(String.format("%,.0f VNĐ\n", originalPrice));
                content.append("Giá bán: ").append(String.format("%,.0f VNĐ\n", salePrice));
                content.append("Còn lại: ").append(quantity).append(" sản phẩm\n");
                content.append("Khối lượng: ").append(String.format("%.2f gram", weight)).append("\n");

                if (totalSold != null && totalSold > 0)
                    content.append("Đã bán: ").append(String.format("%,d sản phẩm", totalSold)).append("\n");
                if (avgRating != null && reviewCount != null && reviewCount > 0)
                    content.append("Đánh giá trung bình: ")
                            .append(String.format("%.1f★ (%d lượt)", avgRating, reviewCount)).append("\n");
                if (shopName != null)
                    content.append("Shop: ").append(shopName).append(" (").append(shopRating).append("★)\n");
                if (published != null)
                    content.append("Ngày phát hành: ").append(published.toString()).append("\n");
                if (description != null && !description.isBlank())
                    content.append("Mô tả: ").append(description).append("\n");

                ins.setString(1, "Product");
                ins.setLong(2, id);
                ins.setString(3, "Sách: " + title);
                ins.setString(4, content.toString());
                ins.addBatch();
                batchCount++;

                if (batchCount % 200 == 0) {
                    ins.executeBatch();
                    batchCount = 0;
                }
            }

            if (batchCount > 0)
                ins.executeBatch();
            System.out.println("[SYNC] Products upsert completed.");
        } catch (SQLException e) {
            System.err.println("[ERROR] ingestProducts: " + e.getMessage());
        }
    }

    private void ingestVouchers(Connection conn) {
        String sql = """
                    SELECT
                        v.VoucherID, v.Code, v.DiscountType, v.Value, v.MaxAmount, v.MinOrderAmount,
                        v.StartAt, v.EndAt, v.[Status], v.[Description], v.IsShopVoucher, v.ShopID,
                        s.Name AS ShopName, s.Description AS ShopDescription, s.RatingAvg,
                        addr.Description AS PickupAddressDesc, addr.City, addr.District, addr.Ward
                    FROM Vouchers v
                    LEFT JOIN Shops s ON v.ShopID = s.ShopID
                    LEFT JOIN Addresses addr ON s.PickupAddressID = addr.AddressID
                    WHERE v.[Status] = 'ACTIVE'
                """;

        String upsertSql = """
                    MERGE Documents AS target
                    USING (VALUES (?, ?, ?, ?)) AS source (Source, SourceID, Title, Content)
                    ON target.Source = source.Source AND target.SourceID = source.SourceID
                    WHEN MATCHED THEN
                        UPDATE SET Title = source.Title, Content = source.Content, UpdatedAt = SYSUTCDATETIME()
                    WHEN NOT MATCHED THEN
                        INSERT (Source, SourceID, Title, Content, CreatedAt, UpdatedAt)
                        VALUES (source.Source, source.SourceID, source.Title, source.Content, SYSUTCDATETIME(), SYSUTCDATETIME());
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatement ins = conn.prepareStatement(upsertSql)) {

            ResultSet rs = ps.executeQuery();
            int batch = 0;

            while (rs.next()) {
                long id = rs.getLong("VoucherID");
                String code = rs.getString("Code");
                String discountType = rs.getString("DiscountType");
                Double value = rs.getDouble("Value");
                Double maxAmount = rs.getDouble("MaxAmount");
                Double minOrder = rs.getDouble("MinOrderAmount");
                Timestamp startAt = rs.getTimestamp("StartAt");
                Timestamp endAt = rs.getTimestamp("EndAt");
                String desc = rs.getString("Description");
                boolean isShopVoucher = rs.getBoolean("IsShopVoucher");

                Long shopId = rs.getObject("ShopID") != null ? rs.getLong("ShopID") : null;
                String shopName = rs.getString("ShopName");
                String shopDesc = rs.getString("ShopDescription");
                Double shopRating = rs.getDouble("RatingAvg");

                String pickupAddrDesc = rs.getString("PickupAddressDesc");
                String pickupCity = rs.getString("City");
                String pickupDistrict = rs.getString("District");
                String pickupWard = rs.getString("Ward");

                StringBuilder content = new StringBuilder();
                content.append("Mã: ").append(code).append("\n");
                content.append("Loại giảm giá: ").append(discountType).append("\n");
                if (value != null)
                    content.append("Giá trị: ").append(formatValueByType(discountType, value)).append("\n");
                if (maxAmount != null)
                    content.append("Giảm tối đa: ").append(String.format("%,.0f VNĐ\n", maxAmount));
                if (minOrder != null)
                    content.append("Đơn tối thiểu: ").append(String.format("%,.0f VNĐ\n", minOrder));
                if (startAt != null)
                    content.append("Bắt đầu: ").append(startAt.toLocalDateTime()).append("\n");
                if (endAt != null)
                    content.append("Hết hạn: ").append(endAt.toLocalDateTime()).append("\n");
                if (!desc.isEmpty())
                    content.append("Mô tả: ").append(desc).append("\n");

                if (isShopVoucher && shopId != null) {
                    content.append("Áp dụng tại shop: ").append(shopName).append(" (ShopID: ").append(shopId)
                            .append(")\n");
                    if (!shopDesc.isEmpty())
                        content.append("Giới thiệu shop: ").append(shopDesc).append("\n");
                    if (shopRating != null)
                        content.append("Đánh giá trung bình: ").append(shopRating).append("\n");
                    if (!pickupAddrDesc.isEmpty() || !pickupWard.isEmpty() || !pickupDistrict.isEmpty()
                            || !pickupCity.isEmpty()) {
                        content.append("Địa chỉ shop: ");
                        if (!pickupAddrDesc.isEmpty())
                            content.append(pickupAddrDesc).append(", ");
                        if (!pickupWard.isEmpty())
                            content.append(pickupWard).append(", ");
                        if (!pickupDistrict.isEmpty())
                            content.append(pickupDistrict).append(", ");
                        if (!pickupCity.isEmpty())
                            content.append(pickupCity).append(".");
                        content.append("\n");
                    }
                } else {
                    content.append("Áp dụng toàn hệ thống.\n");
                }

                ins.setString(1, "Voucher");
                ins.setLong(2, id);
                ins.setString(3, "Voucher: " + code);
                ins.setString(4, content.toString());
                ins.addBatch();
                batch++;

                if (batch % 200 == 0) {
                    ins.executeBatch();
                    batch = 0;
                }
            }
            if (batch > 0)
                ins.executeBatch();
            System.out.println("[SYNC] Vouchers upsert completed.");
        } catch (SQLException e) {
            System.err.println("[ERROR] ingestVouchers: " + e.getMessage());
        }
    }

    private void ingestReviews(Connection conn) {
        String sql = """
                    SELECT r.ReviewID, r.OrderItemID, r.UserID, r.Rating, r.Comment, r.CreatedAt,
                           oi.ProductID, p.Title AS ProductTitle
                    FROM Reviews r
                    LEFT JOIN OrderItems oi ON r.OrderItemID = oi.OrderItemID
                    LEFT JOIN Products p ON oi.ProductID = p.ProductID
                    WHERE r.Rating IS NOT NULL
                """;

        String upsertSql = """
                    MERGE Documents AS target
                    USING (VALUES (?, ?, ?, ?, ?)) AS source (Source, SourceID, Title, Content, CreatedAt)
                    ON target.Source = source.Source AND target.SourceID = source.SourceID
                    WHEN MATCHED THEN
                        UPDATE SET Title = source.Title, Content = source.Content, UpdatedAt = SYSUTCDATETIME()
                    WHEN NOT MATCHED THEN
                        INSERT (Source, SourceID, Title, Content, CreatedAt, UpdatedAt)
                        VALUES (source.Source, source.SourceID, source.Title, source.Content, source.CreatedAt, SYSUTCDATETIME());
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatement ins = conn.prepareStatement(upsertSql)) {

            ResultSet rs = ps.executeQuery();
            int batch = 0;

            while (rs.next()) {
                long reviewId = rs.getLong("ReviewID");
                Integer rating = rs.getInt("Rating");
                String comment = rs.getString("Comment");
                String productTitle = rs.getString("ProductTitle");
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                if (createdAt == null)
                    createdAt = new Timestamp(System.currentTimeMillis());

                String title = String.format("Đánh giá %d★ cho '%s'",
                        rating == null ? 0 : rating,
                        productTitle.isEmpty() ? "(không rõ sản phẩm)" : productTitle);

                StringBuilder content = new StringBuilder();
                content.append("Sản phẩm: ").append(productTitle).append("\n");
                content.append("Điểm đánh giá: ").append(rating == null ? "chưa có" : rating + "★").append("\n");
                content.append("Nhận xét: ").append(comment).append("\n");

                ins.setString(1, "Review");
                ins.setLong(2, reviewId);
                ins.setString(3, title);
                ins.setString(4, content.toString());
                ins.setTimestamp(5, createdAt);
                ins.addBatch();
                batch++;

                if (batch % 200 == 0) {
                    ins.executeBatch();
                    batch = 0;
                }
            }

            if (batch > 0)
                ins.executeBatch();
            System.out.println("[SYNC] Reviews upsert completed.");

        } catch (SQLException e) {
            System.err.println("[ERROR] ingestReviews: " + e.getMessage());
        }
    }

    private static String formatValueByType(String discountType, Double value) {
        if (value == null)
            return "";
        if ("PERCENT".equalsIgnoreCase(discountType) || discountType.toLowerCase().contains("percent"))
            return String.format("%.0f%%", value);
        return String.format("%,.0f₫\n", value);
    }

    public void updateAllDocumentEmbeddings(Connection conn) {
        String selectSql = "SELECT DocumentID, Title, Content FROM Documents WHERE Embedding IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long docId = rs.getLong("DocumentID");
                String title = rs.getString("Title");
                String content = rs.getString("Content");
                String text = (title != null ? title : "") + "\n" + (content != null ? content : "");

                List<Double> vec = null;
                try {
                    vec = generateEmbedding(text);
                } catch (Exception e) {
                    System.err.println("[ERROR] generateEmbedding failed for doc ID " + docId + ": " + e.getMessage());
                    continue;
                }

                if (vec == null || vec.isEmpty()) {
                    System.err.println("[WARN] Empty embedding for doc ID " + docId + ", skipping update.");
                    continue;
                }

                JSONArray arr = new JSONArray();
                for (double v : vec)
                    arr.put(v);

                String updateSql = "UPDATE Documents SET Embedding = ? WHERE DocumentID = ?";
                try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                    update.setNString(1, arr.toString());
                    update.setLong(2, docId);
                    int rows = update.executeUpdate();
                    if (rows > 0) {
                        System.out.println("[EMBED] Updated embedding for doc ID: " + docId);
                    } else {
                        System.err.println("[WARN] No rows updated for doc ID: " + docId);
                    }
                } catch (Exception e) {
                    System.err
                            .println("[ERROR] Failed to update embedding for doc ID " + docId + ": " + e.getMessage());
                }
            }
            conn.commit();
        } catch (Exception e) {
            System.err.println("[ERROR] updateAllDocumentEmbeddings: " + e.getMessage());
        }
    }

    public List<Double> generateEmbedding(String text) {
        try {
            String jsonInput = new JSONObject().put("texts", new JSONArray().put(text)).toString();

            java.net.URI uri = new java.net.URI("http://localhost:5000/embed");
            java.net.URL url = uri.toURL();
            java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.getOutputStream().write(jsonInput.getBytes());

            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
                response.append(line);
            br.close();

            JSONArray arr = new JSONObject(response.toString()).getJSONArray("embeddings").getJSONArray(0);
            List<Double> embedding = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                embedding.add(arr.getDouble(i));
            }
            return embedding;
        } catch (Exception e) {
            System.out.println("[ERROR] generateEmbedding: " + e.getMessage());
        }
        return null;
    }

    private double cosineSimilarity(List<Double> v1, List<Double> v2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            norm1 += Math.pow(v1.get(i), 2);
            norm2 += Math.pow(v2.get(i), 2);
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    public List<Document> searchRelevantDocuments(String query, int topN) {
        List<Double> queryEmbedding = generateEmbedding(query);
        String sql = "SELECT DocumentID, Title, Content, Embedding FROM Documents WHERE Embedding IS NOT NULL";
        try (Connection conn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            List<Document> results = new ArrayList<>();
            while (rs.next()) {
                String embeddingStr = rs.getString("Embedding");
                if (embeddingStr == null || embeddingStr.isEmpty())
                    continue;

                JSONArray arr = new JSONArray(embeddingStr);
                List<Double> docVector = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++)
                    docVector.add(arr.getDouble(i));

                double similarity = cosineSimilarity(queryEmbedding, docVector);

                Document doc = new Document();
                doc.setDocumentID(rs.getLong("DocumentID"));
                doc.setTitle(rs.getString("Title"));
                doc.setContent(rs.getString("Content"));
                doc.setEmbedding(embeddingStr);
                doc.setSimilarity(similarity);
                results.add(doc);
            }
            results.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));
            return results.subList(0, Math.min(topN, results.size()));
        } catch (Exception e) {
            System.out.println("[ERROR] searchRelevantDocuments: " + e.getMessage());
        }
        return null;
    }

}