package com.gym.dao;

import com.gym.model.Subscription;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubscriptionDAO implements CrudRepository<Subscription> {

    @Override
    public List<Subscription> findAll() throws SQLException {
        List<Subscription> list = new ArrayList<>();
        String sql = "SELECT s.*, m.name AS member_name, p.plan_name " +
                     "FROM member_subscriptions s " +
                     "JOIN members m ON s.member_id = m.member_id " +
                     "JOIN membership_plans p ON s.plan_id = p.plan_id " +
                     "ORDER BY s.subscription_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSubscription(rs));
            }
        }
        return list;
    }

    @Override
    public Optional<Subscription> findById(int id) throws SQLException {
        String sql = "SELECT s.*, m.name AS member_name, p.plan_name " +
                     "FROM member_subscriptions s " +
                     "JOIN members m ON s.member_id = m.member_id " +
                     "JOIN membership_plans p ON s.plan_id = p.plan_id " +
                     "WHERE s.subscription_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSubscription(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Subscription> findByMemberId(int memberId) throws SQLException {
        List<Subscription> list = new ArrayList<>();
        String sql = "SELECT s.*, m.name AS member_name, p.plan_name " +
                     "FROM member_subscriptions s " +
                     "JOIN members m ON s.member_id = m.member_id " +
                     "JOIN membership_plans p ON s.plan_id = p.plan_id " +
                     "WHERE s.member_id = ? ORDER BY s.expiry_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSubscription(rs));
                }
            }
        }
        return list;
    }

    public List<Subscription> findExpiringSoon(int daysThreshold) throws SQLException {
        List<Subscription> list = new ArrayList<>();
        String sql = "SELECT s.*, m.name AS member_name, p.plan_name " +
                     "FROM member_subscriptions s " +
                     "JOIN members m ON s.member_id = m.member_id " +
                     "JOIN membership_plans p ON s.plan_id = p.plan_id " +
                     "WHERE date(s.expiry_date) BETWEEN date('now') AND date('now', '+' || ? || ' days') " +
                     "ORDER BY s.expiry_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, daysThreshold);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSubscription(rs));
                }
            }
        }
        return list;
    }

    @Override
    public boolean save(Subscription s) throws SQLException {
        String sql = "INSERT INTO member_subscriptions (member_id, plan_id, start_date, expiry_date, amount_paid, payment_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, s.getMemberId());
            stmt.setInt(2, s.getPlanId());
            stmt.setString(3, s.getStartDate().toString());
            stmt.setString(4, s.getExpiryDate().toString());
            stmt.setDouble(5, s.getAmountPaid());
            stmt.setString(6, s.getPaymentStatus() != null ? s.getPaymentStatus() : "Paid");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        s.setSubscriptionId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Subscription s) throws SQLException {
        String sql = "UPDATE member_subscriptions SET plan_id = ?, start_date = ?, expiry_date = ?, amount_paid = ?, payment_status = ? " +
                     "WHERE subscription_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, s.getPlanId());
            stmt.setString(2, s.getStartDate().toString());
            stmt.setString(3, s.getExpiryDate().toString());
            stmt.setDouble(4, s.getAmountPaid());
            stmt.setString(5, s.getPaymentStatus());
            stmt.setInt(6, s.getSubscriptionId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM member_subscriptions WHERE subscription_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Subscription mapResultSetToSubscription(ResultSet rs) throws SQLException {
        Subscription s = new Subscription(
                rs.getInt("subscription_id"),
                rs.getInt("member_id"),
                rs.getInt("plan_id"),
                LocalDate.parse(rs.getString("start_date")),
                LocalDate.parse(rs.getString("expiry_date")),
                rs.getDouble("amount_paid"),
                rs.getString("payment_status")
        );
        s.setMemberName(rs.getString("member_name"));
        s.setPlanName(rs.getString("plan_name"));
        return s;
    }
}
