package passwortmanager.utilities;

import passwortmanager.window.MainWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import static passwortmanager.utilities.Storage.loadPasswordForEntry;

public class TogglePasswordEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel editorPanel;
    private JButton editButton;
    private JButton toggleButton;
    private JButton copyButton;
    private JTable table;
    private int row;
    private MainWindow mainWindow;
    private boolean isPasswordVisible = false;

    public TogglePasswordEditor(JTable table, MainWindow mainWindow) {
        this.table = table;
        this.mainWindow = mainWindow;
        table.setDefaultEditor(Object.class, null);

        editorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editButton = new JButton("\uD83D\uDD8A\uFE0F");
        toggleButton = new JButton("\uD83D\uDC41");
        copyButton = new JButton("\uD83D\uDCC4");

        editorPanel.add(editButton);
        editorPanel.add(toggleButton);
        editorPanel.add(copyButton);

        editButton.addActionListener(_ -> openEditDialog());

        toggleButton.addActionListener(_ -> {
            MainWindow.ignoreNextTableChange = true;
            togglePassword();
        });

        copyButton.addActionListener(_ -> {
            MainWindow.ignoreNextTableChange = true;
            copyPasswordToClipboard();
        });
    }

    private void openEditDialog() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String name = (String) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);
        String password = loadPasswordForEntry(name, username, mainWindow.m_masterpassword, mainWindow.m_accountName);

        JTextField nameField = new JTextField(name);
        JTextField usernameField = new JTextField(username);
        JPasswordField passwordField = new JPasswordField(password);
        JCheckBox showPassword = new JCheckBox("Passwort anzeigen");
        showPassword.addActionListener(e -> {
            passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : '•');
        });

        Object[] inputs = {
                "Name:", nameField,
                "Benutzername:", usernameField,
                "Passwort:", passwordField,
                showPassword
        };

        Object[] options = {"Speichern", "Abbrechen", "Löschen"};
        int result = JOptionPane.showOptionDialog(
                mainWindow,
                inputs,
                "Eintrag bearbeiten",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (result == JOptionPane.YES_OPTION) {
            String newPassword = passwordField.getText();
            model.setValueAt(nameField.getText(), row, 0);
            model.setValueAt(usernameField.getText(), row, 1);
            model.setValueAt("*".repeat(newPassword.length()), row, 2);

            // Sofortige Speicherung des Passworts im Storage
            Storage.savePasswords(mainWindow, mainWindow.m_masterpassword, mainWindow.m_accountName, nameField.getText(), newPassword);
            //mainWindow.setModified(true);
        } else if (result == JOptionPane.CANCEL_OPTION) {
            fireEditingStopped();
            int confirm = JOptionPane.showConfirmDialog(
                    mainWindow,
                    "Möchtest du diesen Eintrag wirklich löschen?",
                    "Löschen bestätigen",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                model.removeRow(row);
                mainWindow.setModified(true);
            }
        }

        fireEditingStopped();
    }

    private void togglePassword() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String name = (String) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);
        String realPassword = loadPasswordForEntry(name, username, mainWindow.m_masterpassword, mainWindow.m_accountName);

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
            String password = loadPasswordForEntry(name, username, mainWindow.m_masterpassword, mainWindow.m_accountName);
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
