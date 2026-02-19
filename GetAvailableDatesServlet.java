package com.busbooking.servlet;

import com.busbooking.dao.BusDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet("/get-available-dates")
public class GetAvailableDatesServlet extends HttpServlet {
    private BusDAO busDAO = new BusDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String origin = request.getParameter("origin");
        String destination = request.getParameter("destination");
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        JsonArray datesArray = new JsonArray();
        
        if (origin != null && destination != null && !origin.isEmpty() && !destination.isEmpty()) {
            try {
                List<String> availableDates = busDAO.getAvailableDates(origin, destination);
                
                for (String date : availableDates) {
                    datesArray.add(date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("availableDates", datesArray);
        out.print(jsonResponse.toString());
    }
}