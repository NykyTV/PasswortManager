package passwortmanager.utilities;

import org.json.simple.JSONObject;
import passwortmanager.window.MainWindow;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
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

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            createTableIfNotExists(conn);
            return conn;
        } catch (SQLException e) {
            if (e.getMessage().contains("Access denied")) {
                JOptionPane.showMessageDialog(null,
                        "Zugriff auf die Datenbank verweigert.\nBitte überprüfe Benutzername, Passwort oder IP-Berechtigungen.",
                        "Datenbankfehler",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Fehler beim Verbinden zur Datenbank:\n" + e.getMessage(),
                        "Datenbankfehler",
                        JOptionPane.ERROR_MESSAGE);
            }
            return null;
        }
    }

    private static void createTableIfNotExists(Connection conn) {
        String checkAndCreate = """
        CREATE TABLE IF NOT EXISTS user_dateien (
            accountName VARCHAR(255) PRIMARY KEY,
            datei LONGBLOB,
            version BIGINT
        )
    """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(checkAndCreate);
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Tabelle:");
            e.printStackTrace();
        }
    }

    public static void saveUserFileToDatabase(String accountName, File file, MainWindow mainWindow) {
        try (Connection conn = Database.getConnection()) {
            // Get server version first
            long serverVersion = getServerVersion(conn, accountName);
            long localVersion = FileReader.getFileVersion(file.getPath());

            // Only save if local version is newer
            if (localVersion > serverVersion) {
                String insertOrUpdate = """
                    INSERT INTO user_dateien (accountName, datei, version)
                    VALUES (?, ?, ?)
                    ON DUPLICATE KEY UPDATE datei = VALUES(datei), version = VALUES(version)
                """;

                try (PreparedStatement stmt = conn.prepareStatement(insertOrUpdate);
                     FileInputStream fis = new FileInputStream(file)) {

                    stmt.setString(1, accountName);
                    stmt.setBinaryStream(2, fis, (int) file.length());
                    stmt.setLong(3, localVersion);
                    stmt.executeUpdate();
                }

                if (mainWindow != null) mainWindow.setStatus("<html><font color='green'>✔️ Datei gespeichert</font></html>");
                else System.out.println("Datei erfolgreich gespeichert.");
            } else {
                if (mainWindow != null) mainWindow.setStatus("<html><font color='orange'>⚠️ Server-Version ist aktueller</font></html>");
            }
        } catch (Exception e) {
            if (mainWindow != null) mainWindow.setStatus("<html><font color='red'>❌ Fehler beim Speichern: </font></html>" + e.getMessage());
            else e.printStackTrace();
        }
    }

    public static void loadUserFileFromDatabase(String accountName, File targetFile, MainWindow mainWindow) {
        try (Connection conn = Database.getConnection()) {
            if (conn == null) return;

            // Compare versions first
            long serverVersion = getServerVersion(conn, accountName);
            long localVersion = targetFile.exists() ? FileReader.getFileVersion(targetFile.getPath()) : 0;

            // Only load if server version is newer
            if (serverVersion > localVersion) {
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

                                if (mainWindow != null) mainWindow.setStatus("<html><font color='green'>✔️ Datei geladen</font></html>");
                                else System.out.println("Datei erfolgreich geladen.");
                            }
                        }
                    }
                }
            } else if (localVersion > serverVersion) {
                if (mainWindow != null) {
                    mainWindow.setStatus("<html><font color='orange'>⚠️ Lokale Version ist aktueller</font></html>");
                    mainWindow.getButton_Save().setEnabled(true);
                }
            } else {
                if (mainWindow != null) {
                    mainWindow.setStatus("<html><font color='green'>✔️ Versionen sind synchron</font></html>");
                    mainWindow.getButton_Save().setEnabled(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long getServerVersion(Connection conn, String accountName) throws SQLException {
        String select = "SELECT version FROM user_dateien WHERE accountName = ?";
        try (PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setString(1, accountName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("version");
                }
            }
        }
        return 0;
    }

    public static boolean isServerAvailable(String serverUrl, MainWindow mainWindow) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(serverUrl, 3306), 2000);
            return true;
        } catch (IOException e) {
            String message = "<html><font color='red'>❌ Server nicht erreichbar</font></html>";
            if (mainWindow != null) mainWindow.setStatus(message);
            else System.err.println(message);
            return false;
        }
    }
}
