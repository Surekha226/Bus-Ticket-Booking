import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class AdminManageUsersFrame extends JFrame {
    private String adminUsername;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton, refreshButton, backButton, deleteButton, resetPasswordButton;
    private JLabel totalUsersLabel;
    
    public AdminManageUsersFrame(String adminUsername) {
        this.adminUsername = adminUsername;
        
        setTitle("Admin - Manage Users");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
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
        loadAllUsers();
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(155, 89, 182)); // Purple theme
        headerPanel.setPreferredSize(new Dimension(970, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("MANAGE USERS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("View and manage all registered users");
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
                BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
                " User Records ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(155, 89, 182)
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
        
        panel.add(new JLabel("Search:"));
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(155, 89, 182));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> searchUsers());
        
        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(46, 204, 113));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadAllUsers());
        
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
            "User ID", "Username", "Email", "Phone", "Member Since", "Bookings", "Status"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        usersTable = new JTable(tableModel);
        usersTable.setRowHeight(35);
        usersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        usersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        usersTable.getTableHeader().setBackground(new Color(155, 89, 182));
        usersTable.getTableHeader().setForeground(Color.WHITE);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Add zebra striping
        usersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
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
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setPreferredSize(new Dimension(950, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel below table
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        resetPasswordButton = new JButton("Reset Password");
        resetPasswordButton.setBackground(new Color(52, 152, 219));
        resetPasswordButton.setForeground(Color.WHITE);
        resetPasswordButton.setFont(new Font("Arial", Font.BOLD, 12));
        resetPasswordButton.setFocusPainted(false);
        resetPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetPasswordButton.setEnabled(false);
        resetPasswordButton.addActionListener(e -> resetPassword());
        
        deleteButton = new JButton("Delete User");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteUser());
        
        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean selected = usersTable.getSelectedRow() != -1;
                resetPasswordButton.setEnabled(selected);
                deleteButton.setEnabled(selected);
            }
        });
        
        actionPanel.add(resetPasswordButton);
        actionPanel.add(deleteButton);
        
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setPreferredSize(new Dimension(970, 50));
        
        totalUsersLabel = new JLabel("Total Users: 0", SwingConstants.CENTER);
        totalUsersLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalUsersLabel.setForeground(Color.WHITE);
        
        panel.add(totalUsersLabel);
        
        return panel;
    }
    
    private void loadAllUsers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT u.user_id, u.username, u.email, u.phone, u.created_at, " +
                         "(SELECT COUNT(*) FROM bookings b WHERE b.user_id = u.user_id) as booking_count " +
                         "FROM users u ORDER BY u.user_id";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            tableModel.setRowCount(0);
            int totalUsers = 0;
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("user_id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("email") != null ? rs.getString("email") : "Not provided");
                row.add(rs.getString("phone") != null ? rs.getString("phone") : "Not provided");
                
                Timestamp created = rs.getTimestamp("created_at");
                row.add(created != null ? created.toString().substring(0, 10) : "Unknown");
                
                int bookingCount = rs.getInt("booking_count");
                row.add(bookingCount);
                row.add(bookingCount > 0 ? "Active" : "Inactive");
                
                tableModel.addRow(row);
                totalUsers++;
            }
            
            totalUsersLabel.setText("Total Users: " + totalUsers);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading users: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchUsers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAllUsers();
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT u.user_id, u.username, u.email, u.phone, u.created_at, " +
                         "(SELECT COUNT(*) FROM bookings b WHERE b.user_id = u.user_id) as booking_count " +
                         "FROM users u " +
                         "WHERE u.username LIKE ? OR u.email LIKE ? OR u.phone LIKE ? " +
                         "ORDER BY u.user_id";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            tableModel.setRowCount(0);
            int totalUsers = 0;
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("user_id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("email") != null ? rs.getString("email") : "Not provided");
                row.add(rs.getString("phone") != null ? rs.getString("phone") : "Not provided");
                
                Timestamp created = rs.getTimestamp("created_at");
                row.add(created != null ? created.toString().substring(0, 10) : "Unknown");
                
                int bookingCount = rs.getInt("booking_count");
                row.add(bookingCount);
                row.add(bookingCount > 0 ? "Active" : "Inactive");
                
                tableModel.addRow(row);
                totalUsers++;
            }
            
            totalUsersLabel.setText("Total Users: " + totalUsers);
            
            if (totalUsers == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No users found matching '" + searchTerm + "'", 
                    "Search Results", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error searching users: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetPassword() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Show password reset dialog
        JPanel passwordPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPasswordField newPassField = new JPasswordField(15);
        JPasswordField confirmPassField = new JPasswordField(15);
        
        passwordPanel.add(new JLabel("New Password:"));
        passwordPanel.add(newPassField);
        passwordPanel.add(new JLabel("Confirm Password:"));
        passwordPanel.add(confirmPassField);
        
        int result = JOptionPane.showConfirmDialog(this, passwordPanel, 
            "Reset Password for " + username, JOptionPane.OK_CANCEL_OPTION);
            
        if (result == JOptionPane.OK_OPTION) {
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());
            
            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Password cannot be empty", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, 
                    "Passwords do not match", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (newPass.length() < 4) {
                JOptionPane.showMessageDialog(this, 
                    "Password must be at least 4 characters", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "UPDATE users SET password = ? WHERE user_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, newPass);
                pstmt.setInt(2, userId);
                
                int updateResult = pstmt.executeUpdate();
                
                if (updateResult > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Password reset successfully for " + username, 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error resetting password: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        int bookingCount = (int) tableModel.getValueAt(selectedRow, 5);
        
        String message = "Delete user " + username + "?\n\n";
        if (bookingCount > 0) {
            message += "WARNING: This user has " + bookingCount + " booking(s).\n";
            message += "All bookings will also be deleted!\n\n";
        }
        message += "This action cannot be undone!";
        
        int confirm = JOptionPane.showConfirmDialog(this,
            message,
            "Confirm Delete User",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Start transaction
                conn.setAutoCommit(false);
                
                try {
                    // First delete user's bookings
                    String deleteBookings = "DELETE FROM bookings WHERE user_id = ?";
                    PreparedStatement pstmt1 = conn.prepareStatement(deleteBookings);
                    pstmt1.setInt(1, userId);
                    pstmt1.executeUpdate();
                    
                    // Then delete the user
                    String deleteUser = "DELETE FROM users WHERE user_id = ?";
                    PreparedStatement pstmt2 = conn.prepareStatement(deleteUser);
                    pstmt2.setInt(1, userId);
                    int result = pstmt2.executeUpdate();
                    
                    conn.commit();
                    
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, 
                            "User " + username + " deleted successfully", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                        loadAllUsers(); // Refresh
                    }
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error deleting user: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}