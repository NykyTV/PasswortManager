package passwortmanager.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import passwortmanager.window.MainWindow;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Base64;

public class Storage {

    @SuppressWarnings("unchecked")
    public static void savePasswords(MainWindow mainWindow, String masterPassword, String accountName, String newEntryName, String newPassword) {
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

        String jsonString = passwordArray.toString();

        try {
            byte[] salt = AES.generateSalt();
            SecretKey secretKey = AES.deriveKeyFromPassword(masterPassword, salt);
            IvParameterSpec iv = AES.generateIv();

            String encryptedJson = AES.encrypt(jsonString, secretKey, iv);

            JSONObject encryptedObject = new JSONObject();
            encryptedObject.put("iv", Base64.getEncoder().encodeToString(iv.getIV()));
            encryptedObject.put("salt", Base64.getEncoder().encodeToString(salt));
            encryptedObject.put("data", encryptedJson);

            File file = new File(accountName + ".json");
            if (!file.exists()) {
                JOptionPane.showMessageDialog(mainWindow, "Keine gespeicherte Passwort-Datei gefunden.", "Info", JOptionPane.INFORMATION_MESSAGE);
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(encryptedObject.toString());
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainWindow, "Fehler beim Speichern!", "Fehler", JOptionPane.ERROR_MESSAGE);
        }

        if (SettingsLoader.isServerMode()) {
            JSONObject settings = SettingsLoader.loadSettings();
            String serverUrl = extractHostFromUrl((String) settings.get("url"));

            if (Database.isServerAvailable(serverUrl, mainWindow)) {
                Database.saveUserFileToDatabase(accountName, new File(accountName + ".json"), mainWindow);
            } else {
                JOptionPane.showMessageDialog(mainWindow, "Server nicht erreichbar. Änderungen wurden lokal gespeichert und werden später synchronisiert.", "Offline-Modus", JOptionPane.WARNING_MESSAGE);
                mainWindow.setStatus("<html><font color='red'>❌ Server nicht erreichbar. Lokale Speicherung aktiv</font></html>");
                AutoSync.scheduleSync(accountName, mainWindow);
            }
        }

    }

    public static void loadPasswords(MainWindow mainWindow, String masterPassword, String accountName) {
        File file = new File(accountName + ".json");

        if (SettingsLoader.isServerMode()) {
            JSONObject settings = SettingsLoader.loadSettings();
            String serverUrl = extractHostFromUrl((String) settings.get("url"));

            if (Database.isServerAvailable(serverUrl, mainWindow)) {
                Database.loadUserFileFromDatabase(accountName, file, mainWindow);
            } else {
                JOptionPane.showMessageDialog(mainWindow, "Server nicht erreichbar. Offline-Modus wird verwendet.", "Offline-Modus", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (!file.exists()) {
            if (!file.exists() || file.length() == 0) {
                JOptionPane.showMessageDialog(mainWindow, "Keine gespeicherte Passwort-Datei gefunden.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject encryptedObject = (JSONObject) parser.parse(new FileReader(file));

            if (encryptedObject == null ||
                    encryptedObject.get("iv") == null ||
                    encryptedObject.get("salt") == null ||
                    encryptedObject.get("data") == null) {
                //JOptionPane.showMessageDialog(mainWindow, "Die Passwortdatei ist leer oder beschädigt.", "Fehler", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String ivString = (String) encryptedObject.get("iv");
            String saltString = (String) encryptedObject.get("salt");
            String encryptedJson = (String) encryptedObject.get("data");

            byte[] ivBytes = Base64.getDecoder().decode(ivString);
            byte[] saltBytes = Base64.getDecoder().decode(saltString);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            SecretKey secretKey = AES.deriveKeyFromPassword(masterPassword, saltBytes);
            String decryptedJson = AES.decrypt(encryptedJson, secretKey, iv);
            JSONArray passwordArray = (JSONArray) JSONValue.parse(decryptedJson);

            mainWindow.setPasswordTableModel(passwordArray);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainWindow, "Fehler beim Laden!", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String loadPasswordForEntry(String entryName, String username, String masterPassword, String accountName) {
        File file = new File(accountName + ".json");
        if (!file.exists() || file.length() == 0) {
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject encryptedObject = (JSONObject) parser.parse(new FileReader(file));
            if (encryptedObject == null) {
                return null;
            }

            String ivString = (String) encryptedObject.get("iv");
            String saltString = (String) encryptedObject.get("salt");
            String encryptedJson = (String) encryptedObject.get("data");

            if (ivString == null || saltString == null || encryptedJson == null) {
                return null;
            }

            byte[] ivBytes = Base64.getDecoder().decode(ivString);
            byte[] saltBytes = Base64.getDecoder().decode(saltString);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            SecretKey secretKey = AES.deriveKeyFromPassword(masterPassword, saltBytes);
            String decryptedJson = AES.decrypt(encryptedJson, secretKey, iv);

            JSONArray passwordArray = (JSONArray) JSONValue.parse(decryptedJson);
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
