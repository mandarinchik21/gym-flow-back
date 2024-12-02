package com.example.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.example.Model.Membership;
import com.example.Model.TrainerSchedule;
import com.example.Model.IndividualSession;
import com.example.Model.GroupSession;
import com.example.DatabaseConnection;
import com.example.Model.SessionDetails;

public class SessionService {

    public Membership getClientMembership(int clientId) throws SQLException {
        String sql = "SELECT m.id, m.name, m.description, m.price, m.duration, cm.expiry_date " +
                    "FROM memberships m " +
                    "JOIN client_memberships cm ON m.id = cm.membership_id " +
                    "WHERE cm.client_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Membership membership = new Membership();
                membership.setId(rs.getInt("id"));
                membership.setName(rs.getString("name"));
                membership.setDescription(rs.getString("description"));
                membership.setPrice(rs.getDouble("price"));
                membership.setDuration(rs.getInt("duration"));

                Date expiryDate = rs.getDate("expiry_date");
                if (expiryDate == null || expiryDate.before(new Date(System.currentTimeMillis()))) {
                    throw new SQLException("Your membership has expired.");
                }
                return membership;
            } else {
                throw new SQLException("No membership found for client.");
            }
        }
    }

    public boolean signUpForGroupSession(int participantId, int sessionId) {
        String query = "INSERT INTO session_participants (participant_id, session_id) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, participantId);
            statement.setInt(2, sessionId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isClientAlreadySignedUp(int participantId, int sessionId) throws SQLException {
        String query = "SELECT COUNT(*) FROM session_participants WHERE participant_id = ? AND session_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, participantId);
            stmt.setInt(2, sessionId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;  
            }
        }
        return false; 
    }
    
    private static final String INSERT_SESSION_QUERY = 
    "INSERT INTO individual_sessions (client_id, trainer_id, date, time) VALUES (?, ?, ?, ?)";

    public boolean signUpForIndividualSession(int clientId, int trainerId, LocalDate sessionDate, LocalTime sessionTime) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_SESSION_QUERY)) {
                statement.setInt(1, clientId);             
                statement.setInt(2, trainerId);                   
                statement.setDate(3, java.sql.Date.valueOf(sessionDate));
                statement.setTime(4, java.sql.Time.valueOf(sessionTime)); 

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static final String CHECK_SESSION_EXISTS_QUERY = 
    "SELECT COUNT(*) FROM individual_sessions WHERE client_id = ? AND trainer_id = ? AND date = ? AND time = ?";

    public boolean isClientAlreadySignedUp(int clientId, int trainerId, LocalDate sessionDate, LocalTime sessionTime) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CHECK_SESSION_EXISTS_QUERY)) {
                statement.setInt(1, clientId);                     // client_id
                statement.setInt(2, trainerId);                    // trainer_id
                statement.setDate(3, java.sql.Date.valueOf(sessionDate)); // sessionDate
                statement.setTime(4, java.sql.Time.valueOf(sessionTime)); // sessionTime

                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkTrainerAvailability(int trainerId, LocalDate date, LocalTime time) throws SQLException {
        String sql = "SELECT 1 FROM individual_sessions WHERE trainer_id = ? AND date = ? AND time = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setDate(2, Date.valueOf(date)); 
            stmt.setTime(3, Time.valueOf(time));  
            ResultSet rs = stmt.executeQuery();

            return !rs.next();
        }
    }

    public List<GroupSession> getAvailableGroupSessions(int clientId) throws SQLException {
        String subscriptionType = getSubscriptionTypeForClient(clientId);
    
        List<GroupSession> sessions = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (subscriptionType.contains("Group") || subscriptionType.contains("All Training")) {
                String sql = "SELECT id, trainer_id, date, time, max_participants, participant_count " +
                             "FROM group_sessions WHERE date >= CURRENT_DATE";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        int participantCount = rs.getInt("participant_count");
                        int maxParticipants = rs.getInt("max_participants");
                        if (participantCount >= maxParticipants) {
                            continue; 
                        }

                        GroupSession session = new GroupSession();
                        session.setId(rs.getInt("id"));
                        session.setTrainerId(rs.getInt("trainer_id"));
                        session.setDate(rs.getDate("date").toLocalDate());
                        session.setTime(rs.getTime("time").toLocalTime());
                        session.setMaxParticipants(maxParticipants);
                        session.setParticipantCount(participantCount);
                        sessions.add(session);
                    }
                }
            }
        }
        return sessions;
    }
    
    public List<TrainerSchedule> getAvailableTrainerSchedules(int clientId) throws SQLException {
        String subscriptionType = getSubscriptionTypeForClient(clientId);
    
        if (!subscriptionType.contains("All Training")) {
            return new ArrayList<>(); 
        }

        String sql = "SELECT trainer_id, session_date, session_time " +
                     "FROM trainer_schedule " +
                     "WHERE session_date >= CURRENT_DATE";  
    
        List<TrainerSchedule> schedules = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TrainerSchedule schedule = new TrainerSchedule();
                schedule.setTrainerId(rs.getInt("trainer_id"));
                schedule.setSessionDate(rs.getDate("session_date").toLocalDate());
                schedule.setSessionTime(rs.getTime("session_time").toLocalTime());
    
                if (schedule.getTrainerId() != 0 && schedule.getSessionDate() != null && schedule.getSessionTime() != null) {
                    schedules.add(schedule);
                }
            }
        }
        return schedules;
    }

    public String getSubscriptionTypeForClient(int clientId) throws SQLException {
        String sql = "SELECT name FROM memberships WHERE id = (SELECT membership_id FROM clients WHERE id = ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            } else {
                throw new SQLException("No subscription found for client with id " + clientId);
            }
        }
    }


    public List<IndividualSession> getAvailableSessionsForTrainer(int trainerId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM individual_sessions WHERE trainer_id = ? AND date = ? AND time >= CURRENT_TIME";
        List<IndividualSession> sessions = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setDate(2, Date.valueOf(date)); 
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                IndividualSession session = new IndividualSession();
                session.setId(rs.getInt("id"));
                session.setClientId(rs.getInt("client_id"));
                session.setTrainerId(rs.getInt("trainer_id"));
                session.setDate(rs.getDate("date").toLocalDate());
                session.setTime(rs.getTime("time").toLocalTime());
                sessions.add(session);
            }
        }
        return sessions;
    }

    public boolean removeSessionFromTrainerSchedule(int trainerId, LocalDate date, LocalTime time) throws SQLException {
        String sql = "DELETE FROM trainer_schedule WHERE trainer_id = ? AND session_date = ? AND session_time = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setDate(2, Date.valueOf(date));  
            stmt.setTime(3, Time.valueOf(time)); 
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean cancelGroupSession(int clientId, int sessionId) throws SQLException {
        String subscriptionType = getSubscriptionTypeForClient(clientId);
        if (!(subscriptionType.contains("Group") || subscriptionType.contains("All Training"))) {
            throw new SQLException("Your membership does not include group or all training sessions.");
        }
    
        try (Connection connection = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT * FROM session_participants WHERE session_id = ? AND participant_id = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, sessionId);
                checkStmt.setInt(2, clientId);
                ResultSet rs = checkStmt.executeQuery();
    
                if (!rs.next()) {
                    throw new SQLException("You are not registered for this group session.");
                }
            }
    
            String deleteSql = "DELETE FROM session_participants WHERE session_id = ? AND participant_id = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, sessionId);
                deleteStmt.setInt(2, clientId);
                int rowsDeleted = deleteStmt.executeUpdate();
                
                if (rowsDeleted > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancelIndividualSession(int clientId, int sessionId) throws SQLException {
        String subscriptionType = getSubscriptionTypeForClient(clientId);
        if (!subscriptionType.contains("All Training")) {
            throw new SQLException("Your membership does not include individual sessions.");
        }
    
        try (Connection connection = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT * FROM individual_sessions WHERE id = ? AND client_id = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setInt(1, sessionId);
                checkStmt.setInt(2, clientId);
                ResultSet rs = checkStmt.executeQuery();
    
                if (!rs.next()) {
                    throw new SQLException("You are not registered for this individual session.");
                }
            }
    
            String deleteSql = "DELETE FROM individual_sessions WHERE id = ? AND client_id = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, sessionId);
                deleteStmt.setInt(2, clientId);
                int rowsDeleted = deleteStmt.executeUpdate();
    
                if (rowsDeleted > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final String GET_SESSION_ID_QUERY = 
    "SELECT id FROM individual_sessions WHERE client_id = ? AND trainer_id = ? AND date = ? AND time = ?";

    public int getSessionId(int clientId, int trainerId, LocalDate sessionDate, LocalTime sessionTime) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(GET_SESSION_ID_QUERY)) {
                statement.setInt(1, clientId);
                statement.setInt(2, trainerId);
                statement.setDate(3, java.sql.Date.valueOf(sessionDate));
                statement.setTime(4, java.sql.Time.valueOf(sessionTime));

                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id"); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean deleteSession(int sessionId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM individual_sessions WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, sessionId);
                int rowsDeleted = stmt.executeUpdate();
                return rowsDeleted > 0;  
            }
        }
    }

    public List<SessionDetails> getAllSessionsForClient(int clientId) throws SQLException {
        String query = "SELECT 'Individual' AS session_type, i.date, i.time, i.trainer_id " +
                       "FROM individual_sessions i " +
                       "LEFT JOIN session_participants sp ON i.id = sp.session_id " +
                       "WHERE i.client_id = ? " +
                       "UNION " +
                       "SELECT 'Group' AS session_type, gs.date, gs.time, gs.trainer_id " +
                       "FROM group_sessions gs " +
                       "JOIN session_participants sp ON gs.id = sp.session_id " +
                       "WHERE sp.participant_id = ?";
    
        List<SessionDetails> sessions = new ArrayList<>();
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            stmt.setInt(2, clientId);
    
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String sessionType = rs.getString("session_type");
                    LocalDate sessionDate = rs.getDate("date").toLocalDate();
                    LocalTime sessionTime = rs.getTime("time").toLocalTime();
                    int trainerId = rs.getInt("trainer_id");
    
                    SessionDetails session = new SessionDetails(sessionType, sessionDate, sessionTime, trainerId);
                    sessions.add(session);
                }
            }
        }
    
        return sessions;
    }
}
