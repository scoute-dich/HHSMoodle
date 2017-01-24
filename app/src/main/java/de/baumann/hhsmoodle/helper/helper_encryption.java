package de.baumann.hhsmoodle.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class helper_encryption {

    private static void decrypt(Activity activity, String in, String out) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(activity, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

        PackageManager m = activity.getPackageManager();
        String s = activity.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;

            String pathIN = s + in;
            String pathOUT = s + out;

            FileInputStream fis = new FileInputStream(pathIN);
            FileOutputStream fos = new FileOutputStream(pathOUT);

            byte[] key = (sharedPrefSec.getString("generateDBKOK").getBytes("UTF-8"));
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            // Length is 16 byte
            // Create cipher
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            int b;
            byte[] d = new byte[8];
            while((b = cis.read(d)) != -1) {
                fos.write(d, 0, b);
            }
            fos.flush();
            fos.close();
            cis.close();

            File fileIN = new File(pathIN);
            //noinspection ResultOfMethodCallIgnored
            fileIN.delete();

        } catch (PackageManager.NameNotFoundException e) {
            Log.w("HHS_Moodle", "Error Package name not found ", e);
        }
    }

    public static void decryptDatabases (Activity activity) {

        try {
            decrypt(activity, "/databases/random_v2_en.db", "/databases/random_v2.db");
            decrypt(activity, "/databases/todo_v2_en.db", "/databases/todo_v2.db");
            decrypt(activity, "/databases/notes_v2_en.db", "/databases/notes_v2.db");
            decrypt(activity, "/databases/courseList_v2_en.db", "/databases/courseList_v2.db");
            decrypt(activity, "/databases/browser_v2_en.db", "/databases/browser_v2.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void encrypt(Activity activity, String in, String out) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(activity, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

        PackageManager m = activity.getPackageManager();
        String s = activity.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;

            String pathIN = s + in;
            String pathOUT = s + out;

            FileInputStream fis = new FileInputStream(pathIN);
            FileOutputStream fos = new FileOutputStream(pathOUT);

            byte[] key = (sharedPrefSec.getString("generateDBKOK").getBytes("UTF-8"));
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            // Length is 16 byte
            // Create cipher
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            // Wrap the output stream
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            // Write bytes
            int b;
            byte[] d = new byte[8];
            while((b = fis.read(d)) != -1) {
                cos.write(d, 0, b);
            }
            // Flush and close streams.
            cos.flush();
            cos.close();
            fis.close();

            File fileIN = new File(pathIN);
            //noinspection ResultOfMethodCallIgnored
            fileIN.delete();

        } catch (PackageManager.NameNotFoundException e) {
            Log.w("HHS_Moodle", "Error Package name not found ", e);
        }
    }

    public static void encryptDatabases (Activity activity) {

        try {
            encrypt(activity, "/databases/random_v2.db","/databases/random_v2_en.db");
            encrypt(activity, "/databases/todo_v2.db","/databases/todo_v2_en.db");
            encrypt(activity, "/databases/notes_v2.db","/databases/notes_v2_en.db");
            encrypt(activity, "/databases/courseList_v2.db","/databases/courseList_v2_en.db");
            encrypt(activity, "/databases/browser_v2.db","/databases/browser_v2_en.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void encryptBackup (Activity activity, String name) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        PackageManager m = activity.getPackageManager();
        String s = activity.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;

            String pathOUT = Environment.getExternalStorageDirectory() + "/HHS_Moodle/backup/" + name;
            String pathIN = s + "/databases/" + name;

            FileInputStream fis = new FileInputStream(pathIN);
            FileOutputStream fos = new FileOutputStream(pathOUT);

            byte[] key = ("[MGq)sY6k(GV,*?i".getBytes("UTF-8"));
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            // Length is 16 byte
            // Create cipher
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            // Wrap the output stream
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            // Write bytes
            int b;
            byte[] d = new byte[8];
            while((b = fis.read(d)) != -1) {
                cos.write(d, 0, b);
            }
            // Flush and close streams.
            cos.flush();
            cos.close();
            fis.close();

        } catch (PackageManager.NameNotFoundException e) {
            Log.w("HHS_Moodle", "Error Package name not found ", e);
        }
    }
}
