import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JTextField benutzerNameEingabe;
    private JTextField passwortEingabe;
    private JLabel benutzerText;
    private JLabel passwortText;
    private JLabel label_title;

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

        setVisible(true);
    }

    private void setupActionListeners() {
        loginButton.addActionListener(e -> performLogin());
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
        loginButton = new JButton("Klick");
        label_title = new JLabel("Passwort Manager");
        registerButton = new JButton("Register");

        Dimension textFeldGroesse = new Dimension(270, 20);
        benutzerNameEingabe.setMaximumSize(textFeldGroesse);
        passwortEingabe.setMaximumSize(textFeldGroesse);

// Titel
        label_title.setFont(new Font("Arial", Font.BOLD, 24));
        label_title.setForeground(new Color(0, 102, 204));
        label_title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        label_title.setAlignmentX(Component.CENTER_ALIGNMENT);

        //loginButton.addActionListener();

        //addAbstand();
        //LoginWindow.add(Box.createVerticalStrut(20));
        benutzerText.setAlignmentX(Component.CENTER_ALIGNMENT);
        benutzerNameEingabe.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwortText.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwortEingabe.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);




        LoginWindow.add(label_title);
        addAbstand(60);
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
}