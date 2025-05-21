package passwortmanager.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import javax.crypto.SecretKey;
import java.io.*;

public class FileReader {

    public static void saveFile(JSONArray passwords, String filename, String masterPassword) throws Exception {
        String jsonString = passwords.toString();
        File file = new File(filename);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] salt = AES.generateSalt(); // 16 bytes
            SecretKey secretKey = AES.deriveKeyFromPassword(masterPassword, salt);
            byte[] iv = AES.generateIv(); // 16 bytes
            byte[] version = generateVersion(filename); // 8 bytes counter

            byte[] encrypted = AES.encrypt(jsonString, secretKey, iv);

            // Write version, salt and IV
            fos.write(version);
            fos.write(salt);
            fos.write(iv);
            fos.write(encrypted);

        } catch (Exception e) {
            throw new Exception("Fehler beim Speichern!", e);
        }
    }

    public static JSONArray loadFile(String filename, String masterPassword) throws Exception {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) {
            throw new Exception("Keine gespeicherte Passwort-Datei gefunden.");
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            // Read version (8 bytes)
            byte[] versionBytes = new byte[8];
            fis.read(versionBytes);

            // Read salt (16 bytes)
            byte[] saltBytes = new byte[16];
            fis.read(saltBytes);

            // Read IV (16 bytes)
            byte[] ivBytes = new byte[16];
            fis.read(ivBytes);

            SecretKey secretKey = AES.deriveKeyFromPassword(masterPassword, saltBytes);
            String decryptedJson = AES.decrypt(fis.readAllBytes(), secretKey, ivBytes);

            return (JSONArray) JSONValue.parse(decryptedJson);

        } catch (Exception e) {
            throw new Exception("Fehler beim Laden der Datei!", e);
        }
    }

    public static long getFileVersion(String filename) throws Exception {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) {
            return 0;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] versionBytes = new byte[8];
            fis.read(versionBytes);
            return bytesToLong(versionBytes);
        }
    }

    private static byte[] generateVersion(String filename) throws Exception {
        String versionFile = filename + ".version";
        long counter = 1;

        File file = new File(versionFile);
        if (file.exists()) {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
                counter = dis.readLong() + 1;
            } catch (IOException e) {
                counter = getFileVersion(filename) + 1;
            }
        }

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(versionFile))) {
            dos.writeLong(counter);
        }

        return longToBytes(counter);
    }

    private static byte[] longToBytes(long x) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(x & 0xFF);
            x >>= 8;
        }
        return result;
    }

    private static long bytesToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }
}
