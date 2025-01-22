import javax.swing.*;

public class MainWindow extends JFrame{
    private JTextField textfield_pwInput;
    private JPanel panel1;
    private JLabel label_pwLabel;
    private JButton button_add_password;

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

        label_pwLabel = new JLabel("Passwort:");
        textfield_pwInput = new JTextField(20);
        button_add_password = new JButton("+");

        //mainWindow.setLayout(new BoxLayout(mainWindow, BoxLayout.Y_AXIS));

        mainWindow.add(label_pwLabel);
        mainWindow.add(textfield_pwInput);
        mainWindow.add(button_add_password);

        setContentPane(mainWindow);
    }

    private void generatePassword()
    {

    }
}
