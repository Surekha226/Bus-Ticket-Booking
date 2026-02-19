<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.busbooking.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Booking Confirmed - Bus Booking</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }
        .navbar { background: rgba(255,255,255,0.1); padding: 1rem 5%; color: white; display: flex; justify-content: space-between; }
        .container { max-width: 600px; margin: 50px auto; padding: 20px; }
        .confirmation-card { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 10px 40px rgba(0,0,0,0.1); text-align: center; }
        h2 { color: #4CAF50; margin-bottom: 20px; }
        .success-icon { font-size: 80px; color: #4CAF50; margin-bottom: 20px; }
        .btn { display: inline-block; padding: 12px 30px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }
        .btn:hover { background: #5a67d8; }
        p { margin-bottom: 10px; color: #555; }
    </style>
</head>
<body>
    <nav class="navbar">
        <div>🚌 Bus Booking System</div>
        <div>Welcome, <%= user.getFullName() %></div>
    </nav>
    
    <div class="container">
        <div class="confirmation-card">
            <div class="success-icon">✅</div>
            <h2>Booking Confirmed!</h2>
            <p>Your tickets have been booked successfully.</p>
            <p>A confirmation email has been sent to <%= user.getEmail() %></p>
            <p>Thank you for choosing BusBooking!</p>
            <a href="dashboard.jsp" class="btn">Back to Dashboard</a>
        </div>
    </div>
</body>
</html>