package com.busbooking.dao;

import com.busbooking.model.Bus;
import com.busbooking.model.Route;
import com.busbooking.model.Schedule;
import com.busbooking.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BusDAO {
    
    public List<Schedule> searchBuses(String origin, String destination, Date travelDate) {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT s.*, b.bus_name, b.bus_type, b.total_seats, b.amenities, " +
                    "r.origin, r.destination, r.distance, r.duration " +
                    "FROM schedules s " +
                    "JOIN buses b ON s.bus_id = b.bus_id " +
                    "JOIN routes r ON s.route_id = r.route_id " +
                    "WHERE r.origin = ? AND r.destination = ? AND DATE(s.departure_time) = ? " +
                    "AND s.available_seats > 0";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, origin);
            pstmt.setString(2, destination);
            pstmt.setDate(3, new java.sql.Date(travelDate.getTime()));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Schedule schedule = new Schedule();
                schedule.setScheduleId(rs.getInt("schedule_id"));
                schedule.setDepartureTime(rs.getTimestamp("departure_time"));
                schedule.setArrivalTime(rs.getTimestamp("arrival_time"));
                schedule.setFare(rs.getDouble("fare"));
                schedule.setAvailableSeats(rs.getInt("available_seats"));
                
                Bus bus = new Bus();
                bus.setBusName(rs.getString("bus_name"));
                bus.setBusType(rs.getString("bus_type"));
                bus.setTotalSeats(rs.getInt("total_seats"));
                bus.setAmenities(rs.getString("amenities"));
                schedule.setBus(bus);
                
                Route route = new Route();
                route.setOrigin(rs.getString("origin"));
                route.setDestination(rs.getString("destination"));
                schedule.setRoute(route);
                
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }
    
    public List<String> getAvailableDates(String origin, String destination) {
        List<String> dates = new ArrayList<>();
        String sql = "SELECT DISTINCT DATE(departure_time) as travel_date " +
                     "FROM schedules s " +
                     "JOIN routes r ON s.route_id = r.route_id " +
                     "WHERE r.origin = ? AND r.destination = ? " +
                     "AND departure_time >= CURDATE() " +
                     "AND s.available_seats > 0 " +
                     "ORDER BY travel_date " +
                     "LIMIT 30";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, origin);
            pstmt.setString(2, destination);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Date date = rs.getDate("travel_date");
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                dates.add(sdf.format(date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }
}