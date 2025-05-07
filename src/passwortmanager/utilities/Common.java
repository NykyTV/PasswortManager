package passwortmanager.utilities;

public class Common {

    public static final String SETTINGS_FILE = "settings.json";

    public static String getPasswordFilename(String accountName) {
        return accountName + ".bin";
    }
}
