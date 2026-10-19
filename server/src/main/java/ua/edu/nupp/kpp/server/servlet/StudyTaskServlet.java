package ua.edu.nupp.kpp.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.nupp.kpp.server.dao.StudyTaskDao;
import ua.edu.nupp.kpp.server.model.StudyTask;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/tasks")
public class StudyTaskServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(StudyTaskServlet.class.getName());
    private final StudyTaskDao taskDao = new StudyTaskDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        List<StudyTask> tasks = taskDao.getAllTasks();
        objectMapper.writeValue(resp.getWriter(), tasks);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            Long enrollmentId = Long.parseLong(req.getParameter("enrollment_id"));
            String title = req.getParameter("title");
            int maxScore = Integer.parseInt(req.getParameter("max_score"));
            String dueDate = req.getParameter("due_date");

            if (title != null && dueDate != null) {
                StudyTask task = new StudyTask(null, enrollmentId, title, maxScore, dueDate);
                taskDao.addTask(task);
                LOGGER.info("Успішно додано навчальне завдання: " + title);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Неповні дані завдання");
            }
        } catch (Exception e) {
            LOGGER.warning("Помилка додавання завдання: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}