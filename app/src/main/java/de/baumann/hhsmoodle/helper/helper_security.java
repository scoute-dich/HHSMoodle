package de.baumann.hhsmoodle.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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

import de.baumann.hhsmoodle.R;


public class helper_security {

    private static String protect;
    private static SharedPreferences sharedPref;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public static void decrypt(Activity activity, String in, String out) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(activity, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

        PackageManager m = activity.getPackageManager();
        String s = activity.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;

            String pathIN = s + in;
            String pathOUT = s + out;

            File fileIN = new File(pathIN);

            if (fileIN.exists()) {
                FileInputStream fis = new FileInputStream(pathIN);
                FileOutputStream fos = new FileOutputStream(pathOUT);

                byte[] key = (sharedPrefSec.getString("key_encryption_01").getBytes("UTF-8"));
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
                Log.w("HHS_Moodle", "DB decrypted");

                //noinspection ResultOfMethodCallIgnored
                fileIN.delete();
                Log.w("HHS_Moodle", "DB deleted");
            }



        } catch (PackageManager.NameNotFoundException e) {
            Log.w("HHS_Moodle", "Error Package name not found ", e);
        }
    }

    public static void decryptDatabases (Activity activity) {
        try {decrypt(activity, "/databases/subject_DB_v01_en.db", "/databases/subject_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
        try {decrypt(activity, "/databases/random_DB_v01_en.db", "/databases/random_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
        try {decrypt(activity, "/databases/todo_DB_v01_en.db", "/databases/todo_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
        try {decrypt(activity, "/databases/notes_DB_v01_en.db", "/databases/notes_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
        try {decrypt(activity, "/databases/courses_DB_v01_en.db", "/databases/courses_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
        try {decrypt(activity, "/databases/bookmarks_DB_v01_en.db", "/databases/bookmarks_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
        try {decrypt(activity, "/databases/count_DB_v01_en.db", "/databases/count_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
    }

    public static void encrypt(Activity activity, String in, String out) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(activity, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

        PackageManager m = activity.getPackageManager();
        String s = activity.getPackageName();

        try {

            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;

            String pathIN = s + in;
            String pathOUT = s + out;

            File fileIN = new File(pathIN);

            if (fileIN.exists()) {
                FileInputStream fis = new FileInputStream(pathIN);
                FileOutputStream fos = new FileOutputStream(pathOUT);

                byte[] key = (sharedPrefSec.getString("key_encryption_01").getBytes("UTF-8"));
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
                Log.w("HHS_Moodle", "DB encrypted");

                //noinspection ResultOfMethodCallIgnored
                fileIN.delete();
                Log.w("HHS_Moodle", "DB deleted");
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.w("HHS_Moodle", "Error Package name not found ", e);
        }
    }

    public static void encryptDatabases (final Activity activity) {

        try {encrypt(activity, "/databases/subject_DB_v01.db","/databases/subject_DB_v01_en.db");} catch (Exception e) {e.printStackTrace();}
        try {encrypt(activity, "/databases/random_DB_v01.db","/databases/random_DB_v01_en.db");} catch (Exception e) {e.printStackTrace();}
        try {encrypt(activity, "/databases/todo_DB_v01.db","/databases/todo_DB_v01_en.db");} catch (Exception e) {e.printStackTrace();}
        try {encrypt(activity, "/databases/notes_DB_v01.db","/databases/notes_DB_v01_en.db");} catch (Exception e) {e.printStackTrace();}
        try {encrypt(activity, "/databases/courses_DB_v01.db","/databases/courses_DB_v01_en.db");} catch (Exception e) {e.printStackTrace();}
        try {encrypt(activity, "/databases/bookmarks_DB_v01.db","/databases/bookmarks_DB_v01_en.db");} catch (Exception e) {e.printStackTrace();}
        try {encrypt(activity, "/databases/count_DB_v01.db","/databases/count_DB_v01_en.db");} catch (Exception e) {e.printStackTrace();}
    }

    public static void encryptBackup (Activity activity, String name) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        PackageManager m = activity.getPackageManager();
        String s = activity.getPackageName();

        try {

            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;

            String pathOUT = helper_main.appDir() + "/moodle_backup/" + name;
            String pathIN = s + "/databases/" + name;

            File fileIN = new File(pathIN);

            if (fileIN.exists()) {
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
                Log.w("HHS_Moodle", "DB backup");
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.w("HHS_Moodle", "Error Package name not found ", e);
            helper_main.makeToast(activity, activity.getString(R.string.toast_backup_not));
        }
    }

    public static void grantPermissions(final Activity activity) {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (sharedPref.getString ("show_permission", "true").equals("true")){
                int hasWRITE_EXTERNAL_STORAGE = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                    if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        new android.app.AlertDialog.Builder(activity)
                                .setTitle(R.string.app_permissions_title)
                                .setMessage(helper_main.textSpannable(activity.getString(R.string.app_permissions)))
                                .setNeutralButton(R.string.toast_notAgain, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        sharedPref.edit().putString("show_permission", "false").apply();
                                    }
                                })
                                .setPositiveButton(activity.getString(R.string.toast_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (android.os.Build.VERSION.SDK_INT >= 23)
                                            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                })
                                .setNegativeButton(activity.getString(R.string.toast_cancel), null)
                                .show();
                        return;
                    }
                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        }
    }


    public static void checkPin (final Activity activity) {

        PreferenceManager.setDefaultValues(activity, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(activity, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        protect = sharedPrefSec.getString("protect_PW");

        if (protect != null  && protect.length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.YourStyle);
            final View dialogView = View.inflate(activity, R.layout.dialog_password, null);

            final TextView text = dialogView.findViewById(R.id.pass_userPin);

            Button ib0 = dialogView.findViewById(R.id.button0);
            assert ib0 != null;
            ib0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "0");
                }
            });

            Button ib1 = dialogView.findViewById(R.id.button1);
            assert ib1 != null;
            ib1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "1");
                }
            });

            Button ib2 = dialogView.findViewById(R.id.button2);
            assert ib2 != null;
            ib2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "2");
                }
            });

            Button ib3 = dialogView.findViewById(R.id.button3);
            assert ib3 != null;
            ib3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "3");
                }
            });

            Button ib4 = dialogView.findViewById(R.id.button4);
            assert ib4 != null;
            ib4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "4");
                }
            });

            Button ib5 = dialogView.findViewById(R.id.button5);
            assert ib5 != null;
            ib5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "5");
                }
            });

            Button ib6 = dialogView.findViewById(R.id.button6);
            assert ib6 != null;
            ib6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "6");
                }
            });

            Button ib7 = dialogView.findViewById(R.id.button7);
            assert ib7 != null;
            ib7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "7");
                }
            });

            Button ib8 = dialogView.findViewById(R.id.button8);
            assert ib8 != null;
            ib8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "8");
                }
            });

            Button ib9 = dialogView.findViewById(R.id.button9);
            assert ib9 != null;
            ib9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "9");
                }
            });


            ImageButton enter = dialogView.findViewById(R.id.imageButtonEnter);
            assert enter != null;

            final ImageButton cancel = dialogView.findViewById(R.id.imageButtonCancel);
            assert cancel != null;
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text.setText("");
                }
            });

            final Button clear = dialogView.findViewById(R.id.buttonReset);
            assert clear != null;
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Snackbar snackbar = Snackbar
                            .make(clear, activity.getString(R.string.pw_forgotten_dialog), Snackbar.LENGTH_LONG)
                            .setAction(activity.getString(R.string.toast_yes), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        // clearing app data
                                        Runtime runtime = Runtime.getRuntime();
                                        runtime.exec("pm clear de.baumann.hhsmoodle");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    snackbar.show();
                }
            });

            builder.setView(dialogView);
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    activity.finishAffinity();
                }
            });

            final AlertDialog dialog = builder.create();
            // Display the custom alert dialog on interface
            dialog.show();

            enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Password = text.getText().toString().trim();

                    if (Password.equals(protect)) {
                        sharedPref.edit().putBoolean("isOpened", false).apply();
                        dialog.dismiss();
                    } else {
                        Snackbar.make(text, R.string.toast_wrongPW, Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private static void enterNum (View view, String number) {
        TextView text = view.findViewById(R.id.pass_userPin);
        String textNow = text.getText().toString().trim();
        String pin = textNow + number;
        text.setText(pin);
    }
}
