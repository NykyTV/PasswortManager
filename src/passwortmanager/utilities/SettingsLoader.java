package passwortmanager.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import passwortmanager.window.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingsLoader {


    // Bestehende Settings laden
    public static JSONObject loadSettings() {
        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new FileReader(Common.SETTINGS_FILE));
        } catch (FileNotFoundException e) {
            JSONObject settings = new JSONObject();
            settings.put("darkMode", false);
            try {
                Files.write(Paths.get(Common.SETTINGS_FILE), settings.toString().getBytes());
            }catch (Exception ex) {
                ex.printStackTrace();
                return settings; // nur darkmode = false bei Datei nicht vorhanden
            }
            return settings; // nur darkmode = false bei Datei nicht vorhanden
        }catch (Exception e) {
            e.printStackTrace();
            return new JSONObject(); // leeres Objekt bei Fehler
        }
    }

    // DarkMode speichern
    public static boolean saveDarkModeSetting(boolean darkMode) {
        try {
            JSONObject settings = loadSettings();
            settings.put("darkMode", darkMode);
            Files.write(Paths.get(Common.SETTINGS_FILE), settings.toString().getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void showSettingsDialog(JFrame parent, String currentUsername) {
        JSONObject settings = loadSettings();
        JDialog dialog = new JDialog(parent, "Einstellungen", true);

        JTextField urlField = new JTextField((String) settings.getOrDefault("url", ""));
        JTextField userField = new JTextField((String) settings.getOrDefault("user", ""));
        JTextField passwordField = new JTextField((String) settings.getOrDefault("password", ""));
        boolean serverMode = Boolean.parseBoolean((String) settings.getOrDefault("serverMode", "false"));
        boolean darkMode = Boolean.parseBoolean(String.valueOf(settings.getOrDefault("darkMode", "false")));

        JCheckBox serverModeCheckbox = new JCheckBox("Nur Server-Modus verwenden", serverMode);
        JCheckBox darkModeCheckbox = new JCheckBox("Dark Mode aktivieren", darkMode);

        JTextField oldPwField = new JTextField();
        JTextField newPwField = new JTextField();
        JTextField confirmPwField = new JTextField();

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(new JLabel("Datenbank-URL:"), gbc);
        gbc.gridx = 1;
        formPanel.add(urlField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        formPanel.add(new JLabel("Benutzername:"), gbc);
        gbc.gridx = 1;
        formPanel.add(userField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        formPanel.add(new JLabel("Passwort:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        formPanel.add(new JLabel("Server-Modus:"), gbc);
        gbc.gridx = 1;
        formPanel.add(serverModeCheckbox, gbc);

        gbc.gridy++; gbc.gridx = 0;
        formPanel.add(new JLabel("Dark Mode:"), gbc);
        gbc.gridx = 1;
        formPanel.add(darkModeCheckbox, gbc);

        gbc.gridy++; gbc.gridx = 0;
        formPanel.add(new JLabel("Altes Master-Passwort:"), gbc);
        gbc.gridx = 1;
        formPanel.add(oldPwField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        formPanel.add(new JLabel("Neues Master-Passwort:"), gbc);
        gbc.gridx = 1;
        formPanel.add(newPwField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        formPanel.add(new JLabel("Neues Passwort wiederholen:"), gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPwField, gbc);

        JButton changePwButton = new JButton("Master-Passwort ändern");
        changePwButton.addActionListener(evt -> {
            String oldPw = oldPwField.getText();
            String newPw = newPwField.getText();
            String confirmPw = confirmPwField.getText();

            // Validierungscode
            if (!validatePasswords(oldPw, newPw, confirmPw, parent)) return;

            // Passwortänderung durchführen
            boolean success = new SettingsLoader().changeMasterPassword(
                    currentUsername, // Wird automatisch übergeben
                    oldPw,
                    newPw
            );

            if (success) {
                JOptionPane.showMessageDialog(parent, "Master-Passwort erfolgreich geändert.");
                oldPwField.setText("");
                newPwField.setText("");
                confirmPwField.setText("");
            } else {
                JOptionPane.showMessageDialog(parent, "Fehler beim Ändern des Passworts.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton saveButton = new JButton("Speichern");
        saveButton.addActionListener(e -> {
            settings.put("url", urlField.getText());
            settings.put("user", userField.getText());
            settings.put("password", passwordField.getText());
            settings.put("serverMode", String.valueOf(serverModeCheckbox.isSelected()));
            settings.put("darkMode", darkModeCheckbox.isSelected());

            try (FileWriter writer = new FileWriter(Common.SETTINGS_FILE)) {
                writer.write(settings.toString());
                JOptionPane.showMessageDialog(parent, "Einstellungen gespeichert.");

                dialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parent, "Fehler beim Speichern!", "Fehler", JOptionPane.ERROR_MESSAGE);
            }

            if (parent instanceof MainWindow) {
                ((MainWindow) parent).performDarkmode();
                ((MainWindow) parent).updateSaveButtonIcon();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(changePwButton);
        buttonPanel.add(saveButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(contentPanel);
        dialog.pack();
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static boolean isServerMode() {
        JSONObject settings = loadSettings();
        return Boolean.parseBoolean((String) settings.getOrDefault("serverMode", "false"));
    }

    private static boolean validatePasswords(String oldPw, String newPw, String confirmPw, JFrame parent) {
        if (oldPw.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Bitte alle Felder ausfüllen", "Fehler", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!newPw.equals(confirmPw)) {
            JOptionPane.showMessageDialog(parent, "Passwörter stimmen nicht überein", "Fehler", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean changeMasterPassword(String username, String oldPassword, String newPassword) {
        String filename = Common.getPasswordFilename(username);

        File file = new File(filename);
        if (!file.exists()) return false;

        try {
            JSONArray passwords = passwortmanager.utilities.FileReader.loadFile(filename, oldPassword);

            passwortmanager.utilities.FileReader.saveFile(passwords, filename, newPassword);

            if (isServerMode()) {
                Database.saveUserFileToDatabase(username, file, null);
            }

            return true;

        } catch (Exception e) {
            System.err.println("Fehler: " + e.getMessage());
            System.err.println("Fehler: " + e.getClass().getSimpleName());
            e.printStackTrace();
            return false;
        }
    }
}
