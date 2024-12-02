package com.example.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.example.DatabaseConnection;
import com.example.Model.IndividualSession;

public class IndividualSessionDAO {

    public List<IndividualSession> getAllSessions() throws SQLException {
        List<IndividualSession> sessions = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM individual_sessions";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    IndividualSession session = new IndividualSession();
                    session.setId(rs.getInt("id"));
                    session.setClientId(rs.getInt("client_id"));
                    session.setTrainerId(rs.getInt("trainer_id"));

                    LocalDate date = rs.getDate("date").toLocalDate(); 
                    LocalTime time = rs.getTime("time").toLocalTime(); 

                    session.setDate(date);
                    session.setTime(time);

                    sessions.add(session);
                }
            }
        }
        return sessions;
    }


    public IndividualSession getSessionById(int id) throws SQLException {
        IndividualSession session = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM individual_sessions WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        session = new IndividualSession();
                        session.setId(rs.getInt("id"));
                        session.setClientId(rs.getInt("client_id"));
                        session.setTrainerId(rs.getInt("trainer_id"));

                        LocalDate date = rs.getDate("date").toLocalDate(); 
                        LocalTime time = rs.getTime("time").toLocalTime(); 

                        session.setDate(date);
                        session.setTime(time);
                    }
                }
            }
        }
        return session;
    }

    public boolean createSession(IndividualSession session) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO individual_sessions (client_id, trainer_id, date, time) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, session.getClientId());
                stmt.setInt(2, session.getTrainerId());
                stmt.setDate(3, Date.valueOf(session.getDate())); 
                stmt.setTime(4, Time.valueOf(session.getTime())); 

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            session.setId(generatedKeys.getInt(1)); 
                        }
                    }
                }
                return affectedRows > 0;
            }
        }
    }

    public boolean updateSession(IndividualSession session) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE individual_sessions SET client_id = ?, trainer_id = ?, date = ?, time = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, session.getClientId());
                stmt.setInt(2, session.getTrainerId());
                stmt.setDate(3, Date.valueOf(session.getDate())); 
                stmt.setTime(4, Time.valueOf(session.getTime())); 
                stmt.setInt(5, session.getId());

                return stmt.executeUpdate() > 0;
            }
        }
    }

    public boolean deleteSession(int id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM individual_sessions WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        }
    }
}