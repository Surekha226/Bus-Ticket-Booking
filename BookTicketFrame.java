import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Vector;

public class BookTicketFrame extends JFrame {
    private String username;
    private JTable busTable;
    private DefaultTableModel tableModel;
    private JTextField passengerNameField, passengerAgeField, seatField;
    private JComboBox<String> busComboBox;
    private int selectedBusId = -1;
    private double selectedFare = 0;
    
    // Original constructor
    public BookTicketFrame(String username) {
        this.username = username;
        
        setTitle("Bus Ticket Booking - Book Ticket");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(800, 60));
        
        JLabel titleLabel = new JLabel("Book a Ticket");
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
        
        // Center panel with bus list
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Bus table - FIXED: Changed ₹ to Rs.
        String[] columns = {"Bus ID", "Bus Name", "Type", "From", "To", "Departure", "Arrival", "Fare (Rs.)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        busTable = new JTable(tableModel);
        busTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        busTable.setRowHeight(30);
        busTable.setFont(new Font("Arial", Font.PLAIN, 12));
        busTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        busTable.getTableHeader().setBackground(new Color(52, 73, 94));
        busTable.getTableHeader().setForeground(Color.WHITE);
        
        busTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = busTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedBusId = (int) tableModel.getValueAt(selectedRow, 0);
                    selectedFare = (double) tableModel.getValueAt(selectedRow, 7);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(busTable);
        scrollPane.setPreferredSize(new Dimension(750, 200));
        centerPanel.add(scrollPane, BorderLayout.NORTH);
        
        // Booking form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Booking Details"));
        formPanel.setBackground(new Color(236, 240, 241));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Passenger name
        JLabel nameLabel = new JLabel("Passenger Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        
        passengerNameField = new JTextField(20);
        passengerNameField.setFont(new Font("Arial", Font.PLAIN, 12));
        passengerNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(passengerNameField, gbc);
        
        // Passenger age
        JLabel ageLabel = new JLabel("Passenger Age:");
        ageLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(ageLabel, gbc);
        
        passengerAgeField = new JTextField(20);
        passengerAgeField.setFont(new Font("Arial", Font.PLAIN, 12));
        passengerAgeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(passengerAgeField, gbc);
        
        // Seat number
        JLabel seatLabel = new JLabel("Seat Number:");
        seatLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(seatLabel, gbc);
        
        seatField = new JTextField(20);
        seatField.setFont(new Font("Arial", Font.PLAIN, 12));
        seatField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(seatField, gbc);
        
        // Travel date
        JLabel dateLabel = new JLabel("Travel Date:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(dateLabel, gbc);
        
        JTextField dateField = new JTextField(LocalDate.now().toString());
        dateField.setEditable(false);
        dateField.setFont(new Font("Arial", Font.PLAIN, 12));
        dateField.setBackground(new Color(240, 240, 240));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(dateField, gbc);
        
        // Total fare
        JLabel fareLabel = new JLabel("Total Fare:");
        fareLabel.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(fareLabel, gbc);
        
        JTextField fareField = new JTextField();
        fareField.setEditable(false);
        fareField.setFont(new Font("Arial", Font.BOLD, 14));
        fareField.setBackground(new Color(240, 240, 240));
        fareField.setForeground(new Color(46, 204, 113));
        fareField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(fareField, gbc);
        
        // Book button
        JButton bookButton = new JButton("Book Ticket");
        bookButton.setBackground(new Color(46, 204, 113));
        bookButton.setForeground(Color.WHITE);
        bookButton.setFont(new Font("Arial", Font.BOLD, 16));
        bookButton.setFocusPainted(false);
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(bookButton, gbc);
        
        centerPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Load bus data
        loadBuses();
        
        // Action listeners
        backButton.addActionListener(e -> {
            new DashboardFrame(username);
            dispose();
        });
        
        bookButton.addActionListener(e -> {
            bookTicket();
        });
        
        busTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = busTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedFare = (double) tableModel.getValueAt(selectedRow, 7);
                    // FIXED: Changed ₹ to Rs.
                    fareField.setText("Rs." + selectedFare);
                }
            }
        });
        
        // Add Enter key support
        passengerNameField.addActionListener(e -> passengerAgeField.requestFocus());
        passengerAgeField.addActionListener(e -> seatField.requestFocus());
        seatField.addActionListener(e -> bookTicket());
        
        add(mainPanel);
        setVisible(true);
    }
    
    // Constructor for preselected bus from Search feature
    public BookTicketFrame(String username, int busId, String busName, double fare) {
        this(username); // Call the existing constructor
        // Select the bus in the table after data is loaded
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < busTable.getRowCount(); i++) {
                if ((int) tableModel.getValueAt(i, 0) == busId) {
                    busTable.setRowSelectionInterval(i, i);
                    busTable.scrollRectToVisible(busTable.getCellRect(i, 0, true));
                    // FIXED: Changed ₹ to Rs.
                    JOptionPane.showMessageDialog(this, 
                        "Bus '" + busName + "' has been pre-selected for booking.\nFare: Rs." + fare, 
                        "Bus Selected", 
                        JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
        });
    }
    
    private void loadBuses() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM buses ORDER BY source_city, departure_time";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            tableModel.setRowCount(0);
            
            while (rs.next()) {
                int busId = rs.getInt("bus_id");
                String busName = rs.getString("bus_name");
                String busType = rs.getString("bus_type");
                String source = rs.getString("source_city");
                String dest = rs.getString("destination_city");
                Time depTime = rs.getTime("departure_time");
                Time arrTime = rs.getTime("arrival_time");
                double fare = rs.getDouble("fare");
                
                Vector<Object> row = new Vector<>();
                row.add(busId);
                row.add(busName);
                row.add(busType);
                row.add(source);
                row.add(dest);
                row.add(depTime.toString().substring(0, 5));
                row.add(arrTime.toString().substring(0, 5));
                row.add(fare);
                
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading buses: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void bookTicket() {
        if (selectedBusId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a bus from the table above", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String passengerName = passengerNameField.getText().trim();
        String ageStr = passengerAgeField.getText().trim();
        String seatNumber = seatField.getText().trim();
        
        if (passengerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter passenger name", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            passengerNameField.requestFocus();
            return;
        }
        
        if (ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter passenger age", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            passengerAgeField.requestFocus();
            return;
        }
        
        if (seatNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter seat number", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            seatField.requestFocus();
            return;
        }
        
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 1 || age > 120) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid age (1-120)", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid age (numbers only)", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm booking
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirm booking for " + passengerName + "?\n" +
            "Bus ID: " + selectedBusId + "\n" +
            "Seat: " + seatNumber + "\n" +
            // FIXED: Changed ₹ to Rs.
            "Total Fare: Rs." + selectedFare,
            "Confirm Booking",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get user ID
            String userQuery = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement userStmt = conn.prepareStatement(userQuery);
            userStmt.setString(1, username);
            ResultSet userRs = userStmt.executeQuery();
            
            int userId = -1;
            if (userRs.next()) {
                userId = userRs.getInt("user_id");
            }
            
            if (userId == -1) {
                JOptionPane.showMessageDialog(this, 
                    "User not found. Please login again.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if seat is already booked
            String checkQuery = "SELECT * FROM bookings WHERE bus_id = ? AND seat_number = ? AND travel_date = CURDATE() AND status = 'Confirmed'";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, selectedBusId);
            checkStmt.setString(2, seatNumber);
            ResultSet checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                JOptionPane.showMessageDialog(this, 
                    "Seat " + seatNumber + " is already booked for today!", 
                    "Booking Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Insert booking
            String insertQuery = "INSERT INTO bookings (user_id, bus_id, seat_number, passenger_name, " +
                               "passenger_age, booking_date, travel_date, total_fare, status) " +
                               "VALUES (?, ?, ?, ?, ?, CURDATE(), CURDATE(), ?, 'Confirmed')";
            
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, selectedBusId);
            insertStmt.setString(3, seatNumber);
            insertStmt.setString(4, passengerName);
            insertStmt.setInt(5, age);
            insertStmt.setDouble(6, selectedFare);
            
            int result = insertStmt.executeUpdate();
            
            if (result > 0) {
                // FIXED: Changed ₹ to Rs.
                JOptionPane.showMessageDialog(this, 
                    "✅ Ticket booked successfully!\n\n" +
                    "Passenger: " + passengerName + "\n" +
                    "Seat Number: " + seatNumber + "\n" +
                    "Total Fare: Rs." + selectedFare + "\n\n" +
                    "Thank you for booking with us!", 
                    "Booking Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Clear form
                passengerNameField.setText("");
                passengerAgeField.setText("");
                seatField.setText("");
                busTable.clearSelection();
                selectedBusId = -1;
                selectedFare = 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error booking ticket: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}