import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class AdminManageBusesFrame extends JFrame {
    private String adminUsername;
    private JTable busesTable;
    private DefaultTableModel tableModel;
    private JTextField busNameField, busTypeField, totalSeatsField, sourceField, destField, fareField;
    private JTextField depTimeField, arrTimeField;
    private JButton addButton, updateButton, deleteButton, refreshButton, backButton;
    private int selectedBusId = -1;
    
    public AdminManageBusesFrame(String adminUsername) {
        this.adminUsername = adminUsername;
        
        setTitle("Admin - Manage Buses");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Bus table
        JPanel tablePanel = createTablePanel();
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = createFormPanel();
        centerPanel.add(formPanel, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Load data
        loadBuses();
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setPreferredSize(new Dimension(1000, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Manage Buses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            new AdminDashboardFrame(adminUsername);
            dispose();
        });
        
        buttonPanel.add(backButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Bus List"));
        
        String[] columns = {"Bus ID", "Bus Name", "Type", "Seats", "From", "To", "Departure", "Arrival", "Fare (Rs.)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        busesTable = new JTable(tableModel);
        busesTable.setRowHeight(30);
        busesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        busesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        busesTable.getTableHeader().setBackground(new Color(52, 73, 94));
        busesTable.getTableHeader().setForeground(Color.WHITE);
        busesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        busesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedBus();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(busesTable);
        scrollPane.setPreferredSize(new Dimension(950, 250));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Bus Details"));
        panel.setBackground(new Color(236, 240, 241));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 0: Bus Name and Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Bus Name:"), gbc);
        
        busNameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(busNameField, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Bus Type:"), gbc);
        
        busTypeField = new JTextField(10);
        gbc.gridx = 3;
        panel.add(busTypeField, gbc);
        
        // Row 1: Seats and Fare
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Total Seats:"), gbc);
        
        totalSeatsField = new JTextField(5);
        gbc.gridx = 1;
        panel.add(totalSeatsField, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Fare (Rs.):"), gbc);
        
        fareField = new JTextField(10);
        gbc.gridx = 3;
        panel.add(fareField, gbc);
        
        // Row 2: Source and Destination
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("From:"), gbc);
        
        sourceField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(sourceField, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("To:"), gbc);
        
        destField = new JTextField(15);
        gbc.gridx = 3;
        panel.add(destField, gbc);
        
        // Row 3: Departure and Arrival
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Departure:"), gbc);
        
        depTimeField = new JTextField(10);
        depTimeField.setToolTipText("HH:MM:SS (24-hour format)");
        gbc.gridx = 1;
        panel.add(depTimeField, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Arrival:"), gbc);
        
        arrTimeField = new JTextField(10);
        arrTimeField.setToolTipText("HH:MM:SS (24-hour format)");
        gbc.gridx = 3;
        panel.add(arrTimeField, gbc);
        
        // Row 4: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(236, 240, 241));
        
        addButton = createActionButton("Add Bus", new Color(46, 204, 113));
        updateButton = createActionButton("Update", new Color(52, 152, 219));
        deleteButton = createActionButton("Delete", new Color(231, 76, 60));
        refreshButton = createActionButton("Refresh", new Color(155, 89, 182));
        JButton clearButton = createActionButton("Clear", new Color(149, 165, 166));
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(clearButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        panel.add(buttonPanel, gbc);
        
        // Add action listeners
        addButton.addActionListener(e -> addBus());
        updateButton.addActionListener(e -> updateBus());
        deleteButton.addActionListener(e -> deleteBus());
        refreshButton.addActionListener(e -> loadBuses());
        clearButton.addActionListener(e -> clearForm());
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));
        return button;
    }
    
    private void loadBuses() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM buses ORDER BY bus_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            tableModel.setRowCount(0);
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("bus_id"));
                row.add(rs.getString("bus_name"));
                row.add(rs.getString("bus_type"));
                row.add(rs.getInt("total_seats"));
                row.add(rs.getString("source_city"));
                row.add(rs.getString("destination_city"));
                Time depTime = rs.getTime("departure_time");
                Time arrTime = rs.getTime("arrival_time");
                row.add(depTime.toString().substring(0, 5));
                row.add(arrTime.toString().substring(0, 5));
                row.add(rs.getDouble("fare"));
                
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
    
    private void loadSelectedBus() {
        int selectedRow = busesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        selectedBusId = (int) tableModel.getValueAt(selectedRow, 0);
        busNameField.setText((String) tableModel.getValueAt(selectedRow, 1));
        busTypeField.setText((String) tableModel.getValueAt(selectedRow, 2));
        totalSeatsField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
        sourceField.setText((String) tableModel.getValueAt(selectedRow, 4));
        destField.setText((String) tableModel.getValueAt(selectedRow, 5));
        depTimeField.setText((String) tableModel.getValueAt(selectedRow, 6) + ":00");
        arrTimeField.setText((String) tableModel.getValueAt(selectedRow, 7) + ":00");
        fareField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 8)));
    }
    
    private void clearForm() {
        selectedBusId = -1;
        busNameField.setText("");
        busTypeField.setText("");
        totalSeatsField.setText("");
        sourceField.setText("");
        destField.setText("");
        depTimeField.setText("");
        arrTimeField.setText("");
        fareField.setText("");
        busesTable.clearSelection();
    }
    
    private void addBus() {
        if (!validateFields()) return;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO buses (bus_name, bus_type, total_seats, source_city, " +
                         "destination_city, departure_time, arrival_time, fare) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, busNameField.getText());
            pstmt.setString(2, busTypeField.getText());
            pstmt.setInt(3, Integer.parseInt(totalSeatsField.getText()));
            pstmt.setString(4, sourceField.getText());
            pstmt.setString(5, destField.getText());
            pstmt.setString(6, depTimeField.getText());
            pstmt.setString(7, arrTimeField.getText());
            pstmt.setDouble(8, Double.parseDouble(fareField.getText()));
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Bus added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadBuses();
                clearForm();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error adding bus: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBus() {
        if (selectedBusId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a bus to update", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!validateFields()) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Update bus " + busNameField.getText() + "?",
            "Confirm Update",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE buses SET bus_name=?, bus_type=?, total_seats=?, " +
                         "source_city=?, destination_city=?, departure_time=?, " +
                         "arrival_time=?, fare=? WHERE bus_id=?";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, busNameField.getText());
            pstmt.setString(2, busTypeField.getText());
            pstmt.setInt(3, Integer.parseInt(totalSeatsField.getText()));
            pstmt.setString(4, sourceField.getText());
            pstmt.setString(5, destField.getText());
            pstmt.setString(6, depTimeField.getText());
            pstmt.setString(7, arrTimeField.getText());
            pstmt.setDouble(8, Double.parseDouble(fareField.getText()));
            pstmt.setInt(9, selectedBusId);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Bus updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadBuses();
                clearForm();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error updating bus: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteBus() {
        if (selectedBusId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a bus to delete", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete bus " + busNameField.getText() + "?\n" +
            "This will affect related bookings!",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM buses WHERE bus_id=?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, selectedBusId);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Bus deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadBuses();
                clearForm();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error deleting bus: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateFields() {
        if (busNameField.getText().trim().isEmpty() ||
            busTypeField.getText().trim().isEmpty() ||
            totalSeatsField.getText().trim().isEmpty() ||
            sourceField.getText().trim().isEmpty() ||
            destField.getText().trim().isEmpty() ||
            depTimeField.getText().trim().isEmpty() ||
            arrTimeField.getText().trim().isEmpty() ||
            fareField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, 
                "All fields are required", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int seats = Integer.parseInt(totalSeatsField.getText());
            if (seats <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Seats must be positive", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Seats must be a number", 
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
        
        return true;
    }
}