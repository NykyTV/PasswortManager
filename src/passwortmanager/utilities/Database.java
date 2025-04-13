package passwortmanager.utilities;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;

public class Database {

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver nicht gefunden", e);
        }

        JSONObject settings = SettingsLoader.loadSettings();
        String url = (String) settings.get("url");
        String user = (String) settings.get("user");
        String password = (String) settings.get("password");

        Connection conn = DriverManager.getConnection(url, user, password);
        createTableIfNotExists(conn); // <-- Hier Tabelle automatisch anlegen
        return conn;
    }

    private static void createTableIfNotExists(Connection conn) {
        String checkAndCreate = """
        CREATE TABLE IF NOT EXISTS user_dateien (
            accountName VARCHAR(255) PRIMARY KEY,
            datei LONGBLOB
        )
    """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(checkAndCreate);
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Tabelle:");
            e.printStackTrace();
        }
    }

    public static void saveUserFileToDatabase(String accountName, File file) {
        try (Connection conn = Database.getConnection()) {
            String insertOrUpdate = """
            INSERT INTO user_dateien (accountName, datei)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE datei = VALUES(datei)
        """;

            try (PreparedStatement stmt = conn.prepareStatement(insertOrUpdate);
                 FileInputStream fis = new FileInputStream(file)) {

                stmt.setString(1, accountName);
                stmt.setBinaryStream(2, fis, (int) file.length());
                stmt.executeUpdate();
            }

            System.out.println("Datei erfolgreich gespeichert.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadUserFileFromDatabase(String accountName, File targetFile) {
        try (Connection conn = Database.getConnection()) {
            String select = "SELECT datei FROM user_dateien WHERE accountName = ?";

            try (PreparedStatement stmt = conn.prepareStatement(select)) {
                stmt.setString(1, accountName);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        try (InputStream in = rs.getBinaryStream("datei");
                             FileOutputStream fos = new FileOutputStream(targetFile)) {

                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = in.read(buffer)) != -1) {
                                fos.write(buffer, 0, bytesRead);
                            }

                            System.out.println("Datei erfolgreich geladen.");
                        }
                    } else {
                        System.out.println("Keine Datei für diesen Account gefunden.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
