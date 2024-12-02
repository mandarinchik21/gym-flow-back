package com.example.DAO;

import com.example.Model.TrainerSchedule;
import com.example.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainerScheduleDAO {

    public List<TrainerSchedule> getAllSchedules() throws SQLException {
        List<TrainerSchedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM trainer_schedule";
    
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
    
            while (rs.next()) {
                int trainerId = rs.getInt("trainer_id");
                Date sessionDate = rs.getDate("session_date");
                Time sessionTime = rs.getTime("session_time");
    
                TrainerSchedule schedule = new TrainerSchedule();
                schedule.setTrainerId(trainerId);
                schedule.setSessionDate(sessionDate.toLocalDate());
                schedule.setSessionTime(sessionTime.toLocalTime());
    
                schedules.add(schedule);
            }
        }
        return schedules;
    }

    public boolean createTrainerSchedule(TrainerSchedule schedule) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO trainer_schedule (trainer_id, session_date, session_time) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, schedule.getTrainerId());
                stmt.setDate(2, Date.valueOf(schedule.getSessionDate()));
                stmt.setTime(3, Time.valueOf(schedule.getSessionTime()));
                return stmt.executeUpdate() > 0;
            }
        }
    }

    public boolean deleteSchedule(int trainerId, String sessionDate, String sessionTime) throws SQLException {
        String sql = "DELETE FROM trainer_schedule WHERE trainer_id = ? AND session_date = ? AND session_time = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setDate(2, Date.valueOf(sessionDate));
            stmt.setTime(3, Time.valueOf(sessionTime));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
