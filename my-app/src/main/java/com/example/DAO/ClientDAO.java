package com.example.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.DatabaseConnection;
import com.example.Model.Client;
import com.example.Model.Membership;
import com.example.Model.Payment;
import com.example.Model.User;

public class ClientDAO {

    private MembershipDAO membershipDAO;

    public ClientDAO() {
        this.membershipDAO = new MembershipDAO(); 
    }

    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM clients";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Client client = new Client();
                    client.setId(rs.getInt("id"));
                    client.setUserId(rs.getInt("user_id"));
                    client.setMembershipId(rs.getInt("membership_id"));

                    LocalDate startDate = rs.getDate("start_date").toLocalDate();
                    LocalDate endDate = rs.getDate("end_date").toLocalDate();

                    client.setStartDate(startDate); 
                    client.setEndDate(endDate);

                    clients.add(client);
                }
            }
        }
        return clients;
    }

    public Client getClientById(int id) throws SQLException {
        Client client = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM clients WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        client = new Client();
                        client.setId(rs.getInt("id"));
                        client.setUserId(rs.getInt("user_id"));
                        client.setMembershipId(rs.getInt("membership_id"));

                        LocalDate startDate = rs.getDate("start_date").toLocalDate();
                        LocalDate endDate = rs.getDate("end_date").toLocalDate();

                        client.setStartDate(startDate);
                        client.setEndDate(endDate);
                    }
                }
            }
        }
        return client;
    }

    public User getUserByEmail(String email) throws SQLException {
        User user = null;
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
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
        return user;
    }

    public int getUserIdByEmail(String email) throws SQLException {
        String query = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return 0;
    }

    public int getClientIdByEmail(String email) throws SQLException {
        String userQuery = "SELECT id FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(userQuery)) {
    
            stmt.setString(1, email);
    
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
    
                    String clientQuery = "SELECT id FROM clients WHERE user_id = ?";
                    try (PreparedStatement clientStmt = conn.prepareStatement(clientQuery)) {
                        clientStmt.setInt(1, userId);
    
                        try (ResultSet clientRs = clientStmt.executeQuery()) {
                            if (clientRs.next()) {
                                return clientRs.getInt("id");
                            }
                        }
                    }
                }
            }
        }
        return 0;  
    }

    public int createClient(Client client) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO clients (user_id, membership_id, start_date, end_date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, client.getUserId());
                stmt.setInt(2, client.getMembershipId());
                stmt.setDate(3, Date.valueOf(client.getStartDate())); 
                stmt.setDate(4, Date.valueOf(client.getEndDate())); 
    
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);
                            client.setId(generatedId); 
                            return generatedId; 
                        }
                    }
                }
            }
        }
        return -1;
    }

    public boolean updateClient(Client client) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE clients SET user_id = ?, membership_id = ?, start_date = ?, end_date = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, client.getUserId());
                stmt.setInt(2, client.getMembershipId());

                stmt.setDate(3, Date.valueOf(client.getStartDate()));
                stmt.setDate(4, Date.valueOf(client.getEndDate())); 
                stmt.setInt(5, client.getId());

                return stmt.executeUpdate() > 0;
            }
        }
    }

    public boolean deleteClient(int id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM clients WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        }
    }

    public boolean hasActiveMembership(int clientId) throws SQLException {
        String query = "SELECT COUNT(*) FROM clients WHERE id = ? AND DATE(end_date) >= CURRENT_DATE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, clientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Active memberships count for client with id " + clientId + ": " + count);
                    
                    return count > 0;
                }
            }
        }
        return false;
    }
    
    public int getUserIdByClientId(int clientId) throws SQLException {
        String query = "SELECT user_id FROM clients WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        }
        return 0;
    }

    public int getMembershipDuration(int membershipId) throws SQLException {
        String query = "SELECT duration FROM memberships WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, membershipId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("duration");
                }
            }
        }
        return 0; 
    }

    public double getMembershipPrice(int membershipId) throws SQLException {
        String query = "SELECT price FROM memberships WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, membershipId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                }
            }
        }
        return 0.0; 
    }

    public String getMembershipType(Client client) {
        try {
            Membership membership = membershipDAO.getMembershipById(client.getMembershipId());
            return membership != null ? membership.getType() : null;  // Повертаємо тип підписки
        } catch (SQLException e) {
            e.printStackTrace();
            return null;  
        }
    }

    public void addPaymentRecord(Payment payment) throws SQLException {
        String query = "INSERT INTO payments (client_id, amount, payment_date, method, membership_id) VALUES (?, ?, ?, ?::payment_method, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, payment.getClientId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            stmt.setString(4, payment.getMethod());
            stmt.setInt(5, payment.getMembershipId());
            stmt.executeUpdate();
        }
    }

    public boolean hasValidMembership(int clientId, List<Integer> validMemberships) throws SQLException {
        String sql = "SELECT membership_id FROM clients WHERE client_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int membershipId = rs.getInt("membership_id");
                return validMemberships.contains(membershipId);
            }
        }
        return false;
    }

}
