<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.busbooking.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String scheduleId = request.getParameter("scheduleId");
    if (scheduleId == null) {
        response.sendRedirect("dashboard.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Book Tickets - Bus Booking</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }
        .navbar { background: rgba(255,255,255,0.1); padding: 1rem 5%; color: white; display: flex; justify-content: space-between; }
        .container { max-width: 500px; margin: 50px auto; padding: 20px; }
        .booking-card { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 10px 40px rgba(0,0,0,0.1); }
        h2 { text-align: center; margin-bottom: 30px; color: #333; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 5px; color: #555; font-weight: 500; }
        input { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 5px; font-size: 16px; }
        input:focus { outline: none; border-color: #667eea; }
        .btn { width: 100%; padding: 12px; background: #667eea; color: white; border: none; border-radius: 5px; font-size: 16px; cursor: pointer; }
        .btn:hover { background: #5a67d8; }
        .error { color: red; margin-bottom: 15px; text-align: center; }
        .info { background: #e8f4f8; padding: 15px; border-radius: 5px; margin-bottom: 20px; }
        .back-link { text-align: center; margin-top: 20px; }
        .back-link a { color: #667eea; text-decoration: none; }
    </style>
</head>
<body>
    <nav class="navbar">
        <div>🚌 Bus Booking System</div>
        <div>Welcome, <%= user.getFullName() %></div>
    </nav>
    
    <div class="container">
        <div class="booking-card">
            <h2>Book Tickets</h2>
            
            <% if(request.getParameter("error") != null) { %>
                <div class="error"><%= request.getParameter("error") %></div>
            <% } %>
            
            <div class="info">
                <p><strong>Schedule ID:</strong> <%= scheduleId %></p>
                <p><strong>Passenger:</strong> <%= user.getFullName() %></p>
                <p><strong>Email:</strong> <%= user.getEmail() %></p>
            </div>
            
            <form action="book" method="post">
                <input type="hidden" name="scheduleId" value="<%= scheduleId %>">
                <div class="form-group">
                    <label>Number of Seats</label>
                    <input type="number" name="seats" min="1" max="10" required>
                </div>
                <button type="submit" class="btn">Confirm Booking</button>
            </form>
            
            <div class="back-link">
                <a href="search-results.jsp">Back to Search Results</a>
            </div>
        </div>
    </div>
</body>
</html>