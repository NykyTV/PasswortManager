package passwortmanager.utilities;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import passwortmanager.window.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingsLoader {
    private static final String SETTINGS_FILE = "settings.json";

    // Bestehende Settings laden
    public static JSONObject loadSettings() {
        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new FileReader(SETTINGS_FILE));
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject(); // leeres Objekt bei Fehler oder Datei nicht vorhanden
        }
    }

    // DarkMode speichern
    public static boolean saveDarkModeSetting(boolean darkMode) {
        try {
            JSONObject settings = loadSettings();
            settings.put("darkMode", darkMode);
            Files.write(Paths.get(SETTINGS_FILE), settings.toString().getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void showSettingsDialog(JFrame parent) {
        JSONObject settings = loadSettings();

        JTextField urlField = new JTextField((String) settings.getOrDefault("url", ""));
        JTextField userField = new JTextField((String) settings.getOrDefault("user", ""));
        JTextField passwordField = new JTextField((String) settings.getOrDefault("password", ""));
        boolean serverMode = Boolean.parseBoolean((String) settings.getOrDefault("serverMode", "false"));
        boolean darkMode = Boolean.parseBoolean(String.valueOf(settings.getOrDefault("darkMode", "false")));

        JCheckBox serverModeCheckbox = new JCheckBox("Nur Server-Modus verwenden", serverMode);
        JCheckBox darkModeCheckbox = new JCheckBox("Dark Mode aktivieren", darkMode);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10)); // +1 Zeile wegen DarkMode
        panel.add(new JLabel("Datenbank-URL:"));
        panel.add(urlField);
        panel.add(new JLabel("Benutzername:"));
        panel.add(userField);
        panel.add(new JLabel("Passwort:"));
        panel.add(passwordField);
        panel.add(serverModeCheckbox);
        panel.add(darkModeCheckbox);

        // Dialog vorher deklarieren
        JDialog dialog = new JDialog(parent, "Datenbank-Einstellungen", true);

        JButton saveButton = new JButton("Speichern");
        saveButton.addActionListener(e -> {
            settings.put("url", urlField.getText());
            settings.put("user", userField.getText());
            settings.put("password", passwordField.getText());
            settings.put("serverMode", String.valueOf(serverModeCheckbox.isSelected()));
            settings.put("darkMode", darkModeCheckbox.isSelected());

            try (FileWriter writer = new FileWriter(SETTINGS_FILE)) {
                writer.write(settings.toString());
                JOptionPane.showMessageDialog(parent, "Einstellungen gespeichert.");

                dialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parent, "Fehler beim Speichern!", "Fehler", JOptionPane.ERROR_MESSAGE);
            }

            if (parent instanceof MainWindow) {
                ((MainWindow) parent).performDarkmode();
            }
        });

        panel.add(new JLabel());
        panel.add(saveButton);

        dialog.add(panel);
        dialog.pack();
        dialog.setSize(600, 300);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static boolean isServerMode() {
        JSONObject settings = loadSettings();
        return Boolean.parseBoolean((String) settings.getOrDefault("serverMode", "false"));
    }

}
