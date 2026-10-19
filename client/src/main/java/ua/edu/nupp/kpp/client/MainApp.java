package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ua.edu.nupp.kpp.client.Model.Listener;

import java.nio.file.Path;

public class MainApp extends Application {
    private final HttpCourseClient client = new HttpCourseClient();
    private final TableView<Listener> listenerTable = new TableView<>();
    private final Label statusLabel = new Label("Готово до роботи");
    private final ProgressBar progressBar = new ProgressBar(0);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Керування навчальними курсами — Варіант 26");

        TabPane tabPane = new TabPane();
        Tab listenerTab = new Tab("Слухачі", createListenerPane());
        listenerTab.setClosable(false);
        tabPane.getTabs().add(listenerTab);

        Scene scene = new Scene(tabPane, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
        refreshListeners();
    }

    private BorderPane createListenerPane() {
        BorderPane pane = new BorderPane();

        TableColumn<Listener, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        TableColumn<Listener, String> nameCol = new TableColumn<>("ПІБ");
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("fullName"));
        TableColumn<Listener, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
        TableColumn<Listener, String> phoneCol = new TableColumn<>("Телефон");
        phoneCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("phone"));

        listenerTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol);
        pane.setCenter(listenerTable);

        VBox rightBox = new VBox(10);
        rightBox.setPadding(new Insets(10));

        Button importCsvBtn = new Button("Імпортувати CSV");
        importCsvBtn.setOnAction(e -> startCsvImport());

        rightBox.getChildren().addAll(new Label("Фоновий імпорт:"), importCsvBtn, progressBar, statusLabel);
        pane.setRight(rightBox);

        return pane;
    }

    private void startCsvImport() {
        Path csvPath = Path.of("sample-data", "listeners.csv");
        ImportListenersTask task = new ImportListenersTask(csvPath, client);
        progressBar.progressProperty().bind(task.progressProperty());
        statusLabel.textProperty().bind(task.messageProperty());

        task.setOnSucceeded(e -> refreshListeners());

        new Thread(task).start();
    }

    private void refreshListeners() {
        try {
            listenerTable.getItems().setAll(client.getListeners());
        } catch (Exception e) {
            statusLabel.setText("Помилка завантаження даних");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}