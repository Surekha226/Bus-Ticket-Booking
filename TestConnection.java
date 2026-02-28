import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("=== MySQL Connection Test ===");
        System.out.println("URL: jdbc:mysql://localhost:3306/busbooking");
        System.out.println("Username: root");
        System.out.println("Password: root123");
        System.out.println("------------------------");
        
        try {
            // Load driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[OK] MySQL JDBC Driver loaded successfully");
            
            // Try to connect
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/busbooking?useSSL=false&serverTimezone=UTC", 
                "root", 
                "root123"
            );
            System.out.println("[OK] Connected to MySQL successfully!");
            
            // Test query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM buses");
            if (rs.next()) {
                System.out.println("[OK] Found " + rs.getInt("count") + " buses in database");
            }
            
            // Show tables
            rs = stmt.executeQuery("SHOW TABLES");
            System.out.println("Tables in database:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1));
            }
            
            conn.close();
            System.out.println("[OK] Connection closed");
            
        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR] MySQL JDBC Driver not found!");
            System.out.println("Make sure mysql-connector-j-9.6.0.jar is in the lib folder");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("[ERROR] Connection failed!");
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Message: " + e.getMessage());
            System.out.println("------------------------");
            System.out.println("Troubleshooting tips:");
            System.out.println("1. Check if MySQL is running (services.msc)");
            System.out.println("2. Verify password is correct");
            System.out.println("3. Check if database 'busbooking' exists");
            e.printStackTrace();
        }
    }
}