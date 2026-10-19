package ua.edu.nupp.kpp.server.dao;

import ua.edu.nupp.kpp.server.DBUtil;
import ua.edu.nupp.kpp.server.model.Listener;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListenerDao {

    public List<Listener> getAllListeners() {
        List<Listener> listeners = new ArrayList<>();
        String sql = "SELECT id, full_name, email, phone FROM listener";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                listeners.add(new Listener(
                        rs.getLong("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listeners;
    }

    public void addListener(Listener listener) {
        String sql = "INSERT INTO listener (full_name, email, phone) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, listener.getFullName());
            pstmt.setString(2, listener.getEmail());
            pstmt.setString(3, listener.getPhone());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}