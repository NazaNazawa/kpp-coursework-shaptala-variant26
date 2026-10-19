package ua.edu.nupp.kpp.server.dao;

import ua.edu.nupp.kpp.server.DBUtil;
import ua.edu.nupp.kpp.server.model.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDao {

    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT id, course_id, listener_id, state, f1, f2, f3, integral_score FROM enrollment";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                enrollments.add(new Enrollment(
                        rs.getLong("id"),
                        rs.getLong("course_id"),
                        rs.getLong("listener_id"),
                        rs.getString("state"),
                        rs.getDouble("f1"),
                        rs.getDouble("f2"),
                        rs.getDouble("f3"),
                        rs.getDouble("integral_score")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }

    public void updateEnrollment(Enrollment enrollment) {
        String sql = "UPDATE enrollment SET state = ?, f1 = ?, f2 = ?, f3 = ?, integral_score = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, enrollment.getState());
            pstmt.setDouble(2, enrollment.getF1());
            pstmt.setDouble(3, enrollment.getF2());
            pstmt.setDouble(4, enrollment.getF3());
            pstmt.setDouble(5, enrollment.getIntegralScore());
            pstmt.setLong(6, enrollment.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}