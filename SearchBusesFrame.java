import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class SearchBusesFrame extends JFrame {
    private String username;
    private JTable busesTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> sourceCombo, destCombo, typeCombo;
    private JTextField dateField;
    private JButton searchButton, resetButton, bookButton;
    
    public SearchBusesFrame(String username) {
        this.username = username;
        
        setTitle("Bus Ticket Booking - Search Buses");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
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
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Results panel
        JPanel resultsPanel = createResultsPanel();
        centerPanel.add(resultsPanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Load initial data
        loadAllBuses();
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(900, 60));
        
        JLabel titleLabel = new JLabel("Search Buses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 10));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(52, 73, 94));
        
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            new DashboardFrame(username);
            dispose();
        });
        
        buttonPanel.add(backButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Search Criteria"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Source City
        JLabel sourceLabel = new JLabel("From:");
        sourceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        searchPanel.add(sourceLabel, gbc);
        
        sourceCombo = new JComboBox<>();
        sourceCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        sourceCombo.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 1;
        gbc.gridy = 0;
        searchPanel.add(sourceCombo, gbc);
        
        // Destination City
        JLabel destLabel = new JLabel("To:");
        destLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 2;
        gbc.gridy = 0;
        searchPanel.add(destLabel, gbc);
        
        destCombo = new JComboBox<>();
        destCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        destCombo.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 3;
        gbc.gridy = 0;
        searchPanel.add(destCombo, gbc);
        
        // Bus Type
        JLabel typeLabel = new JLabel("Bus Type:");
        typeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 4;
        gbc.gridy = 0;
        searchPanel.add(typeLabel, gbc);
        
        typeCombo = new JComboBox<>(new String[]{"All Types", "AC", "Non-AC", "Sleeper", "Seater"});
        typeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        typeCombo.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 5;
        gbc.gridy = 0;
        searchPanel.add(typeCombo, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.setPreferredSize(new Dimension(120, 35));
        
        resetButton = new JButton("Reset");
        resetButton.setBackground(new Color(155, 89, 182));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setFocusPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.setPreferredSize(new Dimension(120, 35));
        
        buttonPanel.add(searchButton);
        buttonPanel.add(resetButton);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 6;
        gbc.insets = new Insets(15, 5, 5, 5);
        searchPanel.add(buttonPanel, gbc);
        
        // Load combo boxes
        loadCities();
        
        // Action listeners
        searchButton.addActionListener(e -> searchBuses());
        resetButton.addActionListener(e -> resetSearch());
        
        return searchPanel;
    }
    
    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Available Buses"));
        
        // Create table - FIXED: Changed ₹ to Rs.
        String[] columns = {"Bus ID", "Bus Name", "Type", "From", "To", "Departure", "Arrival", "Fare (Rs.)", "Seats"};
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
        
        JScrollPane scrollPane = new JScrollPane(busesTable);
        scrollPane.setPreferredSize(new Dimension(850, 300));
        
        // Book button panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(236, 240, 241));
        
        bookButton = new JButton("Book Selected Bus");
        bookButton.setBackground(new Color(46, 204, 113));
        bookButton.setForeground(Color.WHITE);
        bookButton.setFont(new Font("Arial", Font.BOLD, 14));
        bookButton.setFocusPainted(false);
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookButton.setPreferredSize(new Dimension(200, 40));
        bookButton.setEnabled(false);
        
        bookButton.addActionListener(e -> bookSelectedBus());
        
        busesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                bookButton.setEnabled(busesTable.getSelectedRow() != -1);
            }
        });
        
        bottomPanel.add(bookButton);
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        resultsPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return resultsPanel;
    }
    
    private void loadCities() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Load source cities
            String query = "SELECT DISTINCT source_city FROM buses ORDER BY source_city";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            sourceCombo.addItem("Any City");
            while (rs.next()) {
                sourceCombo.addItem(rs.getString("source_city"));
            }
            
            // Load destination cities
            rs = stmt.executeQuery("SELECT DISTINCT destination_city FROM buses ORDER BY destination_city");
            destCombo.addItem("Any City");
            while (rs.next()) {
                destCombo.addItem(rs.getString("destination_city"));
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading cities: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAllBuses() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM buses ORDER BY source_city, departure_time";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            displayResults(rs);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading buses: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchBuses() {
        String source = (String) sourceCombo.getSelectedItem();
        String dest = (String) destCombo.getSelectedItem();
        String type = (String) typeCombo.getSelectedItem();
        
        StringBuilder query = new StringBuilder("SELECT * FROM buses WHERE 1=1");
        
        if (source != null && !source.equals("Any City")) {
            query.append(" AND source_city = '").append(source).append("'");
        }
        
        if (dest != null && !dest.equals("Any City")) {
            query.append(" AND destination_city = '").append(dest).append("'");
        }
        
        if (type != null && !type.equals("All Types")) {
            query.append(" AND bus_type LIKE '%").append(type).append("%'");
        }
        
        query.append(" ORDER BY source_city, departure_time");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query.toString());
            
            displayResults(rs);
            
            // Show count
            int rowCount = tableModel.getRowCount();
            JOptionPane.showMessageDialog(this, 
                "Found " + rowCount + " bus(es) matching your criteria.", 
                "Search Results", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error searching buses: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayResults(ResultSet rs) throws SQLException {
        tableModel.setRowCount(0);
        
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("bus_id"));
            row.add(rs.getString("bus_name"));
            row.add(rs.getString("bus_type"));
            row.add(rs.getString("source_city"));
            row.add(rs.getString("destination_city"));
            row.add(rs.getTime("departure_time").toString().substring(0, 5));
            row.add(rs.getTime("arrival_time").toString().substring(0, 5));
            row.add(rs.getDouble("fare"));
            row.add(rs.getInt("total_seats"));
            
            tableModel.addRow(row);
        }
    }
    
    private void resetSearch() {
        sourceCombo.setSelectedIndex(0);
        destCombo.setSelectedIndex(0);
        typeCombo.setSelectedIndex(0);
        loadAllBuses();
    }
    
    private void bookSelectedBus() {
        int selectedRow = busesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a bus to book", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int busId = (int) tableModel.getValueAt(selectedRow, 0);
        String busName = (String) tableModel.getValueAt(selectedRow, 1);
        double fare = (double) tableModel.getValueAt(selectedRow, 7);
        
        // FIXED: Changed ₹ to Rs.
        int confirm = JOptionPane.showConfirmDialog(this,
            "Book ticket for " + busName + "?\nFare: Rs." + fare,
            "Confirm Booking",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new BookTicketFrame(username, busId, busName, fare);
            dispose();
        }
    }
}