package com.example.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.DatabaseConnection;
import com.example.Model.Membership;

public class MembershipDAO {

    public List<Membership> getAllMemberships() throws SQLException {
        List<Membership> memberships = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM memberships";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Membership membership = new Membership();
                    membership.setId(rs.getInt("id"));
                    membership.setName(rs.getString("name"));
                    membership.setDescription(rs.getString("description"));
                    membership.setPrice(rs.getDouble("price"));
                    membership.setDuration(rs.getInt("duration"));
                    memberships.add(membership);
                }
            }
        }
        return memberships;
    }

    public Membership getMembershipById(int id) throws SQLException {
        Membership membership = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM memberships WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        membership = new Membership();
                        membership.setId(rs.getInt("id"));
                        membership.setName(rs.getString("name"));
                        membership.setDescription(rs.getString("description"));
                        membership.setPrice(rs.getDouble("price"));
                        membership.setDuration(rs.getInt("duration"));
                    }
                }
            }
        }
        return membership;
    }

    public boolean createMembership(Membership membership) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO memberships (name, description, price, duration) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, membership.getName());
                stmt.setString(2, membership.getDescription());
                stmt.setDouble(3, membership.getPrice());
                stmt.setInt(4, membership.getDuration());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            membership.setId(generatedKeys.getInt(1));
                        }
                    }
                }
                return affectedRows > 0;
            }
        }
    }

    public boolean updateMembership(Membership membership) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE memberships SET name = ?, description = ?, price = ?, duration = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, membership.getName());
                stmt.setString(2, membership.getDescription());
                stmt.setDouble(3, membership.getPrice());
                stmt.setInt(4, membership.getDuration());
                stmt.setInt(5, membership.getId());
                return stmt.executeUpdate() > 0;
            }
        }
    }

    public boolean deleteMembership(int id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM memberships WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        }
    }
}