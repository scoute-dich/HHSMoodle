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

package de.baumann.hhsmoodle.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

import de.baumann.hhsmoodle.HHS_MainScreen;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.about.About_activity;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_encryption;
import de.baumann.hhsmoodle.helper.helper_main;

public class Activity_settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        setTitle(R.string.menu_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper_main.onStart(Activity_settings.this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Display the fragment as the activity_screen_main content
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {

        private void addOpenSettingsListener() {

            final Activity activity = getActivity();
            Preference reset = findPreference("clearCache");

            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    getActivity().startActivity(intent);

                    return true;
                }
            });
        }

        private void addShortcutListener() {

            final Activity activity = getActivity();
            Preference reset = findPreference("shortcuts");

            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    final CharSequence[] options = {
                            getString(R.string.app_name),
                            getString(R.string.title_bookmarks),
                            getString(R.string.todo_title),
                            getString(R.string.title_notes),
                            getString(R.string.schedule_title)};

                    new AlertDialog.Builder(activity)
                            .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            })
                            .setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {

                                    if (options[item].equals (getString(R.string.title_bookmarks))) {
                                        Intent i = new Intent(activity.getApplicationContext(), Activity_splash.class);
                                        i.setAction("shortcutBookmarks");

                                        Intent shortcut = new Intent();
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, (getString(R.string.title_bookmarks)));
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                                Intent.ShortcutIconResource.fromContext(activity.getApplicationContext(), R.mipmap.ic_bookmark));
                                        shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                        activity.sendBroadcast(shortcut);
                                        helper_main.makeToast(activity, getString(R.string.toast_shortcut));
                                    }

                                    if (options[item].equals (getString(R.string.title_notes))) {
                                        Intent i = new Intent(activity.getApplicationContext(), Activity_splash.class);
                                        i.setAction("shortcutNotes");

                                        Intent shortcut = new Intent();
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, (getString(R.string.title_notes)));
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                                Intent.ShortcutIconResource.fromContext(activity.getApplicationContext(), R.mipmap.ic_note));
                                        shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                        activity.sendBroadcast(shortcut);
                                        helper_main.makeToast(activity, getString(R.string.toast_shortcut));
                                    }

                                    if (options[item].equals (getString(R.string.todo_title))) {
                                        Intent i = new Intent(activity.getApplicationContext(), Activity_splash.class);
                                        i.setAction("shortcutToDo");

                                        Intent shortcut = new Intent();
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, (getString(R.string.todo_title)));
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                                Intent.ShortcutIconResource.fromContext(activity.getApplicationContext(), R.mipmap.ic_todo));
                                        shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                        activity.sendBroadcast(shortcut);
                                        helper_main.makeToast(activity, getString(R.string.toast_shortcut));
                                    }

                                    if (options[item].equals (getString(R.string.app_name))) {
                                        Intent i = new Intent(activity.getApplicationContext(), Activity_splash.class);
                                        i.setAction("shortcutBrowser");

                                        Intent shortcut = new Intent();
                                        shortcut.setAction(Intent.ACTION_MAIN);
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, (getString(R.string.bookmark_createNote)));
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                                Intent.ShortcutIconResource.fromContext(activity.getApplicationContext(), R.mipmap.ic_launcher));
                                        shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                        activity.sendBroadcast(shortcut);
                                        helper_main.makeToast(activity, getString(R.string.toast_shortcut));
                                    }

                                    if (options[item].equals (getString(R.string.schedule_title))) {
                                        Intent i = new Intent(activity.getApplicationContext(), Activity_splash.class);
                                        i.setAction("shortcutSchedule");

                                        Intent shortcut = new Intent();
                                        shortcut.setAction(Intent.ACTION_MAIN);
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, (getString(R.string.bookmark_createNote)));
                                        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                                                Intent.ShortcutIconResource.fromContext(activity.getApplicationContext(), R.mipmap.ic_note_plus));
                                        shortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                                        activity.sendBroadcast(shortcut);
                                        helper_main.makeToast(activity, getString(R.string.toast_shortcut));
                                    }

                                }
                            }).show();

                    return true;
                }
            });
        }

        private void addIntroListener() {

            Preference reset = findPreference("intro_show");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    helper_main.switchToActivity(getActivity(), About_activity.class, false);
                    return true;
                }
            });
        }

        private void addPermissionListener() {

            Preference reset = findPreference("perm_notShow");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    if (android.os.Build.VERSION.SDK_INT >= 23) {

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        getActivity().startActivity(intent);
                        helper_main.makeToast(getActivity(), getActivity().getString(R.string.perm_notShow_toast));
                    }

                    return true;
                }
            });
        }

        private void addPermissionDistListener() {

            Preference reset = findPreference("perm_notShow_dist");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    NotificationManager notificationManager =
                            (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivity(intent);
                    }

                    return true;
                }
            });
        }

        private void addProtectListener() {

            Preference reset = findPreference("protect_PW");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    final Activity activity = getActivity();
                    final class_SecurePreferences sharedPrefSec = new class_SecurePreferences(activity, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
                    final String password = sharedPrefSec.getString("protect_PW");

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    View dialogView = View.inflate(activity, R.layout.dialog_pin, null);

                    final EditText pass_userPW = (EditText) dialogView.findViewById(R.id.pass_userPin);
                    pass_userPW.setText(password);

                    builder.setView(dialogView);
                    builder.setTitle(R.string.action_protect);
                    builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            String inputTag = pass_userPW.getText().toString().trim();
                            sharedPrefSec.put("protect_PW", inputTag);

                        }
                    });
                    builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

                    final AlertDialog dialog2 = builder.create();
                    // Display the custom alert dialog on interface
                    dialog2.show();

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            helper_main.showKeyboard(activity, pass_userPW);
                        }
                    }, 200);

                    return true;
                }
            });
        }

        private void addUsernameListener() {

            Preference reset = findPreference("username");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    try {

                        final class_SecurePreferences sharedPrefSec = new class_SecurePreferences(getActivity(), "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        View dialogView = View.inflate(getActivity(), R.layout.dialog_login, null);

                        final EditText pass_userName = (EditText) dialogView.findViewById(R.id.pass_userName);
                        pass_userName.setText(sharedPrefSec.getString("username"));
                        final EditText pass_userPW = (EditText) dialogView.findViewById(R.id.pass_userPW);
                        pass_userPW.setText(sharedPrefSec.getString("password"));

                        builder.setView(dialogView);
                        builder.setTitle(R.string.action_username);
                        builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                String inputTag = pass_userName.getText().toString().trim();
                                sharedPrefSec.put("username", inputTag);

                                String inputTag2 = pass_userPW.getText().toString().trim();
                                sharedPrefSec.put("password", inputTag2);
                            }
                        });
                        builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                        final AlertDialog dialog2 = builder.create();
                        // Display the custom alert dialog on interface
                        dialog2.show();

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                helper_main.showKeyboard(getActivity(), pass_userName);
                            }
                        }, 200);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }
            });
        }

        private void addBackup_dbListener() {

            Preference reset = findPreference("backup_db");
            reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    final CharSequence[] options = {
                            getString(R.string.action_backup),
                            getString(R.string.action_restore)};
                    new AlertDialog.Builder(getActivity())
                            .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            })
                            .setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    if (options[item].equals(getString(R.string.action_backup))) {
                                        try {helper_encryption.encryptBackup(getActivity(),"/bookmarks_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                        try {helper_encryption.encryptBackup(getActivity(),"/courses_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                        try {helper_encryption.encryptBackup(getActivity(),"/notes_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                        try {helper_encryption.encryptBackup(getActivity(),"/random_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                        try {helper_encryption.encryptBackup(getActivity(),"/subject_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                        try {helper_encryption.encryptBackup(getActivity(),"/schedule_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                        try {helper_encryption.encryptBackup(getActivity(),"/todo_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                    }
                                    if (options[item].equals(getString(R.string.action_restore))) {
                                        try {decrypt("/schedule_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                        try {decrypt("/subject_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                        try {decrypt("/bookmarks_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                        try {decrypt("/courses_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                        try {decrypt("/notes_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                        try {decrypt("/random_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                        try {decrypt("/todo_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
                                    }
                                }
                            }).show();

                    return true;
                }
            });
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.user_settings);
            addOpenSettingsListener();
            addUsernameListener();
            addProtectListener();
            addBackup_dbListener();
            addIntroListener();
            addShortcutListener();
            addPermissionListener();
            addPermissionDistListener();
        }

        private void decrypt(String name) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

            PackageManager m = getActivity().getPackageManager();
            String s = getActivity().getPackageName();
            try {
                PackageInfo p = m.getPackageInfo(s, 0);
                s = p.applicationInfo.dataDir;

                String pathIN = Environment.getExternalStorageDirectory() + "/HHS_Moodle/backup/" + name;
                String pathOUT = s + "/databases/" + name;
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
                    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                    CipherInputStream cis = new CipherInputStream(fis, cipher);
                    int b;
                    byte[] d = new byte[8];
                    while ((b = cis.read(d)) != -1) {
                        fos.write(d, 0, b);
                    }
                    fos.flush();
                    fos.close();
                    cis.close();
                    helper_main.makeToast(getActivity(), getActivity().getString(R.string.toast_restore));
                }

            } catch (PackageManager.NameNotFoundException e) {
                Log.w("HHS_Moodle", "Error Package name not found ", e);
                helper_main.makeToast(getActivity(), getActivity().getString(R.string.toast_restore_not));
            }
        }
    }

    @Override
    public void onBackPressed() {
        helper_main.isOpened(Activity_settings.this);
        helper_main.switchToActivity(Activity_settings.this, HHS_MainScreen.class, true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getBoolean ("help", false)){
            menu.getItem(0).setVisible(false); // here pass the index of save menu item
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            helper_main.isOpened(Activity_settings.this);
            helper_main.switchToActivity(Activity_settings.this, HHS_MainScreen.class, true);
        }

        if (id == R.id.action_help) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(Activity_settings.this)
                    .setTitle(R.string.helpSettings_title)
                    .setMessage(helper_main.textSpannable(getString(R.string.helpSettings_text)))
                    .setPositiveButton(getString(R.string.toast_yes), null);
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}