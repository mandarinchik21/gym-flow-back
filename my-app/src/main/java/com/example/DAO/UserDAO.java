package com.example.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.DatabaseConnection;
import com.example.Model.User;

public class UserDAO {

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT id, role, name, surname, email FROM users";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setRole(rs.getString("role"));
                    user.setName(rs.getString("name"));
                    user.setSurname(rs.getString("surname"));
                    user.setEmail(rs.getString("email"));
                    users.add(user);
                }
            }
        }
        return users;
    }

    public User getUserById(int id) throws SQLException {
        User user = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT id, role, name, surname, email FROM users WHERE id = ?"; 
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        user = new User();
                        user.setId(rs.getInt("id"));
                        user.setRole(rs.getString("role"));
                        user.setName(rs.getString("name"));
                        user.setSurname(rs.getString("surname"));
                        user.setEmail(rs.getString("email"));
                    }
                }
            }
        }
        return user;
    }

    public boolean updateUser(User user) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE users SET role = ?::role_type, name = ?, surname = ?, email = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, user.getRole());
                stmt.setString(2, user.getName());
                stmt.setString(3, user.getSurname());
                stmt.setString(4, user.getEmail());
                stmt.setInt(5, user.getId());
                return stmt.executeUpdate() > 0;
            }
        }
    }

    public User getUserByEmail(String email) throws SQLException {
        User user = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE email = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        user = new User();
                        user.setId(rs.getInt("id"));
                        user.setRole(rs.getString("role"));
                        user.setName(rs.getString("name"));
                        user.setSurname(rs.getString("surname"));
                        user.setEmail(rs.getString("email"));
                        user.setPassword(rs.getString("password"));
                    }
                }
            }
        }
        return user;
    }

    public boolean createUser(User user) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (role, name, surname, email, password) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setObject(1, user.getRole(), java.sql.Types.OTHER);
                stmt.setString(2, user.getName());
                stmt.setString(3, user.getSurname());
                stmt.setString(4, user.getEmail());
                stmt.setString(5, user.getPassword());
    
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            user.setId(generatedKeys.getInt(1));
                        }
                    }
                }
                return affectedRows > 0;
            }
        }
    }

    
    public boolean deleteUser(int id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        }
    }
}
