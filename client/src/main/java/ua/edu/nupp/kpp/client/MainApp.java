package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ua.edu.nupp.kpp.client.Model.Course;
import ua.edu.nupp.kpp.client.Model.Enrollment;
import ua.edu.nupp.kpp.client.Model.Listener;
import ua.edu.nupp.kpp.client.Model.StudyTask;

import java.nio.file.Path;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());
    private final HttpCourseClient client = new HttpCourseClient();

    private final TableView<Course> courseTable = new TableView<>();
    private final TableView<Listener> listenerTable = new TableView<>();
    private final TableView<Enrollment> enrollmentTable = new TableView<>();
    private final TableView<StudyTask> taskTable = new TableView<>();

    @Override
    public void start(Stage stage) {
        enableMultiSelect(courseTable);
        enableMultiSelect(listenerTable);
        enableMultiSelect(enrollmentTable);
        enableMultiSelect(taskTable);

        TabPane tabPane = new TabPane(
                new Tab("Курси", createCoursePane()),
                new Tab("Слухачі", createListenerPane()),
                new Tab("Зарахування", createEnrollmentPane()),
                new Tab("Навчальні завдання", createTaskPane())
        );
        tabPane.getTabs().forEach(t -> t.setClosable(false));

        stage.setScene(new Scene(tabPane, 1050, 650));
        stage.setTitle("Керування навчальними курсами — Варіант 26");
        stage.show();

        refreshAll();
    }

    private void enableMultiSelect(TableView<?> table) {
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private Pane createCoursePane() {
        courseTable.getColumns().setAll(
                col("ID", "id"),
                col("Назва", "title"),
                col("Опис", "description"),
                col("Кредити", "credits")
        );

        TextField title = new TextField();
        TextField description = new TextField();
        TextField credits = new TextField();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            client.addCourse(new Course(null, title.getText(), description.getText(), Integer.parseInt(credits.getText())));
            refreshCourses();
        }));

        update.setOnAction(e -> safe(() -> {
            Course c = courseTable.getSelectionModel().getSelectedItem();
            if (c == null) throw new IllegalArgumentException("Оберіть курс для редагування!");
            c.setTitle(title.getText());
            c.setDescription(description.getText());
            c.setCredits(Integer.parseInt(credits.getText()));
            client.updateCourse(c);
            refreshCourses();
        }));

        delete.setOnAction(e -> safe(() -> {
            var selectedItems = courseTable.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) throw new IllegalArgumentException("Оберіть хоча б один курс для видалення!");
            for (Course c : selectedItems) {
                client.deleteCourse(c.getId());
            }
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshCourses());

        courseTable.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null) {
                title.setText(s.getTitle());
                description.setText(s.getDescription());
                credits.setText(String.valueOf(s.getCredits()));
            }
        });

        VBox form = new VBox(8,
                new Label("Назва курсу"), title,
                new Label("Опис"), description,
                new Label("Кредити ECTS"), credits,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(courseTable, form);
    }

    private Pane createListenerPane() {
        listenerTable.getColumns().setAll(
                col("ID", "id"),
                col("ПІБ", "fullName"),
                col("Email", "email"),
                col("Телефон", "phone")
        );

        TextField name = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();

        ProgressBar progressBar = new ProgressBar(0);
        Label statusLabel = new Label("CSV не запущено");

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");
        Button importCsv = new Button("Імпортувати CSV");

        add.setOnAction(e -> safe(() -> {
            client.addListener(new Listener(name.getText(), email.getText(), phone.getText()));
            refreshListeners();
        }));

        update.setOnAction(e -> safe(() -> {
            Listener l = listenerTable.getSelectionModel().getSelectedItem();
            if (l == null) throw new IllegalArgumentException("Оберіть слухача для редагування!");
            l.setFullName(name.getText());
            l.setEmail(email.getText());
            l.setPhone(phone.getText());
            client.updateListener(l);
            refreshListeners();
        }));

        delete.setOnAction(e -> safe(() -> {
            var selectedItems = listenerTable.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) throw new IllegalArgumentException("Оберіть хоча б одного слухача для видалення!");
            for (Listener l : selectedItems) {
                client.deleteListener(l.getId());
            }
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshListeners());

        importCsv.setOnAction(e -> {
            Path csvPath = Path.of("sample-data", "listeners.csv");
            ImportListenersTask task = new ImportListenersTask(csvPath, client);
            progressBar.progressProperty().bind(task.progressProperty());
            statusLabel.textProperty().bind(task.messageProperty());

            task.setOnSucceeded(x -> {
                progressBar.progressProperty().unbind();
                statusLabel.textProperty().unbind();
                statusLabel.setText("Готово. Імпортовано: " + task.getValue());
                refreshListeners();
            });

            task.setOnFailed(x -> {
                progressBar.progressProperty().unbind();
                statusLabel.textProperty().unbind();
                showError(task.getException());
            });

            new Thread(task).start();
        });

        listenerTable.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null) {
                name.setText(s.getFullName());
                email.setText(s.getEmail());
                phone.setText(s.getPhone());
            }
        });

        VBox form = new VBox(8,
                new Label("ПІБ"), name,
                new Label("Email"), email,
                new Label("Телефон"), phone,
                new HBox(8, add, update, delete, refresh),
                new Separator(),
                new Label("Фоновий CSV-імпорт:"), importCsv, progressBar, statusLabel
        );
        return layout(listenerTable, form);
    }

    private Pane createEnrollmentPane() {
        enrollmentTable.getColumns().setAll(
                col("ID", "id"),
                col("Course ID", "courseId"),
                col("Listener ID", "listenerId"),
                col("Стан", "state"),
                col("F1 (Завдання)", "f1"),
                col("F2 (Тести)", "f2"),
                col("F3 (Активність)", "f3"),
                col("Інтегральний балл (I)", "integralScore")
        );

        TextField courseId = new TextField();
        TextField listenerId = new TextField();
        TextField f1 = new TextField("80");
        TextField f2 = new TextField("85");
        TextField f3 = new TextField("90");

        ComboBox<String> stateBox = new ComboBox<>(FXCollections.observableArrayList(
                "REQUESTED", "APPROVED", "ACTIVE", "COMPLETED"
        ));

        Button add = new Button("Створити");
        Button changeState = new Button("Змінити стан");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            client.addEnrollment(
                    Long.parseLong(courseId.getText()),
                    Long.parseLong(listenerId.getText()),
                    Double.parseDouble(f1.getText()),
                    Double.parseDouble(f2.getText()),
                    Double.parseDouble(f3.getText())
            );
            refreshEnrollments();
        }));

        changeState.setOnAction(e -> safe(() -> {
            Enrollment sel = enrollmentTable.getSelectionModel().getSelectedItem();
            if (sel == null) throw new IllegalArgumentException("Оберіть зарахування!");
            client.changeEnrollmentState(sel.getId(), stateBox.getValue());
            refreshEnrollments();
        }));

        delete.setOnAction(e -> safe(() -> {
            var selectedItems = enrollmentTable.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) throw new IllegalArgumentException("Оберіть хоча б одне зарахування для видалення!");
            for (Enrollment en : selectedItems) {
                client.deleteEnrollment(en.getId());
            }
            refreshAll();
        }));

        refresh.setOnAction(e -> refreshEnrollments());

        enrollmentTable.getSelectionModel().selectedItemProperty().addListener((o, a, v) -> {
            if (v != null) {
                courseId.setText(String.valueOf(v.getCourseId()));
                listenerId.setText(String.valueOf(v.getListenerId()));
                f1.setText(String.valueOf(v.getF1()));
                f2.setText(String.valueOf(v.getF2()));
                f3.setText(String.valueOf(v.getF3()));
                stateBox.setValue(v.getState());
            }
        });

        VBox form = new VBox(8,
                new Label("ID курсу"), courseId,
                new Label("ID слухача"), listenerId,
                new Label("F1 (Виконання) 0-100"), f1,
                new Label("F2 (Результати) 0-100"), f2,
                new Label("F3 (Активність) 0-100"), f3,
                new HBox(8, add, delete, refresh),
                new Separator(),
                new Label("Життєвий цикл (Новий стан):"), stateBox, changeState
        );
        return layout(enrollmentTable, form);
    }

    private Pane createTaskPane() {
        taskTable.getColumns().setAll(
                col("ID", "id"),
                col("Enrollment ID", "enrollmentId"),
                col("Назва", "title"),
                col("Макс. балл", "maxScore"),
                col("Дедлайн", "dueDate")
        );

        TextField enrollmentId = new TextField();
        TextField title = new TextField();
        TextField maxScore = new TextField();
        TextField dueDate = new TextField("2026-11-01");

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            client.addStudyTask(new StudyTask(
                    null,
                    Long.parseLong(enrollmentId.getText()),
                    title.getText(),
                    Integer.parseInt(maxScore.getText()),
                    dueDate.getText()
            ));
            refreshTasks();
        }));

        update.setOnAction(e -> safe(() -> {
            StudyTask t = taskTable.getSelectionModel().getSelectedItem();
            if (t == null) throw new IllegalArgumentException("Оберіть завдання для редагування!");
            t.setEnrollmentId(Long.parseLong(enrollmentId.getText()));
            t.setTitle(title.getText());
            t.setMaxScore(Integer.parseInt(maxScore.getText()));
            t.setDueDate(dueDate.getText());
            client.updateStudyTask(t);
            refreshTasks();
        }));

        delete.setOnAction(e -> safe(() -> {
            var selectedItems = taskTable.getSelectionModel().getSelectedItems();
            if (selectedItems.isEmpty()) throw new IllegalArgumentException("Оберіть хоча б одне завдання для видалення!");
            for (StudyTask t : selectedItems) {
                client.deleteStudyTask(t.getId());
            }
            refreshTasks();
        }));

        refresh.setOnAction(e -> refreshTasks());

        taskTable.getSelectionModel().selectedItemProperty().addListener((o, a, t) -> {
            if (t != null) {
                enrollmentId.setText(String.valueOf(t.getEnrollmentId()));
                title.setText(t.getTitle());
                maxScore.setText(String.valueOf(t.getMaxScore()));
                dueDate.setText(t.getDueDate());
            }
        });

        VBox form = new VBox(8,
                new Label("ID Зарахування"), enrollmentId,
                new Label("Назва завдання"), title,
                new Label("Макс. балл"), maxScore,
                new Label("Дедлайн (YYYY-MM-DD)"), dueDate,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(taskTable, form);
    }

    private void refreshAll() {
        refreshCourses();
        refreshListeners();
        refreshEnrollments();
        refreshTasks();
    }

    private void refreshCourses() { safe(() -> courseTable.setItems(FXCollections.observableArrayList(client.getCourses()))); }
    private void refreshListeners() { safe(() -> listenerTable.setItems(FXCollections.observableArrayList(client.getListeners()))); }
    private void refreshEnrollments() { safe(() -> enrollmentTable.setItems(FXCollections.observableArrayList(client.getEnrollments()))); }
    private void refreshTasks() { safe(() -> taskTable.setItems(FXCollections.observableArrayList(client.getTasks()))); }

    private void safe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            showError(e);
        }
    }

    private void showError(Throwable e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage() == null ? "Помилка виконання" : e.getMessage(), ButtonType.OK);
        alert.showAndWait();
    }

    private Pane layout(TableView<?> table, Pane form) {
        SplitPane splitPane = new SplitPane(table, form);
        splitPane.setDividerPositions(0.68);
        BorderPane root = new BorderPane(splitPane);
        root.setPadding(new Insets(10));
        return root;
    }

    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>(property));
        return column;
    }

    public static void main(String[] args) {
        launch(args);
    }
}