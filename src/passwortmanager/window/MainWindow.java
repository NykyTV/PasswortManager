package passwortmanager.window;

import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import passwortmanager.utilities.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

@Getter
public class MainWindow extends JFrame{

    private Darkmode darkmodeUtility;

    // MainWindow.form Variables
    private JPanel MainPanel;
    private JLabel label_AppName;
    private JButton button_logout;
    private JLabel label_EntryName;
    private JTextField textfield_EntryName;
    private JLabel label_Username;
    private JTextField textfield_Username;
    private JLabel label_Password;
    private JPasswordField textfield_Password;
    private JButton button_GeneratePW;
    private JButton button_ADD;
    private JTable passwordTable;
    private JButton button_Save;
    private JPanel TopPanel;
    private JPanel MidPanel;
    private JScrollPane scrollBarPane;
    private JButton button_Show;
    private JButton settingsButton;
    private JLabel statusLabel;
    private static final String SETTINGS_FILE = "settings.json";

    // Local Variables
    private String m_masterpassword;
    private String m_accountName;
    private boolean isModified = false; // Speichert, ob Änderungen gemacht wurden
    public static boolean ignoreNextTableChange = false;

    public MainWindow(String title, String masterPassword, String accountName) {
        super(title);
        setContentPane(MainPanel);
        MainPanel.setOpaque(true);
        darkmodeUtility = new Darkmode(this);
        createTable();
        addListeners();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(600, 800);
        setLocationRelativeTo(null);
        m_masterpassword = masterPassword;
        m_accountName = accountName;

        Storage.loadPasswords(MainWindow.this, masterPassword, accountName);
        setVisible(true);

        button_ADD.setEnabled(false);
        button_Save.setEnabled(false);

        darkmodeUtility.activateDarkMode(darkmodeUtility.getDarkMode());
        button_Show.setText("\uD83D\uDC41"); // "auge icon" setzen da in from Editor nicht möglich
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            LoginWindow loginWindow = new LoginWindow("Login");
        });
    }

    private void addListeners() {
        button_logout.addActionListener(_ -> logout());
        button_GeneratePW.addActionListener(_ -> {
            new GeneratePassword().showPopupWindow(e -> {
                String pw = e.getSource().toString();
                textfield_Password.setText(pw);
            });
        });
        button_ADD.addActionListener(_ -> addPasswordToTable());
        button_Save.addActionListener(_ -> save());
        button_Show.addActionListener(_ -> togglePasswordVisibility());
        settingsButton.addActionListener(_ -> {
            try {
                String settings = getSettings(m_accountName);
                SettingsLoader.showSettingsDialog(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

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
                        Storage.savePasswords(MainWindow.this, m_masterpassword, m_accountName, null, null);
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
                if (MainWindow.ignoreNextTableChange) {
                    MainWindow.ignoreNextTableChange = false; // Zurücksetzen
                    return;
                }

                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 0) { // Name column
                    String newName = (String) passwordTable.getValueAt(row, column);
                    String uniqueName = getUniqueName(newName);
                    if (!newName.equals(uniqueName)) {
                        TableModelListener listener = this;
                        passwordTable.getModel().removeTableModelListener(listener);
                        JOptionPane.showMessageDialog(MainWindow.this, "Ein Eintrag mit diesem Namen existiert bereits. Der Name wurde geändert zu: " + uniqueName, "Fehler", JOptionPane.ERROR_MESSAGE);
                        passwordTable.setValueAt(uniqueName, row, column);
                        passwordTable.getModel().addTableModelListener(listener);
                    }
                }
                if (column >= 0 && column <= 2) {
                    isModified = true;
                    button_Save.setEnabled(true);
                }
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
            if (isModified) {
                int changes = JOptionPane.showConfirmDialog(
                        MainWindow.this,
                        "Es gibt ungespeicherte Änderungen. Möchten Sie diese speichern?",
                        "Änderungen speichern?",
                        JOptionPane.YES_NO_OPTION
                );

                if (changes == JOptionPane.YES_OPTION) {
                    Storage.savePasswords(MainWindow.this, m_masterpassword, m_accountName, null, null);
                }
            }

            this.dispose();

            SwingUtilities.invokeLater(() -> {
                LoginWindow loginWindow = new LoginWindow("Login");
                loginWindow.setVisible(true);
            });
        }
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
            Storage.savePasswords(this, m_masterpassword, m_accountName, name, password);

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

    public void performDarkmode() {
        darkmodeUtility.setDarkMode(!darkmodeUtility.getDarkMode());
        darkmodeUtility.activateDarkMode(darkmodeUtility.getDarkMode());
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
        Storage.savePasswords(MainWindow.this, m_masterpassword, m_accountName, null, null);
        setModified(false);
    }

    public void setStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }
}