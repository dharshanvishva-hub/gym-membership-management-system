package com.gym.dao;

import com.gym.model.Member;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDAO implements CrudRepository<Member> {

    @Override
    public List<Member> findAll() throws SQLException {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY member_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToMember(rs));
            }
        }
        return list;
    }

    @Override
    public Optional<Member> findById(int id) throws SQLException {
        String sql = "SELECT * FROM members WHERE member_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Member> searchByNameOrContact(String keyword) throws SQLException {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE name LIKE ? OR contact_number LIKE ? ORDER BY name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMember(rs));
                }
            }
        }
        return list;
    }

    @Override
    public boolean save(Member member) throws SQLException {
        String sql = "INSERT INTO members (name, age, gender, contact_number, email, registration_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, member.getName());
            stmt.setInt(2, member.getAge());
            stmt.setString(3, member.getGender());
            stmt.setString(4, member.getContactNumber());
            stmt.setString(5, member.getEmail());
            stmt.setString(6, member.getRegistrationDate() != null ? member.getRegistrationDate().toString() : LocalDate.now().toString());
            stmt.setString(7, member.getStatus() != null ? member.getStatus() : "Active");

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        member.setMemberId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Member member) throws SQLException {
        String sql = "UPDATE members SET name = ?, age = ?, gender = ?, contact_number = ?, email = ?, status = ? " +
                     "WHERE member_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getName());
            stmt.setInt(2, member.getAge());
            stmt.setString(3, member.getGender());
            stmt.setString(4, member.getContactNumber());
            stmt.setString(5, member.getEmail());
            stmt.setString(6, member.getStatus());
            stmt.setInt(7, member.getMemberId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        String regDateStr = rs.getString("registration_date");
        LocalDate regDate = regDateStr != null ? LocalDate.parse(regDateStr) : LocalDate.now();
        return new Member(
                rs.getInt("member_id"),
                rs.getString("name"),
                rs.getInt("age"),
                rs.getString("gender"),
                rs.getString("contact_number"),
                rs.getString("email"),
                regDate,
                rs.getString("status")
        );
    }
}
