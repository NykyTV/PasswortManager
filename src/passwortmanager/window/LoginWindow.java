package passwortmanager.window;

import passwortmanager.utilities.Darkmode;
import passwortmanager.utilities.AES;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.*;
import java.security.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import passwortmanager.utilities.Storage;

import java.io.StringReader;

public class LoginWindow extends JFrame {

    private Darkmode darkmodeUtility;
    public JPanel LoginWindow;
    public JButton loginButton;
    public JButton registerButton;
    public JButton darkModeButton;
    public JTextField benutzerNameEingabe;
    public JTextField passwortEingabe;
    public JLabel benutzerText;
    public JLabel passwortText;
    public JLabel label_title;
    private Boolean darkMode = false;
    private static final String CREDENTIALS_FILE = "credentials.json";
    private static final String SETTINGS_FILE = "settings.json";

    public LoginWindow(String title) {
        super(title);
        LoginWindow = new JPanel();
        darkmodeUtility = new Darkmode(this);
        try{
            String test = loadSettings().toString();
            String test1 = loadSettings().toString();
            test = test.substring(12, 16);
            test1 = test1.substring(12, 17);
            System.out.println(test);
            switch (test) {
                case "true":
                    darkMode = true;
                    break;
                default:
                    break;
            }
            switch (test1) {
                case "false":
                    darkMode = false;
                    break;
                default:
                    break;
            }
        }catch (Exception e) {}

        this.setMinimumSize(new Dimension(330, 400));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(LoginWindow);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.pack();
        this.setLayout(new BoxLayout(LoginWindow, BoxLayout.Y_AXIS));

        addComponents();
        setupActionListeners();
        darkmodeUtility.activateDarkMode(darkMode);
        setVisible(true);
    }

    private void setupActionListeners() {
        loginButton.addActionListener(e -> performLogin());
        darkModeButton.addActionListener(e -> performDarkmode());
        registerButton.addActionListener(e -> performRegistration());

        // Fügen Sie einen KeyListener zum Passwort-Feld hinzu
        passwortEingabe.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
    }

    public void addComponents() {
        benutzerText = new JLabel("Geben Sie Ihren Benutzername ein");
        passwortText = new JLabel("Geben Sie Ihr Passwort ein");
        benutzerNameEingabe = new JTextField(20);
        passwortEingabe = new JPasswordField(20);
        loginButton = new JButton("Login");
        darkModeButton = new JButton("Darkmode");
        label_title = new JLabel("Passwort Manager");
        registerButton = new JButton("Register");

        Border textFeldBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        Dimension textFeldGroesse = new Dimension(270, 20);
        benutzerNameEingabe.setMaximumSize(textFeldGroesse);
        benutzerNameEingabe.setBorder(textFeldBorder);
        passwortEingabe.setMaximumSize(textFeldGroesse);

        label_title.setFont(new Font("Arial", Font.BOLD, 24));
        label_title.setForeground(new Color(0, 102, 204));
        label_title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        loginButton.setBackground(Color.WHITE);
        darkModeButton.setBackground(Color.WHITE);
        registerButton.setBackground(Color.WHITE);

        setElementLocation(darkModeButton);
        setElementLocation(label_title);
        setElementLocation(registerButton);
        setElementLocation(benutzerText);
        setElementLocation(benutzerNameEingabe);
        setElementLocation(passwortText);
        setElementLocation(passwortEingabe);
        setElementLocation(loginButton);

        LoginWindow.add(label_title);
        LoginWindow.add(darkModeButton);
        addAbstand(30);
        LoginWindow.add(benutzerText);
        LoginWindow.add(benutzerNameEingabe);
        addAbstand(20);
        LoginWindow.add(passwortText);
        LoginWindow.add(passwortEingabe);
        addAbstand(75);
        LoginWindow.add(loginButton);
        addAbstand(10);
        LoginWindow.add(registerButton);
    }

    public void addAbstand(int Abstand) {
        LoginWindow.add(Box.createVerticalStrut(Abstand));
    }

    private void performLogin() {
        String username = benutzerNameEingabe.getText();
        String password = passwortEingabe.getText();
        if (checkLogin(username, password)) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                MainWindow mainWindow = new MainWindow("Passwort Manager", password);
                mainWindow.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Ungültige Anmeldedaten", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performDarkmode() {
        darkMode = !darkMode;
        saveSettings(darkMode);
        darkmodeUtility.activateDarkMode(darkMode);
    }

    private void performRegistration() {
        String username = benutzerNameEingabe.getText();
        String password = passwortEingabe.getText();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Benutzername und Passwort dürfen nicht leer sein", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (saveCredentials(username, password)) {
            JOptionPane.showMessageDialog(this, "Registrierung erfolgreich", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Registrierung fehlgeschlagen", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean saveCredentials(String username, String password) {
        try {
            String hashedPassword = hashPassword(password);
            JSONObject credentials = loadCredentials();
            credentials.put(username, hashedPassword);
            Files.write(Paths.get(CREDENTIALS_FILE), credentials.toString().getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getStoredHash(String username) throws Exception {
        JSONObject credentials = loadCredentials();
        return (String) credentials.get(username);
    }

    private JSONObject loadCredentials() throws Exception {
        if (Files.exists(Paths.get(CREDENTIALS_FILE))) {
            String content = new String(Files.readAllBytes(Paths.get(CREDENTIALS_FILE)));
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new StringReader(content));
        }
        return new JSONObject();
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private boolean checkLogin(String username, String password) {
        try {
            String storedHash = getStoredHash(username);
            if (storedHash == null) return false;
            String inputHash = hashPassword(password);
            return storedHash.equals(inputHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setElementLocation(JButton element) {
        element.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    private void setElementLocation(JTextField element) {
        element.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    private void setElementLocation(JLabel element) {
        element.setAlignmentX(Component.CENTER_ALIGNMENT);
    }


    private boolean saveSettings(boolean darkMode) {
        try {
            JSONObject settings = loadSettings();
            settings.put("darkMode" ,darkMode);
            Files.write(Paths.get(SETTINGS_FILE), settings.toString().getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getSettings(String username) throws Exception {
        JSONObject settings = loadSettings();
        return (String) settings.get(username);
    }

    private JSONObject loadSettings() throws Exception {
        if (Files.exists(Paths.get(SETTINGS_FILE))) {
            String content = new String(Files.readAllBytes(Paths.get(SETTINGS_FILE)));
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new StringReader(content));
        }
        return new JSONObject();
    }

    public static String removeFirstXCharacters(String input, int x) {
        // Sicherstellen, dass x nicht größer ist als die Länge des Strings
        if (input == null || x >= input.length()) {
            return ""; // Rückgabe eines leeren Strings, wenn x zu groß ist
        }
        return input.substring(x); // Gibt den String ab dem Index x zurück
    }
}