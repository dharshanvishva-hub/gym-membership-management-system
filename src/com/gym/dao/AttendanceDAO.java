package com.gym.dao;

import com.gym.model.Attendance;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendanceDAO implements CrudRepository<Attendance> {

    @Override
    public List<Attendance> findAll() throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, m.name AS member_name " +
                     "FROM attendance a " +
                     "JOIN members m ON a.member_id = m.member_id " +
                     "ORDER BY a.attendance_date DESC, a.check_in_time DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToAttendance(rs));
            }
        }
        return list;
    }

    @Override
    public Optional<Attendance> findById(int id) throws SQLException {
        String sql = "SELECT a.*, m.name AS member_name " +
                     "FROM attendance a " +
                     "JOIN members m ON a.member_id = m.member_id " +
                     "WHERE a.attendance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAttendance(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Attendance> findByMemberId(int memberId) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, m.name AS member_name " +
                     "FROM attendance a " +
                     "JOIN members m ON a.member_id = m.member_id " +
                     "WHERE a.member_id = ? " +
                     "ORDER BY a.attendance_date DESC, a.check_in_time DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAttendance(rs));
                }
            }
        }
        return list;
    }

    public Optional<Attendance> findOpenCheckIn(int memberId, LocalDate date) throws SQLException {
        String sql = "SELECT a.*, m.name AS member_name FROM attendance a " +
                     "JOIN members m ON a.member_id = m.member_id " +
                     "WHERE a.member_id = ? AND a.attendance_date = ? AND a.check_out_time IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setString(2, date.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAttendance(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean recordCheckOut(int attendanceId, LocalTime checkOutTime) throws SQLException {
        String sql = "UPDATE attendance SET check_out_time = ? WHERE attendance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, checkOutTime.toString());
            stmt.setInt(2, attendanceId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean save(Attendance a) throws SQLException {
        String sql = "INSERT INTO attendance (member_id, attendance_date, check_in_time, check_out_time, workout_type) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, a.getMemberId());
            stmt.setString(2, a.getAttendanceDate() != null ? a.getAttendanceDate().toString() : LocalDate.now().toString());
            stmt.setString(3, a.getCheckInTime() != null ? a.getCheckInTime().toString() : LocalTime.now().toString());
            stmt.setString(4, a.getCheckOutTime() != null ? a.getCheckOutTime().toString() : null);
            stmt.setString(5, a.getWorkoutType() != null ? a.getWorkoutType() : "General Workout");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        a.setAttendanceId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Attendance a) throws SQLException {
        String sql = "UPDATE attendance SET check_out_time = ?, workout_type = ? WHERE attendance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, a.getCheckOutTime() != null ? a.getCheckOutTime().toString() : null);
            stmt.setString(2, a.getWorkoutType());
            stmt.setInt(3, a.getAttendanceId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        String checkOutStr = rs.getString("check_out_time");
        LocalTime checkOut = checkOutStr != null ? LocalTime.parse(checkOutStr) : null;
        Attendance a = new Attendance(
                rs.getInt("attendance_id"),
                rs.getInt("member_id"),
                LocalDate.parse(rs.getString("attendance_date")),
                LocalTime.parse(rs.getString("check_in_time")),
                checkOut,
                rs.getString("workout_type")
        );
        a.setMemberName(rs.getString("member_name"));
        return a;
    }
}
