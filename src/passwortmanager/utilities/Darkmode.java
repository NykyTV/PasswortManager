package passwortmanager.utilities;

import passwortmanager.window.LoginWindow;
import passwortmanager.window.MainWindow;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class Darkmode {
    private LoginWindow loginUtility;
    private MainWindow mainUtility;
    public Boolean darkMode;

    public void activateDarkMode(boolean active) {
        Color buttonFarbe;
        Color textFarbe;
        Color hintergrundFarbe;
        Color titlefarbe;
        boolean setFarbeTextField;

        //Farben werden gesetzt
        if (active) {
            buttonFarbe = Color.LIGHT_GRAY;
            textFarbe = Color.LIGHT_GRAY;
            hintergrundFarbe = Color.DARK_GRAY;
            titlefarbe = new Color(173, 216, 230);
            setFarbeTextField = true;
            if (loginUtility != null) {
                loginUtility.LoginWindow.setBackground(hintergrundFarbe);
            }else if (mainUtility != null) {
                mainUtility.MainPanel.setBackground(hintergrundFarbe);
            }
        }else {
            buttonFarbe = Color.WHITE;
            textFarbe = Color.BLACK;
            hintergrundFarbe = null;
            titlefarbe = new Color(0, 102, 204);
            setFarbeTextField = false;
            if (loginUtility != null) {
                loginUtility.LoginWindow.setBackground(hintergrundFarbe);
            }else if (mainUtility != null) {
                mainUtility.MainPanel.setBackground(hintergrundFarbe);
            }
        }

        //Die Farbe der Komponenten wird geändert
        if (mainUtility != null) {
            //Buttons
            mainUtility.darkModeButton.setBackground(buttonFarbe);
            mainUtility.button_GeneratePW.setBackground(buttonFarbe);
            mainUtility.button_ADD.setBackground(buttonFarbe);
            mainUtility.button_Save.setBackground(buttonFarbe);
            mainUtility.button_logout.setBackground(buttonFarbe);
            mainUtility.button_logout.setBackground(buttonFarbe);

            //Labels
            mainUtility.label_AppName.setForeground(titlefarbe);
            mainUtility.label_EntryName.setForeground(textFarbe);
            mainUtility.label_Username.setForeground(textFarbe);
            mainUtility.label_Password.setForeground(textFarbe);
            mainUtility.passwordTable.setForeground(textFarbe);

            //Panels
            mainUtility.TopPanel.setBackground(hintergrundFarbe);
            mainUtility.MidPanel.setBackground(hintergrundFarbe);
            mainUtility.passwordTable.setBackground(hintergrundFarbe);
            mainUtility.passwordTable.setSelectionBackground(Color.LIGHT_GRAY);
//            JTableHeader header = mainUtility.passwordTable.getTableHeader();
//            header.setBackground(textFarbe);
//            header.setForeground(buttonFarbe);

            //Textfelder
            setFarbeTextField(mainUtility.textfield_EntryName, setFarbeTextField);
            setFarbeTextField(mainUtility.textfield_Username, setFarbeTextField);
            setFarbeTextField(mainUtility.textfield_Password, setFarbeTextField);

//            mainUtility.MainPanel.setBackground(buttonFarbe);
        }

        if (loginUtility != null) {
            //Buttons
            loginUtility.loginButton.setBackground(buttonFarbe);
            loginUtility.registerButton.setBackground(buttonFarbe);

            //Labels
            loginUtility.benutzerText.setForeground(textFarbe);
            loginUtility.passwortText.setForeground(textFarbe);
            loginUtility.label_title.setForeground(titlefarbe);

            //Textfelder
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

    public Darkmode(LoginWindow loginUtility) {
        this.loginUtility = loginUtility;
        getDarkmode();
    }

    public Darkmode(MainWindow mainUtility) {
        this.mainUtility = mainUtility;
        getDarkmode1();
    }
}
