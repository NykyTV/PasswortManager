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
    public static void savePasswords(MainWindow mainWindow, String masterPassword) {
        JSONArray passwordArray = new JSONArray();
        DefaultTableModel model = mainWindow.getPasswordTableModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            JSONObject entry = new JSONObject();
            entry.put("name", model.getValueAt(i, 0));
            entry.put("username", model.getValueAt(i, 1));
            entry.put("password", model.getValueAt(i, 2));
            passwordArray.add(entry);
        }

        String jsonString = passwordArray.toString();

        try {
            byte[] salt = AES.generateSalt(); // Salt generieren
            SecretKey secretKey = AES.deriveKeyFromPassword(masterPassword, salt);
            IvParameterSpec iv = AES.generateIv();

            String encryptedJson = AES.encrypt(jsonString, secretKey, iv);

            JSONObject encryptedObject = new JSONObject();
            encryptedObject.put("iv", Base64.getEncoder().encodeToString(iv.getIV()));
            encryptedObject.put("salt", Base64.getEncoder().encodeToString(salt)); // Salt speichern
            encryptedObject.put("data", encryptedJson);

            File file = new File("passwords.json");
            if (!file.exists()) {
                file.createNewFile(); // Datei erstellen falls sie nicht existiert
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

}
