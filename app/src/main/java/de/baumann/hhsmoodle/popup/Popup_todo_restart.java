/*
    This file is part of the HHS Moodle WebApp.

    HHS Moodle WebApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HHS Moodle WebApp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Diaspora Native WebApp.

    If not, see <http://www.gnu.org/licenses/>.
 */

package de.baumann.hhsmoodle.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.databases.Database_Todo;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;

@SuppressWarnings("SameParameterValue")
public class Popup_todo_restart extends Activity {

    private ListView listView = null;
    private class_SecurePreferences sharedPrefSec;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit().putString("notification_running", "true").apply();
        sharedPrefSec = new class_SecurePreferences(Popup_todo_restart.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

        try {
            decrypt("/databases/todo_v2_en.db", "/databases/todo_v2.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_popup_restart);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        listView = (ListView)findViewById(R.id.dialogList);
        setNotesList();
    }

    private void setNotesList() {

        ArrayList<HashMap<String,String>> mapList = new ArrayList<>();

        try {
            Database_Todo db = new Database_Todo(Popup_todo_restart.this);
            ArrayList<String[]> bookmarkList = new ArrayList<>();
            db.getBookmarks(bookmarkList, Popup_todo_restart.this);
            if (bookmarkList.size() == 0) {
                db.loadInitialData();
                db.getBookmarks(bookmarkList, Popup_todo_restart.this);
            }
            db.close();

            for (String[] strAry : bookmarkList) {
                HashMap<String, String> map = new HashMap<>();
                map.put("seqno", strAry[0]);
                map.put("title", strAry[1]);
                map.put("cont", strAry[2]);
                map.put("icon", strAry[3]);
                map.put("attachment", strAry[4]);
                map.put("createDate", strAry[5]);
                mapList.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    Popup_todo_restart.this,
                    mapList,
                    R.layout.list_item_notes,
                    new String[] {"title", "cont", "createDate"},
                    new int[] {R.id.textView_title_notes, R.id.textView_des_notes, R.id.textView_create_notes}
            ) {
                @Override
                public View getView (final int position, View convertView, ViewGroup parent) {

                    @SuppressWarnings("unchecked")
                    HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                    final String title = map.get("title");
                    final String cont = map.get("cont");
                    final String seqnoStr = map.get("seqno");
                    final String attachment = map.get("attachment");

                    View v = super.getView(position, convertView, parent);

                    switch (attachment) {
                        case "":

                            break;
                        default:

                            int n = Integer.valueOf(seqnoStr);

                            android.content.Intent iMain = new android.content.Intent();
                            iMain.setAction("shortcutToDo");
                            iMain.setClassName(Popup_todo_restart.this, "de.baumann.hhsmoodle.activities.Activity_splash");
                            PendingIntent piMain = PendingIntent.getActivity(Popup_todo_restart.this, n, iMain, 0);

                            Notification notification = new NotificationCompat.Builder(Popup_todo_restart.this)
                                    .setSmallIcon(R.drawable.school)
                                    .setContentTitle(title)
                                    .setContentText(cont)
                                    .setContentIntent(piMain)
                                    .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(cont))
                                    .setPriority(Notification.PRIORITY_DEFAULT)
                                    .setVibrate(new long[0])
                                    .build();

                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            notificationManager.notify(n, notification);
                            break;
                    }

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                encrypt("/databases/todo_v2.db","/databases/todo_v2_en.db");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            sharedPref.edit().putString("notification_running", "false").apply();
                            finish();
                        }
                    }, 1000);

                    return v;
                }
            };

            listView.setAdapter(simpleAdapter);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("UnusedParameters")
    private void decrypt(String in, String out) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        PackageManager m = getPackageManager();
        String s = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;

            String pathIN = s + "/databases/todo_v2_en.db";
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void encrypt(String in, String out) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        PackageManager m = getPackageManager();
        String s = getPackageName();
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
            fileIN.delete();

        } catch (PackageManager.NameNotFoundException e) {
            Log.w("HHS_Moodle", "Error Package name not found ", e);
        }
    }

    @Override
    public void onBackPressed() {
        helper_main.isClosed(Popup_todo_restart.this);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_todo_restart.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_todo_restart.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(Popup_todo_restart.this);
    }
}