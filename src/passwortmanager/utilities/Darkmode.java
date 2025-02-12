package passwortmanager.utilities;

import passwortmanager.window.LoginWindow;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Darkmode {
    private LoginWindow loginUtility;

    public void activateDarkMode(boolean active) {
        Color buttonFarbe;
        Color textFarbe;
        Color titlefarbe;
        boolean setFarbeTextField;

        if (active) {
            buttonFarbe = Color.LIGHT_GRAY;
            textFarbe = Color.LIGHT_GRAY;
            titlefarbe = new Color(173, 216, 230);
            setFarbeTextField = true;
            loginUtility.LoginWindow.setBackground(Color.DARK_GRAY);
        }else {
            buttonFarbe = Color.WHITE;
            textFarbe = Color.BLACK;
            titlefarbe = new Color(0, 102, 204);
            setFarbeTextField = false;
            loginUtility.LoginWindow.setBackground(null);
        }

        loginUtility.loginButton.setBackground(buttonFarbe);
        loginUtility.darkModeButton.setBackground(buttonFarbe);
        loginUtility.registerButton.setBackground(buttonFarbe);
        loginUtility.benutzerText.setForeground(textFarbe);
        loginUtility.passwortText.setForeground(textFarbe);
        loginUtility.label_title.setForeground(titlefarbe);
        setFarbeTextField(loginUtility.benutzerNameEingabe, setFarbeTextField);
        setFarbeTextField(loginUtility.passwortEingabe, setFarbeTextField);
    }

    private void setFarbeTextField(JTextField textFeld, boolean darkMode) {
        if (darkMode) {
            Border textFeldBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
            textFeld.setForeground(Color.DARK_GRAY);
            textFeld.setBackground(Color.LIGHT_GRAY);
            textFeld.setBorder(textFeldBorder);
        }else {
            Border textFeldBorder = BorderFactory.createLineBorder(Color.GRAY);
            textFeld.setForeground(null);
            textFeld.setBackground(null);
            textFeld.setBorder(textFeldBorder);
        }
    }

    public Darkmode(LoginWindow loginUtility) {
        this.loginUtility = loginUtility;
    }

}
