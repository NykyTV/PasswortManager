package passwortmanager.utilities;

import org.json.simple.JSONArray;
import passwortmanager.window.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import lombok.Getter;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SettingsLoader {
    private static SettingsLoader instance;
    private static final ObjectMapper mapper = new ObjectMapper();

    // === Fields ===
    @Getter private String url;
    @Getter private boolean serverMode;
    @Getter private String password;
    @Getter private boolean darkMode;
    @Getter private String user;

    // === Private constructor with default values ===
    private SettingsLoader() {
        this.url = "jdbc:mysql://127.0.0.1:3306/passwortmanager ";
        this.serverMode = false;
        this.password = "deinPasswort";
        this.darkMode = false;
        this.user = "root";
    }

    // === Ensures any missing field is set to its default ===
    private static void fillMissingDefaults(SettingsLoader s) {
        if (s.url == null) s.url = "jdbc:mysql://127.0.0.1:3306/passwortmanager ";
        if (s.password == null) s.password = "deinPasswort";
        if (s.user == null) s.user = "root";
        // booleans default to false, so no check needed
    }

    // === Singleton access ===
    public synchronized static SettingsLoader getInstance() {
        if (instance == null) {
            instance = loadFromFile();
        }
        return instance;
    }

    // === Property setters with auto-save ===
    public void setUrl(String url) {
        this.url = url;
        saveToFile();
    }

    public void setServerMode(boolean serverMode) {
        this.serverMode = serverMode;
        saveToFile();
    }

    public void setPassword(String password) {
        this.password = password;
        saveToFile();
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        saveToFile();
    }

    public void setUser(String user) {
        this.user = user;
        saveToFile();
    }

    // === File operations ===
    private static SettingsLoader loadFromFile() {
        try {
            File file = new File(Common.SETTINGS_FILE);
            if (file.exists()) {
                SettingsLoader loaded = mapper.readValue(file, SettingsLoader.class);
                fillMissingDefaults(loaded); // in case any fields are null
                return loaded;
            } else {
                SettingsLoader defaultSettings = new SettingsLoader();
                defaultSettings.saveToFile(); // create default file
                return defaultSettings;
            }
        } catch (IOException e) {
            System.err.println("Failed to load settings: " + e.getMessage());
            return new SettingsLoader();
        }
    }

    private void saveToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(Common.SETTINGS_FILE), this);
        } catch (IOException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
        }
    }

    public static void showSettingsDialog(JFrame parent, String currentUsername) {
        SettingsLoader settings = SettingsLoader.getInstance();
        JDialog dialog = new JDialog(parent, "Einstellungen", true);

        JTextField urlField = new JTextField((String) settings.url);
        JTextField userField = new JTextField((String) settings.user);
        JTextField passwordField = new JTextField((String) settings.password);
        boolean serverMode = settings.serverMode;
        boolean darkMode = settings.darkMode;

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
            settings.setUrl(urlField.getText());
            settings.setUser(userField.getText());
            settings.setPassword(passwordField.getText());
            settings.setServerMode(serverModeCheckbox.isSelected());
            settings.setDarkMode(darkModeCheckbox.isSelected());

            if (parent instanceof MainWindow) {
                ((MainWindow) parent).performDarkmode();
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

    // Geänderte changeMasterPassword-Methode
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
