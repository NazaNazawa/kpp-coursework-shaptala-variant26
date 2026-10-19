package ua.edu.nupp.kpp.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.nupp.kpp.server.dao.EnrollmentDao;
import ua.edu.nupp.kpp.server.model.Enrollment;
import ua.edu.nupp.kpp.server.model.EnrollmentState;
import ua.edu.nupp.kpp.server.service.EnrollmentService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/enrollments")
public class EnrollmentServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(EnrollmentServlet.class.getName());
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();
    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        List<Enrollment> enrollments = enrollmentDao.getAllEnrollments();
        objectMapper.writeValue(resp.getWriter(), enrollments);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("transition".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                EnrollmentState targetState = EnrollmentState.valueOf(req.getParameter("target_state"));

                List<Enrollment> enrollments = enrollmentDao.getAllEnrollments();
                Enrollment target = enrollments.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);

                if (target != null) {
                    enrollmentService.changeState(target, targetState);
                    enrollmentDao.updateEnrollment(target);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Зарахування не знайдено");
                }
            } else {
                Long courseId = Long.parseLong(req.getParameter("course_id"));
                Long listenerId = Long.parseLong(req.getParameter("listener_id"));
                double f1 = Double.parseDouble(req.getParameter("f1"));
                double f2 = Double.parseDouble(req.getParameter("f2"));
                double f3 = Double.parseDouble(req.getParameter("f3"));

                double integralScore = enrollmentService.calculateIntegralScore(f1, f2, f3);

                Enrollment enrollment = new Enrollment(null, courseId, listenerId, EnrollmentState.REQUESTED.name(), f1, f2, f3, integralScore);
                LOGGER.info("Опрацьовано створення зарахування для слухача ID: " + listenerId);
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            LOGGER.warning("Помилка в EnrollmentServlet: " + e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}