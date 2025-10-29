package com.group01.aurora_demo.chatbot.dao;

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
            conn.commit();
            try (Connection embedConn = DataSourceProvider.get().getConnection()) {
                updateNewOrUpdatedEmbeddings(embedConn);
            }
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
                        p.SalePrice,
                        p.Quantity,
                        s.Name AS ShopName,
                        cat.CategoryNames AS CategoryName,
                        revs.AvgRating,
                        revs.ReviewCount,
                        sales.TotalSold
                    FROM Products p
                    LEFT JOIN Shops s ON p.ShopID = s.ShopID
                    LEFT JOIN (
                        SELECT pc.ProductID, STRING_AGG(c.Name, ', ') AS CategoryNames
                        FROM ProductCategory pc
                        JOIN Category c ON pc.CategoryID = c.CategoryID
                        GROUP BY pc.ProductID
                    ) cat ON cat.ProductID = p.ProductID
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
                    WHERE p.Status = 'ACTIVE';
                """;

        String upsertSql = """
                    EXEC sp_executesql N'
                        UPDATE Documents
                        SET Title = @title, Content = @content, UpdatedAt = SYSUTCDATETIME()
                        WHERE Source = @source AND SourceID = @sourceId;

                        IF @@ROWCOUNT = 0
                        BEGIN
                            INSERT INTO Documents (Source, SourceID, Title, Content, CreatedAt, UpdatedAt)
                            VALUES (@source, @sourceId, @title, @content, SYSUTCDATETIME(), SYSUTCDATETIME());
                        END
                    ',
                    N'@source NVARCHAR(100), @sourceId BIGINT, @title NVARCHAR(255), @content NVARCHAR(MAX)',
                    @source=?, @sourceId=?, @title=?, @content=?;
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatement ins = conn.prepareStatement(upsertSql)) {

            conn.setAutoCommit(false);
            ResultSet rs = ps.executeQuery();
            int batchCount = 0;

            while (rs.next()) {
                long id = rs.getLong("ProductID");
                String title = rs.getString("Title");
                double salePrice = rs.getDouble("SalePrice");
                int quantity = rs.getInt("Quantity");

                String shopName = rs.getString("ShopName");
                String categoryName = rs.getString("CategoryName");
                Double avgRating = rs.getObject("AvgRating") != null ? rs.getDouble("AvgRating") : null;
                Long reviewCount = rs.getObject("ReviewCount") != null ? rs.getLong("ReviewCount") : null;
                Long totalSold = rs.getObject("TotalSold") != null ? rs.getLong("TotalSold") : null;

                StringBuilder content = new StringBuilder();
                content.append("Tên sản phẩm: ").append(title).append(". ");
                if (categoryName != null)
                    content.append("Thể loại: ").append(categoryName).append(". ");
                content.append("Giá bán: ").append(String.format("%,.0f VNĐ. ", salePrice));
                content.append("Còn lại: ").append(quantity).append(" sản phẩm. ");
                if (totalSold != null && totalSold > 0)
                    content.append("Đã bán: ").append(String.format("%,d sản phẩm. ", totalSold));
                if (avgRating != null && reviewCount != null && reviewCount > 0)
                    content.append("Đánh giá trung bình: ")
                            .append(String.format("%.1f★ (%d lượt). ", avgRating, reviewCount));
                if (shopName != null)
                    content.append("Shop: ").append(shopName).append(".");

                ins.setString(1, "Product");
                ins.setLong(2, id);
                ins.setString(3, "Sách: " + title);
                ins.setString(4, content.toString());
                ins.addBatch();
                batchCount++;
                if (batchCount % 200 == 0) {
                    ins.executeBatch();
                    conn.commit();
                    batchCount = 0;
                }
            }

            if (batchCount > 0) {
                ins.executeBatch();
                conn.commit();
            }
            System.out.println("[INFO] ingestProducts completed successfully.");
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
                System.out.println(ignored.getMessage());
            }
            System.err.println("[ERROR] ingestProducts: " + e.getMessage());
        }
    }

    private void ingestVouchers(Connection conn) {
        String sql = """
                    SELECT
                        v.VoucherID, v.Code, v.DiscountType, v.Value, v.MaxAmount, v.MinOrderAmount,
                        v.StartAt, v.EndAt, v.IsShopVoucher, s.Name AS ShopName
                    FROM Vouchers v
                    LEFT JOIN Shops s ON v.ShopID = s.ShopID
                    WHERE v.[Status] = 'ACTIVE';
                """;

        String upsertSql = """
                    EXEC sp_executesql N'
                        UPDATE Documents
                        SET Title = @title, Content = @content, UpdatedAt = SYSUTCDATETIME()
                        WHERE Source = @source AND SourceID = @sourceId;

                        IF @@ROWCOUNT = 0
                        BEGIN
                            INSERT INTO Documents (Source, SourceID, Title, Content, CreatedAt, UpdatedAt)
                            VALUES (@source, @sourceId, @title, @content, SYSUTCDATETIME(), SYSUTCDATETIME());
                        END
                    ',
                    N'@source NVARCHAR(100), @sourceId BIGINT, @title NVARCHAR(255), @content NVARCHAR(MAX)',
                    @source=?, @sourceId=?, @title=?, @content=?;
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatement ins = conn.prepareStatement(upsertSql)) {

            conn.setAutoCommit(false);
            ResultSet rs = ps.executeQuery();
            int batch = 0;

            while (rs.next()) {
                long id = rs.getLong("VoucherID");
                String code = rs.getString("Code");
                String discountType = rs.getString("DiscountType");
                Double value = rs.getObject("Value") != null ? rs.getDouble("Value") : null;
                Double maxAmount = rs.getObject("MaxAmount") != null ? rs.getDouble("MaxAmount") : null;
                Double minOrder = rs.getObject("MinOrderAmount") != null ? rs.getDouble("MinOrderAmount") : null;
                Timestamp startAt = rs.getTimestamp("StartAt");
                Timestamp endAt = rs.getTimestamp("EndAt");
                boolean isShopVoucher = rs.getBoolean("IsShopVoucher");
                String shopName = rs.getString("ShopName");

                StringBuilder content = new StringBuilder();
                content.append("Mã: ").append(code).append(". ");
                content.append("Loại giảm giá: ").append(discountType).append(". ");

                if (value != null)
                    content.append("Giá trị: ").append(formatValueByType(discountType, value)).append(". ");
                if (maxAmount != null)
                    content.append("Giảm tối đa: ").append(String.format("%,.0f VNĐ. ", maxAmount));
                if (minOrder != null)
                    content.append("Đơn tối thiểu: ").append(String.format("%,.0f VNĐ. ", minOrder));
                if (startAt != null)
                    content.append("Bắt đầu: ").append(startAt.toLocalDateTime().toLocalDate()).append(". ");
                if (endAt != null)
                    content.append("Hết hạn: ").append(endAt.toLocalDateTime().toLocalDate()).append(". ");

                if (isShopVoucher) {
                    content.append("Áp dụng tại shop: ").append(shopName != null ? shopName : "Không rõ").append(". ");
                } else {
                    content.append("Áp dụng toàn hệ thống. ");
                }

                ins.setString(1, "Voucher");
                ins.setLong(2, id);
                ins.setString(3, "Voucher: " + code);
                ins.setString(4, content.toString());
                ins.addBatch();
                batch++;
                if (batch % 200 == 0) {
                    ins.executeBatch();
                    conn.commit();
                    batch = 0;
                }
            }

            if (batch > 0) {
                ins.executeBatch();
                conn.commit();
            }
            System.out.println("[SYNC] Vouchers upsert completed successfully.");
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
                System.out.println(ignored.getMessage());
            }
            System.err.println("[ERROR] ingestVouchers: " + e.getMessage());
        }
    }

    private static String formatValueByType(String discountType, Double value) {
        if (value == null)
            return "";
        if ("PERCENT".equalsIgnoreCase(discountType) || discountType.toLowerCase().contains("percent"))
            return String.format("%.0f%%. ", value);
        return String.format("%,.0f₫. ", value);
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
        String sql = "SELECT TOP (1000) DocumentID, Title, Content, Embedding FROM Documents WHERE Embedding IS NOT NULL";
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