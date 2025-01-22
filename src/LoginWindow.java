import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.*;
import java.security.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.StringReader;

public class LoginWindow extends JFrame {
    private JPanel LoginWindow;
    private JButton loginButton;
    private JButton registerButton;
    private JButton darkModeButton;
    private JTextField benutzerNameEingabe;
    private JTextField passwortEingabe;
    private JLabel benutzerText;
    private JLabel passwortText;
    private JLabel label_title;
    private Boolean darkMode = false;
    private static final String CREDENTIALS_FILE = "credentials.json";

    public LoginWindow(String title) {
        super(title);
        LoginWindow = new JPanel();

        this.setMinimumSize(new Dimension(330, 400));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(LoginWindow);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.pack();
        this.setLayout(new BoxLayout(LoginWindow, BoxLayout.Y_AXIS));


        addComponents();
        setupActionListeners();
        activateDarkMode(darkMode);
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
        passwortEingabe = new JTextField(20);
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
                MainWindow mainWindow = new MainWindow("Passwort Manager");
                mainWindow.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Ungültige Anmeldedaten", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performDarkmode() {
        darkMode = !darkMode;
        activateDarkMode(darkMode);
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

    private void setFarbeTextField(JTextField textFeld, boolean darkMode) {
        if (darkMode) {
            Border textFeldBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
            textFeld.setForeground(Color.DARK_GRAY);
            textFeld.setBackground(Color.LIGHT_GRAY);
            textFeld.setBorder(textFeldBorder);
        }else {
            Border textFeldBorder = BorderFactory.createLineBorder(Color.GRAY);
            textFeld.setForeground(null);
            textFeld.setBackground(null);
            textFeld.setBorder(textFeldBorder);
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

    private void activateDarkMode(boolean active) {
        Color buttonFarbe;
        Color textFarbe;
        Color titlefarbe;
        boolean setFarbeTextField;
        if (active) {
            buttonFarbe = Color.LIGHT_GRAY;
            textFarbe = Color.LIGHT_GRAY;
            titlefarbe = new Color(173, 216, 230);
            setFarbeTextField = true;
            LoginWindow.setBackground(Color.DARK_GRAY);
        }else {
            buttonFarbe = Color.WHITE;
            textFarbe = Color.BLACK;
            titlefarbe = new Color(0, 102, 204);
            setFarbeTextField = false;
            LoginWindow.setBackground(null);
        }
        loginButton.setBackground(buttonFarbe);
        darkModeButton.setBackground(buttonFarbe);
        registerButton.setBackground(buttonFarbe);
        benutzerText.setForeground(textFarbe);
        passwortText.setForeground(textFarbe);
        label_title.setForeground(titlefarbe);
        setFarbeTextField(benutzerNameEingabe, setFarbeTextField);
        setFarbeTextField(passwortEingabe, setFarbeTextField);
    }
}