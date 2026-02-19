package com.busbooking.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/admin/simple-test")
public class SimpleAdminTest extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>Simple Admin Test</title></head><body>");
        out.println("<h1 style='color:green'>✅ Simple Admin Test Servlet Working!</h1>");
        out.println("<p>Servlet Path: " + request.getServletPath() + "</p>");
        out.println("<p>Context Path: " + request.getContextPath() + "</p>");
        out.println("<p>Request URL: " + request.getRequestURL() + "</p>");
        out.println("<p>If you see this, servlets are working in Tomcat 10 with Jakarta imports!</p>");
        out.println("</body></html>");
    }
}