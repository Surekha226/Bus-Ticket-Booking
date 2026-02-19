package com.busbooking.servlet;

import com.busbooking.dao.BookingDAO;
import com.busbooking.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/book")
public class BookServlet extends HttpServlet {
    private BookingDAO bookingDAO = new BookingDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String scheduleIdStr = request.getParameter("scheduleId");
        
        if (scheduleIdStr == null || scheduleIdStr.isEmpty()) {
            response.sendRedirect("dashboard.jsp?error=Invalid schedule");
            return;
        }
        
        int scheduleId = Integer.parseInt(scheduleIdStr);
        request.setAttribute("scheduleId", scheduleId);
        request.getRequestDispatcher("booking.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String scheduleIdStr = request.getParameter("scheduleId");
        String seatsStr = request.getParameter("seats");
        
        try {
            int scheduleId = Integer.parseInt(scheduleIdStr);
            int seats = Integer.parseInt(seatsStr);
            
            boolean success = bookingDAO.createBooking(user.getUserId(), scheduleId, seats);
            
            if (success) {
                response.sendRedirect("confirmation.jsp?success=true");
            } else {
                response.sendRedirect("booking.jsp?scheduleId=" + scheduleId + "&error=Booking failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?error=Invalid input");
        }
    }
}