package passwortmanager.window;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import passwortmanager.utilities.Storage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import static passwortmanager.utilities.Storage.loadPasswordForEntry;

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
    protected String m_masterpassword;
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
    private String currentPassword; // Speichert das aktuelle Passwort temporär

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

        toggleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPassword();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                hidePassword();
            }
        });

        copyButton.addActionListener(e -> copyPasswordToClipboard());
    }

    private void showPassword() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String name = (String) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);
        try {
            currentPassword = loadPasswordForEntry(name, username, mainWindow.m_masterpassword);
            if (currentPassword != null) {
                model.setValueAt(currentPassword, row, 2);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(table, "Fehler beim Laden des Passworts",
                    "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hidePassword() {
        if (currentPassword != null) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setValueAt("*".repeat(currentPassword.length()), row, 2);
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