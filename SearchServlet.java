package com.busbooking.servlet;

import com.busbooking.dao.BusDAO;
import com.busbooking.model.Schedule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private BusDAO busDAO = new BusDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String origin = request.getParameter("origin");
        String destination = request.getParameter("destination");
        String travelDateStr = request.getParameter("travelDate");
        
        if (origin == null || destination == null || travelDateStr == null) {
            response.sendRedirect("dashboard.jsp");
            return;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date travelDate = sdf.parse(travelDateStr);
            
            List<Schedule> schedules = busDAO.searchBuses(origin, destination, travelDate);
            request.setAttribute("schedules", schedules);
            request.setAttribute("origin", origin);
            request.setAttribute("destination", destination);
            request.setAttribute("travelDate", travelDateStr);
            request.getRequestDispatcher("search-results.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?error=Search failed");
        }
    }
}