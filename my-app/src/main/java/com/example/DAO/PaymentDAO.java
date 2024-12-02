package com.example.DAO;

import java.sql.*;
import java.util.List;

import com.example.DatabaseConnection;
import com.example.Model.Payment;

import java.util.ArrayList;

public class PaymentDAO {

    public boolean createPayment(Payment payment) throws SQLException {
        String query = "INSERT INTO payments (client_id, amount, payment_date, method, membership_id) VALUES (?, ?, ?, ?::payment_method, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, payment.getClientId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setDate(3, Date.valueOf(payment.getPaymentDate()));
            stmt.setString(4, payment.getMethod());
            stmt.setInt(5, payment.getMembershipId()); 
    
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        payment.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public Payment getPaymentById(int id) throws SQLException {
        Payment payment = null;
        String query = "SELECT * FROM payments WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    payment = new Payment();
                    payment.setId(rs.getInt("id"));
                    payment.setClientId(rs.getInt("client_id"));
                    payment.setAmount(rs.getDouble("amount"));
                    payment.setPaymentDate(rs.getString("payment_date"));
                    payment.setMethod(rs.getString("method"));
                    payment.setMembershipId(rs.getInt("membership_id")); 
                }
            }
        }
        return payment;
    }

    public List<Payment> getPaymentsByClientId(int clientId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM payments WHERE client_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();
                    payment.setId(rs.getInt("id"));
                    payment.setClientId(rs.getInt("client_id"));
                    payment.setAmount(rs.getDouble("amount"));
                    payment.setPaymentDate(rs.getString("payment_date"));
                    payment.setMethod(rs.getString("method"));
                    payment.setMembershipId(rs.getInt("membership_id")); 
                    payments.add(payment);
                }
            }
        }
        return payments;
    }

    public boolean updatePayment(Payment payment) throws SQLException {
        String query = "UPDATE payments SET client_id = ?, amount = ?, payment_date = ?, method = ?::payment_method, membership_id = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, payment.getClientId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setDate(3, Date.valueOf(payment.getPaymentDate()));
            stmt.setString(4, payment.getMethod());
            stmt.setInt(5, payment.getMembershipId());  
            stmt.setInt(6, payment.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM payments";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setClientId(rs.getInt("client_id"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setPaymentDate(rs.getString("payment_date"));
                payment.setMethod(rs.getString("method"));
                payment.setMembershipId(rs.getInt("membership_id"));
                payments.add(payment);
            }
        }
        return payments;
    }

    public boolean deletePayment(int id) throws SQLException {
        String query = "DELETE FROM payments WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
