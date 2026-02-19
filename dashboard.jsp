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
    <title>BusBooking - India's No.1 Online Bus Ticket Booking</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
               background: #f0f3f8; }
        
        /* Header/Navbar */
        .top-bar { background: #d84f57; color: white; padding: 8px 10%; 
                   font-size: 13px; display: flex; justify-content: space-between; }
        .top-bar a { color: white; text-decoration: none; margin-left: 20px; }
        
        .navbar { background: white; padding: 15px 10%; display: flex; 
                  justify-content: space-between; align-items: center; 
                  box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .logo-section { display: flex; align-items: center; }
        .logo { font-size: 28px; font-weight: bold; color: #d84f57; margin-right: 40px; }
        .logo span { color: #333; font-weight: normal; font-size: 14px; 
                     margin-left: 5px; }
        .nav-tabs { display: flex; gap: 30px; }
        .nav-tab { color: #666; text-decoration: none; font-weight: 500; 
                   padding-bottom: 5px; border-bottom: 3px solid transparent; }
        .nav-tab.active { color: #d84f57; border-bottom-color: #d84f57; }
        .nav-tab:hover { color: #d84f57; }
        
        .user-section { display: flex; align-items: center; gap: 20px; }
        .user-name { color: #333; font-weight: 500; }
        .help { color: #666; text-decoration: none; }
        .logout { background: #d84f57; color: white; padding: 8px 20px; 
                  border-radius: 20px; text-decoration: none; font-weight: 500; }
        
        /* Hero Search Section */
        .hero { background: linear-gradient(135deg, #d84f57 0%, #b13b42 100%); 
                padding: 40px 10%; color: white; }
        .hero h1 { font-size: 36px; margin-bottom: 10px; }
        .hero p { font-size: 18px; margin-bottom: 30px; opacity: 0.9; }
        
        .search-container { background: white; border-radius: 40px; 
                            padding: 20px 30px; box-shadow: 0 20px 40px rgba(0,0,0,0.2); }
        .search-row { display: flex; align-items: center; gap: 15px; }
        .location-group { flex: 2; position: relative; }
        .location-group label { display: block; color: #666; font-size: 12px; 
                                 margin-bottom: 5px; }
        .location-group input, .location-group select { width: 100%; padding: 12px; 
                                                         border: 1px solid #ddd; 
                                                         border-radius: 30px; 
                                                         font-size: 16px; }
        .location-group input:focus, .location-group select:focus { 
            outline: none; border-color: #d84f57; 
        }
        .swap-icon { position: absolute; right: 15px; top: 38px; color: #d84f57; 
                     cursor: pointer; background: white; padding: 5px; 
                     border-radius: 50%; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        
        .date-group { flex: 1; }
        .date-group label { display: block; color: #666; font-size: 12px; 
                            margin-bottom: 5px; }
        .date-group input { width: 100%; padding: 12px; border: 1px solid #ddd; 
                            border-radius: 30px; font-size: 16px; }
        
        .search-btn-group { flex: 0.5; }
        .search-btn { background: #d84f57; color: white; border: none; 
                      padding: 15px 30px; border-radius: 30px; font-size: 18px; 
                      font-weight: bold; cursor: pointer; width: 100%; }
        .search-btn:hover { background: #b13b42; }
        
        .date-options { display: flex; gap: 20px; margin-top: 20px; 
                        padding-top: 20px; border-top: 1px solid #eee; }
        .date-option { display: flex; align-items: center; gap: 10px; 
                       color: #333; cursor: pointer; }
        .date-option input[type="radio"] { width: 18px; height: 18px; 
                                            accent-color: #d84f57; }
        
        /* Offer Banner */
        .offer-banner { background: white; margin: 30px 10%; padding: 20px; 
                        border-radius: 15px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); 
                        display: flex; align-items: center; gap: 20px; }
        .offer-code { background: #f0f3f8; padding: 10px 20px; border-radius: 30px; 
                      font-weight: bold; color: #d84f57; }
        .offer-text { flex: 1; }
        .offer-btn { background: #d84f57; color: white; padding: 10px 30px; 
                     border-radius: 30px; text-decoration: none; }
        
        /* Features Section */
        .features { display: grid; grid-template-columns: repeat(4, 1fr); 
                    gap: 30px; margin: 40px 10%; }
        .feature { background: white; padding: 30px; border-radius: 15px; 
                   text-align: center; box-shadow: 0 5px 15px rgba(0,0,0,0.05); }
        .feature-icon { font-size: 40px; margin-bottom: 15px; }
        .feature h3 { margin-bottom: 10px; color: #333; }
        .feature p { color: #666; font-size: 14px; }
        
        /* Footer */
        .footer { background: #2c3e50; color: white; padding: 50px 10%; 
                  margin-top: 50px; }
        .footer-grid { display: grid; grid-template-columns: repeat(4, 1fr); 
                       gap: 30px; }
        .footer h4 { margin-bottom: 20px; }
        .footer-links { list-style: none; }
        .footer-links li { margin-bottom: 10px; }
        .footer-links a { color: #bdc3c7; text-decoration: none; }
        .footer-links a:hover { color: white; }
        .copyright { text-align: center; margin-top: 40px; padding-top: 20px; 
                     border-top: 1px solid #34495e; color: #bdc3c7; }
        
        /* Weather Widget */
        .weather-widget { background: #34495e; padding: 10px 20px; 
                          border-radius: 30px; display: inline-flex; 
                          align-items: center; gap: 10px; color: white; }
        
        /* Responsive */
        @media (max-width: 1200px) {
            .search-row { flex-direction: column; }
            .features { grid-template-columns: repeat(2, 1fr); }
            .footer-grid { grid-template-columns: repeat(2, 1fr); }
        }
    </style>
</head>
<body>
    <!-- Top Bar -->
    <div class="top-bar">
        <div>Get 10% Discount Use code APP10 on app | <a href="#">Install BusBooking App</a></div>
        <div>
            <a href="#">Help</a>
            <a href="#">English</a>
            <a href="#">INR</a>
        </div>
    </div>
    
    <!-- Main Navbar -->
    <nav class="navbar">
        <div class="logo-section">
            <div class="logo">BusBooking <span>.com</span></div>
            <div class="nav-tabs">
                <a href="#" class="nav-tab active">Bus Tickets</a>
            </div>
        </div>
        <div class="user-section">
            <span class="user-name">Welcome, <%= user.getFullName() %>!</span>
            <a href="#" class="help">Help</a>
            <a href="logout" class="logout">Logout</a>
        </div>
    </nav>
    
    <!-- Hero Search Section -->
    <div class="hero">
        <h1>India's No. 1 Online Bus Ticket Booking Site</h1>
        <p>Book buses with ease and get exclusive discounts</p>
        
        <div class="search-container">
            <form id="searchForm" action="search" method="get" onsubmit="return validateSearch()">
                <div class="search-row">
                    <div class="location-group">
                        <label>FROM</label>
                        <select name="origin" id="origin" required>
                            <option value="">Select Origin</option>
                            <option value="Mumbai">Mumbai</option>
                            <option value="Delhi">Delhi</option>
                            <option value="Bangalore">Bangalore</option>
                            <option value="Chennai">Chennai</option>
                            <option value="Kolkata">Kolkata</option>
                            <option value="Pune">Pune</option>
                            <option value="Hyderabad">Hyderabad</option>
                            <option value="Ahmedabad">Ahmedabad</option>
                        </select>
                        <span class="swap-icon" onclick="swapLocations()">⇄</span>
                    </div>
                    
                    <div class="location-group">
                        <label>TO</label>
                        <select name="destination" id="destination" required>
                            <option value="">Select Destination</option>
                            <option value="Mumbai">Mumbai</option>
                            <option value="Delhi">Delhi</option>
                            <option value="Bangalore">Bangalore</option>
                            <option value="Chennai">Chennai</option>
                            <option value="Kolkata">Kolkata</option>
                            <option value="Pune">Pune</option>
                            <option value="Hyderabad">Hyderabad</option>
                            <option value="Agra">Agra</option>
                            <option value="Goa">Goa</option>
                            <option value="Mysore">Mysore</option>
                        </select>
                    </div>
                    
                    <div class="date-group">
                        <label>DATE OF JOURNEY</label>
                        <input type="date" name="travelDate" id="travelDate" required 
                               min="2026-02-17" value="2026-02-17">
                    </div>
                    
                    <div class="search-btn-group">
                        <button type="submit" class="search-btn">SEARCH BUSES</button>
                    </div>
                </div>
                
                <div class="date-options">
                    <label class="date-option">
                        <input type="radio" name="dateOption" value="today" checked onclick="setDate('today')"> Today
                    </label>
                    <label class="date-option">
                        <input type="radio" name="dateOption" value="tomorrow" onclick="setDate('tomorrow')"> Tomorrow
                    </label>
                </div>
            </form>
        </div>
    </div>
    
    <!-- Offer Banner -->
    <div class="offer-banner">
        <div class="offer-code">BUS10</div>
        <div class="offer-text">
            <strong>Special Bus Offer!</strong> - Get 10% off on your first booking
        </div>
        <a href="#" class="offer-btn">Book Now</a>
    </div>
    
    <!-- Features Section -->
    <div class="features">
        <div class="feature">
            <div class="feature-icon">🚌</div>
            <h3>200+ Bus Operators</h3>
            <p>Choose from multiple operators</p>
        </div>
        <div class="feature">
            <div class="feature-icon">💰</div>
            <h3>Best Prices</h3>
            <p>Guaranteed lowest fares</p>
        </div>
        <div class="feature">
            <div class="feature-icon">🛡️</div>
            <h3>Safe Travel</h3>
            <p>COVID-19 safety measures</p>
        </div>
        <div class="feature">
            <div class="feature-icon">⭐</div>
            <h3>24/7 Support</h3>
            <p>We're here to help</p>
        </div>
    </div>
    
    <!-- Weather Widget -->
    <div style="position: fixed; bottom: 20px; right: 20px; z-index: 1000;">
        <div class="weather-widget">
            <span>🌡️ 29°C</span>
            <span>☁️ Mostly cloudy</span>
        </div>
    </div>
    
    <!-- Footer -->
    <footer class="footer">
        <div class="footer-grid">
            <div>
                <h4>About BusBooking</h4>
                <ul class="footer-links">
                    <li><a href="#">About Us</a></li>
                    <li><a href="#">Contact Us</a></li>
                    <li><a href="#">Mobile App</a></li>
                </ul>
            </div>
            <div>
                <h4>Info</h4>
                <ul class="footer-links">
                    <li><a href="#">T&C</a></li>
                    <li><a href="#">Privacy Policy</a></li>
                    <li><a href="#">FAQ</a></li>
                </ul>
            </div>
            <div>
                <h4>Popular Routes</h4>
                <ul class="footer-links">
                    <li><a href="#">Mumbai to Pune</a></li>
                    <li><a href="#">Delhi to Agra</a></li>
                    <li><a href="#">Bangalore to Mysore</a></li>
                </ul>
            </div>
            <div>
                <h4>Follow Us</h4>
                <ul class="footer-links">
                    <li><a href="#">Facebook</a></li>
                    <li><a href="#">Twitter</a></li>
                    <li><a href="#">Instagram</a></li>
                </ul>
            </div>
        </div>
        <div class="copyright">
            © 2026 BusBooking.com - India's No.1 Online Bus Ticket Booking Site. All rights reserved.
        </div>
    </footer>
    
    <script>
        function swapLocations() {
            var origin = document.getElementById('origin');
            var dest = document.getElementById('destination');
            var temp = origin.value;
            origin.value = dest.value;
            dest.value = temp;
        }
        
        function setDate(option) {
            var dateInput = document.getElementById('travelDate');
            var today = new Date();
            var tomorrow = new Date(today);
            tomorrow.setDate(tomorrow.getDate() + 1);
            
            if (option === 'today') {
                dateInput.value = today.toISOString().split('T')[0];
            } else if (option === 'tomorrow') {
                dateInput.value = tomorrow.toISOString().split('T')[0];
            }
        }
        
        function validateSearch() {
            var origin = document.getElementById('origin').value;
            var dest = document.getElementById('destination').value;
            
            if (origin === dest) {
                alert('Origin and destination cannot be the same!');
                return false;
            }
            return true;
        }
        
        // Set today's date as default
        window.onload = function() {
            var today = new Date();
            document.getElementById('travelDate').value = today.toISOString().split('T')[0];
        };
    </script>
</body>
</html>