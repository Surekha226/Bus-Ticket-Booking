<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.busbooking.model.*, java.text.SimpleDateFormat" %>
<%
    List<Schedule> schedules = (List<Schedule>) request.getAttribute("schedules");
    User user = (User) session.getAttribute("user");
    String origin = request.getParameter("origin");
    String destination = request.getParameter("destination");
    String travelDate = request.getParameter("travelDate");
    
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Bus Search Results - BusBooking</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
               background: #f0f3f8; }
        
        .top-bar { background: #d84f57; color: white; padding: 8px 10%; 
                   font-size: 13px; display: flex; justify-content: space-between; }
        .top-bar a { color: white; text-decoration: none; margin-left: 20px; }
        
        .navbar { background: white; padding: 15px 10%; display: flex; 
                  justify-content: space-between; align-items: center; 
                  box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .logo { font-size: 28px; font-weight: bold; color: #d84f57; }
        
        .user-section { display: flex; align-items: center; gap: 20px; }
        .user-name { color: #333; font-weight: 500; }
        .logout { background: #d84f57; color: white; padding: 8px 20px; 
                  border-radius: 20px; text-decoration: none; }
        
        .container { max-width: 1200px; margin: 30px auto; padding: 0 20px; }
        
        .search-summary { background: white; padding: 20px; border-radius: 15px; 
                          margin-bottom: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); 
                          display: flex; justify-content: space-between; 
                          align-items: center; }
        .route-info { font-size: 24px; font-weight: bold; color: #333; }
        .route-info small { font-size: 14px; color: #666; font-weight: normal; 
                            margin-left: 10px; }
        .modify-search { background: #d84f57; color: white; padding: 10px 25px; 
                         border-radius: 30px; text-decoration: none; }
        
        .filters { background: white; padding: 20px; border-radius: 15px; 
                   margin-bottom: 30px; display: flex; gap: 20px; 
                   box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
        .filter-group { flex: 1; }
        .filter-group label { display: block; color: #666; font-size: 12px; 
                               margin-bottom: 5px; }
        .filter-group select { width: 100%; padding: 10px; border: 1px solid #ddd; 
                                border-radius: 30px; }
        
        .bus-card { background: white; border-radius: 15px; padding: 20px; 
                    margin-bottom: 15px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); 
                    display: flex; align-items: center; transition: all 0.3s; }
        .bus-card:hover { box-shadow: 0 5px 20px rgba(0,0,0,0.1); 
                          transform: translateY(-2px); }
        
        .bus-info { flex: 2; }
        .bus-name { font-size: 18px; font-weight: bold; color: #333; 
                    margin-bottom: 5px; }
        .bus-type { color: #d84f57; font-size: 14px; margin-bottom: 5px; }
        .bus-amenities { color: #666; font-size: 13px; }
        .bus-amenities span { margin-right: 15px; }
        
        .time-info { flex: 2; display: flex; justify-content: space-around; }
        .departure, .arrival { text-align: center; }
        .time { font-size: 20px; font-weight: bold; color: #333; }
        .place { color: #666; font-size: 14px; }
        .duration { color: #999; font-size: 13px; margin-top: 5px; }
        
        .price-info { flex: 1; text-align: right; }
        .price { font-size: 24px; font-weight: bold; color: #333; }
        .seats { color: #27ae60; font-size: 14px; margin-top: 5px; }
        .book-btn { background: #d84f57; color: white; padding: 10px 25px; 
                    border-radius: 30px; text-decoration: none; display: inline-block; 
                    margin-top: 10px; font-weight: 500; }
        
        .no-results { text-align: center; padding: 60px; background: white; 
                      border-radius: 15px; }
        .no-results h2 { color: #333; margin-bottom: 20px; }
        .no-results p { color: #666; margin-bottom: 30px; }
        .back-btn { background: #d84f57; color: white; padding: 12px 30px; 
                    border-radius: 30px; text-decoration: none; display: inline-block; }
        
        .footer { background: #2c3e50; color: white; padding: 50px 10%; 
                  margin-top: 50px; }
        .copyright { text-align: center; margin-top: 40px; padding-top: 20px; 
                     border-top: 1px solid #34495e; color: #bdc3c7; }
    </style>
</head>
<body>
    <div class="top-bar">
        <div>Get 10% Discount Use code APP10 on app | <a href="#">Install redBus App</a></div>
        <div><a href="#">Help</a> | <a href="#">English</a></div>
    </div>
    
    <nav class="navbar">
        <div class="logo">BusBooking</div>
        <div class="user-section">
            <span class="user-name">Welcome, <%= user.getFullName() %>!</span>
            <a href="logout" class="logout">Logout</a>
        </div>
    </nav>
    
    <div class="container">
        <div class="search-summary">
            <div>
                <span class="route-info">
                    <%= origin %> → <%= destination %>
                    <small><%= travelDate %></small>
                </span>
            </div>
            <a href="dashboard.jsp" class="modify-search">Modify Search</a>
        </div>
        
        <div class="filters">
            <div class="filter-group">
                <label>Bus Type</label>
                <select id="busTypeFilter" onchange="filterBuses()">
                    <option value="all">All Types</option>
                    <option value="AC">AC</option>
                    <option value="Sleeper">Sleeper</option>
                    <option value="Seater">Seater</option>
                    <option value="Non-AC">Non-AC</option>
                </select>
            </div>
            <div class="filter-group">
                <label>Departure Time</label>
                <select id="timeFilter" onchange="filterBuses()">
                    <option value="all">Any Time</option>
                    <option value="morning">Morning (6AM - 12PM)</option>
                    <option value="afternoon">Afternoon (12PM - 6PM)</option>
                    <option value="evening">Evening (6PM - 12AM)</option>
                    <option value="night">Night (12AM - 6AM)</option>
                </select>
            </div>
            <div class="filter-group">
                <label>Price Range</label>
                <select id="priceFilter" onchange="filterBuses()">
                    <option value="all">All Prices</option>
                    <option value="0-500">Under ₹500</option>
                    <option value="500-1000">₹500 - ₹1000</option>
                    <option value="1000-2000">₹1000 - ₹2000</option>
                    <option value="2000+">Above ₹2000</option>
                </select>
            </div>
        </div>
        
        <div id="busResults">
            <% if (schedules == null || schedules.isEmpty()) { %>
                <div class="no-results">
                    <h2>No buses found for this route</h2>
                    <p>Please try a different route or date</p>
                    <a href="dashboard.jsp" class="back-btn">Search Again</a>
                </div>
            <% } else { 
                for (Schedule s : schedules) { 
                    Date depTime = s.getDepartureTime();
                    Date arrTime = s.getArrivalTime();
                    long diff = arrTime.getTime() - depTime.getTime();
                    long hours = diff / (60 * 60 * 1000);
                    long mins = (diff / (60 * 1000)) % 60;
                    String duration = hours + "h " + (mins > 0 ? mins + "m" : "");
            %>
            <div class="bus-card" data-type="<%= s.getBus().getBusType() %>" 
                 data-hour="<%= new SimpleDateFormat("HH").format(depTime) %>"
                 data-price="<%= s.getFare() %>">
                <div class="bus-info">
                    <div class="bus-name"><%= s.getBus().getBusName() %></div>
                    <div class="bus-type"><%= s.getBus().getBusType() %></div>
                    <div class="bus-amenities">
                        <% String[] amenities = s.getBus().getAmenities().split(","); 
                           for(String amenity : amenities) { %>
                            <span>✓ <%= amenity.trim() %></span>
                        <% } %>
                    </div>
                </div>
                
                <div class="time-info">
                    <div class="departure">
                        <div class="time"><%= timeFormat.format(depTime) %></div>
                        <div class="place"><%= s.getRoute().getOrigin() %></div>
                    </div>
                    <div class="duration"><%= duration %></div>
                    <div class="arrival">
                        <div class="time"><%= timeFormat.format(arrTime) %></div>
                        <div class="place"><%= s.getRoute().getDestination() %></div>
                    </div>
                </div>
                
                <div class="price-info">
                    <div class="price">₹<%= (int)s.getFare() %></div>
                    <div class="seats"><%= s.getAvailableSeats() %> seats available</div>
                    <a href="book?scheduleId=<%= s.getScheduleId() %>" class="book-btn">Book Now</a>
                </div>
            </div>
            <% } } %>
        </div>
    </div>
    
    <footer class="footer">
        <div class="copyright">
            © 2026 BusBooking.com India's No.1 Online Bus Ticket Booking Site. All rights reserved.
        </div>
    </footer>
    
    <script>
        function filterBuses() {
            var typeFilter = document.getElementById('busTypeFilter').value;
            var timeFilter = document.getElementById('timeFilter').value;
            var priceFilter = document.getElementById('priceFilter').value;
            var buses = document.querySelectorAll('.bus-card');
            
            buses.forEach(function(bus) {
                var show = true;
                var type = bus.getAttribute('data-type');
                var hour = parseInt(bus.getAttribute('data-hour'));
                var price = parseInt(bus.getAttribute('data-price'));
                
                // Bus type filter
                if (typeFilter !== 'all' && type !== typeFilter) {
                    show = false;
                }
                
                // Time filter
                if (timeFilter !== 'all') {
                    if (timeFilter === 'morning' && (hour < 6 || hour >= 12)) show = false;
                    if (timeFilter === 'afternoon' && (hour < 12 || hour >= 18)) show = false;
                    if (timeFilter === 'evening' && (hour < 18 || hour >= 24)) show = false;
                    if (timeFilter === 'night' && (hour >= 6 && hour < 24)) show = false;
                }
                
                // Price filter
                if (priceFilter !== 'all') {
                    var min = 0, max = Infinity;
                    if (priceFilter === '0-500') { min = 0; max = 500; }
                    if (priceFilter === '500-1000') { min = 500; max = 1000; }
                    if (priceFilter === '1000-2000') { min = 1000; max = 2000; }
                    if (priceFilter === '2000+') { min = 2000; max = Infinity; }
                    if (price < min || price > max) show = false;
                }
                
                bus.style.display = show ? 'flex' : 'none';
            });
        }
    </script>
</body>
</html>