import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class LoginWindow extends JFrame {
    private JPanel LoginWindow;
    private JButton loginButton;
    private JTextField benutzerNameEingabe;
    private JTextField passwortEingabe;
    private JLabel benutzerText;
    private JLabel passwortText;
    private JLabel label_title;

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

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Key Listener für ENTER Taste
        passwortEingabe.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        activateDarkMode(false);
        setVisible(true);
    }

    public void addComponents() {
        benutzerText = new JLabel("Geben Sie Ihren Benutzername ein");
        passwortText = new JLabel("Geben Sie Ihr Passwort ein");
        benutzerNameEingabe = new JTextField(20);
        passwortEingabe = new JTextField(20);
        loginButton = new JButton("Login");
        label_title = new JLabel("Passwort Manager");


        Dimension textFeldGroesse = new Dimension(270, 20);
        benutzerNameEingabe.setMaximumSize(textFeldGroesse);
        passwortEingabe.setMaximumSize(textFeldGroesse);

        label_title.setFont(new Font("Arial", Font.BOLD, 24));
        label_title.setForeground(new Color(0, 102, 204));
        label_title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        setElementLocation(label_title);

        loginButton.setBackground(Color.WHITE);

        setElementLocation(benutzerText);
        setElementLocation(benutzerNameEingabe);
        setElementLocation(passwortText);
        setElementLocation(passwortEingabe);
        setElementLocation(loginButton);

        LoginWindow.add(label_title);
        addAbstand(60);
        LoginWindow.add(benutzerText);
        LoginWindow.add(benutzerNameEingabe);
        addAbstand(20);
        LoginWindow.add(passwortText);
        LoginWindow.add(passwortEingabe);
        addAbstand(105);
        LoginWindow.add(loginButton);
    }

    public void addAbstand(int Abstand) {
        LoginWindow.add(Box.createVerticalStrut(Abstand));
    }

    private void performLogin() {
        if (checkLogin(benutzerNameEingabe.getText(), passwortEingabe.getText())) {
            dispose(); // Schließt das Login-Fenster
            SwingUtilities.invokeLater(() -> {
                MainWindow mainWindow = new MainWindow("Passwort Manager");
                mainWindow.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(LoginWindow.this, "Ungültige Anmeldedaten", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean checkLogin(String username, String password) {
        return username.equals("admin") && password.equals("admin");
    }

    private void setFarbeTextField(JTextField textFeld) {
        Border textFeldBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
        textFeld.setForeground(Color.DARK_GRAY);
        textFeld.setBackground(Color.LIGHT_GRAY);
        textFeld.setBorder(textFeldBorder);
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
        if (active) {


            label_title.setForeground(new Color(173, 216, 230));
            loginButton.setBackground(Color.LIGHT_GRAY);
            benutzerText.setForeground(Color.LIGHT_GRAY);
            passwortText.setForeground(Color.LIGHT_GRAY);
            setFarbeTextField(benutzerNameEingabe);
            setFarbeTextField(passwortEingabe);

            LoginWindow.setBackground(Color.DARK_GRAY);
        }
    }
}