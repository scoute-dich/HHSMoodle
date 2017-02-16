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
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

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
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_todo.Todo_DbAdapter;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;

@SuppressWarnings("SameParameterValue")
public class Popup_todo_restart extends Activity {

    private class_SecurePreferences sharedPrefSec;
    private Todo_DbAdapter db;
    private ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefSec = new class_SecurePreferences(Popup_todo_restart.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

        try {
            decrypt("/databases/todo_DB_v01.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_popup_restart);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        lv = (ListView)findViewById(R.id.dialogList);
        //calling Notes_DbAdapter
        db = new Todo_DbAdapter(Popup_todo_restart.this);
        db.open();
        setTodoList();
    }

    private void setTodoList() {

        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

        //display data
        final int layoutstyle=R.layout.list_item_notes;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.textView_create_notes
        };
        String[] column = new String[] {
                "todo_title",
                "todo_content",
                "todo_creation"
        };
        final Cursor row = db.fetchAllData(Popup_todo_restart.this);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(Popup_todo_restart.this, layoutstyle,row,column, xml_id, 0) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String todo_title = row2.getString(row2.getColumnIndexOrThrow("todo_title"));
                final String todo_content = row2.getString(row2.getColumnIndexOrThrow("todo_content"));
                final String todo_attachment = row2.getString(row2.getColumnIndexOrThrow("todo_attachment"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_attachment = (ImageView) v.findViewById(R.id.att_notes);

                switch (todo_attachment) {
                    case "true":
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.alert_circle);
                        break;
                    default:
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.alert_circle_red);

                        int n = Integer.valueOf(_id);

                        android.content.Intent iMain = new android.content.Intent();
                        iMain.setAction("shortcutToDo");
                        iMain.setClassName(Popup_todo_restart.this, "de.baumann.hhsmoodle.activities.Activity_splash");
                        PendingIntent piMain = PendingIntent.getActivity(Popup_todo_restart.this, n, iMain, 0);

                        NotificationCompat.Builder builderSummary =
                                new NotificationCompat.Builder(Popup_todo_restart.this)
                                        .setSmallIcon(R.drawable.school)
                                        .setColor(ContextCompat.getColor(Popup_todo_restart.this, R.color.colorPrimary))
                                        .setGroup("HHS_Moodle")
                                        .setGroupSummary(true)
                                        .setContentIntent(piMain);

                        Notification notification = new NotificationCompat.Builder(Popup_todo_restart.this)
                                .setColor(ContextCompat.getColor(Popup_todo_restart.this, R.color.colorPrimary))
                                .setSmallIcon(R.drawable.school)
                                .setContentTitle(todo_title)
                                .setContentText(todo_content)
                                .setContentIntent(piMain)
                                .setAutoCancel(true)
                                .setGroup("HHS_Moodle")
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(todo_content))
                                .setPriority(Notification.PRIORITY_DEFAULT)
                                .setVibrate(new long[0])
                                .build();

                        NotificationManager notificationManager = (NotificationManager) Popup_todo_restart.this.getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(n, notification);
                        notificationManager.notify(0, builderSummary.build());
                        break;
                }
                return v;
            }
        };

        lv.setAdapter(adapter);
        //onClick function
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    encrypt("/databases/todo_DB_v01.db");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        }, 1000);
    }

    private void decrypt(String out) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        PackageManager m = getPackageManager();
        String s = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;

            String pathIN = s + "/databases/todo_DB_v01_en.db";
            String pathOUT = s + out;

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

        } catch (PackageManager.NameNotFoundException e) {
            Log.w("HHS_Moodle", "Error Package name not found ", e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void encrypt(String in) {

        PackageManager m = getPackageManager();
        String s = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;

            String pathIN = s + in;
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