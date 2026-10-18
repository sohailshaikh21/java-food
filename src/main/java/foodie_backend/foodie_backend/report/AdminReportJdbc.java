package foodie_backend.foodie_backend.report;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class AdminReportJdbc {

    public static void main(String[] args) throws Exception {
        Properties properties = loadProperties();

        String url = properties.getProperty("spring.datasource.url");
        String username = properties.getProperty("spring.datasource.username");
        String password = properties.getProperty("spring.datasource.password");

        Map<String, Integer> report = countOrdersByStatus(url, username, password);

        if (report.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }
        report.forEach((status, count) -> System.out.println(status + " : " + count));
    }

    public static Map<String, Integer> countOrdersByStatus(String url, String username, String password) {
        String sql = "SELECT status, COUNT(*) AS total FROM orders GROUP BY status ORDER BY status";
        Map<String, Integer> report = new LinkedHashMap<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                report.put(resultSet.getString("status"), resultSet.getInt("total"));
            }
        } catch (Exception e) {
            System.out.println("Could not build the report: " + e.getMessage());
        }
        return report;
    }

    private static Properties loadProperties() throws Exception {
        Properties properties = new Properties();
        try (InputStream input = AdminReportJdbc.class.getResourceAsStream("/application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        }
        return properties;
    }
}
