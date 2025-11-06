package com.group01.aurora_demo.catalog.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import com.group01.aurora_demo.catalog.model.Notification;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public class NotificationDAO {

    public List<Notification> getNotificationsForShop(long shopID) {
        List<Notification> list = new ArrayList<>();
        String sql = """
                SELECT TOP (100)
                NotificationID, RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt
                FROM Notifications
                WHERE RecipientType = 'SELLER' AND RecipientID = ?
                ORDER BY CreatedAt DESC
                """;
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, shopID);
            ResultSet rs = ps.executeQuery();
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
                    case "PRODUCT_ACTIVE":
                        link = "/shop/product?action=detail&productId=" + n.getReferenceID();
                        break;
                    case "PRODUCT_REJECTED":
                        link = "/shop/product?action=detail&productId=" + n.getReferenceID();
                        break;
                }
                n.setLink(link);
                list.add(n);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] catalog/dao/NotificationDAO - getNotificationsForShop: " + e.getMessage());
        }
        return list;
    }

    public List<Notification> getNotificationsForCustomer(long userID) {
        List<Notification> list = new ArrayList<>();
        String sql = """
                SELECT TOP (50)
                NotificationID, RecipientType, RecipientID, Type, Title, Message, ReferenceType, ReferenceID, CreatedAt
                FROM Notifications
                WHERE RecipientType = 'CUSTOMER' AND RecipientID = ?
                ORDER BY CreatedAt DESC
                """;
        try (Connection cn = DataSourceProvider.get().getConnection()) {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setLong(1, userID);
            ResultSet rs = ps.executeQuery();
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

                // Gán link tương ứng với từng loại thông báo
                String link = "#";
                switch (n.getType()) {
                    case "ORDER_SHIPPING":
                    case "ORDER_CANCELLED":
                    case "ORDER_CONFIRM":
                    case "ORDER_RETURNED":
                    case "ORDER_RETURNED_REJECTED":
                        link = "/order/shop?orderId=" + n.getReferenceID();
                        break;
                    default:
                        link = "#";
                        break;
                }
                n.setLink(link);
                list.add(n);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] catalog/dao/NotificationDAO - getNotificationsForCustomer: " + e.getMessage());
        }
        return list;
    }

}
