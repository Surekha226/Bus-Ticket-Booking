<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - Bus Booking</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); height: 100vh; display: flex; justify-content: center; align-items: center; }
        .container { background: white; padding: 40px; border-radius: 10px; box-shadow: 0 10px 40px rgba(0,0,0,0.1); width: 400px; }
        h2 { text-align: center; margin-bottom: 30px; color: #333; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 5px; color: #555; font-weight: 500; }
        input { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 5px; font-size: 16px; }
        input:focus { outline: none; border-color: #667eea; }
        .btn { width: 100%; padding: 12px; background: #667eea; color: white; border: none; border-radius: 5px; font-size: 16px; cursor: pointer; }
        .btn:hover { background: #5a67d8; }
        .error { color: red; margin-bottom: 15px; text-align: center; }
        .success { color: green; margin-bottom: 15px; text-align: center; }
        .login-link { text-align: center; margin-top: 20px; }
        .login-link a { color: #667eea; text-decoration: none; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Create Account</h2>
        
        <% if(request.getParameter("error") != null) { %>
            <div class="error"><%= request.getParameter("error") %></div>
        <% } %>
        
        <% if(request.getParameter("success") != null) { %>
            <div class="success"><%= request.getParameter("success") %></div>
        <% } %>
        
        <form action="register" method="post">
            <div class="form-group">
                <label>Full Name</label>
                <input type="text" name="fullName" required>
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" required>
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" required>
            </div>
            <div class="form-group">
                <label>Confirm Password</label>
                <input type="password" name="confirmPassword" required>
            </div>
            <div class="form-group">
                <label>Phone Number</label>
                <input type="tel" name="phone" required>
            </div>
            <button type="submit" class="btn">Register</button>
        </form>
        <div class="login-link">
            Already have an account? <a href="login.jsp">Login here</a>
        </div>
    </div>
</body>
</html>