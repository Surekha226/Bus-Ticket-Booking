<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/login");
        return;
    }
    List<Map<String, Object>> users = (List<Map<String, Object>>) request.getAttribute("users");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Management - Admin</title>
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
        
        .btn { padding: 5px 10px; border: none; border-radius: 3px; 
               cursor: pointer; text-decoration: none; display: inline-block; 
               font-size: 12px; transition: all 0.3s; }
        .btn-danger { background: #e74c3c; color: white; }
        .btn-danger:hover { background: #c0392b; }
        .btn-primary { background: #3498db; color: white; }
        .btn-primary:hover { background: #2980b9; }
        
        .badge { padding: 5px 10px; border-radius: 20px; font-size: 12px; 
                 font-weight: 600; display: inline-block; }
        .badge-success { background: #d4edda; color: #155724; }
        
        .search-box { margin-bottom: 20px; display: flex; gap: 10px; }
        .search-box input { flex: 1; padding: 10px; border: 1px solid #ddd; 
                            border-radius: 5px; }
        
        .stats-row { display: flex; gap: 20px; margin-bottom: 20px; }
        .stat-item { background: white; padding: 15px; border-radius: 5px; 
                     box-shadow: 0 2px 5px rgba(0,0,0,0.1); flex: 1; }
        .stat-label { color: #7f8c8d; font-size: 12px; text-transform: uppercase; }
        .stat-value { font-size: 24px; font-weight: bold; color: #2c3e50; }
        
        .modal { display: none; position: fixed; z-index: 2000; left: 0; top: 0; 
                 width: 100%; height: 100%; overflow: auto; background: rgba(0,0,0,0.5); }
        .modal-content { background: white; margin: 10% auto; padding: 30px; 
                         border-radius: 10px; width: 400px; position: relative; 
                         text-align: center; }
        .modal-content h3 { margin-bottom: 20px; color: #2c3e50; }
        .modal-content p { margin-bottom: 20px; color: #666; }
        .modal-buttons { display: flex; gap: 10px; justify-content: center; }
        .modal-buttons button { padding: 10px 20px; border: none; border-radius: 5px; 
                                 cursor: pointer; font-size: 14px; }
        .btn-confirm { background: #e74c3c; color: white; }
        .btn-cancel { background: #95a5a6; color: white; }
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
            <li class="active" onclick="window.location.href='users'">👥 Users</li>
            <li onclick="window.location.href='reports'">📈 Reports</li>
        </ul>
    </div>
    
    <div class="main-content">
        <div class="card">
            <div class="card-header">
                <span>👥 User Management</span>
            </div>
            <div class="card-body">
                <div class="stats-row">
                    <% 
                        int totalUsers = 0;
                        if (users != null) {
                            totalUsers = users.size();
                        }
                    %>
                    <div class="stat-item">
                        <div class="stat-label">Total Users</div>
                        <div class="stat-value"><%= totalUsers %></div>
                    </div>
                    <div class="stat-item">
                        <div class="stat-label">Active Users</div>
                        <div class="stat-value"><%= totalUsers %></div>
                    </div>
                </div>
                
                <div class="search-box">
                    <input type="text" id="searchInput" placeholder="Search by name, email, phone..." onkeyup="searchTable()">
                </div>
                
                <table id="usersTable">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Registered On</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (users != null) {
                            for (Map<String, Object> user : users) { 
                        %>
                        <tr>
                            <td><%= user.get("id") %></td>
                            <td><%= user.get("name") %></td>
                            <td><%= user.get("email") %></td>
                            <td><%= user.get("phone") %></td>
                            <td><%= user.get("created") %></td>
                            <td>
                                <button class="btn btn-primary" onclick="viewUserBookings(<%= user.get("id") %>)">View Bookings</button>
                                <button class="btn btn-danger" onclick="confirmDelete(<%= user.get("id") %>, '<%= user.get("name") %>')">Delete</button>
                            </td>
                        </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <!-- Delete Confirmation Modal -->
    <div id="deleteModal" class="modal">
        <div class="modal-content">
            <h3>Confirm Delete</h3>
            <p id="deleteMessage">Are you sure you want to delete this user?</p>
            <p style="color: #e74c3c; font-size: 12px;">This will also delete all bookings by this user!</p>
            <div class="modal-buttons">
                <button class="btn-confirm" onclick="deleteUser()">Delete</button>
                <button class="btn-cancel" onclick="closeModal()">Cancel</button>
            </div>
        </div>
    </div>
    
    <script>
        var currentUserId = null;
        
        function searchTable() {
            var input, filter, table, tr, td, i, j, txtValue;
            input = document.getElementById("searchInput");
            filter = input.value.toUpperCase();
            table = document.getElementById("usersTable");
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
        
        function confirmDelete(userId, userName) {
            currentUserId = userId;
            document.getElementById("deleteMessage").innerHTML = "Are you sure you want to delete <strong>" + userName + "</strong>?";
            document.getElementById("deleteModal").style.display = "block";
        }
        
        function closeModal() {
            document.getElementById("deleteModal").style.display = "none";
            currentUserId = null;
        }
        
        function deleteUser() {
            if (currentUserId) {
                window.location.href = "users?action=delete&id=" + currentUserId;
            }
        }
        
        function viewUserBookings(userId) {
            window.location.href = "bookings?userId=" + userId;
        }
        
        window.onclick = function(event) {
            var modal = document.getElementById("deleteModal");
            if (event.target == modal) {
                closeModal();
            }
        }
    </script>
</body>
</html>
