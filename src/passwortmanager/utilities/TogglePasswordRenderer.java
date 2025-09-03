package passwortmanager.utilities;

import lombok.Getter;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
@Getter
public class TogglePasswordRenderer extends JPanel implements TableCellRenderer {
    private Darkmode darkmodeUtility = new Darkmode(this);
    private JPanel renderPanel;
    private JButton editButton;
    private JButton toggleButton;
    private JButton copyButton;

    public TogglePasswordRenderer() {
        setOpaque(true);
        setLayout(new FlowLayout(FlowLayout.LEFT)); // Explicitly set layout

        renderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        editButton = new JButton("\uD83D\uDD8A\uFE0F");
        toggleButton = new JButton("\uD83D\uDC41");
        copyButton = new JButton("\uD83D\uDCC4");

        renderPanel.add(editButton);
        renderPanel.add(toggleButton);
        renderPanel.add(copyButton);

        editButton.setOpaque(true);
        toggleButton.setOpaque(true);
        copyButton.setOpaque(true);

        darkmodeUtility.activateDarkMode(darkmodeUtility.getDarkMode());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return renderPanel;
    }
}
