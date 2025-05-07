package passwortmanager.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import passwortmanager.window.MainWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;

public class Storage {

    @SuppressWarnings("unchecked")
    public static void savePasswords(MainWindow mainWindow, String masterPassword, String accountName, String newEntryName, String newPassword) {
        String filename = Common.getPasswordFilename(accountName);

        JSONArray passwordArray = new JSONArray();
        DefaultTableModel model = mainWindow.getPasswordTableModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            JSONObject entry = new JSONObject();
            String name = (String) model.getValueAt(i, 0);
            String username = (String) model.getValueAt(i, 1);

            // If this is the new entry, use the actual password
            String password;
            if (name.equals(newEntryName)) {
                password = newPassword;
            } else {
                // For existing entries, load the actual password
                password = loadPasswordForEntry(name, username, masterPassword, accountName);
                if (password == null) {
                    password = (String) model.getValueAt(i, 2);
                }
            }

            entry.put("name", name);
            entry.put("username", username);
            entry.put("password", password);
            passwordArray.add(entry);
        }

        try {
            FileReader.saveFile(passwordArray, filename, masterPassword);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }

        if (SettingsLoader.isServerMode()) {
            JSONObject settings = SettingsLoader.loadSettings();
            String serverUrl = extractHostFromUrl((String) settings.get("url"));

            if (Database.isServerAvailable(serverUrl, mainWindow)) {
                Database.saveUserFileToDatabase(accountName, new File(filename), mainWindow);
            } else {
                JOptionPane.showMessageDialog(mainWindow, "Server nicht erreichbar. Änderungen wurden lokal gespeichert und werden später synchronisiert.", "Offline-Modus", JOptionPane.WARNING_MESSAGE);
                mainWindow.setStatus("<html><font color='red'>❌ Server nicht erreichbar. Lokale Speicherung aktiv</font></html>");
                AutoSync.scheduleSync(accountName, mainWindow);
            }
        }

    }

    public static void loadPasswords(MainWindow mainWindow, String masterPassword, String accountName) {
        String filename = Common.getPasswordFilename(accountName);
        File file = new File(filename);

        if (SettingsLoader.isServerMode()) {
            JSONObject settings = SettingsLoader.loadSettings();
            String serverUrl = extractHostFromUrl((String) settings.get("url"));

            if (Database.isServerAvailable(serverUrl, mainWindow)) {
                Database.loadUserFileFromDatabase(accountName, file, mainWindow);
            } else {
                JOptionPane.showMessageDialog(mainWindow, "Server nicht erreichbar. Offline-Modus wird verwendet.", "Offline-Modus", JOptionPane.WARNING_MESSAGE);
            }
        }

        try
        {
            JSONArray passwordArray = FileReader.loadFile(filename, masterPassword);

            mainWindow.setPasswordTableModel(passwordArray);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String loadPasswordForEntry(String entryName, String username, String masterPassword, String accountName) {
        String filename = Common.getPasswordFilename(accountName);
        try
        {
            JSONArray passwordArray = FileReader.loadFile(filename, masterPassword);

            for (Object obj : passwordArray) {
                JSONObject entry = (JSONObject) obj;
                if (entryName.equals(entry.get("name")) &&
                        username.equals(entry.get("username"))) {
                    return (String) entry.get("password");
                }
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Laden des Passworts: " + e.getMessage());
        }
        return null;
    }

    public static String extractHostFromUrl(String url) {
        // z.B. jdbc:mysql://localhost:3306/dbname
        try {
            return url.split("//")[1].split(":")[0]; // "localhost"
        } catch (Exception e) {
            return "localhost";
        }
    }

}
