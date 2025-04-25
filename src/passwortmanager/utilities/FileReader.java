package passwortmanager.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileReader {

    public static void saveFile(JSONArray passwords, String filename, String masterPassword) throws Exception {
        String jsonString = passwords.toString();

        File file = new File(filename);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] salt = AES.generateSalt();
            SecretKey secretKey = AES.deriveKeyFromPassword(masterPassword, salt);
            byte[] iv = AES.generateIv();

            byte[] encrypted = AES.encrypt(jsonString, secretKey, iv);

            fos.write(salt);
            fos.write(iv);
            fos.write(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Fehler beim Speichern!");
        }
    }

    public static JSONArray loadFile(String filename, String masterPassword) throws Exception {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) {
            throw new Exception("Keine gespeicherte Passwort-Datei gefunden.");
        }

        try (FileInputStream fis = new FileInputStream(file)) {

            // salt lesen
            byte[] saltBytes = new byte[16];
            fis.read(saltBytes);

            // IV lesen
            byte[] ivBytes = new byte[16];
            fis.read(ivBytes);

            SecretKey secretKey = AES.deriveKeyFromPassword(masterPassword, saltBytes);
            String decryptedJson = AES.decrypt(fis.readAllBytes(), secretKey, ivBytes);

            return (JSONArray) JSONValue.parse(decryptedJson);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Fehler beim Laden der Datei!");
        }
    }
}
