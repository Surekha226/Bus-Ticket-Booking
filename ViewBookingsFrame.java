import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ViewBookingsFrame extends JFrame {
    private String username;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    
    public ViewBookingsFrame(String username) {
        this.username = username;
        
        setTitle("Bus Ticket Booking - My Bookings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(900, 60));
        
        JLabel titleLabel = new JLabel("My Bookings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 10));
        
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Bookings table - Using Rs. instead of currency symbol
        String[] columns = {"Booking ID", "Bus Name", "Passenger", "Seat", "Travel Date", "Fare (Rs.)", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingsTable = new JTable(tableModel);
        bookingsTable.setRowHeight(30);
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        bookingsTable.getTableHeader().setBackground(new Color(52, 73, 94));
        bookingsTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Cancel button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(236, 240, 241));
        
        JButton cancelButton = new JButton("Cancel Selected Booking");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(192, 57, 43), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load bookings
        loadBookings();
        
        // Action listeners
        backButton.addActionListener(e -> {
            new DashboardFrame(username);
            dispose();
        });
        
        refreshButton.addActionListener(e -> {
            loadBookings();
        });
        
        cancelButton.addActionListener(e -> {
            cancelBooking();
        });
        
        add(mainPanel);
        setVisible(true);
    }
    
    private void loadBookings() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT b.booking_id, bs.bus_name, b.passenger_name, b.seat_number, " +
                         "b.travel_date, b.total_fare, b.status " +
                         "FROM bookings b " +
                         "JOIN buses bs ON b.bus_id = bs.bus_id " +
                         "JOIN users u ON b.user_id = u.user_id " +
                         "WHERE u.username = ? " +
                         "ORDER BY b.booking_date DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            tableModel.setRowCount(0);
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("booking_id"));
                row.add(rs.getString("bus_name"));
                row.add(rs.getString("passenger_name"));
                row.add(rs.getString("seat_number"));
                row.add(rs.getDate("travel_date"));
                row.add(rs.getDouble("total_fare"));
                row.add(rs.getString("status"));
                
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading bookings: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a booking to cancel", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        String busName = (String) tableModel.getValueAt(selectedRow, 1);
        String passengerName = (String) tableModel.getValueAt(selectedRow, 2);
        String seatNumber = (String) tableModel.getValueAt(selectedRow, 3);
        String status = (String) tableModel.getValueAt(selectedRow, 6);
        
        if (status.equals("Cancelled")) {
            JOptionPane.showMessageDialog(this, 
                "This booking is already cancelled", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this booking?\n\n" +
            "Passenger: " + passengerName + "\n" +
            "Bus: " + busName + "\n" +
            "Seat: " + seatNumber + "\n" +
            "Booking ID: " + bookingId,
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "UPDATE bookings SET status = 'Cancelled' WHERE booking_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, bookingId);
                
                int result = pstmt.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Booking cancelled successfully", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadBookings(); // Refresh the table
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error cancelling booking: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}