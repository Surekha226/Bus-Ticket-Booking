import com.busbooking.util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("🔌 Testing MySQL Connection...");
        
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("✅ SUCCESS! Connected to MySQL database!");
            System.out.println("📊 Database: busbooking");
            System.out.println("🔗 Connection: " + conn);
        } catch (SQLException e) {
            System.out.println("❌ FAILED! Cannot connect to MySQL!");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}