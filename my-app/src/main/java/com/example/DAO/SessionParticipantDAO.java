package com.example.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.DatabaseConnection;
import com.example.Model.SessionParticipant;

public class SessionParticipantDAO {

    public List<SessionParticipant> getAllParticipants() throws SQLException {
        List<SessionParticipant> participants = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM session_participants";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        SessionParticipant participant = new SessionParticipant();
                        participant.setSessionId(rs.getInt("session_id"));
                        participant.setParticipantId(rs.getInt("participant_id"));
                        participants.add(participant);
                    }
                }
            }
        }
        return participants;
    }

    public boolean checkIfParticipantExists(int participantId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM session_participants WHERE participant_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, participantId);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    public List<SessionParticipant> getParticipantsByParticipantId(int participantId) throws SQLException {
        List<SessionParticipant> participants = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM session_participants WHERE participant_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, participantId);  // Встановлюємо participant_id
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        SessionParticipant participant = new SessionParticipant();
                        participant.setSessionId(rs.getInt("session_id"));
                        participant.setParticipantId(rs.getInt("participant_id"));
                        participants.add(participant);
                    }
                }
            }
        }
        return participants;
    }

    public boolean removeParticipantFromSession(int participantId, int sessionId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM session_participants WHERE participant_id = ? AND session_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, participantId); // participant_id
                stmt.setInt(2, sessionId); // session_id
                return stmt.executeUpdate() > 0; 
            }
        }
    }


    public boolean addParticipant(SessionParticipant participant) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO session_participants (session_id, participant_id) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, participant.getSessionId());
                stmt.setInt(2, participant.getParticipantId());
                return stmt.executeUpdate() > 0;
            }
        }
    }
}
