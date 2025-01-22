import java.awt.*;
import javax.swing.*;

public class MainWindow extends JFrame{
    private JPanel panel1;

    private JLabel label_pwInput;
    private JTextField textfield_pwInput;

    private JLabel label_nameInput;
    private JTextField textField_nameInput;

    private JLabel label_usernameInput;
    private JTextField textField_usernameInput;

    private JButton button_add_password;
    private JLabel label_title;

    public MainWindow(String title)
    {
        super(title);

        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 800);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            MainWindow frame = new MainWindow("Passwort Manager");
        });
    }

    private void initComponents() {
        JPanel mainWindow = new JPanel();
        mainWindow.setLayout(new BoxLayout(mainWindow, BoxLayout.Y_AXIS));

        // Titel
        label_title = new JLabel("Passwort Manager");
        label_title.setFont(new Font("Arial", Font.BOLD, 24));
        label_title.setForeground(new Color(0, 102, 204));
        label_title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        label_title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Eintrag-Name
        label_nameInput = new JLabel("Name des Eintrags:");
        label_nameInput.setAlignmentX(Component.CENTER_ALIGNMENT);

        textField_nameInput = new JTextField(20);
        textField_nameInput = new JTextField(20);
        textField_nameInput.setMaximumSize(new Dimension(300, 25));

        // Nutzername
        label_usernameInput = new JLabel("Nutzername:");
        label_usernameInput.setAlignmentX(Component.CENTER_ALIGNMENT);

        textField_usernameInput = new JTextField(20);
        textField_usernameInput = new JTextField(20);
        textField_usernameInput.setMaximumSize(new Dimension(300, 25));

        // Passwort Eingabe
        label_pwInput = new JLabel("Passwort:");
        label_pwInput.setAlignmentX(Component.CENTER_ALIGNMENT);

        textfield_pwInput = new JTextField(20);
        textfield_pwInput = new JTextField(20);
        textfield_pwInput.setMaximumSize(new Dimension(300, 25));

        button_add_password = new JButton("+");
        button_add_password.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainWindow.add(label_title);
        mainWindow.add(label_nameInput);
        mainWindow.add(textField_nameInput);
        mainWindow.add(label_usernameInput);
        mainWindow.add(textField_usernameInput);
        mainWindow.add(label_pwInput);
        mainWindow.add(textfield_pwInput);
        mainWindow.add(button_add_password);
        //mainWindow.add(Box.createRigidArea(new Dimension(0, 10))); // Abstand

        setContentPane(mainWindow);
    }

    private void generatePassword()
    {

    }
}
