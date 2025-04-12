package passwortmanager.utilities;

import passwortmanager.window.LoginWindow;
import passwortmanager.window.MainWindow;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class Darkmode {
    private LoginWindow loginUtility;
    private MainWindow mainUtility;
    public Boolean darkMode;

    public void activateDarkMode(boolean active) {
        Color buttonFarbe;
        Color textFarbe;
        Color hintergrundFarbe;
        Color titelFarbe;

        //Hier werden die Farben gesetzt
        if (active) {
            buttonFarbe = Color.LIGHT_GRAY;
            textFarbe = Color.LIGHT_GRAY;
            hintergrundFarbe = Color.DARK_GRAY;
            titelFarbe = new Color(173, 216, 230);
            List<JPanel> panelList = createPanelListe();
            setPanelBackground(panelList, hintergrundFarbe);
        }else {
            buttonFarbe = Color.WHITE;
            textFarbe = Color.BLACK;
            hintergrundFarbe = null;
            titelFarbe = new Color(0, 102, 204);
            List<JPanel> panelList = createPanelListe();
            setPanelBackground(panelList, hintergrundFarbe);
        }

        //Hier werden die Farben der Komponenten im Mainwindow angepasst.
        if (mainUtility != null) {
            //Buttons
            List<JButton> buttonList = List.of(mainUtility.button_logout, mainUtility.darkModeButton, mainUtility.button_GeneratePW, mainUtility.button_ADD, mainUtility.button_Save);
            setButtonBackground(buttonList, buttonFarbe);

            //Labels
            mainUtility.label_AppName.setForeground(titelFarbe);
            List<JLabel> labelList = List.of(mainUtility.label_EntryName, mainUtility.label_Username, mainUtility.label_Password);
            setLabelForeground(labelList, textFarbe);

            //Panels
            List<JPanel> panelList = List.of(mainUtility.TopPanel, mainUtility.MidPanel);
            setPanelBackground(panelList, hintergrundFarbe);

            //Textfelder
            List<JTextField> textFieldList = List.of(mainUtility.textfield_EntryName, mainUtility.textfield_Username, mainUtility.textfield_Password);
            setFarbeTextField(textFieldList);

            //Passworttabelle
            mainUtility.passwordTable.setForeground(textFarbe);
            mainUtility.passwordTable.setBackground(hintergrundFarbe);
            mainUtility.passwordTable.setSelectionBackground(Color.LIGHT_GRAY);
            JTableHeader header = mainUtility.passwordTable.getTableHeader();
            header.setBackground(hintergrundFarbe);
            header.setForeground(textFarbe);

//            mainUtility.MainPanel.setBackground(buttonFarbe);
        }

        //Hier werden die Farben der Komponenten im Loginwindow angepasst.
        if (loginUtility != null) {
            //Buttons
            List<JButton> buttonList = List.of(loginUtility.loginButton, loginUtility.registerButton);
            setButtonBackground(buttonList, buttonFarbe);

            //Labels
            loginUtility.label_title.setForeground(titelFarbe);
            List<JLabel> labelList = List.of(loginUtility.benutzerText, loginUtility.passwortText);
            setLabelForeground(labelList, textFarbe);

            //Textfelder
            List<JTextField> textFieldList = List.of(loginUtility.benutzerNameEingabe, loginUtility.passwortEingabe);
            setFarbeTextField(textFieldList);
        }
    }

    //Diese Funktion setzt den Hintergrund von JButtons.
    private void setButtonBackground(List<JButton> buttonList, Color buttonFarbe) {
        for (JButton jButton : buttonList) {
            jButton.setBackground(buttonFarbe);
        }
    }

    //Diese Funktion setzt den Vordergrund von JLabels.
    private void setLabelForeground(List<JLabel> labelList, Color textFarbe) {
        for (JLabel jLabel : labelList) {
            jLabel.setForeground(textFarbe);
        }
    }

    //Diese Funktion setzt den Hintergrund von JPanel.
    private void setPanelBackground(List<JPanel> panelList, Color hintergrundFarbe) {
        for (JPanel jPanel : panelList) {
            jPanel.setBackground(hintergrundFarbe);
        }
    }

    //Diese Funktion erstellt eine Liste von JPanels für die Panel "LoginWindow" und "MainPanel".
    private List<JPanel> createPanelListe() {
        List<JPanel> panelList = List.of();
        if (loginUtility != null) {
            panelList = List.of(loginUtility.LoginWindow);
        }else if (mainUtility != null) {
            panelList = List.of(mainUtility.MainPanel);
        }
        return panelList;
    }

    //Diese Funktion setzt den Vordergrund, Hintergrund und die Border von JTextFields.
    private void setFarbeTextField(List<JTextField> textFieldList) {
        if (darkMode) {
            Border textFeldBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
            for (JTextField jTextField : textFieldList) {
                jTextField.setForeground(Color.DARK_GRAY);
                jTextField.setBackground(Color.LIGHT_GRAY);
                jTextField.setBorder(textFeldBorder);
            }
        }else {
            Border textFeldBorder = BorderFactory.createLineBorder(Color.GRAY);
            for (JTextField jTextField : textFieldList) {
                jTextField.setForeground(null);
                jTextField.setBackground(null);
                jTextField.setBorder(textFeldBorder);
            }
        }
    }

    //Diese Funktion holt den Wert "darkMode" aus der Datei "settings.json"
    public boolean getDarkmode() {
        if (loginUtility != null) {
            try{
                String darkModeString = loginUtility.loadSettings().toString().substring(12, 17);
                if (darkModeString.contains("true")) {
                    darkMode = true;
                }else if (darkModeString.contains("false")) {
                    darkMode = false;
                }
            }catch (Exception _) {}
        }else if (mainUtility != null) {
            try{
                String darkModeString = mainUtility.loadSettings().toString().substring(12, 17);
                if (darkModeString.contains("true")) {
                    darkMode = true;
                }else if (darkModeString.contains("false")) {
                    darkMode = false;
                }
            }catch (Exception _) {}
        }
        return darkMode;
    }

    public Darkmode(LoginWindow loginUtility) {
        this.loginUtility = loginUtility;
        getDarkmode();
    }

    public Darkmode(MainWindow mainUtility) {
        this.mainUtility = mainUtility;
        getDarkmode();
    }
}
