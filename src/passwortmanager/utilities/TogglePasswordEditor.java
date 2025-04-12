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

        deleteButton.addActionListener(_ -> {
            int option = JOptionPane.showConfirmDialog(
                    mainWindow,
                    "Möchten Sie das Passwort wirklich löschen?",
                    "Löschen bestätigen",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (option == JOptionPane.YES_OPTION) {
                fireEditingStopped();
                if (row >= 0 && row < table.getRowCount()) {
                    ((DefaultTableModel) table.getModel()).removeRow(row);
                    mainWindow.setModified(true);
                }
            }
        });


        toggleButton.addActionListener(_ -> {
            MainWindow.ignoreNextTableChange = true;
            togglePassword();
        });

        copyButton.addActionListener(_ -> {
            MainWindow.ignoreNextTableChange = true;
            copyPasswordToClipboard();
        });
    }

    private void togglePassword() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String name = (String) model.getValueAt(row, 0);
        String username = (String) model.getValueAt(row, 1);
        String currentValue = (String) model.getValueAt(row, 2);
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
