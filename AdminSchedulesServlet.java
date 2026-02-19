package com.busbooking.servlet;

import com.busbooking.dao.AdminDAO;
import com.busbooking.model.Bus;
import com.busbooking.model.Route;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

// @WebServlet("/admin/schedules")
public class AdminSchedulesServlet extends HttpServlet {
    private AdminDAO adminDAO = new AdminDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        String action = request.getParameter("action");
        String id = request.getParameter("id");
        
        if ("delete".equals(action) && id != null) {
            adminDAO.deleteSchedule(Integer.parseInt(id));
            response.sendRedirect(request.getContextPath() + "/admin/schedules");
            return;
        }
        
        List<Bus> buses = adminDAO.getAllBuses();
        List<Route> routes = adminDAO.getAllRoutes();
        List<Map<String, Object>> schedules = adminDAO.getAllSchedules();
        
        request.setAttribute("buses", buses);
        request.setAttribute("routes", routes);
        request.setAttribute("schedules", schedules);
        request.getRequestDispatcher("/admin/schedules.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            int busId = Integer.parseInt(request.getParameter("busId"));
            int routeId = Integer.parseInt(request.getParameter("routeId"));
            String departure = request.getParameter("departureDate") + " " + request.getParameter("departureTime") + ":00";
            String arrival = request.getParameter("arrivalDate") + " " + request.getParameter("arrivalTime") + ":00";
            double fare = Double.parseDouble(request.getParameter("fare"));
            int seats = Integer.parseInt(request.getParameter("seats"));
            
            adminDAO.addSchedule(busId, routeId, departure, arrival, fare, seats);
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/schedules");
    }
}
