<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/login");
        return;
    }
    List<Map<String, Object>> bookings = (List<Map<String, Object>>) request.getAttribute("bookings");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Booking Management - Admin</title>
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
                       color: white; padding: 15px 20px; font-size: 18px; font-weight: bold; }
        .card-body { padding: 20px; }
        
        table { width: 100%; border-collapse: collapse; }
        th { background: #f8f9fa; padding: 12px; text-align: left; font-weight: 600; 
             color: #2c3e50; border-bottom: 2px solid #dee2e6; }
        td { padding: 12px; border-bottom: 1px solid #dee2e6; }
        tr:hover { background: #f8f9fa; }
        
        .btn { padding: 5px 10px; border: none; border-radius: 3px; 
               cursor: pointer; text-decoration: none; display: inline-block; 
               font-size: 12px; transition: all 0.3s; }
        .btn-danger { background: #e74c3c; color: white; }
        .btn-danger:hover { background: #c0392b; }
        .btn-success { background: #27ae60; color: white; }
        .btn-success:hover { background: #229954; }
        .btn-warning { background: #f39c12; color: white; }
        .btn-warning:hover { background: #e67e22; }
        
        .badge { padding: 5px 10px; border-radius: 20px; font-size: 12px; 
                 font-weight: 600; display: inline-block; }
        .badge-success { background: #d4edda; color: #155724; }
        .badge-warning { background: #fff3cd; color: #856404; }
        .badge-danger { background: #f8d7da; color: #721c24; }
        .badge-info { background: #d1ecf1; color: #0c5460; }
        
        .search-box { margin-bottom: 20px; display: flex; gap: 10px; }
        .search-box input { flex: 1; padding: 10px; border: 1px solid #ddd; 
                            border-radius: 5px; }
        .filter-box { display: flex; gap: 10px; margin-bottom: 20px; }
        .filter-box select { padding: 10px; border: 1px solid #ddd; 
                             border-radius: 5px; }
        
        .stats-row { display: flex; gap: 20px; margin-bottom: 20px; }
        .stat-item { background: white; padding: 15px; border-radius: 5px; 
                     box-shadow: 0 2px 5px rgba(0,0,0,0.1); flex: 1; }
        .stat-label { color: #7f8c8d; font-size: 12px; text-transform: uppercase; }
        .stat-value { font-size: 24px; font-weight: bold; color: #2c3e50; }
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
            <li class="active" onclick="window.location.href='bookings'">🎫 Bookings</li>
            <li onclick="window.location.href='users'">👥 Users</li>
            <li onclick="window.location.href='reports'">📈 Reports</li>
        </ul>
    </div>
    
    <div class="main-content">
        <div class="card">
            <div class="card-header">
                <span>🎫 Booking Management</span>
            </div>
            <div class="card-body">
                <div class="stats-row">
                    <% 
                        int totalBookings = 0;
                        double totalRevenue = 0;
                        int confirmedBookings = 0;
                        int cancelledBookings = 0;
                        
                        if (bookings != null) {
                            totalBookings = bookings.size();
                            for (Map<String, Object> booking : bookings) {
                                totalRevenue += (Double) booking.get("amount");
                                if ("Confirmed".equals(booking.get("bookingStatus"))) {
                                    confirmedBookings++;
                                } else if ("Cancelled".equals(booking.get("bookingStatus"))) {
                                    cancelledBookings++;
                                }
                            }
                        }
                    %>
                    <div class="stat-item">
                        <div class="stat-label">Total Bookings</div>
                        <div class="stat-value"><%= totalBookings %></div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-label">Total Revenue</div>
                        <div class="stat-value">₹<%= totalRevenue %></div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-label">Confirmed</div>
                        <div class="stat-value"><%= confirmedBookings %></div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-label">Cancelled</div>
                        <div class="stat-value"><%= cancelledBookings %></div>
                    </div>
                </div>
                
                <div class="search-box">
                    <input type="text" id="searchInput" placeholder="Search by user, route, booking ID..." onkeyup="searchTable()">
                </div>
                
                <div class="filter-box">
                    <select id="statusFilter" onchange="filterTable()">
                        <option value="all">All Status</option>
                        <option value="Confirmed">Confirmed</option>
                        <option value="Cancelled">Cancelled</option>
                        <option value="Pending">Pending</option>
                    </select>
                    <select id="paymentFilter" onchange="filterTable()">
                        <option value="all">All Payment Status</option>
                        <option value="Paid">Paid</option>
                        <option value="Pending">Pending</option>
                        <option value="Failed">Failed</option>
                    </select>
                </div>
                
                <table id="bookingsTable">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>User</th>
                            <th>Contact</th>
                            <th>Route</th>
                            <th>Departure</th>
                            <th>Amount</th>
                            <th>Booking Status</th>
                            <th>Payment Status</th>
                            <th>Booking Date</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (bookings != null) {
                            for (Map<String, Object> booking : bookings) { 
                                String statusClass = "";
                                if ("Confirmed".equals(booking.get("bookingStatus"))) {
                                    statusClass = "badge-success";
                                } else if ("Cancelled".equals(booking.get("bookingStatus"))) {
                                    statusClass = "badge-danger";
                                } else {
                                    statusClass = "badge-warning";
                                }
                                
                                String paymentClass = "";
                                if ("Paid".equals(booking.get("paymentStatus"))) {
                                    paymentClass = "badge-success";
                                } else if ("Failed".equals(booking.get("paymentStatus"))) {
                                    paymentClass = "badge-danger";
                                } else {
                                    paymentClass = "badge-warning";
                                }
                        %>
                        <tr>
                            <td>#<%= booking.get("id") %></td>
                            <td><%= booking.get("userName") %></td>
                            <td>
                                <%= booking.get("userEmail") %><br>
                                <small><%= booking.get("userPhone") %></small>
                            </td>
                            <td><%= booking.get("route") %></td>
                            <td><%= booking.get("departure") %></td>
                            <td>₹<%= booking.get("amount") %></td>
                            <td><span class="badge <%= statusClass %>"><%= booking.get("bookingStatus") %></span></td>
                            <td><span class="badge <%= paymentClass %>"><%= booking.get("paymentStatus") %></span></td>
                            <td><%= booking.get("bookingDate") %></td>
                            <td>
                                <a href="bookings?action=view&id=<%= booking.get("id") %>" class="btn btn-success">View</a>
                                <% if (!"Cancelled".equals(booking.get("bookingStatus"))) { %>
                                    <a href="bookings?action=cancel&id=<%= booking.get("id") %>" class="btn btn-danger" onclick="return confirm('Cancel this booking?')">Cancel</a>
                                <% } %>
                            </td>
                        </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <script>
        function searchTable() {
            var input, filter, table, tr, td, i, j, txtValue;
            input = document.getElementById("searchInput");
            filter = input.value.toUpperCase();
            table = document.getElementById("bookingsTable");
            tr = table.getElementsByTagName("tr");
            
            for (i = 1; i < tr.length; i++) {
                tr[i].style.display = "none";
                td = tr[i].getElementsByTagName("td");
                for (j = 0; j < td.length; j++) {
                    if (td[j]) {
                        txtValue = td[j].textContent || td[j].innerText;
                        if (txtValue.toUpperCase().indexOf(filter) > -1) {
                            tr[i].style.display = "";
                            break;
                        }
                    }
                }
            }
            
            applyFilters();
        }
        
        function filterTable() {
            var statusFilter = document.getElementById("statusFilter").value;
            var paymentFilter = document.getElementById("paymentFilter").value;
            var table = document.getElementById("bookingsTable");
            var tr = table.getElementsByTagName("tr");
            
            for (var i = 1; i < tr.length; i++) {
                var statusTd = tr[i].getElementsByTagName("td")[6];
                var paymentTd = tr[i].getElementsByTagName("td")[7];
                
                if (statusTd && paymentTd) {
                    var status = statusTd.textContent || statusTd.innerText;
                    var payment = paymentTd.textContent || paymentTd.innerText;
                    
                    var statusMatch = (statusFilter === "all" || status.includes(statusFilter));
                    var paymentMatch = (paymentFilter === "all" || payment.includes(paymentFilter));
                    
                    if (statusMatch && paymentMatch && tr[i].style.display !== "none") {
                        tr[i].style.display = "";
                    } else {
                        tr[i].style.display = "none";
                    }
                }
            }
        }
        
        function applyFilters() {
            filterTable();
        }
    </script>
</body>
</html>