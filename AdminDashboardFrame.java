import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminDashboardFrame extends JFrame {
    private String adminUsername;
    
    public AdminDashboardFrame(String adminUsername) {
        this.adminUsername = adminUsername;
        
        setTitle("Bus Ticket Booking - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top Panel - Header
        JPanel topPanel = createHeaderPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - Buttons Grid
        JPanel centerPanel = createButtonPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel - Stats
        JPanel bottomPanel = createStatsPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(44, 62, 80));
        panel.setPreferredSize(new Dimension(970, 90));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Left side with title and welcome
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("ADMIN DASHBOARD");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel welcomeLabel = new JLabel("Welcome, " + adminUsername + " (Administrator)");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(200, 200, 200));
        
        leftPanel.add(titleLabel);
        leftPanel.add(welcomeLabel);
        
        // Logout button
        JButton logoutButton = new JButton("LOGOUT");
        logoutButton.setBackground(new Color(192, 57, 43));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(120, 45));
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Logout from admin panel?", "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new AdminLoginFrame();
                dispose();
            }
        });
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(logoutButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        // Use GridLayout - 3 rows, 2 columns with gaps
        JPanel panel = new JPanel(new GridLayout(3, 2, 20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                " ADMIN CONTROLS ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 22),
                new Color(52, 152, 219)
            ),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        // Create buttons with solid colors
        JButton manageBusesBtn = createSolidButton("MANAGE BUSES", new Color(52, 152, 219)); // Blue
        JButton allBookingsBtn = createSolidButton("ALL BOOKINGS", new Color(46, 204, 113)); // Green
        JButton manageUsersBtn = createSolidButton("MANAGE USERS", new Color(155, 89, 182)); // Purple
        JButton viewReportsBtn = createSolidButton("VIEW REPORTS", new Color(241, 196, 15)); // Yellow
        JButton busSchedulesBtn = createSolidButton("BUS SCHEDULES", new Color(230, 126, 34)); // Orange
        JButton settingsBtn = createSolidButton("SETTINGS", new Color(52, 73, 94)); // Dark Blue
        
        // Add buttons to panel
        panel.add(manageBusesBtn);
        panel.add(allBookingsBtn);
        panel.add(manageUsersBtn);
        panel.add(viewReportsBtn);
        panel.add(busSchedulesBtn);
        panel.add(settingsBtn);
        
        // Action listeners
        manageBusesBtn.addActionListener(e -> {
            new AdminManageBusesFrame(adminUsername);
            dispose();
        });
        
        allBookingsBtn.addActionListener(e -> {
            new AdminAllBookingsFrame(adminUsername);
            dispose();
        });
        
        manageUsersBtn.addActionListener(e -> {
            new AdminManageUsersFrame(adminUsername);
            dispose();
        });
        
        viewReportsBtn.addActionListener(e -> {
            new AdminReportsFrame(adminUsername);
            dispose();
        });
        
        busSchedulesBtn.addActionListener(e -> {
            new BusSchedulesFrame(adminUsername);
            dispose();
        });
        
        // UPDATED: Settings button now opens SettingsFrame
        settingsBtn.addActionListener(e -> {
            new SettingsFrame(adminUsername);
            dispose();
        });
        
        return panel;
    }
    
    private JButton createSolidButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(280, 85));
        button.setMinimumSize(new Dimension(280, 85));
        button.setMaximumSize(new Dimension(280, 85));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 4),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.YELLOW, 4),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 4),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });
        
        return button;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(970, 110));
        
        int[] stats = getDashboardStats();
        
        panel.add(createStatCard("TOTAL BUSES", String.valueOf(stats[0]), new Color(52, 152, 219)));
        panel.add(createStatCard("TOTAL USERS", String.valueOf(stats[1]), new Color(46, 204, 113)));
        panel.add(createStatCard("TOTAL BOOKINGS", String.valueOf(stats[2]), new Color(155, 89, 182)));
        panel.add(createStatCard("REVENUE (Rs.)", String.valueOf(stats[3]), new Color(241, 196, 15)));
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 3),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setOpaque(true);
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private int[] getDashboardStats() {
        int[] stats = new int[4];
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM buses");
            if (rs.next()) stats[0] = rs.getInt(1);
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) stats[1] = rs.getInt(1);
            
            rs = stmt.executeQuery("SELECT COUNT(*), SUM(total_fare) FROM bookings WHERE status='Confirmed'");
            if (rs.next()) {
                stats[2] = rs.getInt(1);
                stats[3] = rs.getInt(2);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return stats;
    }
}