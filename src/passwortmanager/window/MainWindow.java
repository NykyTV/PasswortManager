package passwortmanager.window;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import passwortmanager.utilities.Darkmode;
import passwortmanager.window.LoginWindow;

import java.awt.*;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class MainWindow extends JFrame{

    private LoginWindow loginUtility;
    private Darkmode darkmodeUtility;
    private JPanel MainPanel;
    private JLabel label_AppName;
    private JButton button_logout;
    private JLabel label_EntryName;
    private JTextField textfield_EntryName;
    private JLabel label_Username;
    private JTextField textfield_Username;
    private JLabel label_Password;
    private JTextField textfield_Password;
    private JButton button_GeneratePW;
    private JButton button_ADD;
    private JTable passwordTable;
    public JButton darkModeButton;
    private DefaultTableModel tableModel;
    private static final String SETTINGS_FILE = "settings.json";

    public MainWindow(String title) {
        super(title);
        setContentPane(MainPanel);
        darkmodeUtility = new Darkmode(this);
        createTable();
        addListeners();
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

    private void addListeners() {
        darkModeButton.addActionListener(e -> performDarkmode());
        button_logout.addActionListener(_ -> logout());
        button_GeneratePW.addActionListener(_ -> textfield_Password.setText(generatePassword()));
        button_ADD.addActionListener(_ -> addPasswordToTable());
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
        String name = textfield_EntryName.getText();
        String username = textfield_Username.getText();
        String password = textfield_Password.getText();

        if (!name.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            Object[] rowData = {name, username, password, "DEL"}; // Statt JButton einfach "DEL" als Text speichern
            ((DefaultTableModel) passwordTable.getModel()).addRow(rowData);

            // Eingabefelder leeren
            textfield_EntryName.setText("");
            textfield_Username.setText("");
            textfield_Password.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Bitte füllen Sie alle Felder aus.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void setupButtonColumn(JTable table, int column) {
        table.getColumnModel().getColumn(column).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(column).setCellEditor(new ButtonEditor(new JCheckBox(), table));
    }

    private void createTable() {
        // Spalten definieren
        passwordTable.setModel(new DefaultTableModel(null, new String[] {"Name", "Nutzername", "Passwort", "Aktionen"}));

        // Button-Renderer und Editor für die letzte Spalte (Aktionen) setzen
        setupButtonColumn(passwordTable, 3);
    }
    private void performDarkmode() {
        loginUtility.darkMode = !loginUtility.darkMode;
        saveSettings(loginUtility.darkMode);
        darkmodeUtility.activateDarkMode(loginUtility.darkMode);
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


}


// -------- CUSTOM CLASS to render Button in JTable ---------

class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private JTable table;
    private int row;

    public ButtonEditor(JCheckBox checkBox, JTable table) {
        super(checkBox);
        this.table = table;
        button = new JButton("DEL");
        button.setOpaque(true);

        button.addActionListener(_ -> {
            fireEditingStopped(); // Beende den Bearbeitungsmodus zuerst!

            if (row >= 0 && row < table.getRowCount()) { // Sicherstellen, dass die Zeile existiert
                ((DefaultTableModel) table.getModel()).removeRow(row);
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        button.setText((value == null) ? "" : value.toString());
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return button.getText();
    }

}
