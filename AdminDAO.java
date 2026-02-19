package com.busbooking.dao;

import com.busbooking.model.Bus;
import com.busbooking.model.Route;
import com.busbooking.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDAO {
    
    // ============== DASHBOARD STATISTICS ==============
    
    public int getTotalBuses() {
        String sql = "SELECT COUNT(*) FROM buses";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTotalRoutes() {
        String sql = "SELECT COUNT(*) FROM routes";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTotalSchedules() {
        String sql = "SELECT COUNT(*) FROM schedules";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTotalBookings() {
        String sql = "SELECT COUNT(*) FROM bookings";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM bookings WHERE booking_status='Confirmed'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<Map<String, Object>> getRecentBookings(int limit) {
        List<Map<String, Object>> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, u.full_name, r.origin, r.destination, " +
                     "s.departure_time, b.total_amount, b.booking_status " +
                     "FROM bookings b " +
                     "JOIN users u ON b.user_id = u.user_id " +
                     "JOIN schedules sc ON b.schedule_id = sc.schedule_id " +
                     "JOIN routes r ON sc.route_id = r.route_id " +
                     "ORDER BY b.booking_date DESC LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> booking = new HashMap<>();
                booking.put("id", rs.getInt("booking_id"));
                booking.put("user", rs.getString("full_name"));
                booking.put("route", rs.getString("origin") + " → " + rs.getString("destination"));
                booking.put("departure", rs.getTimestamp("departure_time"));
                booking.put("amount", rs.getDouble("total_amount"));
                booking.put("status", rs.getString("booking_status"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public List<Map<String, Object>> getPopularRoutes(int limit) {
        List<Map<String, Object>> routes = new ArrayList<>();
        String sql = "SELECT r.origin, r.destination, COUNT(*) as booking_count, SUM(b.total_amount) as revenue " +
                     "FROM bookings b " +
                     "JOIN schedules s ON b.schedule_id = s.schedule_id " +
                     "JOIN routes r ON s.route_id = r.route_id " +
                     "GROUP BY r.route_id " +
                     "ORDER BY booking_count DESC LIMIT ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> route = new HashMap<>();
                route.put("origin", rs.getString("origin"));
                route.put("destination", rs.getString("destination"));
                route.put("count", rs.getInt("booking_count"));
                route.put("revenue", rs.getDouble("revenue"));
                routes.add(route);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }
    
    // ============== BUS MANAGEMENT ==============
    
    public List<Bus> getAllBuses() {
        List<Bus> buses = new ArrayList<>();
        String sql = "SELECT * FROM buses ORDER BY bus_id";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Bus bus = new Bus();
                bus.setBusId(rs.getInt("bus_id"));
                bus.setBusName(rs.getString("bus_name"));
                bus.setBusType(rs.getString("bus_type"));
                bus.setTotalSeats(rs.getInt("total_seats"));
                bus.setAmenities(rs.getString("amenities"));
                buses.add(bus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buses;
    }
    
    public Bus getBusById(int busId) {
        String sql = "SELECT * FROM buses WHERE bus_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, busId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Bus bus = new Bus();
                bus.setBusId(rs.getInt("bus_id"));
                bus.setBusName(rs.getString("bus_name"));
                bus.setBusType(rs.getString("bus_type"));
                bus.setTotalSeats(rs.getInt("total_seats"));
                bus.setAmenities(rs.getString("amenities"));
                return bus;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addBus(Bus bus) {
        String sql = "INSERT INTO buses (bus_name, bus_type, total_seats, amenities) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bus.getBusName());
            pstmt.setString(2, bus.getBusType());
            pstmt.setInt(3, bus.getTotalSeats());
            pstmt.setString(4, bus.getAmenities());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateBus(Bus bus) {
        String sql = "UPDATE buses SET bus_name=?, bus_type=?, total_seats=?, amenities=? WHERE bus_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bus.getBusName());
            pstmt.setString(2, bus.getBusType());
            pstmt.setInt(3, bus.getTotalSeats());
            pstmt.setString(4, bus.getAmenities());
            pstmt.setInt(5, bus.getBusId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteBus(int busId) {
        String sql = "DELETE FROM buses WHERE bus_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, busId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ============== ROUTE MANAGEMENT ==============
    
    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT * FROM routes ORDER BY origin, destination";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Route route = new Route();
                route.setRouteId(rs.getInt("route_id"));
                route.setOrigin(rs.getString("origin"));
                route.setDestination(rs.getString("destination"));
                route.setDistance(rs.getInt("distance"));
                route.setDuration(rs.getInt("duration"));
                routes.add(route);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }
    
    public Route getRouteById(int routeId) {
        String sql = "SELECT * FROM routes WHERE route_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, routeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Route route = new Route();
                route.setRouteId(rs.getInt("route_id"));
                route.setOrigin(rs.getString("origin"));
                route.setDestination(rs.getString("destination"));
                route.setDistance(rs.getInt("distance"));
                route.setDuration(rs.getInt("duration"));
                return route;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addRoute(Route route) {
        String sql = "INSERT INTO routes (origin, destination, distance, duration) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, route.getOrigin());
            pstmt.setString(2, route.getDestination());
            pstmt.setInt(3, route.getDistance());
            pstmt.setInt(4, route.getDuration());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateRoute(Route route) {
        String sql = "UPDATE routes SET origin=?, destination=?, distance=?, duration=? WHERE route_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, route.getOrigin());
            pstmt.setString(2, route.getDestination());
            pstmt.setInt(3, route.getDistance());
            pstmt.setInt(4, route.getDuration());
            pstmt.setInt(5, route.getRouteId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteRoute(int routeId) {
        String sql = "DELETE FROM routes WHERE route_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, routeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ============== SCHEDULE MANAGEMENT ==============
    
    public List<Map<String, Object>> getAllSchedules() {
        List<Map<String, Object>> schedules = new ArrayList<>();
        String sql = "SELECT s.schedule_id, b.bus_name, b.bus_type, r.origin, r.destination, " +
                     "s.departure_time, s.arrival_time, s.fare, s.available_seats " +
                     "FROM schedules s " +
                     "JOIN buses b ON s.bus_id = b.bus_id " +
                     "JOIN routes r ON s.route_id = r.route_id " +
                     "ORDER BY s.departure_time DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> schedule = new HashMap<>();
                schedule.put("id", rs.getInt("schedule_id"));
                schedule.put("bus", rs.getString("bus_name"));
                schedule.put("busType", rs.getString("bus_type"));
                schedule.put("route", rs.getString("origin") + " → " + rs.getString("destination"));
                schedule.put("departure", rs.getTimestamp("departure_time"));
                schedule.put("arrival", rs.getTimestamp("arrival_time"));
                schedule.put("fare", rs.getDouble("fare"));
                schedule.put("seats", rs.getInt("available_seats"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    
    public boolean addSchedule(int busId, int routeId, String departure, String arrival, double fare, int seats) {
        String sql = "INSERT INTO schedules (bus_id, route_id, departure_time, arrival_time, fare, available_seats) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, busId);
            pstmt.setInt(2, routeId);
            pstmt.setString(3, departure);
            pstmt.setString(4, arrival);
            pstmt.setDouble(5, fare);
            pstmt.setInt(6, seats);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteSchedule(int scheduleId) {
        String sql = "DELETE FROM schedules WHERE schedule_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, scheduleId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ============== BOOKING MANAGEMENT ==============
    
    public List<Map<String, Object>> getAllBookings() {
        List<Map<String, Object>> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, u.full_name, u.email, u.phone, " +
                     "r.origin, r.destination, s.departure_time, " +
                     "b.total_amount, b.booking_status, b.payment_status, b.booking_date " +
                     "FROM bookings b " +
                     "JOIN users u ON b.user_id = u.user_id " +
                     "JOIN schedules sc ON b.schedule_id = sc.schedule_id " +
                     "JOIN routes r ON sc.route_id = r.route_id " +
                     "ORDER BY b.booking_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> booking = new HashMap<>();
                booking.put("id", rs.getInt("booking_id"));
                booking.put("userName", rs.getString("full_name"));
                booking.put("userEmail", rs.getString("email"));
                booking.put("userPhone", rs.getString("phone"));
                booking.put("route", rs.getString("origin") + " → " + rs.getString("destination"));
                booking.put("departure", rs.getTimestamp("departure_time"));
                booking.put("amount", rs.getDouble("total_amount"));
                booking.put("bookingStatus", rs.getString("booking_status"));
                booking.put("paymentStatus", rs.getString("payment_status"));
                booking.put("bookingDate", rs.getTimestamp("booking_date"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE bookings SET booking_status='Cancelled' WHERE booking_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ============== USER MANAGEMENT ==============
    
    public List<Map<String, Object>> getAllUsers() {
        List<Map<String, Object>> users = new ArrayList<>();
        String sql = "SELECT user_id, full_name, email, phone, created_at FROM users ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getInt("user_id"));
                user.put("name", rs.getString("full_name"));
                user.put("email", rs.getString("email"));
                user.put("phone", rs.getString("phone"));
                user.put("created", rs.getTimestamp("created_at"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
        // ============== REPORT METHODS ==============
    
    public List<Map<String, Object>> getDailyRevenue(int days) {
        List<Map<String, Object>> dailyData = new ArrayList<>();
        String sql = "SELECT DATE(booking_date) as date, COUNT(*) as count, SUM(total_amount) as revenue " +
                     "FROM bookings " +
                     "WHERE booking_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                     "GROUP BY DATE(booking_date) " +
                     "ORDER BY date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, days);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> day = new HashMap<>();
                day.put("date", rs.getDate("date"));
                day.put("count", rs.getInt("count"));
                day.put("revenue", rs.getDouble("revenue"));
                dailyData.add(day);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dailyData;
    }
    
    public List<Map<String, Object>> getMonthlyRevenue(int months) {
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(booking_date, '%Y-%m') as month, " +
                     "COUNT(*) as count, SUM(total_amount) as revenue " +
                     "FROM bookings " +
                     "WHERE booking_date >= DATE_SUB(CURDATE(), INTERVAL ? MONTH) " +
                     "GROUP BY DATE_FORMAT(booking_date, '%Y-%m') " +
                     "ORDER BY month DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, months);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> month = new HashMap<>();
                month.put("month", rs.getString("month"));
                month.put("count", rs.getInt("count"));
                month.put("revenue", rs.getDouble("revenue"));
                monthlyData.add(month);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return monthlyData;
    }
    
    public List<Map<String, Object>> getRouteRevenue() {
        List<Map<String, Object>> routeData = new ArrayList<>();
        String sql = "SELECT CONCAT(r.origin, ' → ', r.destination) as route, " +
                     "COUNT(b.booking_id) as bookings, " +
                     "SUM(b.total_amount) as revenue, " +
                     "AVG(s.fare) as avgFare " +
                     "FROM bookings b " +
                     "JOIN schedules s ON b.schedule_id = s.schedule_id " +
                     "JOIN routes r ON s.route_id = r.route_id " +
                     "GROUP BY r.route_id " +
                     "ORDER BY revenue DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> route = new HashMap<>();
                route.put("route", rs.getString("route"));
                route.put("bookings", rs.getInt("bookings"));
                route.put("revenue", rs.getDouble("revenue"));
                route.put("avgFare", rs.getDouble("avgFare"));
                routeData.add(route);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routeData;
    }
    
    public List<Map<String, Object>> getBusUtilization() {
        List<Map<String, Object>> busData = new ArrayList<>();
        String sql = "SELECT b.bus_name, b.bus_type, " +
                     "COUNT(DISTINCT s.schedule_id) as trips, " +
                     "COUNT(bk.booking_id) as bookings, " +
                     "COALESCE(SUM(bk.total_amount), 0) as revenue, " +
                     "ROUND((COUNT(bk.booking_id) / (b.total_seats * COUNT(DISTINCT s.schedule_id))) * 100, 2) as utilization " +
                     "FROM buses b " +
                     "LEFT JOIN schedules s ON b.bus_id = s.bus_id " +
                     "LEFT JOIN bookings bk ON s.schedule_id = bk.schedule_id " +
                     "GROUP BY b.bus_id " +
                     "ORDER BY utilization DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> bus = new HashMap<>();
                bus.put("busName", rs.getString("bus_name"));
                bus.put("busType", rs.getString("bus_type"));
                bus.put("trips", rs.getInt("trips"));
                bus.put("bookings", rs.getInt("bookings"));
                bus.put("revenue", rs.getDouble("revenue"));
                bus.put("utilization", rs.getDouble("utilization"));
                busData.add(bus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return busData;
    }
}