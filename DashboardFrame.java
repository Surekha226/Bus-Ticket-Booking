import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardFrame extends JFrame {
    private String username;
    
    public DashboardFrame(String username) {
        this.username = username;
        
        setTitle("Bus Ticket Booking System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Force a specific look and feel for better button visibility
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(580, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setPreferredSize(new Dimension(100, 40));
        logoutButton.setBorder(BorderFactory.createRaisedBevelBorder());
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Make sure button is visible
        logoutButton.setOpaque(true);
        logoutButton.setContentAreaFilled(true);
        logoutButton.setBorderPainted(true);
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel with menu buttons
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(new Color(236, 240, 241));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // Create buttons with improved visibility
        JButton bookTicketBtn = createMenuButton(">> Book Ticket <<", new Color(52, 152, 219));
        JButton viewBookingsBtn = createMenuButton(">> View My Bookings <<", new Color(46, 204, 113));
        JButton searchBusBtn = createMenuButton(">> Search Buses <<", new Color(155, 89, 182));
        JButton profileBtn = createMenuButton(">> My Profile <<", new Color(241, 196, 15));
        
        // Add buttons to panel
        gbc.gridy = 0;
        menuPanel.add(bookTicketBtn, gbc);
        
        gbc.gridy = 1;
        menuPanel.add(viewBookingsBtn, gbc);
        
        gbc.gridy = 2;
        menuPanel.add(searchBusBtn, gbc);
        
        gbc.gridy = 3;
        menuPanel.add(profileBtn, gbc);
        
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        
        // Action listeners
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(DashboardFrame.this,
                "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame();
                dispose();
            }
        });
        
        bookTicketBtn.addActionListener(e -> {
            new BookTicketFrame(username);
            dispose();
        });
        
        viewBookingsBtn.addActionListener(e -> {
            new ViewBookingsFrame(username);
            dispose();
        });
        
        searchBusBtn.addActionListener(e -> {
            new SearchBusesFrame(username);
            dispose();
        });
        
        // UPDATED: Profile button now opens ProfileFrame
        profileBtn.addActionListener(e -> {
            new ProfileFrame(username);
            dispose();
        });
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(300, 60));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        
        // Make sure button is always visible
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect for better UX
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
}