import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class MainWindow extends JFrame{
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

    public MainWindow(String title) {
        super(title);
        setContentPane(MainPanel);
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
        button_logout.addActionListener(e -> logout());
        button_GeneratePW.addActionListener(e -> textfield_Password.setText(generatePassword()));
        button_ADD.addActionListener(e -> addPasswordToTable());
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

        button.addActionListener(e -> {
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
