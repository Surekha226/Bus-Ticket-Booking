package com.busbooking.servlet;

import com.busbooking.dao.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

// @WebServlet("/admin/bookings")
public class AdminBookingsServlet extends HttpServlet {
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
        
        if ("cancel".equals(action) && id != null) {
            adminDAO.cancelBooking(Integer.parseInt(id));
            response.sendRedirect(request.getContextPath() + "/admin/bookings");
            return;
        }
        
        List<Map<String, Object>> bookings = adminDAO.getAllBookings();
        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("/admin/bookings.jsp").forward(request, response);
    }
}
