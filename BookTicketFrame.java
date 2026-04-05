import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class BookTicketFrame extends JFrame {
    private String username;
    private JTable busTable;
    private DefaultTableModel tableModel;
    private JTextField passengerNameField, passengerAgeField;
    private JPanel seatGridPanel;
    private JButton selectedSeatButton = null;
    private int selectedBusId = -1;
    private double selectedFare = 0;
    private JLabel seatSelectionLabel;
    private JTextField fareField;
    private Set<String> bookedSeats = new HashSet<>();
    private String selectedSeatNumber = "";
    
    // Constants for seat grid
    private static final int SEAT_ROWS = 10;
    private static final int SEAT_COLS = 4;
    private static final int TOTAL_SEATS = SEAT_ROWS * SEAT_COLS; // 40 seats
    
    public BookTicketFrame(String username) {
        this.username = username;
        
        setTitle("Bus Ticket Booking - Book Ticket");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel with bus list and booking
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Load bus data
        loadBuses();
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(1100, 60));
        
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
        backButton.addActionListener(e -> {
            new DashboardFrame(username);
            dispose();
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.setBackground(new Color(236, 240, 241));
        
        // Bus table
        String[] columns = {"Bus ID", "Bus Name", "Type", "From", "To", "Departure", "Arrival", "Fare (Rs.)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        busTable = new JTable(tableModel);
        busTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        busTable.setRowHeight(25);
        busTable.setFont(new Font("Arial", Font.PLAIN, 11));
        busTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        busTable.getTableHeader().setBackground(new Color(52, 73, 94));
        busTable.getTableHeader().setForeground(Color.WHITE);
        
        busTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = busTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedBusId = (int) tableModel.getValueAt(selectedRow, 0);
                    selectedFare = (double) tableModel.getValueAt(selectedRow, 7);
                    fareField.setText("Rs. " + selectedFare);
                    
                    // Load seat availability for selected bus
                    loadSeatAvailability();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(busTable);
        scrollPane.setPreferredSize(new Dimension(1050, 120));
        centerPanel.add(scrollPane, BorderLayout.NORTH);
        
        // Split pane for booking form and seat selection
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setBorder(null);
        
        // Left panel - Booking form
        JPanel formPanel = createBookingFormPanel();
        splitPane.setLeftComponent(formPanel);
        
        // Right panel - Seat selection
        JPanel seatPanel = createSeatSelectionPanel();
        splitPane.setRightComponent(seatPanel);
        
        centerPanel.add(splitPane, BorderLayout.CENTER);
        
        return centerPanel;
    }
    
    private JPanel createBookingFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94), 2),
            "Booking Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(52, 73, 94)
        ));
        formPanel.setBackground(new Color(236, 240, 241));
        formPanel.setPreferredSize(new Dimension(280, 450));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Passenger name
        JLabel nameLabel = new JLabel("Passenger Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(nameLabel, gbc);
        
        passengerNameField = new JTextField(12);
        passengerNameField.setFont(new Font("Arial", Font.PLAIN, 11));
        passengerNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 1),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(passengerNameField, gbc);
        
        // Passenger age
        JLabel ageLabel = new JLabel("Passenger Age:");
        ageLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(ageLabel, gbc);
        
        passengerAgeField = new JTextField(12);
        passengerAgeField.setFont(new Font("Arial", Font.PLAIN, 11));
        passengerAgeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 1),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(passengerAgeField, gbc);
        
        // Selected seat
        JLabel seatLabel = new JLabel("Selected Seat:");
        seatLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(seatLabel, gbc);
        
        seatSelectionLabel = new JLabel("None");
        seatSelectionLabel.setFont(new Font("Arial", Font.BOLD, 12));
        seatSelectionLabel.setForeground(new Color(52, 152, 219));
        seatSelectionLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        seatSelectionLabel.setPreferredSize(new Dimension(80, 22));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(seatSelectionLabel, gbc);
        
        // Travel date
        JLabel dateLabel = new JLabel("Travel Date:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(dateLabel, gbc);
        
        JTextField dateField = new JTextField(LocalDate.now().toString());
        dateField.setEditable(false);
        dateField.setFont(new Font("Arial", Font.PLAIN, 11));
        dateField.setBackground(new Color(240, 240, 240));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(dateField, gbc);
        
        // Total fare
        JLabel fareLabel = new JLabel("Total Fare:");
        fareLabel.setFont(new Font("Arial", Font.BOLD, 11));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(fareLabel, gbc);
        
        fareField = new JTextField(12);
        fareField.setEditable(false);
        fareField.setFont(new Font("Arial", Font.BOLD, 12));
        fareField.setBackground(new Color(240, 240, 240));
        fareField.setForeground(new Color(46, 204, 113));
        fareField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(fareField, gbc);
        
        // Book button
        JButton bookButton = new JButton("Book Ticket");
        bookButton.setBackground(new Color(46, 204, 113));
        bookButton.setForeground(Color.WHITE);
        bookButton.setFont(new Font("Arial", Font.BOLD, 14));
        bookButton.setFocusPainted(false);
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        bookButton.addActionListener(e -> bookTicket());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.add(bookButton);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    private JPanel createSeatSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 73, 94), 2),
            "Select Your Seat",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(52, 73, 94)
        ));
        panel.setBackground(new Color(236, 240, 241));
        
        // Legend panel
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 2));
        legendPanel.setBackground(new Color(236, 240, 241));
        
        legendPanel.add(createLegendItem("Available", new Color(46, 204, 113)));
        legendPanel.add(createLegendItem("Booked", new Color(231, 76, 60)));
        legendPanel.add(createLegendItem("Selected", new Color(52, 152, 219)));
        
        panel.add(legendPanel, BorderLayout.NORTH);
        
        // Seat grid panel - 10 rows x 4 columns = 40 seats
        seatGridPanel = new JPanel(new GridLayout(SEAT_ROWS, SEAT_COLS, 5, 5));
        seatGridPanel.setBackground(Color.WHITE);
        seatGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(seatGridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(500, 450));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load initial empty grid
        loadEmptySeatGrid();
        
        return panel;
    }
    
    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        panel.setBackground(new Color(236, 240, 241));
        
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        
        panel.add(colorBox);
        panel.add(label);
        
        return panel;
    }
    
    private void loadEmptySeatGrid() {
        seatGridPanel.removeAll();
        
        // Create 40 seats (10 rows x 4 columns)
        for (int row = 0; row < SEAT_ROWS; row++) {
            for (int col = 0; col < SEAT_COLS; col++) {
                int seatNum = (row * SEAT_COLS) + col + 1;
                String seatNumber = String.format("%02d", seatNum);
                
                JButton seatButton = createSmallSeatButton(seatNumber, Color.LIGHT_GRAY);
                seatButton.setEnabled(false);
                seatGridPanel.add(seatButton);
            }
        }
        
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }
    
    private JButton createSmallSeatButton(String seatNumber, Color color) {
        JButton button = new JButton(seatNumber);
        button.setBackground(color);
        button.setForeground(color.equals(Color.LIGHT_GRAY) ? Color.BLACK : Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 10));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        button.setPreferredSize(new Dimension(45, 35));
        button.setMargin(new Insets(1, 1, 1, 1));
        
        return button;
    }
    
    private void loadSeatAvailability() {
        if (selectedBusId == -1) {
            loadEmptySeatGrid();
            return;
        }
        
        bookedSeats.clear();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Query to get booked seats for selected bus
            // Note: Using CURDATE() for MySQL. For Oracle, use TRUNC(SYSDATE)
            String query = "SELECT seat_number FROM bookings WHERE bus_id = ? AND travel_date = CURDATE() AND status = 'Confirmed'";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedBusId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookedSeats.add(rs.getString("seat_number"));
            }
            
            // Create seat grid (40 seats: 10 rows x 4 columns)
            seatGridPanel.removeAll();
            
            for (int row = 0; row < SEAT_ROWS; row++) {
                for (int col = 0; col < SEAT_COLS; col++) {
                    int seatNum = (row * SEAT_COLS) + col + 1;
                    String seatNumber = String.format("%02d", seatNum);
                    
                    JButton seatButton;
                    
                    if (bookedSeats.contains(seatNumber)) {
                        // Booked seat - RED
                        seatButton = createSmallSeatButton(seatNumber, new Color(231, 76, 60));
                        seatButton.setEnabled(false);
                        seatButton.setToolTipText("Seat " + seatNumber + " - Already Booked");
                    } else {
                        // Available seat - GREEN
                        seatButton = createSmallSeatButton(seatNumber, new Color(46, 204, 113));
                        seatButton.setEnabled(true);
                        seatButton.setToolTipText("Seat " + seatNumber + " - Available");
                        
                        final String seatNumStr = seatNumber;
                        final JButton button = seatButton;
                        seatButton.addActionListener(e -> selectSeat(button, seatNumStr));
                    }
                    
                    seatGridPanel.add(seatButton);
                }
            }
            
            seatGridPanel.revalidate();
            seatGridPanel.repaint();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading seat availability: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void selectSeat(JButton seatButton, String seatNumber) {
        // Deselect previous seat
        if (selectedSeatButton != null) {
            selectedSeatButton.setBackground(new Color(46, 204, 113));
            selectedSeatButton.setForeground(Color.WHITE);
        }
        
        // Select new seat
        selectedSeatButton = seatButton;
        selectedSeatNumber = seatNumber;
        seatButton.setBackground(new Color(52, 152, 219)); // Blue for selected
        seatButton.setForeground(Color.WHITE);
        seatSelectionLabel.setText(seatNumber);
    }
    
    public BookTicketFrame(String username, int busId, String busName, double fare) {
        this(username);
        
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < busTable.getRowCount(); i++) {
                if ((int) tableModel.getValueAt(i, 0) == busId) {
                    busTable.setRowSelectionInterval(i, i);
                    busTable.scrollRectToVisible(busTable.getCellRect(i, 0, true));
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
        String seatNumber = selectedSeatNumber;
        
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
                "Please select a seat from the grid", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
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
        
        // Double-check if seat is still available
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkQuery = "SELECT * FROM bookings WHERE bus_id = ? AND seat_number = ? AND travel_date = CURDATE() AND status = 'Confirmed'";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, selectedBusId);
            checkStmt.setString(2, seatNumber);
            ResultSet checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                JOptionPane.showMessageDialog(this, 
                    "Sorry! Seat " + seatNumber + " was just booked by someone else.\nPlease select another seat.", 
                    "Seat Unavailable", 
                    JOptionPane.ERROR_MESSAGE);
                loadSeatAvailability();
                selectedSeatNumber = "";
                seatSelectionLabel.setText("None");
                selectedSeatButton = null;
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        // Confirm booking
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirm booking for " + passengerName + "?\n" +
            "Bus ID: " + selectedBusId + "\n" +
            "Seat: " + seatNumber + "\n" +
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
                selectedSeatNumber = "";
                seatSelectionLabel.setText("None");
                selectedSeatButton = null;
                fareField.setText("");
                busTable.clearSelection();
                selectedBusId = -1;
                selectedFare = 0;
                
                // Reset seat grid
                loadEmptySeatGrid();
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
