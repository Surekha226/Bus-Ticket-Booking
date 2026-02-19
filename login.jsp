<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Login - Bus Booking</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
               background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%); 
               height: 100vh; display: flex; justify-content: center; align-items: center; }
        .container { background: white; padding: 40px; border-radius: 10px; 
                     box-shadow: 0 10px 40px rgba(0,0,0,0.2); width: 400px; }
        h2 { text-align: center; margin-bottom: 30px; color: #333; }
        h2 i { color: #3498db; }
        .form-group { margin-bottom: 20px; }
        label { display: block; margin-bottom: 5px; color: #555; font-weight: 500; }
        input { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 5px; 
                font-size: 16px; transition: border-color 0.3s; }
        input:focus { outline: none; border-color: #3498db; }
        .btn { width: 100%; padding: 12px; background: #3498db; color: white; 
               border: none; border-radius: 5px; font-size: 16px; cursor: pointer; 
               transition: background 0.3s; }
        .btn:hover { background: #2980b9; }
        .error { color: #e74c3c; margin-bottom: 15px; text-align: center; 
                 padding: 10px; background: #fadbd8; border-radius: 5px; }
        .back-link { text-align: center; margin-top: 20px; }
        .back-link a { color: #3498db; text-decoration: none; }
        .back-link a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="container">
        <h2><i>👑</i> Admin Login</h2>
        
        <% if(request.getParameter("error") != null) { %>
            <div class="error">Invalid username or password</div>
        <% } %>
        
        <form action="login" method="post">
            <div class="form-group">
                <label>Username</label>
                <input type="text" name="username" required placeholder="Enter admin username">
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" required placeholder="Enter admin password">
            </div>
            <button type="submit" class="btn">Login to Admin Panel</button>
        </form>
        <div class="back-link">
            <a href="${pageContext.request.contextPath}/">← Back to Home</a>
        </div>
    </div>
</body>
</html>