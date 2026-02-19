package com.busbooking.servlet;

import com.busbooking.dao.AdminDAO;
import com.busbooking.model.Bus;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

// @WebServlet("/admin/buses")
public class AdminBusesServlet extends HttpServlet {
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
            adminDAO.deleteBus(Integer.parseInt(id));
            response.sendRedirect(request.getContextPath() + "/admin/buses");
            return;
        }
        
        if ("edit".equals(action) && id != null) {
            Bus bus = adminDAO.getBusById(Integer.parseInt(id));
            request.setAttribute("editBus", bus);
        }
        
        List<Bus> buses = adminDAO.getAllBuses();
        request.setAttribute("buses", buses);
        request.getRequestDispatcher("/admin/buses.jsp").forward(request, response);
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
            Bus bus = new Bus();
            bus.setBusName(request.getParameter("busName"));
            bus.setBusType(request.getParameter("busType"));
            bus.setTotalSeats(Integer.parseInt(request.getParameter("totalSeats")));
            bus.setAmenities(request.getParameter("amenities"));
            adminDAO.addBus(bus);
        } else if ("update".equals(action)) {
            Bus bus = new Bus();
            bus.setBusId(Integer.parseInt(request.getParameter("busId")));
            bus.setBusName(request.getParameter("busName"));
            bus.setBusType(request.getParameter("busType"));
            bus.setTotalSeats(Integer.parseInt(request.getParameter("totalSeats")));
            bus.setAmenities(request.getParameter("amenities"));
            adminDAO.updateBus(bus);
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/buses");
    }
}