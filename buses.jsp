<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.busbooking.model.Bus" %>
<%
    if (session.getAttribute("admin") == null) {
        response.sendRedirect(request.getContextPath() + "/admin/login");
        return;
    }
    List<Bus> buses = (List<Bus>) request.getAttribute("buses");
    Bus editBus = (Bus) request.getAttribute("editBus");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Bus Management - Admin</title>
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
                         border-radius: 10px; width: 500px; position: relative; }
        .close { color: #aaa; float: right; font-size: 28px; font-weight: bold; 
                 cursor: pointer; position: absolute; right: 20px; top: 10px; }
        .close:hover { color: #333; }
        
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; color: #555; font-weight: 500; }
        input, select, textarea { width: 100%; padding: 10px; border: 1px solid #ddd; 
                                   border-radius: 5px; font-size: 14px; }
        input:focus, select:focus, textarea:focus { outline: none; border-color: #3498db; }
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
            <li class="active" onclick="window.location.href='buses'">🚌 Bus Management</li>
            <li onclick="window.location.href='routes'">🛣️ Route Management</li>
            <li onclick="window.location.href='schedules'">📅 Schedule Management</li>
            <li onclick="window.location.href='bookings'">🎫 Bookings</li>
            <li onclick="window.location.href='users'">👥 Users</li>
            <li onclick="window.location.href='reports'">📈 Reports</li>
        </ul>
    </div>
    
    <div class="main-content">
        <div class="card">
            <div class="card-header">
                <span>🚌 Bus Management</span>
                <button class="btn btn-success" onclick="openAddModal()">+ Add New Bus</button>
            </div>
            <div class="card-body">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Bus Name</th>
                            <th>Type</th>
                            <th>Seats</th>
                            <th>Amenities</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (buses != null) {
                            for (Bus bus : buses) { %>
                        <tr>
                            <td><%= bus.getBusId() %></td>
                            <td><%= bus.getBusName() %></td>
                            <td>
                                <% if ("AC".equals(bus.getBusType())) { %>
                                    <span class="badge badge-info"><%= bus.getBusType() %></span>
                                <% } else if ("Sleeper".equals(bus.getBusType())) { %>
                                    <span class="badge badge-success"><%= bus.getBusType() %></span>
                                <% } else { %>
                                    <span class="badge badge-warning"><%= bus.getBusType() %></span>
                                <% } %>
                            </td>
                            <td><%= bus.getTotalSeats() %></td>
                            <td><%= bus.getAmenities() %></td>
                            <td>
                                <button class="btn btn-primary btn-sm" onclick="editBus(<%= bus.getBusId() %>, '<%= bus.getBusName().replace("'", "\\'") %>', '<%= bus.getBusType() %>', <%= bus.getTotalSeats() %>, '<%= bus.getAmenities().replace("'", "\\'") %>')">Edit</button>
                                <a href="buses?action=delete&id=<%= bus.getBusId() %>" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this bus?')">Delete</a>
                            </td>
                        </tr>
                        <% } } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <!-- Add/Edit Bus Modal -->
    <div id="busModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal()">&times;</span>
            <h2 id="modalTitle">Add New Bus</h2>
            <form action="buses" method="post" id="busForm">
                <input type="hidden" name="action" id="formAction" value="add">
                <input type="hidden" name="busId" id="busId">
                
                <div class="form-group">
                    <label>Bus Name</label>
                    <input type="text" name="busName" id="busName" required>
                </div>
                
                <div class="form-group">
                    <label>Bus Type</label>
                    <select name="busType" id="busType" required>
                        <option value="AC">AC</option>
                        <option value="Sleeper">Sleeper</option>
                        <option value="Seater">Seater</option>
                        <option value="Non-AC">Non-AC</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label>Total Seats</label>
                    <input type="number" name="totalSeats" id="totalSeats" min="10" max="60" required>
                </div>
                
                <div class="form-group">
                    <label>Amenities</label>
                    <textarea name="amenities" id="amenities" rows="3" placeholder="WiFi, Charging Point, Water Bottle, etc." required></textarea>
                </div>
                
                <button type="submit" class="btn btn-primary" style="width: 100%;">Save Bus</button>
            </form>
        </div>
    </div>
    
    <script>
        function openAddModal() {
            document.getElementById('modalTitle').innerText = 'Add New Bus';
            document.getElementById('formAction').value = 'add';
            document.getElementById('busId').value = '';
            document.getElementById('busName').value = '';
            document.getElementById('busType').value = 'AC';
            document.getElementById('totalSeats').value = '40';
            document.getElementById('amenities').value = '';
            document.getElementById('busModal').style.display = 'block';
        }
        
        function editBus(id, name, type, seats, amenities) {
            document.getElementById('modalTitle').innerText = 'Edit Bus';
            document.getElementById('formAction').value = 'update';
            document.getElementById('busId').value = id;
            document.getElementById('busName').value = name;
            document.getElementById('busType').value = type;
            document.getElementById('totalSeats').value = seats;
            document.getElementById('amenities').value = amenities;
            document.getElementById('busModal').style.display = 'block';
        }
        
        function closeModal() {
            document.getElementById('busModal').style.display = 'none';
        }
        
        window.onclick = function(event) {
            if (event.target == document.getElementById('busModal')) {
                closeModal();
            }
        }
        
        <% if (editBus != null) { %>
            window.onload = function() {
                editBus(<%= editBus.getBusId() %>, '<%= editBus.getBusName().replace("'", "\\'") %>', '<%= editBus.getBusType() %>', <%= editBus.getTotalSeats() %>, '<%= editBus.getAmenities().replace("'", "\\'") %>');
            }
        <% } %>
    </script>
</body>
</html>