package passwortmanager.window;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import passwortmanager.utilities.ButtonEditor;
import passwortmanager.utilities.ButtonRenderer;
import passwortmanager.utilities.Darkmode;
import passwortmanager.utilities.Storage;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import static passwortmanager.utilities.Storage.loadPasswordForEntry;

public class MainWindow extends JFrame{

    private Darkmode darkmodeUtility;

    // MainWindow.form Variables
    public JPanel MainPanel;
    public JLabel label_AppName;
    public JButton button_logout;
    public JLabel label_EntryName;
    public JTextField textfield_EntryName;
    public JLabel label_Username;
    public JTextField textfield_Username;
    public JLabel label_Password;
    public JPasswordField textfield_Password;
    public JButton button_GeneratePW;
    public JButton button_ADD;
    public JTable passwordTable;
    public JButton darkModeButton;
    private static final String SETTINGS_FILE = "settings.json";
    public JButton button_Save;
    public JPanel TopPanel;
    public JPanel MidPanel;
    public JScrollPane scrollBarPane;
    public JButton button_Show;
    private DefaultTableModel tableModel;

    // Local Variables
    protected String m_masterpassword;
    private boolean isModified = false; // Speichert, ob Änderungen gemacht wurden

    public MainWindow(String title, String masterPassword) {
        super(title);
        setContentPane(MainPanel);
        MainPanel.setOpaque(true);
        darkmodeUtility = new Darkmode(this);
        createTable();
        addListeners();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 800);
        setLocationRelativeTo(null);
        m_masterpassword = masterPassword;

        Storage.loadPasswords(MainWindow.this, masterPassword);
        setVisible(true);

        button_ADD.setEnabled(false);
        button_Save.setEnabled(false);

        darkmodeUtility.activateDarkMode(darkmodeUtility.darkMode);
        button_Show.setText("\uD83D\uDC41"); // "auge icon" setzen da in from Editor nicht möglich
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
        button_Save.addActionListener(_ -> save());
        button_Show.addActionListener(_ -> togglePasswordVisibility());

        // Beim Schließen speichern
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (isModified) {
                    int option = JOptionPane.showConfirmDialog(
                            MainWindow.this,
                            "Es gibt ungespeicherte Änderungen. Möchten Sie diese speichern?",
                            "Änderungen speichern?",
                            JOptionPane.YES_NO_CANCEL_OPTION
                    );

                    if (option == JOptionPane.YES_OPTION) {
                        Storage.savePasswords(MainWindow.this, m_masterpassword, null, null);
                        System.exit(0);
                    } else if (option == JOptionPane.NO_OPTION) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });

        addTextFieldListeners(textfield_EntryName);
        addTextFieldListeners(textfield_Username);
        addTextFieldListeners(textfield_Password);

        passwordTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 0) { // Name column
                    String newName = (String) passwordTable.getValueAt(row, column);
                    String uniqueName = getUniqueName(newName);
                    if (!newName.equals(uniqueName)) {
                        // Temporarily remove the listener to avoid infinite loop
                        TableModelListener listener = this;
                        passwordTable.getModel().removeTableModelListener(listener);
                        JOptionPane.showMessageDialog(MainWindow.this, "Ein Eintrag mit diesem Namen existiert bereits. Der Name wurde geändert zu: " + uniqueName, "Fehler", JOptionPane.ERROR_MESSAGE);
                        passwordTable.setValueAt(uniqueName, row, column);
                        // Re-add the listener
                        passwordTable.getModel().addTableModelListener(listener);
                    }
                }
                button_Save.setEnabled(true);
            }
        });
    }

    private void togglePasswordVisibility() {
        if (textfield_Password.getEchoChar() == '\u2022') {
            textfield_Password.setEchoChar((char) 0); // Show password
        } else {
            textfield_Password.setEchoChar('\u2022'); // Hide password
        }
    }

    private void addTextFieldListeners(JTextField textField)
    {
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                if (!textfield_EntryName.getText().isEmpty() && !textfield_Username.getText().isEmpty() && !textfield_Password.getText().isEmpty())
                    button_ADD.setEnabled(true);
            }
        };

        textField.getDocument().addDocumentListener(documentListener);
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
            name = getUniqueName(name);

            String maskedPassword = "*".repeat(password.length());
            Object[] rowData = {name, username, maskedPassword, "DEL"};
            ((DefaultTableModel) passwordTable.getModel()).addRow(rowData);

            // Save immediately with the actual password
            Storage.savePasswords(this, m_masterpassword, name, password);

            setModified(true);
            button_ADD.setEnabled(false);

            textfield_EntryName.setText("");
            textfield_Username.setText("");
            textfield_Password.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Bitte füllen Sie alle Felder aus.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getUniqueName(String name) {
        DefaultTableModel model = (DefaultTableModel) passwordTable.getModel();
        String uniqueName = name;
        int counter = 1;

        while (isNameDuplicate(uniqueName)) {
            uniqueName = name + " (" + counter + ")";
            counter++;
        }

        return uniqueName;
    }

    private boolean isNameDuplicate(String name) {
        DefaultTableModel model = (DefaultTableModel) passwordTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (name.equals(model.getValueAt(i, 0))) {
                return true;
            }
        }
        return false;
    }


    private void setupButtonColumn(JTable table) {
        table.getColumnModel().getColumn(3).setCellRenderer(new TogglePasswordRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new TogglePasswordEditor(table, this));
    }

    private void createTable() {
        passwordTable.setModel(new DefaultTableModel(null, new String[] {"Name", "Nutzername", "Passwort", "Aktionen"}));
        setupButtonColumn(passwordTable);
        passwordTable.setRowHeight(35);
        passwordTable.getColumnModel().getColumn(3).setPreferredWidth(105);
    }

    private void performDarkmode() {
        darkmodeUtility.darkMode = !darkmodeUtility.darkMode;
        saveSettings(darkmodeUtility.darkMode);
        darkmodeUtility.activateDarkMode(darkmodeUtility.darkMode);
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


    public DefaultTableModel getPasswordTableModel() {
        return (DefaultTableModel) passwordTable.getModel();
    }

    public void setPasswordTableModel(JSONArray passwordArray) {
        DefaultTableModel model = (DefaultTableModel) passwordTable.getModel();
        model.setRowCount(0);
        for (Object obj : passwordArray) {
            JSONObject entry = (JSONObject) obj;
            String name = (String) entry.get("name");
            String username = (String) entry.get("username");
            String password = (String) entry.get("password");
            // Passwort ausblenden
            String maskedPassword = "*".repeat(password.length());
            Object[] rowData = {name, username, maskedPassword, "DEL"};
            model.addRow(rowData);
        }
    }

    public void setModified(boolean modified) {
        // sets if window is modified
        isModified = modified;
        button_Save.setEnabled(modified);
    }

    public void save() {
        Storage.savePasswords(MainWindow.this, m_masterpassword, null, null);
        setModified(false);
    }
}


// -------- CUSTOM CLASS to render Button in JTable ---------

class TogglePasswordRenderer extends JPanel implements TableCellRenderer {
    private JPanel renderPanel;
    private JButton deleteButton;
    private JButton toggleButton;
    private JButton copyButton;

    public TogglePasswordRenderer() {
        setOpaque(true);
        setLayout(new FlowLayout(FlowLayout.LEFT)); // Explicitly set layout

        renderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        deleteButton = new JButton("DEL");
        toggleButton = new JButton("\uD83D\uDC41");
        copyButton = new JButton("\uD83D\uDCC4");

        renderPanel.add(deleteButton);
        renderPanel.add(toggleButton);
        renderPanel.add(copyButton);

        deleteButton.setOpaque(true);
        toggleButton.setOpaque(true);
        copyButton.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return renderPanel;
    }
}

class TogglePasswordEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel editorPanel;
    private JButton deleteButton;
    private JButton toggleButton;
    private JButton copyButton;
    private JTable table;
    private int row;
    private MainWindow mainWindow;
    private boolean isPasswordVisible = false;

    public TogglePasswordEditor(JTable table, MainWindow mainWindow) {
        this.table = table;
        this.mainWindow = mainWindow;
        editorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deleteButton = new JButton("DEL");
        toggleButton = new JButton("\uD83D\uDC41");
        copyButton = new JButton("\uD83D\uDCC4");
        editorPanel.add(deleteButton);
        editorPanel.add(toggleButton);
        editorPanel.add(copyButton);
        deleteButton.setOpaque(true);
        toggleButton.setOpaque(true);
        copyButton.setOpaque(true);

        deleteButton.addActionListener(e -> {
            fireEditingStopped();
            if (row >= 0 && row < table.getRowCount()) {
                ((DefaultTableModel) table.getModel()).removeRow(row);
                mainWindow.setModified(true);
            }
        });

        toggleButton.addActionListener(e -> togglePassword());

        copyButton.addActionListener(e -> copyPasswordToClipboard());
    }

    private void togglePassword() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String name = (String) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);
        String currentValue = (String) model.getValueAt(row, 2);
        String realPassword = loadPasswordForEntry(name, username, mainWindow.m_masterpassword);

        if (realPassword != null) {
            isPasswordVisible = !isPasswordVisible; // Toggle the state
            if (isPasswordVisible) {
                // Zeige das echte Passwort
                model.setValueAt(realPassword, row, 2);
                System.out.println("Entschlüsselt: " + realPassword);
            } else {
                // Zeige die Maskierung
                model.setValueAt("*".repeat(realPassword.length()), row, 2);
                System.out.println("Verschlüsselt: " + "*".repeat(realPassword.length()));
            }
        }
    }

    private void copyPasswordToClipboard() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String name = (String) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);
        try {
            String password = loadPasswordForEntry(name, username, mainWindow.m_masterpassword);
            if (password != null) {
                StringSelection stringSelection = new StringSelection(password);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(table, "Fehler beim Laden des Passworts",
                    "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        return editorPanel;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
}