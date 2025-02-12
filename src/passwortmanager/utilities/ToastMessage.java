package passwortmanager.utilities;

import javax.swing.*;
import java.awt.*;

public class ToastMessage extends JWindow {
    public ToastMessage(String message, int duration) {
        setLayout(new BorderLayout());
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 170));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        add(label, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);

        new Thread(() -> {
            try {
                Thread.sleep(duration);
                dispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void showToast(Component parent, String message, int duration) {
        ToastMessage toast = new ToastMessage(message, duration);
        toast.setVisible(true);
    }
}