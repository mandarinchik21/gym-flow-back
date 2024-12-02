package com.example.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.DatabaseConnection;
import com.example.Model.IndividualSession;
import com.example.Model.Trainer;

public class TrainerDAO {

    public List<Trainer> getAllTrainers() throws SQLException {
        List<Trainer> trainers = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM trainers";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Trainer trainer = new Trainer();
                    trainer.setId(rs.getInt("id"));
                    trainer.setName(rs.getString("name"));
                    trainer.setSurname(rs.getString("surname"));
                    trainer.setSpecialization(rs.getString("specialization"));
                    trainer.setExperience(rs.getInt("experience"));
                    trainers.add(trainer);
                }
            }
        }
        return trainers;
    }

    public Trainer getTrainerById(int id) throws SQLException {
        Trainer trainer = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM trainers WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        trainer = new Trainer();
                        trainer.setId(rs.getInt("id"));
                        trainer.setName(rs.getString("name"));
                        trainer.setSurname(rs.getString("surname"));
                        trainer.setSpecialization(rs.getString("specialization"));
                        trainer.setExperience(rs.getInt("experience"));
                    }
                }
            }
        }
        return trainer;
    }

    public boolean createTrainer(Trainer trainer) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO trainers (name, surname, specialization, experience) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, trainer.getName());
                stmt.setString(2, trainer.getSurname());
                stmt.setString(3, trainer.getSpecialization());
                stmt.setInt(4, trainer.getExperience());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            trainer.setId(generatedKeys.getInt(1));
                        }
                    }
                }
                return affectedRows > 0;
            }
        }
    }

    public boolean updateTrainer(Trainer trainer) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE trainers SET name = ?, surname = ?, specialization = ?, experience = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, trainer.getName());
                stmt.setString(2, trainer.getSurname());
                stmt.setString(3, trainer.getSpecialization());
                stmt.setInt(4, trainer.getExperience());
                stmt.setInt(5, trainer.getId());
                return stmt.executeUpdate() > 0;
            }
        }
    }

    public boolean deleteTrainer(int id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM trainers WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        }
    }

    public List<IndividualSession> getAvailableSessionsForTrainer(int trainerId, LocalDate date) throws SQLException {
    // Якщо дата вказана, шукаємо заняття для конкретної дати
    if (date != null) {
        String sql = "SELECT * FROM trainer_schedule WHERE trainer_id = ? AND session_date = ? AND session_time >= CURRENT_TIME";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            stmt.setDate(2, Date.valueOf(date));  // Перетворення LocalDate в SQL Date
            ResultSet rs = stmt.executeQuery();
            
            List<IndividualSession> sessions = new ArrayList<>();
            while (rs.next()) {
                IndividualSession session = new IndividualSession();
                session.setTrainerId(rs.getInt("trainer_id"));
                session.setDate(rs.getDate("session_date").toLocalDate());
                session.setTime(rs.getTime("session_time").toLocalTime());
                sessions.add(session);
            }
            return sessions;
        }
    } else {
        String sql = "SELECT * FROM trainer_schedule WHERE trainer_id = ? AND session_time >= CURRENT_TIME";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();
            
            List<IndividualSession> sessions = new ArrayList<>();
            while (rs.next()) {
                IndividualSession session = new IndividualSession();
                session.setTrainerId(rs.getInt("trainer_id"));
                session.setDate(rs.getDate("session_date").toLocalDate());
                session.setTime(rs.getTime("session_time").toLocalTime());
                sessions.add(session);
            }
            return sessions;
        }
    }
}
}
