package passwortmanager.utilities;

import lombok.Getter;
import lombok.Setter;
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
    private TogglePasswordRenderer togglePasswordRenderer;

    @Getter
    @Setter
    private Boolean darkMode = (Boolean) SettingsLoader.loadSettings().get("darkMode");

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
            List<JButton> buttonList = List.of(mainUtility.getButton_logout(), mainUtility.getButton_GeneratePW(), mainUtility.getButton_ADD(), mainUtility.getButton_Save(), mainUtility.getSettingsButton(), mainUtility.getButton_Show());
            setButtonBackground(buttonList, buttonFarbe);

            //Labels
            mainUtility.getLabel_AppName().setForeground(titelFarbe);
            List<JLabel> labelList = List.of(mainUtility.getLabel_EntryName(), mainUtility.getLabel_Username(), mainUtility.getLabel_Password());
            setLabelForeground(labelList, textFarbe);

            //Panels
            List<JPanel> panelList = List.of(mainUtility.getTopPanel(), mainUtility.getMidPanel());
            setPanelBackground(panelList, hintergrundFarbe);

            //Textfelder
            List<JTextField> textFieldList = List.of(mainUtility.getTextfield_EntryName(), mainUtility.getTextfield_Username(), mainUtility.getTextfield_Password());
            setFarbeTextField(textFieldList);

            //Passworttabelle
            mainUtility.getPasswordTable().setForeground(textFarbe);
            mainUtility.getPasswordTable().setBackground(hintergrundFarbe);
            mainUtility.getPasswordTable().setSelectionBackground(Color.LIGHT_GRAY);
            JTableHeader header = mainUtility.getPasswordTable().getTableHeader();
            header.setBackground(hintergrundFarbe);
            header.setForeground(textFarbe);

//            mainUtility.MainPanel.setBackground(buttonFarbe);
        }

        if (togglePasswordRenderer != null) {
            //Buttons
            List<JButton> buttonList = List.of(togglePasswordRenderer.getToggleButton(), togglePasswordRenderer.getCopyButton(), togglePasswordRenderer.getEditButton());
            setButtonBackground(buttonList, buttonFarbe);

            //Panels
            List<JPanel> panelList = List.of(togglePasswordRenderer.getRenderPanel());
            setPanelBackground(panelList, hintergrundFarbe);
        }

        //Hier werden die Farben der Komponenten im Loginwindow angepasst.
        if (loginUtility != null) {
            //Buttons
            List<JButton> buttonList = List.of(loginUtility.getLoginButton(), loginUtility.getRegisterButton(), loginUtility.getZuruekButton());
            setButtonBackground(buttonList, buttonFarbe);

            //Labels
            loginUtility.getLabel_title().setForeground(titelFarbe);
            List<JLabel> labelList = List.of(loginUtility.getBenutzerText(), loginUtility.getPasswortText(), loginUtility.getPasswortBestaetigenText());
            setLabelForeground(labelList, textFarbe);

            //Textfelder
            List<JTextField> textFieldList = List.of(loginUtility.getBenutzerNameEingabe(), loginUtility.getPasswortEingabe(), loginUtility.getPasswortBestaetigenEingabe());
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
            panelList = List.of(loginUtility.getLoginWindow());
        }else if (mainUtility != null) {
            panelList = List.of(mainUtility.getMainPanel());
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
    public boolean loadDarkModeFromFile() {
        if (loginUtility != null) {
            try{
                darkMode = (Boolean) SettingsLoader.loadSettings().get("darkMode");
            }catch (Exception _) {}
        }else if (mainUtility != null) {
            try{
                darkMode = (Boolean) SettingsLoader.loadSettings().get("darkMode");
            }catch (Exception _) {}
        }
        return darkMode;
    }

    public Darkmode(LoginWindow loginUtility) {
        this.loginUtility = loginUtility;
        loadDarkModeFromFile();
    }

    public Darkmode(MainWindow mainUtility) {
        this.mainUtility = mainUtility;
        loadDarkModeFromFile();
    }

    public Darkmode(TogglePasswordRenderer togglePasswordRenderer) {
        this.togglePasswordRenderer = togglePasswordRenderer;
        loadDarkModeFromFile();
    }
}
