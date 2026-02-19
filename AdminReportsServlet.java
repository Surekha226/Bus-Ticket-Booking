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

// @WebServlet("/admin/reports")
public class AdminReportsServlet extends HttpServlet {
    private AdminDAO adminDAO = new AdminDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return;
        }
        
        String reportType = request.getParameter("type");
        if (reportType == null) reportType = "daily";
        
        List<Map<String, Object>> reportData = null;
        double totalRevenue = adminDAO.getTotalRevenue();
        int totalBookings = adminDAO.getTotalBookings();
        int totalUsers = adminDAO.getTotalUsers();
        
        switch(reportType) {
            case "daily":
                reportData = adminDAO.getDailyRevenue(30);
                break;
            case "monthly":
                reportData = adminDAO.getMonthlyRevenue(6);
                break;
            case "routes":
                reportData = adminDAO.getRouteRevenue();
                break;
            case "buses":
                reportData = adminDAO.getBusUtilization();
                break;
        }
        
        request.setAttribute("reportType", reportType);
        request.setAttribute("reportData", reportData);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalBookings", totalBookings);
        request.setAttribute("totalUsers", totalUsers);
        
        request.getRequestDispatcher("/admin/reports.jsp").forward(request, response);
    }
}
