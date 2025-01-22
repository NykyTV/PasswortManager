import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MainWindow extends JFrame{
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
    private JButton logoutButton;

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
        setLayout(new BorderLayout());

        // North Panel (Title and Logout)
        JPanel northPanel = new JPanel(new BorderLayout());
        label_title = new JLabel("Passwort Manager", SwingConstants.CENTER);
        label_title.setFont(new Font("Arial", Font.BOLD, 24));
        label_title.setForeground(new Color(0, 102, 204));
        label_title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        northPanel.add(label_title, BorderLayout.CENTER);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(logoutButton);
        northPanel.add(logoutPanel, BorderLayout.EAST);

        add(northPanel, BorderLayout.NORTH);

        // Center Panel (Input Fields and Add Button)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        label_nameInput = new JLabel("Name des Eintrags:");
        textfield_nameInput = new JTextField(20);
        textfield_nameInput.setMaximumSize(new Dimension(300, 25));

        label_usernameInput = new JLabel("Nutzername:");
        textfield_usernameInput = new JTextField(20);
        textfield_usernameInput.setMaximumSize(new Dimension(300, 25));

        label_pwInput = new JLabel("Passwort:");
        textfield_pwInput = new JTextField(20);
        textfield_pwInput.setMaximumSize(new Dimension(300, 25));

        button_generate_password = new JButton("GEN");
        button_generate_password.addActionListener(e -> textfield_pwInput.setText(generatePassword()));

        button_add_password = new JButton("+");
        button_add_password.addActionListener(e -> addPasswordToTable());

        // Zentrieren der Komponenten
        label_nameInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        textfield_nameInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        label_usernameInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        textfield_usernameInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        label_pwInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        textfield_pwInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        button_generate_password.setAlignmentX(Component.CENTER_ALIGNMENT);
        button_add_password.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(label_nameInput);
        centerPanel.add(textfield_nameInput);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(label_usernameInput);
        centerPanel.add(textfield_usernameInput);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(label_pwInput);
        centerPanel.add(textfield_pwInput);
        centerPanel.add(button_generate_password);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(button_add_password);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);

        // South Panel (Password Table)
        String[] columnNames = {"Name", "Nutzername", "Passwort", "Aktionen"};
        tableModel = new DefaultTableModel(columnNames, 0);
        passwordTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(passwordTable);
        add(scrollPane, BorderLayout.SOUTH);
    }


    private void logout() {
        int option = JOptionPane.showConfirmDialog(this,
                "Möchten Sie sich wirklich abmelden?",
                "Logout bestätigen",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            this.dispose();

            SwingUtilities.invokeLater(() -> {
                LoginWindow loginWindow = new LoginWindow("Login");
                loginWindow.setVisible(true);
            });
        }
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
}
