package passwortmanager.utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.security.SecureRandom;

public class GeneratePassword {

    private final SecureRandom random = new SecureRandom();

    public void showPopupWindow(JFrame parent, ActionListener onPasswordGenerated) {
        JDialog popup = new JDialog(parent, "Generator", true);
        popup.setSize(600, 250);
        popup.setLayout(new BorderLayout());

        // Oben: Titel
        JLabel titleLabel = new JLabel("Passwortgenerator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        popup.add(titleLabel, BorderLayout.NORTH);

        // Mitte: Hauptpanel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Passwortausgabe
        JTextField passwordField = new JTextField();
        passwordField.setEditable(false);
        passwordField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        centerPanel.add(passwordField);
        centerPanel.add(Box.createVerticalStrut(10));

        // Länge
        JPanel lengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lengthPanel.add(new JLabel("Länge:"));
        JTextField lengthField = new JTextField("20", 5);
        lengthPanel.add(lengthField);
        centerPanel.add(lengthPanel);

        // Checkboxen
        JCheckBox upperCase = new JCheckBox("A-Z", true);
        JCheckBox lowerCase = new JCheckBox("a-z", true);
        JCheckBox digits = new JCheckBox("0-9", true);
        JCheckBox specialChars = new JCheckBox("!@#$%^&*", true);

        JPanel checkboxPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        checkboxPanel.add(upperCase);
        checkboxPanel.add(lowerCase);
        checkboxPanel.add(digits);
        checkboxPanel.add(specialChars);
        centerPanel.add(checkboxPanel);

        popup.add(centerPanel, BorderLayout.CENTER);

        // Unten: Panel mit zwei Buttons (links + rechts)
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Links: Neu generieren
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton regenerateBtn = new JButton("Neu generieren");
        leftPanel.add(regenerateBtn);
        bottomPanel.add(leftPanel, BorderLayout.WEST);

        // Rechts: Dieses Passwort verwenden
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton usePasswordBtn = new JButton("Dieses Passwort verwenden");
        rightPanel.add(usePasswordBtn);
        bottomPanel.add(rightPanel, BorderLayout.EAST);

        popup.add(bottomPanel, BorderLayout.SOUTH);


        // Passwort-Generierung
        Runnable updatePassword = () -> {
            int length;
            try {
                length = Integer.parseInt(lengthField.getText());
                if (length < 5 || length > 128) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(popup, "Bitte gültige Länge zwischen 5 und 128 eingeben.", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String password = generatePassword(
                    length,
                    upperCase.isSelected(),
                    lowerCase.isSelected(),
                    digits.isSelected(),
                    specialChars.isSelected()
            );
            passwordField.setText(password);
        };

        // Button-Listener
        regenerateBtn.addActionListener(e -> updatePassword.run());

        usePasswordBtn.addActionListener(e -> {
            if (passwordField.getText().isEmpty()) updatePassword.run();
            e.setSource(passwordField.getText());
            onPasswordGenerated.actionPerformed(e);
            popup.dispose();
        });

        // Initiales Passwort anzeigen
        updatePassword.run();

        popup.setLocationRelativeTo(null);
        popup.setVisible(true);
    }

    private String generatePassword(int length, boolean useUpper, boolean useLower,
                                    boolean useDigits, boolean useSpecial) {

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*";

        StringBuilder charPool = new StringBuilder();
        if (useUpper) charPool.append(upper);
        if (useLower) charPool.append(lower);
        if (useDigits) charPool.append(digits);
        if (useSpecial) charPool.append(special);

        if (charPool.length() == 0) return "";

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(charPool.length());
            password.append(charPool.charAt(index));
        }

        return password.toString();
    }
}
