package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Система керування курсами (Варіант 26)");

        TableView<CourseData> table = new TableView<>();

        TableColumn<CourseData, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<CourseData, String> titleCol = new TableColumn<>("Назва курсу");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<CourseData, String> descCol = new TableColumn<>("Опис");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<CourseData, Integer> creditsCol = new TableColumn<>("Кредити");
        creditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));

        table.getColumns().addAll(idCol, titleCol, descCol, creditsCol);

        table.setItems(getCourseData());

        VBox vbox = new VBox(table);
        Scene scene = new Scene(vbox, 650, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ObservableList<CourseData> getCourseData() {
        List<CourseData> list = new ArrayList<>();
        String url = "jdbc:mysql://localhost:3306/course_management";
        String user = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, title, description, credits FROM course")) {

            while (rs.next()) {
                list.add(new CourseData(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("credits")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return FXCollections.observableArrayList(list);
    }

    public static class CourseData {
        private Long id;
        private String title;
        private String description;
        private int credits;

        public CourseData(Long id, String title, String description, int credits) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.credits = credits;
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public int getCredits() { return credits; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}