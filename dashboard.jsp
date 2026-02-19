<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/login");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - Bus Booking</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
               background: #f5f6fa; }
        
        .navbar { background: #2c3e50; padding: 1rem 5%; color: white; 
                  display: flex; justify-content: space-between; align-items: center; 
                  position: fixed; width: 100%; top: 0; z-index: 1000; }
        .logo { font-size: 24px; font-weight: bold; }
        .logo span { color: #3498db; }
        .admin-info { display: flex; align-items: center; gap: 20px; }
        .admin-info span { background: #3498db; padding: 5px 15px; border-radius: 20px; }
        .logout { color: white; text-decoration: none; background: #e74c3c; 
                  padding: 5px 15px; border-radius: 5px; transition: background 0.3s; }
        .logout:hover { background: #c0392b; }
        
        .sidebar { width: 250px; background: white; position: fixed; left: 0; 
                   top: 70px; bottom: 0; box-shadow: 2px 0 10px rgba(0,0,0,0.1); 
                   padding: 20px 0; overflow-y: auto; }
        .sidebar-menu { list-style: none; }
        .sidebar-menu li { padding: 12px 25px; border-left: 3px solid transparent; 
                           transition: all 0.3s; cursor: pointer; }
        .sidebar-menu li:hover { background: #f0f3f8; border-left-color: #3498db; }
        .sidebar-menu li.active { background: #f0f3f8; border-left-color: #3498db; 
                                   font-weight: bold; color: #3498db; }
        .sidebar-menu li i { margin-right: 10px; width: 20px; display: inline-block; }
        
        .main-content { margin-left: 270px; margin-right: 20px; 
                        margin-top: 90px; margin-bottom: 20px; }
        
        .stats-container { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); 
                           gap: 20px; margin-bottom: 30px; }
        .stat-card { background: white; padding: 25px; border-radius: 10px; 
                     box-shadow: 0 5px 15px rgba(0,0,0,0.1); 
                     transition: transform 0.3s; }
        .stat-card:hover { transform: translateY(-5px); }
        .stat-title { color: #7f8c8d; font-size: 14px; text-transform: uppercase; 
                      letter-spacing: 1px; margin-bottom: 10px; }
        .stat-value { font-size: 36px; font-weight: bold; color: #2c3e50; 
                      margin-bottom: 5px; }
        .stat-icon { float: right; font-size: 40px; opacity: 0.3; }
        
        .card { background: white; border-radius: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); 
                margin-bottom: 30px; overflow: hidden; }
        .card-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
                       color: white; padding: 15px 20px; font-size: 18px; font-weight: bold; }
        .card-body { padding: 20px; }
        
        table { width: 100%; border-collapse: collapse; }
        th { background: #f8f9fa; padding: 12px; text-align: left; font-weight: 600; 
             color: #2c3e50; border-bottom: 2px solid #dee2e6; }
        td { padding: 12px; border-bottom: 1px solid #dee2e6; }
        tr:hover { background: #f8f9fa; }
        
        .badge { padding: 5px 10px; border-radius: 20px; font-size: 12px; 
                 font-weight: 600; display: inline-block; }
        .badge-success { background: #d4edda; color: #155724; }
        .badge-warning { background: #fff3cd; color: #856404; }
        .badge-danger { background: #f8d7da; color: #721c24; }
        
        .charts-container { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        .chart-box { background: white; border-radius: 10px; padding: 20px; 
                     box-shadow: 0 5px 15px rgba(0,0,0,0.1); }
        .chart-title { font-size: 16px; font-weight: 600; color: #2c3e50; 
                       margin-bottom: 15px; padding-bottom: 10px; 
                       border-bottom: 1px solid #dee2e6; }
    </style>
</head>
<body>
    <nav class="navbar">
        <div class="logo">BusBooking <span>Admin</span></div>
        <div class="admin-info">
            <span>👑 <%= session.getAttribute("admin") %></span>
            <a href="logout" class="logout">Logout</a>
        </div>
    </nav>
    
    <div class="sidebar">
        <ul class="sidebar-menu">
            <li class="active" onclick="window.location.href='dashboard'">📊 Dashboard</li>
            <li onclick="window.location.href='buses'">🚌 Bus Management</li>
            <li onclick="window.location.href='routes'">🛣️ Route Management</li>
            <li onclick="window.location.href='schedules'">📅 Schedule Management</li>
            <li onclick="window.location.href='bookings'">🎫 Bookings</li>
            <li onclick="window.location.href='users'">👥 Users</li>
            <li onclick="window.location.href='reports'">📈 Reports</li>
        </ul>
    </div>
    
    <div class="main-content">
        <div class="stats-container">
            <div class="stat-card">
                <div class="stat-icon">🚌</div>
                <div class="stat-title">Total Buses</div>
                <div class="stat-value"><%= request.getAttribute("totalBuses") %></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">🛣️</div>
                <div class="stat-title">Total Routes</div>
                <div class="stat-value"><%= request.getAttribute("totalRoutes") %></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">📅</div>
                <div class="stat-title">Total Schedules</div>
                <div class="stat-value"><%= request.getAttribute("totalSchedules") %></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">👥</div>
                <div class="stat-title">Total Users</div>
                <div class="stat-value"><%= request.getAttribute("totalUsers") %></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">🎫</div>
                <div class="stat-title">Total Bookings</div>
                <div class="stat-value"><%= request.getAttribute("totalBookings") %></div>
            </div>
            <div class="stat-card">
                <div class="stat-icon">💰</div>
                <div class="stat-title">Total Revenue</div>
                <div class="stat-value">₹<%= request.getAttribute("totalRevenue") %></div>
            </div>
        </div>
        
        <div class="charts-container">
            <div class="chart-box">
                <div class="chart-title">Popular Routes</div>
                <table>
                    <tr>
                        <th>Route</th>
                        <th>Bookings</th>
                        <th>Revenue</th>
                    </tr>
                    <% 
                        List<Map<String, Object>> popularRoutes = 
                            (List<Map<String, Object>>) request.getAttribute("popularRoutes");
                        if (popularRoutes != null) {
                            for (Map<String, Object> route : popularRoutes) { 
                    %>
                    <tr>
                        <td><%= route.get("origin") %> → <%= route.get("destination") %></td>
                        <td><span class="badge badge-success"><%= route.get("count") %></span></td>
                        <td>₹<%= route.get("revenue") %></td>
                    </tr>
                    <% 
                            }
                        } 
                    %>
                </table>
            </div>
            
            <div class="chart-box">
                <div class="chart-title">Recent Bookings</div>
                <table>
                    <tr>
                        <th>User</th>
                        <th>Route</th>
                        <th>Amount</th>
                    </tr>
                    <% 
                        List<Map<String, Object>> recentBookings = 
                            (List<Map<String, Object>>) request.getAttribute("recentBookings");
                        if (recentBookings != null) {
                            for (Map<String, Object> booking : recentBookings) { 
                    %>
                    <tr>
                        <td><%= booking.get("user") %></td>
                        <td><%= booking.get("route") %></td>
                        <td>₹<%= booking.get("amount") %></td>
                    </tr>
                    <% 
                            }
                        } 
                    %>
                </table>
            </div>
        </div>
    </div>
</body>
</html>