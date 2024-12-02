package com.example.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.DatabaseConnection;
import com.example.Model.GroupSession;

public class GroupSessionDAO {

    public List<GroupSession> getAllSessions() throws SQLException {
        List<GroupSession> sessions = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM group_sessions";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GroupSession session = new GroupSession();
                    session.setId(rs.getInt("id"));
                    session.setTrainerId(rs.getInt("trainer_id"));
                    session.setDate(rs.getDate("date").toLocalDate());
                    session.setTime(rs.getTime("time").toLocalTime());
                    session.setMaxParticipants(rs.getInt("max_participants"));
                    session.setParticipantCount(rs.getInt("participant_count"));

                    sessions.add(session);
                }
            }
        }
        return sessions;
    }

    public List<GroupSession> getAvailableGroupSessions() throws SQLException {
        List<GroupSession> availableSessions = new ArrayList<>();
        
        try (Connection connection = DatabaseConnection.getConnection()) {
            // SQL-запит для вибору всіх групових сесій
            String query = "SELECT * FROM group_sessions WHERE participant_count < max_participants";
            
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GroupSession session = new GroupSession();
                    session.setId(rs.getInt("id"));
                    session.setTrainerId(rs.getInt("trainer_id"));
                    session.setDate(rs.getDate("date").toLocalDate());
                    session.setTime(rs.getTime("time").toLocalTime());
                    session.setMaxParticipants(rs.getInt("max_participants"));
                    session.setParticipantCount(rs.getInt("participant_count"));
    
                    availableSessions.add(session);
                }
            }
        }
        
        return availableSessions;
    }

    public GroupSession getSessionById(int id) throws SQLException {
        GroupSession session = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM group_sessions WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        session = new GroupSession();
                        session.setId(rs.getInt("id"));
                        session.setTrainerId(rs.getInt("trainer_id"));
                        session.setDate(rs.getDate("date").toLocalDate());
                        session.setTime(rs.getTime("time").toLocalTime());
                        session.setMaxParticipants(rs.getInt("max_participants"));
                        session.setParticipantCount(rs.getInt("participant_count"));
                    }
                }
            }
        }
        return session;
    }

    public boolean createSession(GroupSession session) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO group_sessions (trainer_id, date, time, max_participants, participant_count) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, session.getTrainerId());
                stmt.setDate(2, Date.valueOf(session.getDate()));
                stmt.setTime(3, Time.valueOf(session.getTime()));
                stmt.setInt(4, session.getMaxParticipants());
                stmt.setInt(5, session.getParticipantCount());

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

    public boolean updateSession(GroupSession session) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE group_sessions SET trainer_id = ?, date = ?, time = ?, max_participants = ?, participant_count = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, session.getTrainerId());
                stmt.setDate(2, Date.valueOf(session.getDate()));
                stmt.setTime(3, Time.valueOf(session.getTime()));
                stmt.setInt(4, session.getMaxParticipants());
                stmt.setInt(5, session.getParticipantCount());
                stmt.setInt(6, session.getId());

                return stmt.executeUpdate() > 0;
            }
        }
    }

    public boolean deleteSession(int id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM group_sessions WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        }
    }
    
}
