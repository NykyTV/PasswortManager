import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
    private JPanel LoginWindow;
    private JButton loginButton;

    public LoginWindow(String title) {
        super(title);
        LoginWindow = new JPanel();
        loginButton = new JButton();

        this.setMinimumSize(new Dimension(700, 900));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(LoginWindow);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.pack();

        loginButton.setMinimumSize(new Dimension(50, 25));



    }
}
