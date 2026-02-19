package com.busbooking.servlet;

import com.busbooking.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

@WebServlet("/testdb")
public class TestDBServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>Database Test</title></head><body>");
        out.println("<h1>Database Connection Test</h1>");
        
        try {
            Connection conn = DBConnection.getConnection();
            out.println("<p style='color:green'>✅ SUCCESS! Connected to MySQL database!</p>");
            out.println("<p>Database: busbooking</p>");
            out.println("<p>Connection Info: " + conn + "</p>");
            conn.close();
        } catch (Exception e) {
            out.println("<p style='color:red'>❌ FAILED! Cannot connect to MySQL!</p>");
            out.println("<p>Error: " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        }
        
        out.println("</body></html>");
    }
}