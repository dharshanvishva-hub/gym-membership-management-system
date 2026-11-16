package com.gym.dao;

import com.gym.model.TrainerAssignment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainerAssignmentDAO implements CrudRepository<TrainerAssignment> {

    @Override
    public List<TrainerAssignment> findAll() throws SQLException {
        List<TrainerAssignment> list = new ArrayList<>();
        String sql = "SELECT ta.*, m.name AS member_name, t.name AS trainer_name, t.specialization " +
                     "FROM trainer_assignments ta " +
                     "JOIN members m ON ta.member_id = m.member_id " +
                     "JOIN trainers t ON ta.trainer_id = t.trainer_id " +
                     "ORDER BY ta.assignment_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToAssignment(rs));
            }
        }
        return list;
    }

    @Override
    public Optional<TrainerAssignment> findById(int id) throws SQLException {
        String sql = "SELECT ta.*, m.name AS member_name, t.name AS trainer_name, t.specialization " +
                     "FROM trainer_assignments ta " +
                     "JOIN members m ON ta.member_id = m.member_id " +
                     "JOIN trainers t ON ta.trainer_id = t.trainer_id " +
                     "WHERE ta.assignment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAssignment(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<TrainerAssignment> findByTrainerId(int trainerId) throws SQLException {
        List<TrainerAssignment> list = new ArrayList<>();
        String sql = "SELECT ta.*, m.name AS member_name, t.name AS trainer_name, t.specialization " +
                     "FROM trainer_assignments ta " +
                     "JOIN members m ON ta.member_id = m.member_id " +
                     "JOIN trainers t ON ta.trainer_id = t.trainer_id " +
                     "WHERE ta.trainer_id = ? AND ta.status = 'Active'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAssignment(rs));
                }
            }
        }
        return list;
    }

    @Override
    public boolean save(TrainerAssignment ta) throws SQLException {
        String sql = "INSERT INTO trainer_assignments (member_id, trainer_id, assigned_date, fitness_goal, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ta.getMemberId());
            stmt.setInt(2, ta.getTrainerId());
            stmt.setString(3, ta.getAssignedDate() != null ? ta.getAssignedDate().toString() : LocalDate.now().toString());
            stmt.setString(4, ta.getFitnessGoal());
            stmt.setString(5, ta.getStatus() != null ? ta.getStatus() : "Active");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ta.setAssignmentId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(TrainerAssignment ta) throws SQLException {
        String sql = "UPDATE trainer_assignments SET trainer_id = ?, fitness_goal = ?, status = ? WHERE assignment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ta.getTrainerId());
            stmt.setString(2, ta.getFitnessGoal());
            stmt.setString(3, ta.getStatus());
            stmt.setInt(4, ta.getAssignmentId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM trainer_assignments WHERE assignment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private TrainerAssignment mapResultSetToAssignment(ResultSet rs) throws SQLException {
        TrainerAssignment ta = new TrainerAssignment(
                rs.getInt("assignment_id"),
                rs.getInt("member_id"),
                rs.getInt("trainer_id"),
                LocalDate.parse(rs.getString("assigned_date")),
                rs.getString("fitness_goal"),
                rs.getString("status")
        );
        ta.setMemberName(rs.getString("member_name"));
        ta.setTrainerName(rs.getString("trainer_name"));
        ta.setSpecialization(rs.getString("specialization"));
        return ta;
    }
}
