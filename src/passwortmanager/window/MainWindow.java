package passwortmanager.window;

import com.sun.tools.javac.Main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainWindow extends JFrame{
    private LoginWindow loginUtility;

    private JPanel panel1;

    private JLabel label_pwInput;
    private JTextField textfield_pwInput;

    private JLabel label_nameInput;
    private JTextField textfield_nameInput;

    private JLabel label_usernameInput;
    private JTextField textfield_usernameInput;

    private JButton button_add_password;
    private JButton button_generate_password;
    private JLabel label_title;

    private JTable passwordTable;
    private DefaultTableModel tableModel;

    public MainWindow(String title) {
        super(title);

        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 800);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            LoginWindow loginWindow = new LoginWindow("Login");
        });
    }

    private void initComponents() {
        JPanel mainWindow = new JPanel();
        mainWindow.setLayout(new BoxLayout(mainWindow, BoxLayout.Y_AXIS));

        // Titel
        label_title = new JLabel("Passwort Manager");
        label_title.setFont(new Font("Arial", Font.BOLD, 24));
        label_title.setForeground(new Color(0, 102, 204));
        label_title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        label_title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Eintrag-Name
        label_nameInput = new JLabel("Name des Eintrags:");
        label_nameInput.setAlignmentX(Component.CENTER_ALIGNMENT);

        textfield_nameInput = new JTextField(20);
        textfield_nameInput = new JTextField(20);
        textfield_nameInput.setMaximumSize(new Dimension(300, 25));

        // Nutzername
        label_usernameInput = new JLabel("Nutzername:");
        label_usernameInput.setAlignmentX(Component.CENTER_ALIGNMENT);

        textfield_usernameInput = new JTextField(20);
        textfield_usernameInput = new JTextField(20);
        textfield_usernameInput.setMaximumSize(new Dimension(300, 25));

        // Passwort Eingabe
        label_pwInput = new JLabel("Passwort:");
        label_pwInput.setAlignmentX(Component.CENTER_ALIGNMENT);

        textfield_pwInput = new JTextField(20);
        textfield_pwInput = new JTextField(20);
        textfield_pwInput.setMaximumSize(new Dimension(300, 25));

        button_generate_password = new JButton("GEN");
        button_generate_password.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String generatedPassword = generatePassword();
                textfield_pwInput.setText(generatedPassword);
            }
        });
        button_add_password = new JButton("+");
        button_add_password.setAlignmentX(Component.CENTER_ALIGNMENT);
        button_add_password.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPasswordToTable();
            }
        });

        // Tabelle erstellen
        String[] columnNames = {"Name", "Nutzername", "Passwort", "Aktionen"};
        tableModel = new DefaultTableModel(columnNames, 0);
        passwordTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(passwordTable);
        scrollPane.setPreferredSize(new Dimension(550, 200));

        mainWindow.add(label_title);
        mainWindow.add(label_nameInput);
        mainWindow.add(textfield_nameInput);
        mainWindow.add(label_usernameInput);
        mainWindow.add(textfield_usernameInput);
        mainWindow.add(label_pwInput);
        mainWindow.add(button_generate_password);
        mainWindow.add(textfield_pwInput);
        mainWindow.add(button_add_password);
        mainWindow.add(Box.createRigidArea(new Dimension(0, 20))); // Abstand

        mainWindow.add(scrollPane);
        setContentPane(mainWindow);
    }

    private String generatePassword() {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*+-=<>?";
        String allChars = upperCase + lowerCase + numbers + specialChars;

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 12; i++) {  // Generiert ein 12-stelliges Passwort
            int index = random.nextInt(allChars.length());
            password.append(allChars.charAt(index));
        }

        return password.toString();
    }

    private void addPasswordToTable() {
        String name = textfield_nameInput.getText();
        String username = textfield_usernameInput.getText();
        String password = textfield_pwInput.getText();

        if (!name.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            Object[] rowData = {name, username, password, "Bearbeiten/Löschen"};
            tableModel.addRow(rowData);

            // Eingabefelder leeren
            textfield_nameInput.setText("");
            textfield_usernameInput.setText("");
            textfield_pwInput.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Bitte füllen Sie alle Felder aus.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public MainWindow(LoginWindow loginUtility) {
        this.loginUtility = loginUtility;
    }
}
