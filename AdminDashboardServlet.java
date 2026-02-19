package com.busbooking.servlet;

import com.busbooking.dao.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// @WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private AdminDAO adminDAO = new AdminDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        // Get statistics
        request.setAttribute("totalBuses", adminDAO.getTotalBuses());
        request.setAttribute("totalRoutes", adminDAO.getTotalRoutes());
        request.setAttribute("totalSchedules", adminDAO.getTotalSchedules());
        request.setAttribute("totalUsers", adminDAO.getTotalUsers());
        request.setAttribute("totalBookings", adminDAO.getTotalBookings());
        request.setAttribute("totalRevenue", adminDAO.getTotalRevenue());
        request.setAttribute("recentBookings", adminDAO.getRecentBookings(10));
        request.setAttribute("popularRoutes", adminDAO.getPopularRoutes(5));
        
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
}