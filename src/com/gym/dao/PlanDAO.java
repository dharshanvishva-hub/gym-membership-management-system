package com.gym.dao;

import com.gym.model.MembershipPlan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlanDAO implements CrudRepository<MembershipPlan> {

    @Override
    public List<MembershipPlan> findAll() throws SQLException {
        List<MembershipPlan> list = new ArrayList<>();
        String sql = "SELECT * FROM membership_plans ORDER BY plan_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPlan(rs));
            }
        }
        return list;
    }

    @Override
    public Optional<MembershipPlan> findById(int id) throws SQLException {
        String sql = "SELECT * FROM membership_plans WHERE plan_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPlan(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean save(MembershipPlan plan) throws SQLException {
        String sql = "INSERT INTO membership_plans (plan_name, duration_months, fee, facilities_offered) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, plan.getPlanName());
            stmt.setInt(2, plan.getDurationMonths());
            stmt.setDouble(3, plan.getFee());
            stmt.setString(4, plan.getFacilitiesOffered());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        plan.setPlanId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(MembershipPlan plan) throws SQLException {
        String sql = "UPDATE membership_plans SET plan_name = ?, duration_months = ?, fee = ?, facilities_offered = ? WHERE plan_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plan.getPlanName());
            stmt.setInt(2, plan.getDurationMonths());
            stmt.setDouble(3, plan.getFee());
            stmt.setString(4, plan.getFacilitiesOffered());
            stmt.setInt(5, plan.getPlanId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM membership_plans WHERE plan_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private MembershipPlan mapResultSetToPlan(ResultSet rs) throws SQLException {
        return new MembershipPlan(
                rs.getInt("plan_id"),
                rs.getString("plan_name"),
                rs.getInt("duration_months"),
                rs.getDouble("fee"),
                rs.getString("facilities_offered")
        );
    }
}
