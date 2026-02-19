<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.busbooking.model.Bus, com.busbooking.model.Route" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/login");
        return;
    }
    List<Map<String, Object>> schedules = (List<Map<String, Object>>) request.getAttribute("schedules");
    List<Bus> buses = (List<Bus>) request.getAttribute("buses");
    List<Route> routes = (List<Route>) request.getAttribute("routes");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Schedule Management - Admin</title>
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
        
        table { width: 100%; border-collapse: collapse; }
        th { background: #f8f9fa; padding: 12px; text-align: left; font-weight: 600; 
             color: #2c3e50; border-bottom: 2px solid #dee2e6; }
        td { padding: 12px; border-bottom: 1px solid #dee2e6; }
        tr:hover { background: #f8f9fa; }
        
        .btn { padding: 8px 15px; border: none; border-radius: 5px; 
               cursor: pointer; text-decoration: none; display: inline-block; 
               font-size: 14px; transition: all 0.3s; }
        .btn-primary { background: #3498db; color: white; }
        .btn-primary:hover { background: #2980b9; }
        .btn-danger { background: #e74c3c; color: white; }
        .btn-danger:hover { background: #c0392b; }
        .btn-success { background: #27ae60; color: white; }
        .btn-success:hover { background: #229954; }
        .btn-sm { padding: 5px 10px; font-size: 12px; }
        
        .badge { padding: 5px 10px; border-radius: 20px; font-size: 12px; 
                 font-weight: 600; display: inline-block; }
        .badge-success { background: #d4edda; color: #155724; }
        .badge-warning { background: #fff3cd; color: #856404; }
        .badge-info { background: #d1ecf1; color: #0c5460; }
        
        .modal { display: none; position: fixed; z-index: 2000; left: 0; top: 0; 
                 width: 100%; height: 100%; overflow: auto; background: rgba(0,0,0,0.5); }
        .modal-content { background: white; margin: 5% auto; padding: 30px; 
                         border-radius: 10px; width: 600px; position: relative; }
        .close { color: #aaa; float: right; font-size: 28px; font-weight: bold; 
                 cursor: pointer; position: absolute; right: 20px; top: 10px; }
        .close:hover { color: #333; }
        
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; color: #555; font-weight: 500; }
        input, select { width: 100%; padding: 10px; border: 1px solid #ddd; 
                        border-radius: 5px; font-size: 14px; }
        input:focus, select:focus { outline: none; border-color: #3498db; }
        
        .form-row { display: flex; gap: 15px; }
        .form-row .form-group { flex: 1; }
        
        .search-box { margin-bottom: 20px; display: flex; gap: 10px; }
        .search-box input { flex: 1; padding: 10px; border: 1px solid #ddd; 
                            border-radius: 5px; }
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
            <li class="active" onclick="window.location.href='schedules'">📅 Schedule Management</li>
            <li onclick="window.location.href='bookings'">🎫 Bookings</li>
            <li onclick="window.location.href='users'">👥 Users</li>
            <li onclick="window.location.href='reports'">📈 Reports</li>
        </ul>
    </div>
    
    <div class="main-content">
        <div class="card">
            <div class="card-header">
                <span>📅 Schedule Management</span>
                <button class="btn btn-success" onclick="openAddModal()">+ Add New Schedule</button>
            </div>
            <div class="card-body">
                <div class="search-box">
                    <input type="text" id="searchInput" placeholder="Search by bus, route, date..." onkeyup="searchTable()">
                </div>
                <table id="schedulesTable">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Bus</th>
                            <th>Type</th>
                            <th>Route</th>
                            <th>Departure</th>
                            <th>Arrival</th>
                            <th>Fare (₹)</th>
                            <th>Seats</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (schedules != null) {
                            for (Map<String, Object> schedule : schedules) { %>
                        <tr>
                            <td><%= schedule.get("id") %></td>
                            <td><%= schedule.get("bus") %></td>
                            <td>
                                <% if ("AC".equals(schedule.get("busType"))) { %>
                                    <span class="badge badge-info"><%= schedule.get("busType") %></span>
                                <% } else if ("Sleeper".equals(schedule.get("busType"))) { %>
                                    <span class="badge badge-success"><%= schedule.get("busType") %></span>
                                <% } else { %>
                                    <span class="badge badge-warning"><%= schedule.get("busType") %></span>
                                <% } %>
                            </td>
                            <td><%= schedule.get("route") %></td>
                            <td><%= schedule.get("departure") %></td>
                            <td><%= schedule.get("arrival") %></td>
                            <td>₹<%= schedule.get("fare") %></td>
                            <td><%= schedule.get("seats") %></td>
                            <td>
                                <a href="schedules?action=delete&id=<%= schedule.get("id") %>" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this schedule?')">Delete</a>
                            </td>
                        </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <!-- Add Schedule Modal -->
    <div id="scheduleModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal()">&times;</span>
            <h2>Add New Schedule</h2>
            <form action="schedules" method="post">
                <input type="hidden" name="action" value="add">
                
                <div class="form-group">
                    <label>Select Bus</label>
                    <select name="busId" required>
                        <option value="">-- Select Bus --</option>
                        <% if (buses != null) {
                            for (Bus bus : buses) { %>
                        <option value="<%= bus.getBusId() %>"><%= bus.getBusName() %> (<%= bus.getBusType() %> - <%= bus.getTotalSeats() %> seats)</option>
                        <% } } %>
                    </select>
                </div>
                
                <div class="form-group">
                    <label>Select Route</label>
                    <select name="routeId" required>
                        <option value="">-- Select Route --</option>
                        <% if (routes != null) {
                            for (Route route : routes) { %>
                        <option value="<%= route.getRouteId() %>"><%= route.getOrigin() %> → <%= route.getDestination() %> (<%= route.getDistance() %> km)</option>
                        <% } } %>
                    </select>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>Departure Date</label>
                        <input type="date" name="departureDate" required>
                    </div>
                    <div class="form-group">
                        <label>Departure Time</label>
                        <input type="time" name="departureTime" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>Arrival Date</label>
                        <input type="date" name="arrivalDate" required>
                    </div>
                    <div class="form-group">
                        <label>Arrival Time</label>
                        <input type="time" name="arrivalTime" required>
                    </div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>Fare (₹)</label>
                        <input type="number" name="fare" min="1" step="0.01" required>
                    </div>
                    <div class="form-group">
                        <label>Available Seats</label>
                        <input type="number" name="seats" min="1" required>
                    </div>
                </div>
                
                <button type="submit" class="btn btn-primary" style="width: 100%;">Add Schedule</button>
            </form>
        </div>
    </div>
    
    <script>
        function openAddModal() {
            document.getElementById('scheduleModal').style.display = 'block';
        }
        
        function closeModal() {
            document.getElementById('scheduleModal').style.display = 'none';
        }
        
        window.onclick = function(event) {
            if (event.target == document.getElementById('scheduleModal')) {
                closeModal();
            }
        }
        
        function searchTable() {
            var input, filter, table, tr, td, i, j, txtValue;
            input = document.getElementById("searchInput");
            filter = input.value.toUpperCase();
            table = document.getElementById("schedulesTable");
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
        }
    </script>
</body>
</html>