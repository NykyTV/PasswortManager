package passwortmanager.utilities;

import javax.swing.*;
import java.awt.*;

public class Toast extends JWindow {
    private static final int FADE_IN_TIME = 200;
    private static final int DISPLAY_TIME = 2000;
    private static final int FADE_OUT_TIME = 200;

    public Toast(String message, int x, int y) {
        setLayout(new BorderLayout());
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(0, 0, 0, 170));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        add(label, BorderLayout.CENTER);
        pack();
        setLocation(x - getWidth() / 2, y - getHeight() / 2);

        new Thread(() -> {
            fadeIn();
            try { Thread.sleep(DISPLAY_TIME); } catch (InterruptedException ignored) {}
            fadeOut();
            dispose();
        }).start();
    }

    private void fadeIn() {
        for (float i = 0f; i <= 1f; i += 0.1f) {
            setOpacity(i);
            sleep(FADE_IN_TIME / 10);
        }
    }

    private void fadeOut() {
        for (float i = 1f; i >= 0f; i -= 0.1f) {
            setOpacity(i);
            sleep(FADE_OUT_TIME / 10);
        }
    }

    private void sleep(int duration) {
        try { Thread.sleep(duration); } catch (InterruptedException ignored) {}
    }

    public static void showToast(Component parent, String message) {
        Point location = parent.getLocationOnScreen();
        new Toast(message, location.x + parent.getWidth() / 2, location.y + parent.getHeight() - 50).setVisible(true);
    }
}
