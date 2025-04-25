package passwortmanager.window;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import passwortmanager.utilities.*;
import passwortmanager.utilities.FileReader;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.List;

import static passwortmanager.utilities.Storage.extractHostFromUrl;

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

    public LoginWindow(String title) {
        super(title);
        LoginWindow = new JPanel();
        darkmodeUtility = new Darkmode(this);

        this.setMinimumSize(new Dimension(330, 400));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
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

        List<JButton> buttonList = List.of(registerButton, loginButton);
        setButtonElementLocation(buttonList);

        //JLabels
        benutzerText = new JLabel("Geben Sie Ihren Benutzername ein");
        passwortText = new JLabel("Geben Sie Ihr Passwort ein");
        label_title = new JLabel("Passwort Manager");
        label_title.setFont(new Font("Arial", Font.BOLD, 24));
        label_title.setForeground(new Color(0, 102, 204));
        label_title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        List<JLabel> labelList = List.of(label_title, benutzerText, passwortText);
        setLabelElementLocation(labelList);

        //JTextField
        benutzerNameEingabe = new JTextField(20);
        benutzerNameEingabe.setMaximumSize(textFeldGroesse);
        benutzerNameEingabe.setBorder(textFeldBorder);
        passwortEingabe = new JPasswordField(20);
        passwortEingabe.setMaximumSize(textFeldGroesse);

        List<JTextField> textFieldList = List.of(benutzerNameEingabe, passwortEingabe);
        setTextFieldElementLocation(textFieldList);

        arrangeComponents();
    }

    //Diese Funktion erstellt einen Abstand mit der übergebenen Höhe. Wird in "arrangeComponents()" verwendet.
    private Component Abstand(int hoehe) {
        return Box.createVerticalStrut(hoehe);
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
                MainWindow mainWindow = new MainWindow("Passwort Manager", password, benutzerNameEingabe.getText());
                mainWindow.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Ungültige Anmeldedaten", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performRegistration() {
        String username = benutzerNameEingabe.getText();
        String password = passwortEingabe.getText();

        String filename = Common.getPasswordFilename(username);

        File file = new File(filename);

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Benutzername und Passwort dürfen nicht leer sein", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (file.exists()) {
            JOptionPane.showMessageDialog(this, "Benutzername bereits vergeben", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // lege leere Passwortliste an
            JSONArray leereListe = new JSONArray();

            FileReader.saveFile(leereListe, filename, password);

            JOptionPane.showMessageDialog(this, "Registrierung erfolgreich", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Fehler: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Registrierung fehlgeschlagen", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean checkLogin(String username, String password) {
        String filename = Common.getPasswordFilename(username);
        File file = new File(filename);

        // Server-Modus prüfen
        if (SettingsLoader.isServerMode()) {
            JSONObject settings = SettingsLoader.loadSettings();
            String serverUrl = extractHostFromUrl((String) settings.get("url"));

            if (Database.isServerAvailable(serverUrl, null)) {
                // Versuche Datei vom Server zu holen
                Database.loadUserFileFromDatabase(username, file, null);
            } else {
                JOptionPane.showMessageDialog(this, "Server nicht erreichbar. Anmeldung im Offline-Modus.", "Offline-Modus", JOptionPane.WARNING_MESSAGE);
            }
        }

        if (!file.exists()) return false;

        try {
            // versuche die Entschlüsselung – wenn es fehlschlägt → falsches Passwort
            FileReader.loadFile(filename, password);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void setButtonElementLocation(List<JButton> buttonList) {
        for (JButton jButton : buttonList) {
            jButton.setAlignmentX(CENTER_ALIGNMENT);
        }
    }
    private void setTextFieldElementLocation(List<JTextField> textFieldList) {
        for (JTextField jTextField : textFieldList) {
            jTextField.setAlignmentX(CENTER_ALIGNMENT);
        }
    }

    private void setLabelElementLocation(List<JLabel> labelList) {
        for (JLabel jLabel : labelList) {
            jLabel.setAlignmentX(CENTER_ALIGNMENT);
        }
    }
}