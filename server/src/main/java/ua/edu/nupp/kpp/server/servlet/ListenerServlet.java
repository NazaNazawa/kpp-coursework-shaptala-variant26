package ua.edu.nupp.kpp.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.nupp.kpp.server.dao.ListenerDao;
import ua.edu.nupp.kpp.server.model.Listener;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/listeners")
public class ListenerServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ListenerServlet.class.getName());
    private final ListenerDao listenerDao = new ListenerDao();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<Listener> listeners = listenerDao.getAllListeners();
        objectMapper.writeValue(resp.getWriter(), listeners);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        try {
            String action = req.getParameter("action");

            if ("delete".equals(action)) {
                Long id = Long.parseLong(req.getParameter("id"));

                listenerDao.deleteListener(id);

                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            String fullName = req.getParameter("full_name");
            String email = req.getParameter("email");
            String phone = req.getParameter("phone");

            if (fullName != null && email != null && phone != null) {
                Listener listener = new Listener(null, fullName, email, phone);
                listenerDao.addListener(listener);

                LOGGER.info("Успішно додано слухача: " + fullName);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Неповні дані слухача"
                );
            }
        } catch (Exception e) {
            LOGGER.warning("Помилка обробки слухача: " + e.getMessage());
            resp.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        }
    }
}