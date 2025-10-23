package com.group01.aurora_demo.catalog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.group01.aurora_demo.catalog.model.Notification;
import com.group01.aurora_demo.common.config.DataSourceProvider;

public class NotificationDAO {

    public List<Notification> getNotificationsForShop(long shopID) throws SQLException {
        List<Notification> list = new ArrayList<>();

        String sql = """
                SELECT TOP (100)
                NotificationID, RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt
                FROM Notifications
                WHERE RecipientType = 'SELLER' AND RecipientID = ?
                ORDER BY CreatedAt DESC
                """;

        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, shopID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setNotificationID(rs.getLong("NotificationID"));
                    n.setRecipientType(rs.getString("RecipientType"));
                    n.setRecipientID(rs.getLong("RecipientID"));
                    n.setType(rs.getString("Type"));
                    n.setTitle(rs.getString("Title"));
                    n.setMessage(rs.getString("Message"));
                    n.setReferenceType(rs.getString("ReferenceType"));
                    n.setReferenceID(rs.getLong("ReferenceID"));
                    n.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    String link = "#";
                    switch (n.getType()) {
                        case "ORDER_NEW":
                            link = "/shop/orders?action=detail&orderShopId=" + n.getReferenceID();
                            break;
                        case "ORDER_DELIVERED":
                            link = "/shop/orders?action=detail&orderShopId=" + n.getReferenceID();
                            break;
                        case "OUT_OF_STOCK":
                            link = "/shop/product?action=detail&productId=" + n.getReferenceID();
                            break;
                        case "RETURN_REQUESTED":
                            link = "/shop/orders?action=detail&orderShopId=" + n.getReferenceID();
                            break;
                        case "ORDER_CANCELLED":
                            link = "/shop/orders?action=detail&orderShopId=" + n.getReferenceID();
                            break;
                        case "VOUCHER_ACTIVE":
                            link = "/shop/voucher?action=detail&voucherID=" + n.getReferenceID();
                            break;
                        case "VOUCHER_OUT_OF_STOCK":
                            link = "/shop/voucher?action=detail&voucherID=" + n.getReferenceID();
                            break;
                        case "VOUCHER_EXPIRED":
                            link = "/shop/voucher?action=detail&voucherID=" + n.getReferenceID();
                            break;
                    }
                    n.setLink(link);
                    list.add(n);
                }
            }
        }

        return list;
    }
}
