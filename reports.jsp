<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, java.text.SimpleDateFormat" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/login");
        return;
    }
    String reportType = (String) request.getAttribute("reportType");
    List<Map<String, Object>> reportData = (List<Map<String, Object>>) request.getAttribute("reportData");
    Double totalRevenue = (Double) request.getAttribute("totalRevenue");
    Integer totalBookings = (Integer) request.getAttribute("totalBookings");
    Integer totalUsers = (Integer) request.getAttribute("totalUsers");
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reports - Admin</title>
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
                  padding: 5px 15px; border-radius: 5px; }
        
        .sidebar { width: 250px; background: white; position: fixed; left: 0; 
                   top: 70px; bottom: 0; box-shadow: 2px 0 10px rgba(0,0,0,0.1); 
                   padding: 20px 0; overflow-y: auto; }
        .sidebar-menu { list-style: none; }
        .sidebar-menu li { padding: 12px 25px; border-left: 3px solid transparent; 
                           transition: all 0.3s; cursor: pointer; }
        .sidebar-menu li:hover { background: #f0f3f8; border-left-color: #3498db; }
        .sidebar-menu li.active { background: #f0f3f8; border-left-color: #3498db; 
                                   font-weight: bold; color: #3498db; }
        
        .main-content { margin-left: 270px; margin-right: 20px; 
                        margin-top: 90px; margin-bottom: 20px; }
        
        .card { background: white; border-radius: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); 
                margin-bottom: 30px; overflow: hidden; }
        .card-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
                       color: white; padding: 15px 20px; font-size: 18px; font-weight: bold; 
                       display: flex; justify-content: space-between; align-items: center; }
        .card-body { padding: 20px; }
        
        .stats-row { display: flex; gap: 20px; margin-bottom: 30px; }
        .stat-item { background: white; padding: 20px; border-radius: 10px; 
                     box-shadow: 0 5px 15px rgba(0,0,0,0.1); flex: 1; 
                     transition: transform 0.3s; }
        .stat-item:hover { transform: translateY(-5px); }
        .stat-label { color: #7f8c8d; font-size: 14px; text-transform: uppercase; 
                      letter-spacing: 1px; margin-bottom: 10px; }
        .stat-value { font-size: 36px; font-weight: bold; color: #2c3e50; 
                      margin-bottom: 5px; }
        .stat-icon { float: right; font-size: 40px; opacity: 0.3; }
        
        .report-tabs { display: flex; gap: 10px; margin-bottom: 20px; 
                       border-bottom: 2px solid #dee2e6; padding-bottom: 10px; }
        .tab { padding: 10px 20px; cursor: pointer; border-radius: 5px 5px 0 0; 
               transition: all 0.3s; }
        .tab:hover { background: #e9ecef; }
        .tab.active { background: #3498db; color: white; }
        
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th { background: #f8f9fa; padding: 12px; text-align: left; font-weight: 600; 
             color: #2c3e50; border-bottom: 2px solid #dee2e6; }
        td { padding: 12px; border-bottom: 1px solid #dee2e6; }
        tr:hover { background: #f8f9fa; }
        
        .btn { padding: 8px 15px; border: none; border-radius: 5px; 
               cursor: pointer; text-decoration: none; display: inline-block; 
               font-size: 14px; transition: all 0.3s; }
        .btn-primary { background: #3498db; color: white; }
        .btn-primary:hover { background: #2980b9; }
        .btn-success { background: #27ae60; color: white; }
        .btn-success:hover { background: #229954; }
        
        .chart-container { height: 400px; margin: 20px 0; position: relative; }
        .bar-chart { display: flex; align-items: flex-end; gap: 20px; 
                     height: 300px; margin-top: 50px; padding: 0 20px; }
        .bar-wrapper { flex: 1; display: flex; flex-direction: column; 
                       align-items: center; }
        .bar { width: 100%; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
               border-radius: 5px 5px 0 0; transition: height 0.3s; 
               position: relative; min-height: 5px; }
        .bar-label { margin-top: 10px; font-size: 12px; color: #666; 
                     transform: rotate(-45deg); white-space: nowrap; }
        .bar-value { position: absolute; top: -25px; left: 50%; 
                     transform: translateX(-50%); font-size: 12px; 
                     font-weight: bold; color: #2c3e50; }
        
        .export-buttons { display: flex; gap: 10px; margin-bottom: 20px; }
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
            <li onclick="window.location.href='dashboard'">📊 Dashboard</li>
            <li onclick="window.location.href='buses'">🚌 Bus Management</li>
            <li onclick="window.location.href='routes'">🛣️ Route Management</li>
            <li onclick="window.location.href='schedules'">📅 Schedule Management</li>
            <li onclick="window.location.href='bookings'">🎫 Bookings</li>
            <li onclick="window.location.href='users'">👥 Users</li>
            <li class="active" onclick="window.location.href='reports'">📈 Reports</li>
        </ul>
    </div>
    
    <div class="main-content">
        <div class="stats-row">
            <div class="stat-item">
                <div class="stat-icon">💰</div>
                <div class="stat-label">Total Revenue</div>
                <div class="stat-value">₹<%= totalRevenue != null ? totalRevenue : 0 %></div>
            </div>
            <div class="stat-item">
                <div class="stat-icon">🎫</div>
                <div class="stat-label">Total Bookings</div>
                <div class="stat-value"><%= totalBookings != null ? totalBookings : 0 %></div>
            </div>
            <div class="stat-item">
                <div class="stat-icon">👥</div>
                <div class="stat-label">Total Users</div>
                <div class="stat-value"><%= totalUsers != null ? totalUsers : 0 %></div>
            </div>
        </div>
        
        <div class="card">
            <div class="card-header">
                <span>📊 Reports Dashboard</span>
                <div class="export-buttons">
                    <button class="btn btn-success" onclick="exportToPDF()">📥 Export PDF</button>
                    <button class="btn btn-primary" onclick="exportToExcel()">📥 Export Excel</button>
                </div>
            </div>
            <div class="card-body">
                <div class="report-tabs">
                    <a href="reports?type=daily" class="tab <%= "daily".equals(reportType) ? "active" : "" %>">Daily Revenue</a>
                    <a href="reports?type=monthly" class="tab <%= "monthly".equals(reportType) ? "active" : "" %>">Monthly Revenue</a>
                    <a href="reports?type=routes" class="tab <%= "routes".equals(reportType) ? "active" : "" %>">Route Performance</a>
                    <a href="reports?type=buses" class="tab <%= "buses".equals(reportType) ? "active" : "" %>">Bus Utilization</a>
                </div>
                
                <% if ("daily".equals(reportType)) { %>
                    <h3>Daily Revenue Report</h3>
                    <div class="chart-container">
                        <div class="bar-chart" id="dailyChart">
                            <% 
                                if (reportData != null) {
                                    double maxRevenue = 0;
                                    for (Map<String, Object> item : reportData) {
                                        Double revenue = (Double) item.get("revenue");
                                        if (revenue > maxRevenue) maxRevenue = revenue;
                                    }
                                    for (Map<String, Object> item : reportData) {
                                        Date date = (Date) item.get("date");
                                        Double revenue = (Double) item.get("revenue");
                                        Integer count = (Integer) item.get("count");
                                        int barHeight = maxRevenue > 0 ? (int)((revenue / maxRevenue) * 250) : 0;
                            %>
                            <div class="bar-wrapper">
                                <div class="bar" style="height: <%= barHeight %>px;">
                                    <span class="bar-value">₹<%= revenue %></span>
                                </div>
                                <span class="bar-label"><%= dateFormat.format(date) %></span>
                                <small><%= count %> bookings</small>
                            </div>
                            <% }} %>
                        </div>
                    </div>
                    
                    <table>
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Bookings</th>
                                <th>Revenue (₹)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (reportData != null) {
                                for (Map<String, Object> item : reportData) { %>
                            <tr>
                                <td><%= dateFormat.format((Date) item.get("date")) %></td>
                                <td><%= item.get("count") %></td>
                                <td>₹<%= item.get("revenue") %></td>
                            </tr>
                            <% }} %>
                        </tbody>
                    </table>
                    
                <% } else if ("monthly".equals(reportType)) { %>
                    <h3>Monthly Revenue Report</h3>
                    <div class="chart-container">
                        <div class="bar-chart" id="monthlyChart">
                            <% 
                                if (reportData != null) {
                                    double maxRevenue = 0;
                                    for (Map<String, Object> item : reportData) {
                                        Double revenue = (Double) item.get("revenue");
                                        if (revenue > maxRevenue) maxRevenue = revenue;
                                    }
                                    for (Map<String, Object> item : reportData) {
                                        String month = (String) item.get("month");
                                        Double revenue = (Double) item.get("revenue");
                                        Integer count = (Integer) item.get("count");
                                        int barHeight = maxRevenue > 0 ? (int)((revenue / maxRevenue) * 250) : 0;
                            %>
                            <div class="bar-wrapper">
                                <div class="bar" style="height: <%= barHeight %>px;">
                                    <span class="bar-value">₹<%= revenue %></span>
                                </div>
                                <span class="bar-label"><%= month %></span>
                                <small><%= count %> bookings</small>
                            </div>
                            <% }} %>
                        </div>
                    </div>
                    
                    <table>
                        <thead>
                            <tr>
                                <th>Month</th>
                                <th>Bookings</th>
                                <th>Revenue (₹)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (reportData != null) {
                                for (Map<String, Object> item : reportData) { %>
                            <tr>
                                <td><%= item.get("month") %></td>
                                <td><%= item.get("count") %></td>
                                <td>₹<%= item.get("revenue") %></td>
                            </tr>
                            <% }} %>
                        </tbody>
                    </table>
                    
                <% } else if ("routes".equals(reportType)) { %>
                    <h3>Route Performance Report</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>Route</th>
                                <th>Total Bookings</th>
                                <th>Total Revenue (₹)</th>
                                <th>Avg. Fare (₹)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (reportData != null) {
                                for (Map<String, Object> item : reportData) { %>
                            <tr>
                                <td><%= item.get("route") %></td>
                                <td><%= item.get("bookings") %></td>
                                <td>₹<%= item.get("revenue") %></td>
                                <td>₹<%= item.get("avgFare") %></td>
                            </tr>
                            <% }} %>
                        </tbody>
                    </table>
                    
                <% } else if ("buses".equals(reportType)) { %>
                    <h3>Bus Utilization Report</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>Bus Name</th>
                                <th>Type</th>
                                <th>Total Trips</th>
                                <th>Total Bookings</th>
                                <th>Revenue (₹)</th>
                                <th>Utilization %</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (reportData != null) {
                                for (Map<String, Object> item : reportData) { %>
                            <tr>
                                <td><%= item.get("busName") %></td>
                                <td><%= item.get("busType") %></td>
                                <td><%= item.get("trips") %></td>
                                <td><%= item.get("bookings") %></td>
                                <td>₹<%= item.get("revenue") %></td>
                                <td>
                                    <div style="background: #e9ecef; border-radius: 10px; height: 20px; width: 100%;">
                                        <div style="background: #27ae60; border-radius: 10px; height: 20px; width: <%= item.get("utilization") %>%; text-align: center; color: white; font-size: 12px;">
                                            <%= item.get("utilization") %>%
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <% }} %>
                        </tbody>
                    </table>
                <% } %>
            </div>
        </div>
    </div>
    
    <script>
        function exportToPDF() {
            window.print();
        }
        
        function exportToExcel() {
            var table = document.querySelector('table');
            var html = table.outerHTML;
            var url = 'data:application/vnd.ms-excel,' + escape(html);
            var downloadLink = document.createElement('a');
            downloadLink.href = url;
            downloadLink.download = 'report_<%= reportType %>.xls';
            document.body.appendChild(downloadLink);
            downloadLink.click();
            document.body.removeChild(downloadLink);
        }
    </script>
</body>
</html>