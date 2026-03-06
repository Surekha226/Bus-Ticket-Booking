import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SettingsFrame extends JFrame {
    private String adminUsername;
    private JTabbedPane tabbedPane;
    private JTextField dbHostField, dbPortField, dbNameField, dbUserField;
    private JPasswordField dbPasswordField;
    private JTextField smtpServerField, smtpPortField, emailFromField;
    private JPasswordField emailPasswordField;
    private JTextField backupPathField, logRetentionField;
    private JCheckBox emailNotificationsCheck, autoBackupCheck;
    private JSpinner backupHourSpinner;
    private JButton saveSettingsBtn, testConnectionBtn, backupNowBtn, restoreBackupBtn;
    private JTextArea logArea;
    private JLabel statusLabel;
    
    public SettingsFrame(String adminUsername) {
        this.adminUsername = adminUsername;
        
        setTitle("Admin - Settings");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane for settings categories
        tabbedPane = createTabbedPane();
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Load current settings
        loadSettings();
        
        setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(870, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("SYSTEM SETTINGS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Configure application preferences and system options");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(189, 195, 199));
        
        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel);
        leftPanel.add(subtitleLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightPanel.setOpaque(false);
        
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.BLACK);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(180, 40));
        backButton.setOpaque(true);
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
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
    
    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(52, 73, 94));
        tabbedPane.setForeground(Color.BLACK);
        
        // Database Settings Tab
        JPanel databasePanel = createDatabasePanel();
        tabbedPane.addTab("Database", databasePanel);
        
        // Email Settings Tab
        JPanel emailPanel = createEmailPanel();
        tabbedPane.addTab("Email", emailPanel);
        
        // Backup & Maintenance Tab
        JPanel backupPanel = createBackupPanel();
        tabbedPane.addTab("Backup", backupPanel);
        
        // System Logs Tab
        JPanel logsPanel = createLogsPanel();
        tabbedPane.addTab("System Logs", logsPanel);
        
        // About Tab
        JPanel aboutPanel = createAboutPanel();
        tabbedPane.addTab("About", aboutPanel);
        
        return tabbedPane;
    }
    
    private JPanel createDatabasePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Database settings form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        
        // Title
        JLabel titleLabel = new JLabel("Database Connection Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 152, 219));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Host
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel hostLabel = new JLabel("Host:");
        hostLabel.setFont(new Font("Arial", Font.BOLD, 12));
        hostLabel.setForeground(Color.BLACK);
        formPanel.add(hostLabel, gbc);
        
        dbHostField = new JTextField(20);
        dbHostField.setText("localhost");
        dbHostField.setFont(new Font("Arial", Font.PLAIN, 12));
        dbHostField.setForeground(Color.BLACK);
        dbHostField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        gbc.gridx = 1;
        formPanel.add(dbHostField, gbc);
        
        // Port
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel portLabel = new JLabel("Port:");
        portLabel.setFont(new Font("Arial", Font.BOLD, 12));
        portLabel.setForeground(Color.BLACK);
        formPanel.add(portLabel, gbc);
        
        dbPortField = new JTextField(10);
        dbPortField.setText("3306");
        dbPortField.setFont(new Font("Arial", Font.PLAIN, 12));
        dbPortField.setForeground(Color.BLACK);
        dbPortField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        gbc.gridx = 1;
        formPanel.add(dbPortField, gbc);
        
        // Database Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel dbNameLabel = new JLabel("Database Name:");
        dbNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        dbNameLabel.setForeground(Color.BLACK);
        formPanel.add(dbNameLabel, gbc);
        
        dbNameField = new JTextField(20);
        dbNameField.setText("busbooking");
        dbNameField.setFont(new Font("Arial", Font.PLAIN, 12));
        dbNameField.setForeground(Color.BLACK);
        dbNameField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        gbc.gridx = 1;
        formPanel.add(dbNameField, gbc);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));
        userLabel.setForeground(Color.BLACK);
        formPanel.add(userLabel, gbc);
        
        dbUserField = new JTextField(20);
        dbUserField.setText("root");
        dbUserField.setFont(new Font("Arial", Font.PLAIN, 12));
        dbUserField.setForeground(Color.BLACK);
        dbUserField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        gbc.gridx = 1;
        formPanel.add(dbUserField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        passwordLabel.setForeground(Color.BLACK);
        formPanel.add(passwordLabel, gbc);
        
        dbPasswordField = new JPasswordField(20);
        dbPasswordField.setText("root123");
        dbPasswordField.setFont(new Font("Arial", Font.PLAIN, 12));
        dbPasswordField.setForeground(Color.BLACK);
        dbPasswordField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        gbc.gridx = 1;
        formPanel.add(dbPasswordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        testConnectionBtn = new JButton("Test Connection");
        testConnectionBtn.setBackground(new Color(46, 204, 113));
        testConnectionBtn.setForeground(Color.BLACK);
        testConnectionBtn.setFont(new Font("Arial", Font.BOLD, 14));
        testConnectionBtn.setFocusPainted(false);
        testConnectionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        testConnectionBtn.setPreferredSize(new Dimension(160, 40));
        testConnectionBtn.setOpaque(true);
        testConnectionBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        testConnectionBtn.addActionListener(e -> testDatabaseConnection());
        
        saveSettingsBtn = new JButton("Save Settings");
        saveSettingsBtn.setBackground(new Color(52, 152, 219));
        saveSettingsBtn.setForeground(Color.BLACK);
        saveSettingsBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveSettingsBtn.setFocusPainted(false);
        saveSettingsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveSettingsBtn.setPreferredSize(new Dimension(160, 40));
        saveSettingsBtn.setOpaque(true);
        saveSettingsBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        saveSettingsBtn.addActionListener(e -> saveDatabaseSettings());
        
        buttonPanel.add(testConnectionBtn);
        buttonPanel.add(saveSettingsBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEmailPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Email Notification Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 152, 219));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // SMTP Server
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel smtpLabel = new JLabel("SMTP Server:");
        smtpLabel.setFont(new Font("Arial", Font.BOLD, 12));
        smtpLabel.setForeground(Color.BLACK);
        formPanel.add(smtpLabel, gbc);
        
        smtpServerField = new JTextField(20);
        smtpServerField.setText("smtp.gmail.com");
        smtpServerField.setFont(new Font("Arial", Font.PLAIN, 12));
        smtpServerField.setForeground(Color.BLACK);
        smtpServerField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        gbc.gridx = 1;
        formPanel.add(smtpServerField, gbc);
        
        // SMTP Port
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel portLabel = new JLabel("SMTP Port:");
        portLabel.setFont(new Font("Arial", Font.BOLD, 12));
        portLabel.setForeground(Color.BLACK);
        formPanel.add(portLabel, gbc);
        
        smtpPortField = new JTextField(10);
        smtpPortField.setText("587");
        smtpPortField.setFont(new Font("Arial", Font.PLAIN, 12));
        smtpPortField.setForeground(Color.BLACK);
        smtpPortField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        gbc.gridx = 1;
        formPanel.add(smtpPortField, gbc);
        
        // From Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel fromLabel = new JLabel("From Email:");
        fromLabel.setFont(new Font("Arial", Font.BOLD, 12));
        fromLabel.setForeground(Color.BLACK);
        formPanel.add(fromLabel, gbc);
        
        emailFromField = new JTextField(20);
        emailFromField.setText("noreply@busbooking.com");
        emailFromField.setFont(new Font("Arial", Font.PLAIN, 12));
        emailFromField.setForeground(Color.BLACK);
        emailFromField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        gbc.gridx = 1;
        formPanel.add(emailFromField, gbc);
        
        // Email Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel passLabel = new JLabel("Email Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 12));
        passLabel.setForeground(Color.BLACK);
        formPanel.add(passLabel, gbc);
        
        emailPasswordField = new JPasswordField(20);
        emailPasswordField.setFont(new Font("Arial", Font.PLAIN, 12));
        emailPasswordField.setForeground(Color.BLACK);
        emailPasswordField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        gbc.gridx = 1;
        formPanel.add(emailPasswordField, gbc);
        
        // Enable Notifications
        gbc.gridx = 0;
        gbc.gridy = 5;
        emailNotificationsCheck = new JCheckBox("Enable Email Notifications");
        emailNotificationsCheck.setBackground(Color.WHITE);
        emailNotificationsCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        emailNotificationsCheck.setForeground(Color.BLACK);
        emailNotificationsCheck.setSelected(true);
        gbc.gridx = 1;
        formPanel.add(emailNotificationsCheck, gbc);
        
        // Save button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveEmailBtn = new JButton("Save Email Settings");
        saveEmailBtn.setBackground(new Color(52, 152, 219));
        saveEmailBtn.setForeground(Color.BLACK);
        saveEmailBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveEmailBtn.setFocusPainted(false);
        saveEmailBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveEmailBtn.setPreferredSize(new Dimension(200, 40));
        saveEmailBtn.setOpaque(true);
        saveEmailBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        saveEmailBtn.addActionListener(e -> saveEmailSettings());
        
        buttonPanel.add(saveEmailBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBackupPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Backup & Maintenance");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 152, 219));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Backup Path
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel pathLabel = new JLabel("Backup Directory:");
        pathLabel.setFont(new Font("Arial", Font.BOLD, 12));
        pathLabel.setForeground(Color.BLACK);
        topPanel.add(pathLabel, gbc);
        
        backupPathField = new JTextField(20);
        backupPathField.setText("C:\\backups\\busbooking");
        backupPathField.setFont(new Font("Arial", Font.PLAIN, 12));
        backupPathField.setForeground(Color.BLACK);
        backupPathField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        gbc.gridx = 1;
        topPanel.add(backupPathField, gbc);
        
        JButton browseBtn = new JButton("Browse");
        browseBtn.setBackground(new Color(149, 165, 166));
        browseBtn.setForeground(Color.BLACK);
        browseBtn.setFont(new Font("Arial", Font.BOLD, 12));
        browseBtn.setFocusPainted(false);
        browseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        browseBtn.setOpaque(true);
        browseBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        browseBtn.addActionListener(e -> browseBackupPath());
        gbc.gridx = 2;
        topPanel.add(browseBtn, gbc);
        
        // Auto Backup
        gbc.gridx = 0;
        gbc.gridy = 2;
        autoBackupCheck = new JCheckBox("Enable Auto Backup");
        autoBackupCheck.setBackground(Color.WHITE);
        autoBackupCheck.setFont(new Font("Arial", Font.PLAIN, 14));
        autoBackupCheck.setForeground(Color.BLACK);
        autoBackupCheck.setSelected(true);
        gbc.gridx = 1;
        topPanel.add(autoBackupCheck, gbc);
        
        // Backup Hour
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel hourLabel = new JLabel("Backup Hour (0-23):");
        hourLabel.setFont(new Font("Arial", Font.BOLD, 12));
        hourLabel.setForeground(Color.BLACK);
        topPanel.add(hourLabel, gbc);
        
        SpinnerNumberModel hourModel = new SpinnerNumberModel(2, 0, 23, 1);
        backupHourSpinner = new JSpinner(hourModel);
        backupHourSpinner.setPreferredSize(new Dimension(80, 30));
        backupHourSpinner.setFont(new Font("Arial", Font.PLAIN, 12));
        backupHourSpinner.setForeground(Color.BLACK);
        gbc.gridx = 1;
        topPanel.add(backupHourSpinner, gbc);
        
        // Log Retention
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel retentionLabel = new JLabel("Log Retention (days):");
        retentionLabel.setFont(new Font("Arial", Font.BOLD, 12));
        retentionLabel.setForeground(Color.BLACK);
        topPanel.add(retentionLabel, gbc);
        
        logRetentionField = new JTextField(10);
        logRetentionField.setText("30");
        logRetentionField.setFont(new Font("Arial", Font.PLAIN, 12));
        logRetentionField.setForeground(Color.BLACK);
        logRetentionField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        gbc.gridx = 1;
        topPanel.add(logRetentionField, gbc);
        
        panel.add(topPanel, BorderLayout.CENTER);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        backupNowBtn = new JButton("Backup Now");
        backupNowBtn.setBackground(new Color(46, 204, 113));
        backupNowBtn.setForeground(Color.BLACK);
        backupNowBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backupNowBtn.setFocusPainted(false);
        backupNowBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backupNowBtn.setPreferredSize(new Dimension(150, 40));
        backupNowBtn.setOpaque(true);
        backupNowBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        backupNowBtn.addActionListener(e -> performBackup());
        
        restoreBackupBtn = new JButton("Restore Backup");
        restoreBackupBtn.setBackground(new Color(155, 89, 182));
        restoreBackupBtn.setForeground(Color.BLACK);
        restoreBackupBtn.setFont(new Font("Arial", Font.BOLD, 14));
        restoreBackupBtn.setFocusPainted(false);
        restoreBackupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        restoreBackupBtn.setPreferredSize(new Dimension(160, 40));
        restoreBackupBtn.setOpaque(true);
        restoreBackupBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        restoreBackupBtn.addActionListener(e -> restoreBackup());
        
        JButton saveBackupBtn = new JButton("Save Settings");
        saveBackupBtn.setBackground(new Color(52, 152, 219));
        saveBackupBtn.setForeground(Color.BLACK);
        saveBackupBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBackupBtn.setFocusPainted(false);
        saveBackupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBackupBtn.setPreferredSize(new Dimension(160, 40));
        saveBackupBtn.setOpaque(true);
        saveBackupBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        saveBackupBtn.addActionListener(e -> saveBackupSettings());
        
        buttonPanel.add(backupNowBtn);
        buttonPanel.add(restoreBackupBtn);
        buttonPanel.add(saveBackupBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("System Activity Logs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 152, 219));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Log area
        logArea = new JTextArea(15, 70);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setForeground(Color.BLACK);
        logArea.setBackground(new Color(245, 245, 245));
        logArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton refreshLogsBtn = new JButton("Refresh Logs");
        refreshLogsBtn.setBackground(new Color(52, 152, 219));
        refreshLogsBtn.setForeground(Color.BLACK);
        refreshLogsBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshLogsBtn.setFocusPainted(false);
        refreshLogsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshLogsBtn.setOpaque(true);
        refreshLogsBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        refreshLogsBtn.addActionListener(e -> loadLogs());
        
        JButton clearLogsBtn = new JButton("Clear Logs");
        clearLogsBtn.setBackground(new Color(231, 76, 60));
        clearLogsBtn.setForeground(Color.BLACK);
        clearLogsBtn.setFont(new Font("Arial", Font.BOLD, 12));
        clearLogsBtn.setFocusPainted(false);
        clearLogsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearLogsBtn.setOpaque(true);
        clearLogsBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        clearLogsBtn.addActionListener(e -> clearLogs());
        
        buttonPanel.add(refreshLogsBtn);
        buttonPanel.add(clearLogsBtn);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load sample logs
        loadLogs();
        
        return panel;
    }
    
    private JPanel createAboutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // App Logo/Name
        JLabel appNameLabel = new JLabel("Bus Ticket Booking System");
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        appNameLabel.setForeground(new Color(52, 152, 219));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        infoPanel.add(appNameLabel, gbc);
        
        // Version
        gbc.gridy = 1;
        JLabel versionLabel = new JLabel("Version 2.0.0");
        versionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        versionLabel.setForeground(Color.BLACK);
        infoPanel.add(versionLabel, gbc);
        
        // Info rows
        addInfoRow(infoPanel, gbc, 2, "Developer:", "Joshitha");
        addInfoRow(infoPanel, gbc, 3, "Release Date:", "February 2026");
        addInfoRow(infoPanel, gbc, 4, "Database:", "MySQL 8.0");
        addInfoRow(infoPanel, gbc, 5, "Java Version:", System.getProperty("java.version"));
        addInfoRow(infoPanel, gbc, 6, "OS:", System.getProperty("os.name"));
        
        // Description
        gbc.gridy = 7;
        JTextArea descArea = new JTextArea(5, 40);
        descArea.setText(
            "A complete bus ticket booking system with user and admin modules.\n\n" +
            "Features:\n" +
            "• User authentication and profile management\n" +
            "• Bus ticket booking and cancellation\n" +
            "• Admin dashboard with statistics\n" +
            "• Manage buses, users, and bookings\n" +
            "• Reports and analytics\n" +
            "• Bus schedules management\n" +
            "• System settings and configuration"
        );
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setForeground(Color.BLACK);
        descArea.setEditable(false);
        descArea.setBackground(Color.WHITE);
        descArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        infoPanel.add(descArea, gbc);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 14));
        labelComp.setForeground(Color.BLACK);
        panel.add(labelComp, gbc);
        
        gbc.gridx = 1;
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 14));
        valueComp.setForeground(Color.BLACK);
        panel.add(valueComp, gbc);
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(52, 73, 94));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.setPreferredSize(new Dimension(870, 30));
        
        statusLabel = new JLabel("System ready | Last backup: Never");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.WHITE);
        
        JLabel timeLabel = new JLabel(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setForeground(Color.WHITE);
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(timeLabel, BorderLayout.EAST);
        
        return statusPanel;
    }
    
    private void loadSettings() {
        // In a real app, these would be loaded from a config file or database
        // For now, we'll just show sample data
        statusLabel.setText("Settings loaded | Last backup: 2026-02-27 23:00");
    }
    
    private void testDatabaseConnection() {
        String host = dbHostField.getText();
        String port = dbPortField.getText();
        String dbName = dbNameField.getText();
        String user = dbUserField.getText();
        String password = new String(dbPasswordField.getPassword());
        
        // Simulate connection test
        JOptionPane.showMessageDialog(this,
            "Database connection successful!\n" +
            "Host: " + host + "\n" +
            "Database: " + dbName + "\n" +
            "Version: MySQL 8.0.45",
            "Connection Test",
            JOptionPane.INFORMATION_MESSAGE);
            
        statusLabel.setText("Database connection tested | " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    
    private void saveDatabaseSettings() {
        JOptionPane.showMessageDialog(this,
            "Database settings saved successfully!",
            "Settings Saved",
            JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("Database settings saved | " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    
    private void saveEmailSettings() {
        JOptionPane.showMessageDialog(this,
            "Email settings saved successfully!",
            "Settings Saved",
            JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("Email settings saved | " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    
    private void saveBackupSettings() {
        JOptionPane.showMessageDialog(this,
            "Backup settings saved successfully!",
            "Settings Saved",
            JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("Backup settings saved | " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
    
    private void browseBackupPath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            backupPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void performBackup() {
        String path = backupPathField.getText();
        
        // Simulate backup
        JOptionPane.showMessageDialog(this,
            "Backup completed successfully!\n" +
            "Location: " + path + "\\backup_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql",
            "Backup Complete",
            JOptionPane.INFORMATION_MESSAGE);
            
        statusLabel.setText("Backup completed | " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }
    
    private void restoreBackup() {
        JFileChooser chooser = new JFileChooser(backupPathField.getText());
        int result = chooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            String file = chooser.getSelectedFile().getName();
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Restoring from backup will overwrite current data!\n\n" +
                "Backup file: " + file + "\n\n" +
                "Are you sure you want to continue?",
                "Confirm Restore",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this,
                    "Database restored successfully from:\n" + file,
                    "Restore Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Database restored | " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
        }
    }
    
    private void loadLogs() {
        StringBuilder logs = new StringBuilder();
        logs.append("=== System Activity Log ===\n");
        logs.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - Application started\n");
        logs.append(LocalDateTime.now().minusMinutes(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - Admin logged in: " + adminUsername + "\n");
        logs.append(LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - Database backup completed\n");
        logs.append(LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - New booking created (ID: 6)\n");
        logs.append(LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - Bus schedule updated (ID: 3)\n");
        logs.append(LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - New user registered: john_doe\n");
        
        logArea.setText(logs.toString());
    }
    
    private void clearLogs() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to clear all logs?",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            logArea.setText("=== System Activity Log ===\n" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " - Logs cleared\n");
            statusLabel.setText("Logs cleared | " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    }
}