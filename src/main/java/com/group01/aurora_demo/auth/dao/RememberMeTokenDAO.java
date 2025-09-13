package com.group01.aurora_demo.auth.dao;

import com.group01.aurora_demo.common.config.DataSourceProvider;
import javax.sql.DataSource;
import java.time.Instant;
import java.sql.*;

public class RememberMeTokenDAO {
    private final DataSource ds = DataSourceProvider.get();

    public void insert(long userId, String selector, byte[] validatorHash, Instant expiresAt) {
        String sql = "INSERT INTO dbo.RememberMeTokens(UserID, Selector, ValidatorHash, ExpiresAt) VALUES (?,?,?,?)";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, selector);
            ps.setBytes(3, validatorHash);
            ps.setTimestamp(4, Timestamp.from(expiresAt));
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public TokenRow findBySelector(String selector) {
        String sql = "SELECT TOP 1 TokenID, UserID, Selector, ValidatorHash, ExpiresAt FROM dbo.RememberMeTokens WHERE Selector = ?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, selector);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return null;
                TokenRow row = new TokenRow();
                row.tokenId = rs.getLong("TokenID");
                row.userId = rs.getLong("UserID");
                row.selector = rs.getString("Selector");
                row.validatorHash = rs.getBytes("ValidatorHash");
                row.expiresAt = rs.getTimestamp("ExpiresAt").toInstant();
                return row;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public void deleteByTokenId(long tokenId) {
        try (Connection c = ds.getConnection();
                PreparedStatement ps = c.prepareStatement("DELETE FROM dbo.RememberMeTokens WHERE TokenID=?")) {
            ps.setLong(1, tokenId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void deleteAllForUser(long userId) {
        try (Connection c = ds.getConnection();
                PreparedStatement ps = c.prepareStatement("DELETE FROM dbo.RememberMeTokens WHERE UserID=?")) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static class TokenRow {
        public long tokenId;
        public long userId;
        public String selector;
        public byte[] validatorHash;
        public Instant expiresAt;
    }
}