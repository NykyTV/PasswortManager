package passwortmanager.utilities;

import passwortmanager.window.LoginWindow;
import passwortmanager.window.MainWindow;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Darkmode {
    private LoginWindow loginUtility;
    private MainWindow mainUtility;
    public Boolean darkMode;

    public void activateDarkMode(boolean active) {
        Color buttonFarbe;
        Color textFarbe;
        Color titlefarbe;
        boolean setFarbeTextField;

        //Farben werden gesetzt
        if (active) {
            buttonFarbe = Color.LIGHT_GRAY;
            textFarbe = Color.LIGHT_GRAY;
            titlefarbe = new Color(173, 216, 230);
            setFarbeTextField = true;
            if (loginUtility != null)
                loginUtility.LoginWindow.setBackground(Color.DARK_GRAY);
        }else {
            buttonFarbe = Color.WHITE;
            textFarbe = Color.BLACK;
            titlefarbe = new Color(0, 102, 204);
            setFarbeTextField = false;
            if (loginUtility != null)
                loginUtility.LoginWindow.setBackground(null);
        }

        //Die Farbe der Komponenten wird geändert
        if (mainUtility != null) {
            mainUtility.darkModeButton.setBackground(buttonFarbe);
        }

        if (loginUtility != null) {
            loginUtility.loginButton.setBackground(buttonFarbe);
            loginUtility.registerButton.setBackground(buttonFarbe);
            loginUtility.benutzerText.setForeground(textFarbe);
            loginUtility.passwortText.setForeground(textFarbe);
            loginUtility.label_title.setForeground(titlefarbe);
            setFarbeTextField(loginUtility.benutzerNameEingabe, setFarbeTextField);
            setFarbeTextField(loginUtility.passwortEingabe, setFarbeTextField);
        }
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

    public boolean getDarkmode() {
        try{
            String tureString = loginUtility.loadSettings().toString();
            String falseString = loginUtility.loadSettings().toString();
            tureString = tureString.substring(12, 16);
            falseString = falseString.substring(12, 17);
            System.out.println(tureString);
            System.out.println(falseString);
            switch (tureString) {
                case "true":
                    darkMode = true;
                    break;
                default:
                    break;
            }
            switch (falseString) {
                case "false":
                    darkMode = false;
                    break;
                default:
                    break;
            }
        }catch (Exception e) {}
        return darkMode;
    }

    public boolean getDarkmode1() {
        try{
            String tureString = mainUtility.loadSettings().toString();
            String falseString = mainUtility.loadSettings().toString();
            tureString = tureString.substring(12, 16);
            falseString = falseString.substring(12, 17);
            System.out.println(tureString);
            System.out.println(falseString);
            switch (tureString) {
                case "true":
                    darkMode = true;
                    break;
                default:
                    break;
            }
            switch (falseString) {
                case "false":
                    darkMode = false;
                    break;
                default:
                    break;
            }
        }catch (Exception e) {}
    }

    public Darkmode(LoginWindow loginUtility) {
        this.loginUtility = loginUtility;
        getDarkmode();
    }

    public Darkmode(MainWindow mainUtility) {
        this.mainUtility = mainUtility;
        getDarkmode1();
    }
}
