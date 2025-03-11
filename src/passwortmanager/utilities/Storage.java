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
    public static void savePasswords(MainWindow mainWindow, String masterPassword, String newEntryName, String newPassword) {
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
                password = loadPasswordForEntry(name, username, masterPassword);
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

            File file = new File("passwords.json");
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(encryptedObject.toString());
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainWindow, "Fehler beim Speichern!", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void loadPasswords(MainWindow mainWindow, String masterPassword) {
        File file = new File("passwords.json");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(mainWindow, "Keine gespeicherte Passwort-Datei gefunden.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject encryptedObject = (JSONObject) parser.parse(new FileReader(file));

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

    public static String loadPasswordForEntry(String entryName, String username, String masterPassword) {
        try {
            File file = new File("passwords.json");
            JSONParser parser = new JSONParser();
            JSONObject encryptedObject = (JSONObject) parser.parse(new FileReader(file));

            String ivString = (String) encryptedObject.get("iv");
            String saltString = (String) encryptedObject.get("salt");
            String encryptedJson = (String) encryptedObject.get("data");

            byte[] ivBytes = Base64.getDecoder().decode(ivString);
            byte[] saltBytes = Base64.getDecoder().decode(saltString);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            SecretKey secretKey = AES.deriveKeyFromPassword(masterPassword, saltBytes);
            String decryptedJson = AES.decrypt(encryptedJson, secretKey, iv);

            // Suche nach Name UND Benutzername
            JSONArray passwordArray = (JSONArray) JSONValue.parse(decryptedJson);
            for (Object obj : passwordArray) {
                JSONObject entry = (JSONObject) obj;
                if (entryName.equals(entry.get("name")) &&
                        username.equals(entry.get("username"))) {
                    return (String) entry.get("password");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
