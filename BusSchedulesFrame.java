import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class BusSchedulesFrame extends JFrame {
    private String adminUsername;
    private JTable schedulesTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> busCombo;
    private JTextField dateField, departureTimeField, arrivalTimeField, fareField, availableSeatsField;
    private JButton addButton, updateButton, deleteButton, refreshButton, backButton;
    private JCheckBox recurringCheck;
    private JSpinner recurringDaysSpinner;
    private int selectedScheduleId = -1;
    
    public BusSchedulesFrame(String adminUsername) {
        this.adminUsername = adminUsername;
        
        setTitle("Admin - Bus Schedules");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 126, 34), 3),
                " Bus Schedules Management ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18),
                new Color(230, 126, 34)
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Top controls - ENHANCED VISIBILITY
        JPanel topPanel = createTopPanel();
        centerPanel.add(topPanel, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = createFormPanel();
        centerPanel.add(formPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Load data
        loadBuses();
        loadSchedules();
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(230, 126, 34));
        headerPanel.setPreferredSize(new Dimension(1170, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("BUS SCHEDULES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Manage bus departure schedules and availability");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);
        
        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel);
        leftPanel.add(subtitleLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false);
        
        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(52, 73, 94));
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
    
    // ENHANCED: Top panel with visible bus selector
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 126, 34)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel busLabel = new JLabel("Select Bus:");
        busLabel.setFont(new Font("Arial", Font.BOLD, 16));
        busLabel.setForeground(new Color(44, 62, 80));
        panel.add(busLabel);
        
        busCombo = new JComboBox<>();
        busCombo.setPreferredSize(new Dimension(300, 35));
        busCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        busCombo.setBackground(Color.WHITE);
        busCombo.setForeground(Color.BLACK);
        busCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 126, 34), 2),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        busCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        busCombo.addActionListener(e -> filterByBus());
        panel.add(busCombo);
        
        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(46, 204, 113));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(120, 35));
        refreshButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        refreshButton.addActionListener(e -> {
            loadBuses();
            loadSchedules();
        });
        panel.add(refreshButton);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        String[] columns = {"Schedule ID", "Bus Name", "Travel Date", "Departure", "Arrival", "Fare (Rs.)", "Available Seats", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        schedulesTable = new JTable(tableModel);
        schedulesTable.setRowHeight(35);
        schedulesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        schedulesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        schedulesTable.getTableHeader().setBackground(new Color(230, 126, 34));
        schedulesTable.getTableHeader().setForeground(Color.WHITE);
        schedulesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        schedulesTable.setGridColor(new Color(230, 126, 34, 50));
        schedulesTable.setShowGrid(true);
        
        schedulesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedSchedule();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(schedulesTable);
        scrollPane.setPreferredSize(new Dimension(1150, 250));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 126, 34), 1));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(230, 126, 34), 1),
            " Schedule Details ",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(230, 126, 34)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 0: Travel Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel dateLabel = new JLabel("Travel Date:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(dateLabel, gbc);
        
        dateField = new JTextField(15);
        dateField.setText(LocalDate.now().toString());
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        panel.add(dateField, gbc);
        
        JLabel dateHint = new JLabel("(YYYY-MM-DD)");
        dateHint.setFont(new Font("Arial", Font.ITALIC, 12));
        dateHint.setForeground(Color.GRAY);
        gbc.gridx = 2;
        panel.add(dateHint, gbc);
        
        // Row 1: Departure Time
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel depLabel = new JLabel("Departure Time:");
        depLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(depLabel, gbc);
        
        departureTimeField = new JTextField(10);
        departureTimeField.setText("08:00:00");
        departureTimeField.setFont(new Font("Arial", Font.PLAIN, 14));
        departureTimeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        panel.add(departureTimeField, gbc);
        
        JLabel timeHint = new JLabel("(HH:MM:SS)");
        timeHint.setFont(new Font("Arial", Font.ITALIC, 12));
        timeHint.setForeground(Color.GRAY);
        gbc.gridx = 2;
        panel.add(timeHint, gbc);
        
        // Row 2: Arrival Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel arrLabel = new JLabel("Arrival Time:");
        arrLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(arrLabel, gbc);
        
        arrivalTimeField = new JTextField(10);
        arrivalTimeField.setText("12:00:00");
        arrivalTimeField.setFont(new Font("Arial", Font.PLAIN, 14));
        arrivalTimeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        panel.add(arrivalTimeField, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("(HH:MM:SS)"), gbc);
        
        // Row 3: Fare and Available Seats
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel fareLabel = new JLabel("Fare (Rs.):");
        fareLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(fareLabel, gbc);
        
        fareField = new JTextField(10);
        fareField.setFont(new Font("Arial", Font.PLAIN, 14));
        fareField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 1;
        panel.add(fareField, gbc);
        
        gbc.gridx = 2;
        JLabel seatsLabel = new JLabel("Available Seats:");
        seatsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(seatsLabel, gbc);
        
        availableSeatsField = new JTextField(5);
        availableSeatsField.setFont(new Font("Arial", Font.PLAIN, 14));
        availableSeatsField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gbc.gridx = 3;
        panel.add(availableSeatsField, gbc);
        
        // Row 4: Recurring options
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel recurringLabel = new JLabel("Recurring:");
        recurringLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(recurringLabel, gbc);
        
        recurringCheck = new JCheckBox("Create for multiple days");
        recurringCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        recurringCheck.setBackground(Color.WHITE);
        recurringCheck.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(recurringCheck, gbc);
        
        // Row 5: Recurring days
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        JLabel repeatLabel = new JLabel("Repeat for days:");
        repeatLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(repeatLabel, gbc);
        
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(7, 1, 30, 1);
        recurringDaysSpinner = new JSpinner(spinnerModel);
        recurringDaysSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        recurringDaysSpinner.setPreferredSize(new Dimension(80, 35));
        recurringDaysSpinner.setEnabled(false);
        gbc.gridx = 1;
        panel.add(recurringDaysSpinner, gbc);
        
        recurringCheck.addActionListener(e -> 
            recurringDaysSpinner.setEnabled(recurringCheck.isSelected())
        );
        
        // Row 6: Buttons - NO EMOJIS
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        addButton = createButton("Add Schedule", new Color(46, 204, 113));
        updateButton = createButton("Update", new Color(52, 152, 219));
        deleteButton = createButton("Delete", new Color(231, 76, 60));
        JButton clearButton = createButton("Clear", new Color(149, 165, 166));
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(15, 8, 8, 8);
        panel.add(buttonPanel, gbc);
        
        // Action listeners
        addButton.addActionListener(e -> addSchedule());
        updateButton.addActionListener(e -> updateSchedule());
        deleteButton.addActionListener(e -> deleteSchedule());
        clearButton.addActionListener(e -> clearForm());
        
        return panel;
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 40));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Ensure button is visible
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void loadBuses() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT bus_id, bus_name FROM buses ORDER BY bus_name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            busCombo.removeAllItems();
            busCombo.addItem("All Buses");
            
            while (rs.next()) {
                String item = rs.getInt("bus_id") + " - " + rs.getString("bus_name");
                busCombo.addItem(item);
            }
            
            // Force repaint
            busCombo.repaint();
            busCombo.revalidate();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading buses: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSchedules() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT bs.schedule_id, b.bus_name, bs.departure_date, " +
                         "b.departure_time, b.arrival_time, bs.fare, bs.available_seats, bs.status " +
                         "FROM bus_schedules bs " +
                         "JOIN buses b ON bs.bus_id = b.bus_id " +
                         "ORDER BY bs.departure_date DESC, bs.schedule_id DESC";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            tableModel.setRowCount(0);
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("schedule_id"));
                row.add(rs.getString("bus_name"));
                row.add(rs.getDate("departure_date"));
                row.add(rs.getTime("departure_time").toString().substring(0, 5));
                row.add(rs.getTime("arrival_time").toString().substring(0, 5));
                row.add(rs.getDouble("fare"));
                row.add(rs.getInt("available_seats"));
                row.add(rs.getString("status"));
                
                tableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading schedules: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterByBus() {
        String selected = (String) busCombo.getSelectedItem();
        if (selected == null || selected.equals("All Buses")) {
            loadSchedules();
            return;
        }
        
        int busId = Integer.parseInt(selected.split(" - ")[0]);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT bs.schedule_id, b.bus_name, bs.departure_date, " +
                         "b.departure_time, b.arrival_time, bs.fare, bs.available_seats, bs.status " +
                         "FROM bus_schedules bs " +
                         "JOIN buses b ON bs.bus_id = b.bus_id " +
                         "WHERE bs.bus_id = ? " +
                         "ORDER BY bs.departure_date DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, busId);
            ResultSet rs = pstmt.executeQuery();
            
            tableModel.setRowCount(0);
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("schedule_id"));
                row.add(rs.getString("bus_name"));
                row.add(rs.getDate("departure_date"));
                row.add(rs.getTime("departure_time").toString().substring(0, 5));
                row.add(rs.getTime("arrival_time").toString().substring(0, 5));
                row.add(rs.getDouble("fare"));
                row.add(rs.getInt("available_seats"));
                row.add(rs.getString("status"));
                
                tableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error filtering schedules: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSelectedSchedule() {
        int selectedRow = schedulesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        selectedScheduleId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Get bus name and set combo
        String busName = (String) tableModel.getValueAt(selectedRow, 1);
        for (int i = 0; i < busCombo.getItemCount(); i++) {
            String item = busCombo.getItemAt(i);
            if (item.contains(busName)) {
                busCombo.setSelectedIndex(i);
                break;
            }
        }
        
        dateField.setText(tableModel.getValueAt(selectedRow, 2).toString());
        
        // Get times from bus table
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT departure_time, arrival_time FROM buses WHERE bus_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, busName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                departureTimeField.setText(rs.getTime("departure_time").toString());
                arrivalTimeField.setText(rs.getTime("arrival_time").toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        fareField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 5)));
        availableSeatsField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 6)));
    }
    
    private void clearForm() {
        selectedScheduleId = -1;
        busCombo.setSelectedIndex(0);
        dateField.setText(LocalDate.now().toString());
        departureTimeField.setText("08:00:00");
        arrivalTimeField.setText("12:00:00");
        fareField.setText("");
        availableSeatsField.setText("");
        recurringCheck.setSelected(false);
        recurringDaysSpinner.setEnabled(false);
        schedulesTable.clearSelection();
    }
    
    private void addSchedule() {
        if (!validateFields()) return;
        
        String selectedBus = (String) busCombo.getSelectedItem();
        if (selectedBus == null || selectedBus.equals("All Buses")) {
            JOptionPane.showMessageDialog(this, 
                "Please select a specific bus", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int busId = Integer.parseInt(selectedBus.split(" - ")[0]);
        LocalDate date = LocalDate.parse(dateField.getText());
        
        if (recurringCheck.isSelected()) {
            int days = (Integer) recurringDaysSpinner.getValue();
            createRecurringSchedules(busId, date, days);
        } else {
            createSingleSchedule(busId, date);
        }
    }
    
    private void createSingleSchedule(int busId, LocalDate date) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if schedule exists
            String checkQuery = "SELECT * FROM bus_schedules WHERE bus_id = ? AND departure_date = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, busId);
            checkStmt.setDate(2, Date.valueOf(date));
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, 
                    "Schedule already exists for this date", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String query = "INSERT INTO bus_schedules (bus_id, departure_date, fare, available_seats, status) " +
                         "VALUES (?, ?, ?, ?, 'Scheduled')";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, busId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setDouble(3, Double.parseDouble(fareField.getText()));
            pstmt.setInt(4, Integer.parseInt(availableSeatsField.getText()));
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Schedule added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                filterByBus();
                clearForm();
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error adding schedule: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createRecurringSchedules(int busId, LocalDate startDate, int days) {
        int successCount = 0;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            for (int i = 0; i < days; i++) {
                LocalDate date = startDate.plusDays(i);
                
                String checkQuery = "SELECT * FROM bus_schedules WHERE bus_id = ? AND departure_date = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setInt(1, busId);
                checkStmt.setDate(2, Date.valueOf(date));
                ResultSet rs = checkStmt.executeQuery();
                
                if (!rs.next()) {
                    String query = "INSERT INTO bus_schedules (bus_id, departure_date, fare, available_seats, status) " +
                                 "VALUES (?, ?, ?, ?, 'Scheduled')";
                    
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, busId);
                    pstmt.setDate(2, Date.valueOf(date));
                    pstmt.setDouble(3, Double.parseDouble(fareField.getText()));
                    pstmt.setInt(4, Integer.parseInt(availableSeatsField.getText()));
                    
                    pstmt.executeUpdate();
                    successCount++;
                }
            }
            
            conn.commit();
            
            JOptionPane.showMessageDialog(this, 
                successCount + " schedules created successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            filterByBus();
            clearForm();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error creating schedules: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateSchedule() {
        if (selectedScheduleId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a schedule to update", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!validateFields()) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Update this schedule?", "Confirm Update",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE bus_schedules SET fare = ?, available_seats = ? WHERE schedule_id = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setDouble(1, Double.parseDouble(fareField.getText()));
            pstmt.setInt(2, Integer.parseInt(availableSeatsField.getText()));
            pstmt.setInt(3, selectedScheduleId);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Schedule updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                filterByBus();
                clearForm();
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error updating schedule: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSchedule() {
        if (selectedScheduleId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a schedule to delete", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this schedule? This action cannot be undone!",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM bus_schedules WHERE schedule_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, selectedScheduleId);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Schedule deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                filterByBus();
                clearForm();
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error deleting schedule: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateFields() {
        if (fareField.getText().trim().isEmpty() || availableSeatsField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all fields", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            double fare = Double.parseDouble(fareField.getText());
            if (fare <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Fare must be positive", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Fare must be a number", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int seats = Integer.parseInt(availableSeatsField.getText());
            if (seats <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Available seats must be positive", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Available seats must be a number", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            LocalDate.parse(dateField.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid date format. Use YYYY-MM-DD", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
}