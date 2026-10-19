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

        try {
            String action = req.getParameter("action");
            if ("delete".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                taskDao.deleteTask(id);
                LOGGER.info("Успішно видалено завдання ID: " + id);
            }
            List<StudyTask> tasks = taskDao.getAllTasks();
            objectMapper.writeValue(resp.getWriter(), tasks);
        } catch (Exception e) {
            LOGGER.warning("Помилка в StudyTaskServlet GET: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        try {
            String action = req.getParameter("action");
            Long enrollmentId = Long.parseLong(req.getParameter("enrollment_id"));
            String title = req.getParameter("title");
            int maxScore = Integer.parseInt(req.getParameter("max_score"));
            String dueDate = req.getParameter("due_date");

            if ("update".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                StudyTask task = new StudyTask(id, enrollmentId, title, maxScore, dueDate);
                taskDao.updateTask(task);
                LOGGER.info("Успішно оновлено завдання: " + title);
            } else {
                StudyTask task = new StudyTask(null, enrollmentId, title, maxScore, dueDate);
                taskDao.addTask(task);
                LOGGER.info("Успішно додано завдання: " + title);
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            LOGGER.warning("Помилка обробки завдання: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}