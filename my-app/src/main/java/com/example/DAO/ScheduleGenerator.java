package com.example.DAO;

import java.sql.*;
import java.time.*;
import java.util.Locale;
import com.example.DatabaseConnection;

public class ScheduleGenerator {

    public void generateWeeklyGroupSessions() {
        String selectTemplateQuery = "SELECT trainer_id, day_of_week, time FROM schedule_templates";
        String insertGroupSessionQuery = "INSERT INTO group_sessions (trainer_id, date, time, max_participants, participant_count) VALUES (?, ?, ?, 10, 0)";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = connection.prepareStatement(selectTemplateQuery);
             ResultSet rs = selectStmt.executeQuery();
             PreparedStatement insertStmt = connection.prepareStatement(insertGroupSessionQuery)) {
    
            LocalDate today = LocalDate.now();
            LocalDate nextWeekStart = today.plusWeeks(1).with(DayOfWeek.MONDAY);
    
            while (rs.next()) {
                String dayOfWeek = rs.getString("day_of_week");
                int trainerId = rs.getInt("trainer_id");
                LocalTime time = rs.getTime("time").toLocalTime();
                DayOfWeek targetDay = DayOfWeek.valueOf(dayOfWeek.toUpperCase(Locale.ENGLISH));
                LocalDate sessionDate = nextWeekStart.with(targetDay);

                insertStmt.setInt(1, trainerId);
                insertStmt.setDate(2, Date.valueOf(sessionDate));
                insertStmt.setTime(3, Time.valueOf(time));
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
