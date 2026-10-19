package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.nupp.kpp.client.Model.Listener;

import java.net.URI;
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

    public List<Listener> getListeners() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/listeners"))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Arrays.asList(objectMapper.readValue(response.body(), Listener[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void addListener(Listener listener) {
        try {
            String formData = "full_name=" + URLEncoder.encode(listener.getFullName(), StandardCharsets.UTF_8) +
                    "&email=" + URLEncoder.encode(listener.getEmail(), StandardCharsets.UTF_8) +
                    "&phone=" + URLEncoder.encode(listener.getPhone(), StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/listeners"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}