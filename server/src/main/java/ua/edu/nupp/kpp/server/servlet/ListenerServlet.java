package ua.edu.nupp.kpp.server.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.nupp.kpp.server.dao.ListenerDao;
import ua.edu.nupp.kpp.server.model.Listener;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/listeners")
public class ListenerServlet extends HttpServlet {
    private final ListenerDao listenerDao = new ListenerDao();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            if ("delete".equals(req.getParameter("action"))) {
                listenerDao.deleteListener(Long.parseLong(req.getParameter("id")));
                resp.getWriter().write("{\"status\":\"success\"}");
                return;
            }
            mapper.writeValue(resp.getWriter(), listenerDao.getAllListeners());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            mapper.writeValue(resp.getWriter(), err);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        try {
            String idStr = req.getParameter("id");
            Listener listener = new Listener();
            listener.setFullName(req.getParameter("full_name"));
            listener.setEmail(req.getParameter("email"));
            listener.setPhone(req.getParameter("phone"));

            if (idStr != null && !idStr.isEmpty()) {
                listener.setId(Long.parseLong(idStr));
            } else {
                listenerDao.addListener(listener);
            }
            resp.getWriter().write("{\"status\":\"success\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            mapper.writeValue(resp.getWriter(), err);
        }
    }
}