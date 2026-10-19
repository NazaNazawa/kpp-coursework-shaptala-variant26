package ua.edu.nupp.kpp.server.dao;

import ua.edu.nupp.kpp.server.DBUtil;
import ua.edu.nupp.kpp.server.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT id, title, description, credits FROM course";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Course course = new Course(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("credits")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    public void addCourse(Course course) {
        String sql = "INSERT INTO course (title, description, credits) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getTitle());
            pstmt.setString(2, course.getDescription());
            pstmt.setInt(3, course.getCredits());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCourse(Long id) {
        String sql = "DELETE FROM course WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Помилка видалення курсу ID " + id, e);
        }
    }
}