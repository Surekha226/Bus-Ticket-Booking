package com.busbooking.servlet;

import com.busbooking.dao.AdminDAO;
import com.busbooking.model.Route;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

// @WebServlet("/admin/routes")
public class AdminRoutesServlet extends HttpServlet {
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
            adminDAO.deleteRoute(Integer.parseInt(id));
            response.sendRedirect(request.getContextPath() + "/admin/routes");
            return;
        }
        
        if ("edit".equals(action) && id != null) {
            Route route = adminDAO.getRouteById(Integer.parseInt(id));
            request.setAttribute("editRoute", route);
        }
        
        List<Route> routes = adminDAO.getAllRoutes();
        request.setAttribute("routes", routes);
        request.getRequestDispatcher("/admin/routes.jsp").forward(request, response);
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
            Route route = new Route();
            route.setOrigin(request.getParameter("origin"));
            route.setDestination(request.getParameter("destination"));
            route.setDistance(Integer.parseInt(request.getParameter("distance")));
            route.setDuration(Integer.parseInt(request.getParameter("duration")));
            adminDAO.addRoute(route);
        } else if ("update".equals(action)) {
            Route route = new Route();
            route.setRouteId(Integer.parseInt(request.getParameter("routeId")));
            route.setOrigin(request.getParameter("origin"));
            route.setDestination(request.getParameter("destination"));
            route.setDistance(Integer.parseInt(request.getParameter("distance")));
            route.setDuration(Integer.parseInt(request.getParameter("duration")));
            adminDAO.updateRoute(route);
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/routes");
    }
}