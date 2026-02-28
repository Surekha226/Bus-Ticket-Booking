import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class AdminAllBookingsFrame extends JFrame {
    private String adminUsername;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    private JTextField searchField;
    private JButton searchButton, refreshButton, backButton, deleteButton;
    private JLabel totalBookingsLabel, totalRevenueLabel;
    
    public AdminAllBookingsFrame(String adminUsername) {
        this.adminUsername = adminUsername;
        
        setTitle("Admin - All Bookings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with stats
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Load data
        loadAllBookings();
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setPreferredSize(new Dimension(1070, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("ALL BOOKINGS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("View and manage all customer bookings");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        
        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel);
        leftPanel.add(subtitleLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false);
        
        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(44, 62, 80));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(180, 40));
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        backButton.addActionListener(e -> {
            new AdminDashboardFrame(adminUsername);
            dispose();
        });
        
        rightPanel.add(backButton);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
                " Booking Records ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(52, 152, 219)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(Color.WHITE);
        
        panel.add(new JLabel("Filter by:"));
        
        String[] filters = {"All Bookings", "Today's Bookings", "This Week", "This Month", "By Bus", "By User"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        filterCombo.setPreferredSize(new Dimension(150, 30));
        filterCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterCombo.addActionListener(e -> applyFilter());
        
        panel.add(filterCombo);
        
        panel.add(new JLabel("Search:"));
        
        searchField = new JTextField(15);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> searchBookings());
        
        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(46, 204, 113));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadAllBookings());
        
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Table columns
        String[] columns = {
            "Booking ID", "User", "Bus Name", "Passenger", "Age", 
            "Seat", "Travel Date", "Booking Date", "Fare (Rs.)", "Status"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingsTable = new JTable(tableModel);
        bookingsTable.setRowHeight(35);
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        bookingsTable.getTableHeader().setBackground(new Color(52, 73, 94));
        bookingsTable.getTableHeader().setForeground(Color.WHITE);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add color coding for status
        bookingsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                if (column == 9) { // Status column
                    String status = value.toString();
                    if (status.equals("Confirmed")) {
                        c.setForeground(new Color(39, 174, 96)); // Green
                    } else if (status.equals("Cancelled")) {
                        c.setForeground(new Color(192, 57, 43)); // Red
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setForeground(Color.BLACK);
                }
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(245, 245, 245));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setPreferredSize(new Dimension(1050, 350));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel below table
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        deleteButton = new JButton("Cancel Selected Booking");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> cancelBooking());
        
        bookingsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(bookingsTable.getSelectedRow() != -1);
            }
        });
        
        actionPanel.add(deleteButton);
        
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        panel.setPreferredSize(new Dimension(1070, 70));
        
        totalBookingsLabel = new JLabel("Total Bookings: 0", SwingConstants.CENTER);
        totalBookingsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalBookingsLabel.setForeground(Color.WHITE);
        
        totalRevenueLabel = new JLabel("Total Revenue: Rs. 0", SwingConstants.CENTER);
        totalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalRevenueLabel.setForeground(Color.WHITE);
        
        panel.add(totalBookingsLabel);
        panel.add(totalRevenueLabel);
        
        return panel;
    }
    
    private void loadAllBookings() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT b.booking_id, u.username, bs.bus_name, b.passenger_name, " +
                         "b.passenger_age, b.seat_number, b.travel_date, b.booking_date, " +
                         "b.total_fare, b.status " +
                         "FROM bookings b " +
                         "JOIN users u ON b.user_id = u.user_id " +
                         "JOIN buses bs ON b.bus_id = bs.bus_id " +
                         "ORDER BY b.booking_date DESC, b.booking_id DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            displayResults(rs);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading bookings: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayResults(ResultSet rs) throws SQLException {
        tableModel.setRowCount(0);
        double totalRevenue = 0;
        int bookingCount = 0;
        
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("booking_id"));
            row.add(rs.getString("username"));
            row.add(rs.getString("bus_name"));
            row.add(rs.getString("passenger_name"));
            row.add(rs.getInt("passenger_age"));
            row.add(rs.getString("seat_number"));
            row.add(rs.getDate("travel_date"));
            row.add(rs.getDate("booking_date"));
            double fare = rs.getDouble("total_fare");
            row.add(fare);
            row.add(rs.getString("status"));
            
            tableModel.addRow(row);
            
            if (rs.getString("status").equals("Confirmed")) {
                totalRevenue += fare;
                bookingCount++;
            }
        }
        
        totalBookingsLabel.setText("Total Bookings: " + bookingCount);
        totalRevenueLabel.setText("Total Revenue: Rs. " + totalRevenue);
    }
    
    private void applyFilter() {
        String filter = (String) filterCombo.getSelectedItem();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "";
            
            switch (filter) {
                case "Today's Bookings":
                    query = "SELECT b.booking_id, u.username, bs.bus_name, b.passenger_name, " +
                           "b.passenger_age, b.seat_number, b.travel_date, b.booking_date, " +
                           "b.total_fare, b.status " +
                           "FROM bookings b " +
                           "JOIN users u ON b.user_id = u.user_id " +
                           "JOIN buses bs ON b.bus_id = bs.bus_id " +
                           "WHERE DATE(b.travel_date) = CURDATE() " +
                           "ORDER BY b.booking_date DESC";
                    break;
                    
                case "This Week":
                    query = "SELECT b.booking_id, u.username, bs.bus_name, b.passenger_name, " +
                           "b.passenger_age, b.seat_number, b.travel_date, b.booking_date, " +
                           "b.total_fare, b.status " +
                           "FROM bookings b " +
                           "JOIN users u ON b.user_id = u.user_id " +
                           "JOIN buses bs ON b.bus_id = bs.bus_id " +
                           "WHERE YEARWEEK(b.travel_date) = YEARWEEK(CURDATE()) " +
                           "ORDER BY b.booking_date DESC";
                    break;
                    
                case "This Month":
                    query = "SELECT b.booking_id, u.username, bs.bus_name, b.passenger_name, " +
                           "b.passenger_age, b.seat_number, b.travel_date, b.booking_date, " +
                           "b.total_fare, b.status " +
                           "FROM bookings b " +
                           "JOIN users u ON b.user_id = u.user_id " +
                           "JOIN buses bs ON b.bus_id = bs.bus_id " +
                           "WHERE MONTH(b.travel_date) = MONTH(CURDATE()) " +
                           "AND YEAR(b.travel_date) = YEAR(CURDATE()) " +
                           "ORDER BY b.booking_date DESC";
                    break;
                    
                default:
                    loadAllBookings();
                    return;
            }
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            displayResults(rs);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error applying filter: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchBookings() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllBookings();
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT b.booking_id, u.username, bs.bus_name, b.passenger_name, " +
                         "b.passenger_age, b.seat_number, b.travel_date, b.booking_date, " +
                         "b.total_fare, b.status " +
                         "FROM bookings b " +
                         "JOIN users u ON b.user_id = u.user_id " +
                         "JOIN buses bs ON b.bus_id = bs.bus_id " +
                         "WHERE u.username LIKE ? OR b.passenger_name LIKE ? OR bs.bus_name LIKE ? " +
                         "OR b.seat_number LIKE ? OR b.status LIKE ? " +
                         "ORDER BY b.booking_date DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setString(5, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            displayResults(rs);
            
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No bookings found matching '" + searchTerm + "'", 
                    "Search Results", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error searching bookings: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        String passengerName = (String) tableModel.getValueAt(selectedRow, 3);
        String status = (String) tableModel.getValueAt(selectedRow, 9);
        
        if (status.equals("Cancelled")) {
            JOptionPane.showMessageDialog(this, 
                "This booking is already cancelled", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Cancel booking for " + passengerName + "?\n" +
            "Booking ID: " + bookingId + "\n\n" +
            "This action cannot be undone!",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
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
                    loadAllBookings(); // Refresh
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