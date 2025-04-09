package passwortmanager.utilities;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TogglePasswordRenderer extends JPanel implements TableCellRenderer {
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
