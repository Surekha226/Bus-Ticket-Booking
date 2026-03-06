import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.Pattern;

public class SignupFrame extends JFrame {
    private JTextField usernameField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    
    // Validation patterns
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PHONE_REGEX = "^[0-9]{10}$";
    
    public SignupFrame() {
        setTitle("Bus Ticket Booking System - Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(52, 73, 94));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Signup panel
        JPanel signupPanel = new JPanel(new GridBagLayout());
        signupPanel.setBackground(new Color(52, 73, 94));
        signupPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        signupPanel.add(userLabel, gbc);
        
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBackground(Color.WHITE);
        usernameField.setForeground(Color.BLACK);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        signupPanel.add(usernameField, gbc);
        
        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        signupPanel.add(emailLabel, gbc);
        
        emailField = new JTextField(15);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBackground(Color.WHITE);
        emailField.setForeground(Color.BLACK);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 1;
        signupPanel.add(emailField, gbc);
        
        // Add input validation listener for email
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateEmail();
            }
        });
        
        // Phone
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setForeground(Color.WHITE);
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        signupPanel.add(phoneLabel, gbc);
        
        phoneField = new JTextField(15);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneField.setBackground(Color.WHITE);
        phoneField.setForeground(Color.BLACK);
        phoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 2;
        signupPanel.add(phoneField, gbc);
        
        // Add input validation listener for phone
        phoneField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validatePhone();
            }
        });
        
        // Restrict phone field to only numbers
        phoneField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
        
        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        signupPanel.add(passLabel, gbc);
        
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(Color.BLACK);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 3;
        signupPanel.add(passwordField, gbc);
        
        // Confirm Password
        JLabel confirmLabel = new JLabel("Confirm:");
        confirmLabel.setForeground(Color.WHITE);
        confirmLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        signupPanel.add(confirmLabel, gbc);
        
        confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmPasswordField.setBackground(Color.WHITE);
        confirmPasswordField.setForeground(Color.BLACK);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        gbc.gridx = 1;
        gbc.gridy = 4;
        signupPanel.add(confirmPasswordField, gbc);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(52, 73, 94));
        
        // Sign Up Button - Green with BLACK text
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBackground(new Color(46, 204, 113));
        signupButton.setForeground(Color.BLACK);
        signupButton.setFocusPainted(false);
        signupButton.setFont(new Font("Arial", Font.BOLD, 18));
        signupButton.setPreferredSize(new Dimension(140, 45));
        signupButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 3),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.setOpaque(true);
        
        // Back Button - Blue with BLACK text
        JButton backButton = new JButton("Back to Login");
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Arial", Font.BOLD, 18));
        backButton.setPreferredSize(new Dimension(160, 45));
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 3),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setOpaque(true);
        
        // Add hover effects
        signupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signupButton.setBackground(new Color(39, 174, 96)); // Darker green
            }
            @Override
            public void mouseExited(MouseEvent e) {
                signupButton.setBackground(new Color(46, 204, 113)); // Original green
            }
        });
        
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(new Color(41, 128, 185)); // Darker blue
            }
            @Override
            public void mouseExited(MouseEvent e) {
                backButton.setBackground(new Color(52, 152, 219)); // Original blue
            }
        });
        
        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        signupPanel.add(buttonPanel, gbc);
        
        mainPanel.add(signupPanel, BorderLayout.CENTER);
        
        // Action listeners
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginFrame();
                dispose();
            }
        });
        
        // Enter key support
        usernameField.addActionListener(e -> emailField.requestFocus());
        emailField.addActionListener(e -> phoneField.requestFocus());
        phoneField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> confirmPasswordField.requestFocus());
        confirmPasswordField.addActionListener(e -> registerUser());
        
        add(mainPanel);
        setVisible(true);
    }
    
    private boolean validateEmail() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            return true; // Don't validate empty field, will be caught in registerUser
        }
        
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        if (!pattern.matcher(email).matches()) {
            emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            return false;
        } else {
            emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            return true;
        }
    }
    
    private boolean validatePhone() {
        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            return true; // Don't validate empty field, will be caught in registerUser
        }
        
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        if (!pattern.matcher(phone).matches()) {
            phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            return false;
        } else {
            phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            return true;
        }
    }
    
    private void registerUser() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        
        // Validation
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all fields", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Email format validation
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
        if (!emailPattern.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid email address (e.g., user@example.com)", 
                "Invalid Email", 
                JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return;
        }
        
        // Phone number validation (10 digits)
        Pattern phonePattern = Pattern.compile(PHONE_REGEX);
        if (!phonePattern.matcher(phone).matches()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid 10-digit phone number", 
                "Invalid Phone Number", 
                JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords do not match", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, 
                "Password must be at least 4 characters", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if username exists
            String checkQuery = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, 
                    "Username already exists", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if email already exists
            String checkEmailQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailQuery);
            checkEmailStmt.setString(1, email);
            ResultSet emailRs = checkEmailStmt.executeQuery();
            
            if (emailRs.next()) {
                JOptionPane.showMessageDialog(this, 
                    "Email already registered", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if phone already exists
            String checkPhoneQuery = "SELECT * FROM users WHERE phone = ?";
            PreparedStatement checkPhoneStmt = conn.prepareStatement(checkPhoneQuery);
            checkPhoneStmt.setString(1, phone);
            ResultSet phoneRs = checkPhoneStmt.executeQuery();
            
            if (phoneRs.next()) {
                JOptionPane.showMessageDialog(this, 
                    "Phone number already registered", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Insert new user
            String insertQuery = "INSERT INTO users (username, password, email, phone) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, email);
            insertStmt.setString(4, phone);
            
            int result = insertStmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Registration successful! Please login.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                new LoginFrame();
                dispose();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Database error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}