package com.busbooking.dao;

import com.busbooking.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    
    public boolean createBooking(int userId, int scheduleId, int seats) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Check available seats
            String checkSql = "SELECT available_seats, fare FROM schedules WHERE schedule_id = ? FOR UPDATE";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setInt(1, scheduleId);
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                return false;
            }
            
            int availableSeats = rs.getInt("available_seats");
            double fare = rs.getDouble("fare");
            
            if (availableSeats < seats) {
                return false;
            }
            
            // Create booking
            String bookingSql = "INSERT INTO bookings (user_id, schedule_id, total_amount, booking_status, payment_status) VALUES (?, ?, ?, 'Confirmed', 'Paid')";
            pstmt = conn.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, scheduleId);
            pstmt.setDouble(3, fare * seats);
            pstmt.executeUpdate();
            
            rs = pstmt.getGeneratedKeys();
            int bookingId = 0;
            if (rs.next()) {
                bookingId = rs.getInt(1);
            }
            
            // Update available seats
            String updateSql = "UPDATE schedules SET available_seats = available_seats - ? WHERE schedule_id = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setInt(1, seats);
            pstmt.setInt(2, scheduleId);
            pstmt.executeUpdate();
            
            // Create seat records
            for (int i = 1; i <= seats; i++) {
                String seatSql = "INSERT INTO seats (schedule_id, seat_number, booking_id, status) VALUES (?, ?, ?, 'Booked')";
                pstmt = conn.prepareStatement(seatSql);
                pstmt.setInt(1, scheduleId);
                pstmt.setString(2, "A" + i);
                pstmt.setInt(3, bookingId);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn, pstmt, rs);
        }
    }
}