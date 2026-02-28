import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class AdminReportsFrame extends JFrame {
    private String adminUsername;
    private JLabel totalBusesLabel, totalUsersLabel, totalBookingsLabel, totalRevenueLabel;
    private JLabel todayBookingsLabel, todayRevenueLabel, weekBookingsLabel, weekRevenueLabel;
    private JLabel monthBookingsLabel, monthRevenueLabel, yearBookingsLabel, yearRevenueLabel;
    private JTable popularBusesTable, activeUsersTable;
    private DefaultTableModel busTableModel, userTableModel;
    private JComboBox<String> reportPeriodCombo;
    private JButton backButton, refreshButton, exportButton;
    
    public AdminReportsFrame(String adminUsername) {
        this.adminUsername = adminUsername;
        
        setTitle("Admin - Reports & Analytics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel with tabs
        JTabbedPane tabbedPane = createTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Load all data
        loadAllReports();
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(241, 196, 15));
        headerPanel.setPreferredSize(new Dimension(1170, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("REPORTS & ANALYTICS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("View detailed statistics and insights");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(44, 62, 80));
        
        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel);
        leftPanel.add(subtitleLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false);
        
        refreshButton = new JButton("Refresh Data");
        refreshButton.setBackground(new Color(46, 204, 113));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(150, 40));
        refreshButton.addActionListener(e -> loadAllReports());
        
        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(52, 73, 94));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(180, 40));
        backButton.addActionListener(e -> {
            new AdminDashboardFrame(adminUsername);
            dispose();
        });
        
        rightPanel.add(refreshButton);
        rightPanel.add(backButton);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16));
        tabbedPane.setBackground(new Color(241, 196, 15));
        tabbedPane.setForeground(Color.WHITE);
        
        tabbedPane.addTab("Overview", createOverviewPanel());
        tabbedPane.addTab("Popular Buses", createPopularBusesPanel());
        tabbedPane.addTab("User Activity", createUserActivityPanel());
        tabbedPane.addTab("Revenue", createRevenuePanel());
        
        return tabbedPane;
    }
    
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Period selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(new JLabel("Select Period:"));
        
        String[] periods = {"Today", "This Week", "This Month", "This Year", "All Time"};
        reportPeriodCombo = new JComboBox<>(periods);
        reportPeriodCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        reportPeriodCombo.setPreferredSize(new Dimension(150, 30));
        reportPeriodCombo.addActionListener(e -> updateOverviewStats());
        topPanel.add(reportPeriodCombo);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Stats grid
        JPanel statsGrid = new JPanel(new GridLayout(2, 4, 15, 15));
        statsGrid.setBackground(Color.WHITE);
        statsGrid.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Initialize labels
        totalBusesLabel = new JLabel("0", SwingConstants.CENTER);
        totalUsersLabel = new JLabel("0", SwingConstants.CENTER);
        totalBookingsLabel = new JLabel("0", SwingConstants.CENTER);
        totalRevenueLabel = new JLabel("Rs. 0", SwingConstants.CENTER);
        todayBookingsLabel = new JLabel("0", SwingConstants.CENTER);
        weekBookingsLabel = new JLabel("0", SwingConstants.CENTER);
        monthBookingsLabel = new JLabel("0", SwingConstants.CENTER);
        yearBookingsLabel = new JLabel("0", SwingConstants.CENTER);
        
        statsGrid.add(createStatCard("TOTAL BUSES", totalBusesLabel, new Color(52, 152, 219)));
        statsGrid.add(createStatCard("TOTAL USERS", totalUsersLabel, new Color(46, 204, 113)));
        statsGrid.add(createStatCard("TOTAL BOOKINGS", totalBookingsLabel, new Color(155, 89, 182)));
        statsGrid.add(createStatCard("TOTAL REVENUE", totalRevenueLabel, new Color(241, 196, 15)));
        statsGrid.add(createStatCard("TODAY'S BOOKINGS", todayBookingsLabel, new Color(230, 126, 34)));
        statsGrid.add(createStatCard("WEEKLY BOOKINGS", weekBookingsLabel, new Color(52, 73, 94)));
        statsGrid.add(createStatCard("MONTHLY BOOKINGS", monthBookingsLabel, new Color(192, 57, 43)));
        statsGrid.add(createStatCard("YEARLY BOOKINGS", yearBookingsLabel, new Color(39, 174, 96)));
        
        panel.add(statsGrid, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPopularBusesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Most Popular Buses");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Bus Name", "Type", "Total Bookings", "Revenue (Rs.)", "Occupancy Rate"};
        busTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        popularBusesTable = new JTable(busTableModel);
        popularBusesTable.setRowHeight(30);
        popularBusesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        popularBusesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        popularBusesTable.getTableHeader().setBackground(new Color(241, 196, 15));
        popularBusesTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(popularBusesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createUserActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Most Active Users");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Username", "Email", "Total Bookings", "Total Spent (Rs.)", "Last Booking"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        activeUsersTable = new JTable(userTableModel);
        activeUsersTable.setRowHeight(30);
        activeUsersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        activeUsersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        activeUsersTable.getTableHeader().setBackground(new Color(241, 196, 15));
        activeUsersTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(activeUsersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRevenuePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        todayRevenueLabel = new JLabel("Rs. 0", SwingConstants.CENTER);
        weekRevenueLabel = new JLabel("Rs. 0", SwingConstants.CENTER);
        monthRevenueLabel = new JLabel("Rs. 0", SwingConstants.CENTER);
        yearRevenueLabel = new JLabel("Rs. 0", SwingConstants.CENTER);
        
        summaryPanel.add(createRevenueCard("Today", todayRevenueLabel, new Color(230, 126, 34)));
        summaryPanel.add(createRevenueCard("This Week", weekRevenueLabel, new Color(52, 152, 219)));
        summaryPanel.add(createRevenueCard("This Month", monthRevenueLabel, new Color(46, 204, 113)));
        summaryPanel.add(createRevenueCard("This Year", yearRevenueLabel, new Color(155, 89, 182)));
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(new Color(245, 245, 245));
        chartPanel.setBorder(BorderFactory.createTitledBorder("Revenue Trend"));
        
        JLabel chartLabel = new JLabel("Revenue Chart (Coming Soon)", SwingConstants.CENTER);
        chartLabel.setFont(new Font("Arial", Font.BOLD, 18));
        chartLabel.setForeground(Color.GRAY);
        chartPanel.add(chartLabel, BorderLayout.CENTER);
        
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        card.setOpaque(true);
        
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valueLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE);
        
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createRevenueCard(String period, JLabel amountLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        card.setOpaque(true);
        
        JLabel periodLabel = new JLabel(period, SwingConstants.CENTER);
        periodLabel.setFont(new Font("Arial", Font.BOLD, 14));
        periodLabel.setForeground(Color.WHITE);
        
        amountLabel.setFont(new Font("Arial", Font.BOLD, 18));
        amountLabel.setForeground(Color.WHITE);
        
        card.add(periodLabel, BorderLayout.NORTH);
        card.add(amountLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadAllReports() {
        loadOverviewStats();
        loadPopularBuses();
        loadActiveUsers();
        loadRevenueStats();
    }
    
    private void loadOverviewStats() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM buses");
            if (rs.next()) totalBusesLabel.setText(String.valueOf(rs.getInt(1)));
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) totalUsersLabel.setText(String.valueOf(rs.getInt(1)));
            
            rs = stmt.executeQuery("SELECT COUNT(*), SUM(total_fare) FROM bookings WHERE status='Confirmed'");
            if (rs.next()) {
                totalBookingsLabel.setText(String.valueOf(rs.getInt(1)));
                totalRevenueLabel.setText("Rs. " + rs.getInt(2));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM bookings WHERE status='Confirmed' AND DATE(travel_date) = CURDATE()");
            if (rs.next()) todayBookingsLabel.setText(String.valueOf(rs.getInt(1)));
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM bookings WHERE status='Confirmed' AND YEARWEEK(travel_date) = YEARWEEK(CURDATE())");
            if (rs.next()) weekBookingsLabel.setText(String.valueOf(rs.getInt(1)));
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM bookings WHERE status='Confirmed' AND MONTH(travel_date) = MONTH(CURDATE()) AND YEAR(travel_date) = YEAR(CURDATE())");
            if (rs.next()) monthBookingsLabel.setText(String.valueOf(rs.getInt(1)));
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM bookings WHERE status='Confirmed' AND YEAR(travel_date) = YEAR(CURDATE())");
            if (rs.next()) yearBookingsLabel.setText(String.valueOf(rs.getInt(1)));
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadPopularBuses() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT bs.bus_name, bs.bus_type, COUNT(*) as booking_count, " +
                         "SUM(b.total_fare) as revenue, " +
                         "ROUND((COUNT(*) * 100.0 / bs.total_seats), 2) as occupancy " +
                         "FROM bookings b " +
                         "JOIN buses bs ON b.bus_id = bs.bus_id " +
                         "WHERE b.status='Confirmed' " +
                         "GROUP BY b.bus_id " +
                         "ORDER BY booking_count DESC LIMIT 10";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            busTableModel.setRowCount(0);
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("bus_name"));
                row.add(rs.getString("bus_type"));
                row.add(rs.getInt("booking_count"));
                row.add(rs.getDouble("revenue"));
                row.add(rs.getDouble("occupancy") + "%");
                
                busTableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadActiveUsers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT u.username, u.email, COUNT(*) as booking_count, " +
                         "SUM(b.total_fare) as total_spent, " +
                         "MAX(b.travel_date) as last_booking " +
                         "FROM users u " +
                         "LEFT JOIN bookings b ON u.user_id = b.user_id AND b.status='Confirmed' " +
                         "GROUP BY u.user_id " +
                         "HAVING booking_count > 0 " +
                         "ORDER BY total_spent DESC LIMIT 10";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            userTableModel.setRowCount(0);
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("username"));
                row.add(rs.getString("email"));
                row.add(rs.getInt("booking_count"));
                row.add(rs.getDouble("total_spent"));
                
                Date lastBooking = rs.getDate("last_booking");
                row.add(lastBooking != null ? lastBooking.toString() : "Never");
                
                userTableModel.addRow(row);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadRevenueStats() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery("SELECT SUM(total_fare) FROM bookings WHERE status='Confirmed' AND DATE(travel_date) = CURDATE()");
            if (rs.next()) todayRevenueLabel.setText("Rs. " + rs.getInt(1));
            
            rs = stmt.executeQuery("SELECT SUM(total_fare) FROM bookings WHERE status='Confirmed' AND YEARWEEK(travel_date) = YEARWEEK(CURDATE())");
            if (rs.next()) weekRevenueLabel.setText("Rs. " + rs.getInt(1));
            
            rs = stmt.executeQuery("SELECT SUM(total_fare) FROM bookings WHERE status='Confirmed' AND MONTH(travel_date) = MONTH(CURDATE()) AND YEAR(travel_date) = YEAR(CURDATE())");
            if (rs.next()) monthRevenueLabel.setText("Rs. " + rs.getInt(1));
            
            rs = stmt.executeQuery("SELECT SUM(total_fare) FROM bookings WHERE status='Confirmed' AND YEAR(travel_date) = YEAR(CURDATE())");
            if (rs.next()) yearRevenueLabel.setText("Rs. " + rs.getInt(1));
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void updateOverviewStats() {
        loadAllReports();
        String period = (String) reportPeriodCombo.getSelectedItem();
        JOptionPane.showMessageDialog(this, 
            "Reports updated for period: " + period, 
            "Info", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}