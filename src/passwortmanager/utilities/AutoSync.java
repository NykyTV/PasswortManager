package passwortmanager.utilities;

import org.json.simple.JSONObject;
import passwortmanager.window.MainWindow;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class AutoSync {
    private static Timer timer = null;

    public static void scheduleSync(String accountName, MainWindow mainWindow) {
        String filename = Common.getPasswordFilename(accountName);

        if (timer != null) return;

        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                JSONObject settings = SettingsLoader.loadSettings();
                String serverUrl = extractHostFromUrl((String) settings.get("url"));

                if (Database.isServerAvailable(serverUrl, mainWindow)) {
                    File file = new File(filename);
                    if (file.exists()) {
                        if (mainWindow != null) mainWindow.setStatus("<html><font color='blue'>🔄 Server wieder online. Synchronisiere...</font></html>");
                        Database.saveUserFileToDatabase(accountName, file, mainWindow);
                        timer.cancel();
                        timer = null;
                    }
                }
            }
        }, 10_000, 30_000);
    }

    private static String extractHostFromUrl(String url) {
        try {
            return url.split("//")[1].split(":")[0];
        } catch (Exception e) {
            return "localhost";
        }
    }
}
