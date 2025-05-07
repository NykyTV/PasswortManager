package passwortmanager.utilities;

import javax.swing.*;

public class Common {

    private static ImageIcon icon = new ImageIcon("K:\\Documents\\Java\\PasswortManager\\src\\passwortmanager\\resources\\Icon32px.png");
    public static final String SETTINGS_FILE = "settings.json";

    public static String getPasswordFilename(String accountName) {
        return accountName + ".bin";
    }

    public static void setAppIcon(JFrame frame) {
        frame.setIconImage(icon.getImage());
    }
}
