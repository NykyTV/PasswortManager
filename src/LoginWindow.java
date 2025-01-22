import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        });

        setVisible(true);
    }

    public void addComponents() {
        benutzerText = new JLabel("Geben Sie Ihren Benutzername ein");
        passwortText = new JLabel("Geben Sie Ihr Passwort ein");
        benutzerNameEingabe = new JTextField(20);
        passwortEingabe = new JTextField(20);
        loginButton = new JButton("Klick");
        label_title = new JLabel("Passwort Manager");

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

    private boolean checkLogin(String username, String password) {
        return username.equals("admin") && password.equals("admin");
    }
}