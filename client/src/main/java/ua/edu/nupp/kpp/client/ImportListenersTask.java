package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;
import ua.edu.nupp.kpp.client.Model.Listener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ImportListenersTask extends Task<Integer> {
    private final Path csvPath;
    private final HttpCourseClient client;

    public ImportListenersTask(Path csvPath, HttpCourseClient client) {
        this.csvPath = csvPath;
        this.client = client;
    }

    @Override
    protected Integer call() throws Exception {
        if (!Files.exists(csvPath)) {
            updateMessage("Файл не знайдено!");
            return 0;
        }

        List<String> lines = Files.readAllLines(csvPath);
        int importedCount = 0;

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(";");
            if (parts.length >= 3) {
                Listener listener = new Listener(parts[0].trim(), parts[1].trim(), parts[2].trim());
                client.addListener(listener);
                importedCount++;
            }
            updateProgress(i, lines.size() - 1);
            updateMessage("Імпортовано: " + importedCount);
        }
        updateMessage("Готово. Імпортовано: " + importedCount);
        return importedCount;
    }
}