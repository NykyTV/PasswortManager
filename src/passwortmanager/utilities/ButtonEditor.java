package passwortmanager.utilities;

import passwortmanager.window.MainWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private JTable table;
    private MainWindow mainWindow;
    private int row;

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