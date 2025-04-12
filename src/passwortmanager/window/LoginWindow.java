package passwortmanager.window;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import passwortmanager.utilities.Darkmode;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class LoginWindow extends JFrame {

    private Darkmode darkmodeUtility;
    public JLabel label_title;
    public JPanel LoginWindow;
    public JButton loginButton;
    public JButton registerButton;
    public JTextField benutzerNameEingabe;
    public JTextField passwortEingabe;
    public JLabel benutzerText;
    public JLabel passwortText;
    private static final String CREDENTIALS_FILE = "credentials.json";
    private static final String SETTINGS_FILE = "settings.json";

    public LoginWindow(String title) {
        super(title);
        LoginWindow = new JPanel();
        darkmodeUtility = new Darkmode(this);

        this.setMinimumSize(new Dimension(330, 400));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(LoginWindow);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.pack();
        this.setLayout(new BoxLayout(LoginWindow, BoxLayout.Y_AXIS));

        addComponents();
        setupActionListeners();
        darkmodeUtility.activateDarkMode(darkmodeUtility.darkMode);
        setVisible(true);
    }

    private void setupActionListeners() {
        loginButton.addActionListener(_ -> performLogin());
        registerButton.addActionListener(_ -> performRegistration());

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
        Border textFeldBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
        Dimension textFeldGroesse = new Dimension(270, 20);

        //Buttons
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        loginButton.setBackground(Color.WHITE);
        registerButton.setBackground(Color.WHITE);

        List<JButton> buttonList = java.util.List.of(registerButton, loginButton);
        setButtonElementLocation(buttonList);

        //JLabels
        benutzerText = new JLabel("Geben Sie Ihren Benutzername ein");
        passwortText = new JLabel("Geben Sie Ihr Passwort ein");
        label_title = new JLabel("Passwort Manager");
        label_title.setFont(new Font("Arial", Font.BOLD, 24));
        label_title.setForeground(new Color(0, 102, 204));
        label_title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        List<JLabel> labelList = java.util.List.of(label_title, benutzerText, passwortText);
        setLabelElementLocation(labelList);

        //JTextField
        benutzerNameEingabe = new JTextField(20);
        benutzerNameEingabe.setMaximumSize(textFeldGroesse);
        benutzerNameEingabe.setBorder(textFeldBorder);
        passwortEingabe = new JPasswordField(20);
        passwortEingabe.setMaximumSize(textFeldGroesse);

        List<JTextField> textFieldList = java.util.List.of(benutzerNameEingabe, passwortEingabe);
        setTextFieldElementLocation(textFieldList);

        arrangeComponents();
    }

    //Diese Funktion erstellt einen Abstand mit der übergebenen Höhe. Wird in "arrangeComponents()" verwendet.
    private Component Abstand(int höhe) {
        return Box.createVerticalStrut(höhe);
    }

    //Diese Funktion ordnet die Komponenten richtig an.
    public void arrangeComponents() {
        List<Component> liste = List.of(label_title, Abstand(30), benutzerText, benutzerNameEingabe, Abstand(20), passwortText, passwortEingabe, Abstand(75), loginButton, Abstand(10), registerButton);
        for (Component component : liste) {
            LoginWindow.add(component);
        }
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
        darkmodeUtility.darkMode = !darkmodeUtility.darkMode;
        saveSettings(darkmodeUtility.darkMode);
        darkmodeUtility.activateDarkMode(darkmodeUtility.darkMode);
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

    private void setButtonElementLocation(List<JButton> buttonList) {
        for (JButton jButton : buttonList) {
            jButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
    }
    private void setTextFieldElementLocation(List<JTextField> textFieldList) {
        for (JTextField jTextField : textFieldList) {
            jTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
    }

    private void setLabelElementLocation(List<JLabel> labelList) {
        for (JLabel jLabel : labelList) {
            jLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
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

    public JSONObject loadSettings() throws Exception {
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