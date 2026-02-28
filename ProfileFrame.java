import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProfileFrame extends JFrame {
    private String username;
    private JTextField usernameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JLabel userIdLabel, createdDateLabel;
    private JButton editButton, saveButton, changePasswordButton, backButton;
    private boolean isEditing = false;
    
    public ProfileFrame(String username) {
        this.username = username;
        
        setTitle("Bus Ticket Booking - My Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Profile content
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Load user data
        loadUserData();
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(500, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        backButton.addActionListener(e -> {
            new DashboardFrame(username);
            dispose();
        });
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Profile Information"),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        // User ID (read-only)
        JLabel idLabel = new JLabel("User ID:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(idLabel, gbc);
        
        userIdLabel = new JLabel("-");
        userIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userIdLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        userIdLabel.setBackground(new Color(245, 245, 245));
        userIdLabel.setOpaque(true);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(userIdLabel, gbc);
        
        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        contentPanel.add(userLabel, gbc);
        
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        usernameField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        contentPanel.add(usernameField, gbc);
        
        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        contentPanel.add(emailLabel, gbc);
        
        emailField = new JTextField(15);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        emailField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        contentPanel.add(emailField, gbc);
        
        // Phone
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        contentPanel.add(phoneLabel, gbc);
        
        phoneField = new JTextField(15);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        phoneField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        contentPanel.add(phoneField, gbc);
        
        // Created Date
        JLabel createdLabel = new JLabel("Member Since:");
        createdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        contentPanel.add(createdLabel, gbc);
        
        createdDateLabel = new JLabel("-");
        createdDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        createdDateLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        createdDateLabel.setBackground(new Color(245, 245, 245));
        createdDateLabel.setOpaque(true);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPanel.add(createdDateLabel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        editButton = new JButton("Edit Profile");
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Arial", Font.BOLD, 14));
        editButton.setFocusPainted(false);
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editButton.setPreferredSize(new Dimension(150, 40));
        editButton.addActionListener(e -> toggleEdit());
        
        saveButton = new JButton("Save Changes");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> saveChanges());
        
        changePasswordButton = new JButton("Change Password");
        changePasswordButton.setBackground(new Color(155, 89, 182));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setFont(new Font("Arial", Font.BOLD, 14));
        changePasswordButton.setFocusPainted(false);
        changePasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changePasswordButton.setPreferredSize(new Dimension(180, 40));
        changePasswordButton.addActionListener(e -> changePassword());
        
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 5, 5, 5);
        contentPanel.add(buttonPanel, gbc);
        
        gbc.gridy = 6;
        contentPanel.add(changePasswordButton, gbc);
        
        return contentPanel;
    }
    
    private void loadUserData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT user_id, username, email, phone, created_at FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                userIdLabel.setText(String.valueOf(rs.getInt("user_id")));
                usernameField.setText(rs.getString("username"));
                emailField.setText(rs.getString("email") != null ? rs.getString("email") : "");
                phoneField.setText(rs.getString("phone") != null ? rs.getString("phone") : "");
                
                Timestamp created = rs.getTimestamp("created_at");
                if (created != null) {
                    createdDateLabel.setText(created.toString().substring(0, 10));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading profile: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void toggleEdit() {
        isEditing = !isEditing;
        
        emailField.setEditable(isEditing);
        phoneField.setEditable(isEditing);
        
        // Username cannot be changed (it's unique)
        usernameField.setEditable(false);
        
        editButton.setEnabled(!isEditing);
        saveButton.setEnabled(isEditing);
        
        if (isEditing) {
            editButton.setBackground(Color.GRAY);
            saveButton.setBackground(new Color(46, 204, 113));
            emailField.requestFocus();
        } else {
            editButton.setBackground(new Color(52, 152, 219));
            saveButton.setEnabled(false);
        }
    }
    
    private void saveChanges() {
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
        if (email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Email and Phone cannot be empty", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid email address", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Save these changes?\n\nEmail: " + email + "\nPhone: " + phone,
            "Confirm Changes",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE users SET email = ?, phone = ? WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, phone);
            pstmt.setString(3, username);
            
            int result = pstmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Profile updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                toggleEdit(); // Exit edit mode
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error updating profile: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void changePassword() {
        JPanel passwordPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        
        JPasswordField currentPass = new JPasswordField(15);
        JPasswordField newPass = new JPasswordField(15);
        JPasswordField confirmPass = new JPasswordField(15);
        
        passwordPanel.add(new JLabel("Current Password:"));
        passwordPanel.add(currentPass);
        passwordPanel.add(new JLabel("New Password:"));
        passwordPanel.add(newPass);
        passwordPanel.add(new JLabel("Confirm New Password:"));
        passwordPanel.add(confirmPass);
        
        int result = JOptionPane.showConfirmDialog(this, passwordPanel, 
            "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            String current = new String(currentPass.getPassword());
            String newPwd = new String(newPass.getPassword());
            String confirm = new String(confirmPass.getPassword());
            
            if (current.isEmpty() || newPwd.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "All fields are required", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newPwd.equals(confirm)) {
                JOptionPane.showMessageDialog(this, 
                    "New passwords do not match", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (newPwd.length() < 4) {
                JOptionPane.showMessageDialog(this, 
                    "Password must be at least 4 characters", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verify current password and update
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Check current password
                String checkQuery = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, username);
                checkStmt.setString(2, current);
                ResultSet rs = checkStmt.executeQuery();
                
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, 
                        "Current password is incorrect", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update password
                String updateQuery = "UPDATE users SET password = ? WHERE username = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, newPwd);
                updateStmt.setString(2, username);
                
                int updateResult = updateStmt.executeUpdate();
                
                if (updateResult > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Password changed successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error changing password: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}