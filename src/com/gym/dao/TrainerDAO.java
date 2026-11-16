package com.gym.dao;

import com.gym.model.Trainer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainerDAO implements CrudRepository<Trainer> {

    @Override
    public List<Trainer> findAll() throws SQLException {
        List<Trainer> list = new ArrayList<>();
        String sql = "SELECT * FROM trainers ORDER BY trainer_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTrainer(rs));
            }
        }
        return list;
    }

    @Override
    public Optional<Trainer> findById(int id) throws SQLException {
        String sql = "SELECT * FROM trainers WHERE trainer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTrainer(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean save(Trainer trainer) throws SQLException {
        String sql = "INSERT INTO trainers (name, specialization, contact_number, email, experience_years, hourly_rate) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, trainer.getName());
            stmt.setString(2, trainer.getSpecialization());
            stmt.setString(3, trainer.getContactNumber());
            stmt.setString(4, trainer.getEmail());
            stmt.setInt(5, trainer.getExperienceYears());
            stmt.setDouble(6, trainer.getHourlyRate());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        trainer.setTrainerId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Trainer trainer) throws SQLException {
        String sql = "UPDATE trainers SET name = ?, specialization = ?, contact_number = ?, email = ?, experience_years = ?, hourly_rate = ? " +
                     "WHERE trainer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trainer.getName());
            stmt.setString(2, trainer.getSpecialization());
            stmt.setString(3, trainer.getContactNumber());
            stmt.setString(4, trainer.getEmail());
            stmt.setInt(5, trainer.getExperienceYears());
            stmt.setDouble(6, trainer.getHourlyRate());
            stmt.setInt(7, trainer.getTrainerId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM trainers WHERE trainer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Trainer mapResultSetToTrainer(ResultSet rs) throws SQLException {
        return new Trainer(
                rs.getInt("trainer_id"),
                rs.getString("name"),
                rs.getString("specialization"),
                rs.getString("contact_number"),
                rs.getString("email"),
                rs.getInt("experience_years"),
                rs.getDouble("hourly_rate")
        );
    }
}
