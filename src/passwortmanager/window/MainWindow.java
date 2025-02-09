package passwortmanager.window;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import passwortmanager.utilities.Storage;

import java.awt.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class MainWindow extends JFrame{

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
    private DefaultTableModel tableModel;

    // Local Variables
    private String m_masterpassword;
    private boolean isModified = false; // Speichert, ob Änderungen gemacht wurden

    public MainWindow(String title, String masterPassword) {
        super(title);
        setContentPane(MainPanel);
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
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            LoginWindow loginWindow = new LoginWindow("Login");
        });
    }

    private void addListeners() {
        button_logout.addActionListener(_ -> logout());
        button_GeneratePW.addActionListener(_ -> textfield_Password.setText(generatePassword()));
        button_ADD.addActionListener(_ -> addPasswordToTable());
        button_Save.addActionListener(_ -> save());

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
                        Storage.savePasswords(MainWindow.this, m_masterpassword);
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
                button_Save.setEnabled(true);
            }
        });
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
            Object[] rowData = {name, username, password, "DEL"}; // Statt JButton einfach "DEL" als Text speichern
            ((DefaultTableModel) passwordTable.getModel()).addRow(rowData);

            setModified(true);
            button_ADD.setEnabled(false);

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
        table.getColumnModel().getColumn(column).setCellEditor(new ButtonEditor(new JCheckBox(), table, this));
    }

    private void createTable() {
        // Spalten definieren
        passwordTable.setModel(new DefaultTableModel(null, new String[] {"Name", "Nutzername", "Passwort", "Aktionen"}));

        // Button-Renderer und Editor für die letzte Spalte (Aktionen) setzen
        setupButtonColumn(passwordTable, 3);
    }

    public DefaultTableModel getPasswordTableModel() {
        return (DefaultTableModel) passwordTable.getModel();
    }

    public void setPasswordTableModel(JSONArray passwordArray) {
        DefaultTableModel model = (DefaultTableModel) passwordTable.getModel();
        model.setRowCount(0); // Löscht alte Daten

        for (Object obj : passwordArray) {
            JSONObject entry = (JSONObject) obj;
            Object[] rowData = {entry.get("name"), entry.get("username"), entry.get("password"), "DEL"};
            model.addRow(rowData);
        }
    }

    public void setModified(boolean modified) {
        // sets if window is modified
        isModified = modified;
        button_Save.setEnabled(modified);
    }

    public void save() {
        Storage.savePasswords(MainWindow.this, m_masterpassword);
        setModified(false);
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
    private MainWindow mainWindow;

    public ButtonEditor(JCheckBox checkBox, JTable table, MainWindow mainWindow) {
        super(checkBox);
        this.table = table;
        this.mainWindow = mainWindow;
        button = new JButton("DEL");
        button.setOpaque(true);

        button.addActionListener(_ -> {
            fireEditingStopped(); // Beende den Bearbeitungsmodus zuerst!

            if (row >= 0 && row < table.getRowCount()) { // Sicherstellen, dass die Zeile existiert
                ((DefaultTableModel) table.getModel()).removeRow(row);
                mainWindow.setModified(true);
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
