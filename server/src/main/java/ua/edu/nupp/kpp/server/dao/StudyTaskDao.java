package ua.edu.nupp.kpp.server.dao;

import ua.edu.nupp.kpp.server.DBUtil;
import ua.edu.nupp.kpp.server.model.StudyTask;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudyTaskDao {

    public List<StudyTask> getAllTasks() {
        List<StudyTask> tasks = new ArrayList<>();
        String sql = "SELECT id, enrollment_id, title, max_score, due_date FROM study_task";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(new StudyTask(
                        rs.getLong("id"),
                        rs.getLong("enrollment_id"),
                        rs.getString("title"),
                        rs.getInt("max_score"),
                        rs.getString("due_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void addTask(StudyTask task) {
        String sql = "INSERT INTO study_task (enrollment_id, title, max_score, due_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, task.getEnrollmentId());
            pstmt.setString(2, task.getTitle());
            pstmt.setInt(3, task.getMaxScore());
            pstmt.setString(4, task.getDueDate());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(StudyTask task) {
        String sql = "UPDATE study_task SET title = ?, max_score = ?, due_date = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, task.getTitle());
            pstmt.setInt(2, task.getMaxScore());
            pstmt.setString(3, task.getDueDate());
            pstmt.setLong(4, task.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTask(Long id) {
        String sql = "DELETE FROM study_task WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}