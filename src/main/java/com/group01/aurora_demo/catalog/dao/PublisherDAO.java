package com.group01.aurora_demo.catalog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.group01.aurora_demo.common.config.DataSourceProvider;

public class PublisherDAO {
    public Long findPublisherIdByName(String name) throws SQLException {
        String sql = "SELECT PublisherID FROM Publishers WHERE Name = ?";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong("PublisherID");
            }
        }
        return null;
    }

    public Long insertPublisher(String name) throws SQLException {
        String sql = "INSERT INTO Publishers (Name) VALUES (?)";
        try (Connection cn = DataSourceProvider.get().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return null;
    }

    public Long findOrCreatePublisher(String name) throws SQLException {
        Long id = findPublisherIdByName(name);
        if (id != null) {
            return id;
        }
        return insertPublisher(name);
    }
}
