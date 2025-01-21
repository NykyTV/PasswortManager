import javax.swing.*;

public class MainWindow extends JFrame{
    private JPanel mainWindow;

    public MainWindow(String title)
    {
        super(title);

        mainWindow = new JPanel();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainWindow);
        this.pack();
    }

    public static void main(String[] args)
    {
        JFrame frame = new MainWindow("Passwort Manager");
        frame.setVisible(true);
    }
}
