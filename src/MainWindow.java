import javax.swing.*;

public class MainWindow extends JFrame{
    private JPanel mainWindow;

    public MainWindow(String title) {
        super(title);

        mainWindow = new JPanel();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setContentPane(mainWindow);
        this.pack();
    }

    public static void main(String[] args) {
        JFrame loginFrame = new LoginWindow("Passwort Manager | Login");
        //JFrame mainFrame = new MainWindow("Passwort Manager");

        loginFrame.setVisible(true);
        //mainFrame.setVisible(true);
    }
}
