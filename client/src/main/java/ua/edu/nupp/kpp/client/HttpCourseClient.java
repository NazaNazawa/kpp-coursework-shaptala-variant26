package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.nupp.kpp.client.Model.Course;
import ua.edu.nupp.kpp.client.Model.Enrollment;
import ua.edu.nupp.kpp.client.Model.Listener;
import ua.edu.nupp.kpp.client.Model.StudyTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class HttpCourseClient {
    private static final String BASE_URL = "http://localhost:8080/server";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Course> getCourses() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/courses"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            return Arrays.asList(
                    objectMapper.readValue(response.body(), Course[].class)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void addCourse(Course course) {
        String formData = "title=" + enc(course.getTitle()) +
                "&description=" + enc(course.getDescription()) +
                "&credits=" + course.getCredits();

        post("/courses", formData);
    }

    public void updateCourse(Course course) {
        String formData = "action=update&id=" + course.getId() +
                "&title=" + enc(course.getTitle()) +
                "&description=" + enc(course.getDescription()) +
                "&credits=" + course.getCredits();

        post("/courses", formData);
    }

    public void deleteCourse(Long id) {
        post("/courses", "action=delete&id=" + id);
    }

    public List<Listener> getListeners() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/listeners"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            return Arrays.asList(
                    objectMapper.readValue(response.body(), Listener[].class)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void addListener(Listener listener) {
        String formData = "full_name=" + enc(listener.getFullName()) +
                "&email=" + enc(listener.getEmail()) +
                "&phone=" + enc(listener.getPhone());

        post("/listeners", formData);
    }

    public void updateListener(Listener listener) {
        String formData = "action=update&id=" + listener.getId() +
                "&full_name=" + enc(listener.getFullName()) +
                "&email=" + enc(listener.getEmail()) +
                "&phone=" + enc(listener.getPhone());

        post("/listeners", formData);
    }

    // Зроблено 1 в 1 як у робочому проєкті через HttpURLConnection та GET
    public void deleteListener(Long id) {
        try {
            URL url = new URL(BASE_URL + "/listeners?action=delete&id=" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Enrollment> getEnrollments() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/enrollments"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            return Arrays.asList(
                    objectMapper.readValue(response.body(), Enrollment[].class)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void addEnrollment(
            Long courseId,
            Long listenerId,
            double f1,
            double f2,
            double f3
    ) {
        String formData = "course_id=" + courseId +
                "&listener_id=" + listenerId +
                "&f1=" + f1 +
                "&f2=" + f2 +
                "&f3=" + f3;

        post("/enrollments", formData);
    }

    public void changeEnrollmentState(Long id, String targetState) {
        String formData = "action=transition&id=" + id +
                "&target_state=" + targetState;

        post("/enrollments", formData);
    }

    public void deleteEnrollment(Long id) {
        post("/enrollments", "action=delete&id=" + id);
    }

    public List<StudyTask> getTasks() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/tasks"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            return Arrays.asList(
                    objectMapper.readValue(response.body(), StudyTask[].class)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void addStudyTask(StudyTask task) {
        String formData = "enrollment_id=" + task.getEnrollmentId() +
                "&title=" + enc(task.getTitle()) +
                "&max_score=" + task.getMaxScore() +
                "&due_date=" + enc(task.getDueDate());

        post("/tasks", formData);
    }

    public void updateStudyTask(StudyTask task) {
        String formData = "action=update&id=" + task.getId() +
                "&enrollment_id=" + task.getEnrollmentId() +
                "&title=" + enc(task.getTitle()) +
                "&max_score=" + task.getMaxScore() +
                "&due_date=" + enc(task.getDueDate());

        post("/tasks", formData);
    }

    public void deleteStudyTask(Long id) {
        post("/tasks", "action=delete&id=" + id);
    }

    private void post(String path, String formData) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .header(
                            "Content-Type",
                            "application/x-www-form-urlencoded; charset=UTF-8"
                    )
                    .POST(
                            HttpRequest.BodyPublishers.ofString(
                                    formData, StandardCharsets.UTF_8
                            )
                    )
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 400) {
                throw new RuntimeException(
                        "Сервер повернув помилку: " + response.body()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String enc(String value) {
        return URLEncoder.encode(
                value == null ? "" : value,
                StandardCharsets.UTF_8
        );
    }
}