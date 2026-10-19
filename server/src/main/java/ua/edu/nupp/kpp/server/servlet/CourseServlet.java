package ua.edu.nupp.kpp.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.nupp.kpp.server.dao.CourseDao;
import ua.edu.nupp.kpp.server.model.Course;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/courses")
public class CourseServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CourseServlet.class.getName());
    private final CourseDao courseDao = new CourseDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<Course> courses = courseDao.getAllCourses();
        objectMapper.writeValue(resp.getWriter(), courses);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        try {
            String action = req.getParameter("action");

            if ("delete".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));

                courseDao.deleteCourse(id);

                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            String title = req.getParameter("title");
            String description = req.getParameter("description");
            int credits = Integer.parseInt(req.getParameter("credits"));

            if (title != null && description != null) {
                Course course = new Course(null, title, description, credits);
                courseDao.addCourse(course);

                LOGGER.info("Успішно додано курс: " + title);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Неповні дані курсу"
                );
            }
        } catch (Exception e) {
            LOGGER.warning("Помилка обробки курсу: " + e.getMessage());
            resp.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        }
    }
}