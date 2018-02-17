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

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Random;

import de.baumann.hhsmoodle.HHS_MainScreen;
import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_security;


public class Activity_splash extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private ImageView Image;
    private SharedPreferences sharedPref;
    private class_SecurePreferences sharedPrefSec;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefSec = new class_SecurePreferences(Activity_splash.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

        if (sharedPref.getString("key_generated_01", "no").equals("no")) {
            char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!§$%&/()=?;:_-.,+#*<>".toCharArray();
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 16; i++) {
                char c = chars[random.nextInt(chars.length)];
                sb.append(c);
            }
            sharedPrefSec.put("key_encryption_01", sb.toString());
            sharedPref.edit().putString("key_generated_01", "yes").apply();
        }

        helper_security.decryptDatabases(Activity_splash.this);
        helper_main.onStart(Activity_splash.this);

        TextInputLayout editUsernameLayout = findViewById(R.id.editUsernameLayout);
        editUsernameLayout.setVisibility(View.INVISIBLE);
        TextInputLayout editPasswordLayout = findViewById(R.id.editPasswordLayout);
        editPasswordLayout.setVisibility(View.INVISIBLE);

        editUsername = findViewById(R.id.editUsername);
        assert editUsername != null;
        editUsername.setVisibility(View.INVISIBLE);
        editUsername.getBackground().mutate().setColorFilter(ContextCompat.getColor(Activity_splash.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        editPassword = findViewById(R.id.editPassword);
        assert editPassword != null;
        editPassword.setVisibility(View.INVISIBLE);
        editPassword.getBackground().mutate().setColorFilter(ContextCompat.getColor(Activity_splash.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        Image = findViewById(R.id.image);
        FloatingActionButton fab = findViewById(R.id.fab);
        assert fab != null;
        fab.setVisibility(View.INVISIBLE);

        boolean show = sharedPref.getBoolean("showIntroScreen_showIntro", true);

        if (show){
            helper_main.switchToActivity(Activity_splash.this, Activity_intro.class, true);
        } else {
            if (sharedPrefSec.getString("password") == null
                    || sharedPrefSec.getString("username") == null
                    || sharedPrefSec.getString("password").isEmpty()
                    || sharedPrefSec.getString("username").isEmpty()) {

                editUsernameLayout.setVisibility(View.VISIBLE);
                editPasswordLayout.setVisibility(View.VISIBLE);
                editUsername.setVisibility(View.VISIBLE);
                editPassword.setVisibility(View.VISIBLE);
                editUsername.setText(sharedPrefSec.getString("username"));
                editPassword.setText(sharedPrefSec.getString("password"));
                fab.setVisibility(View.VISIBLE);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String Username = editUsername.getText().toString().trim();
                        String Password = editPassword.getText().toString().trim();

                        if (Username.isEmpty() || Password.isEmpty()) {
                            Snackbar.make(Image, R.string.login_hint, Snackbar.LENGTH_LONG).show();
                        } else {
                            sharedPrefSec.put("username", Username);
                            sharedPrefSec.put("password", Password);

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    helper_main.switchToActivity(Activity_splash.this, HHS_MainScreen.class, true);
                                }
                            }, 500);
                        }
                    }
                });

            } else {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        helper_main.switchToActivity(Activity_splash.this, HHS_MainScreen.class, true);
                    }
                }, 500);
            }
        }

        onNewIntent(getIntent());
    }

    protected void onNewIntent(final Intent intent) {

        String action = intent.getAction();

        if (Intent.ACTION_SEND.equals(action)) {
            helper_security.decryptDatabases(Activity_splash.this);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent mainIntent = new Intent(Activity_splash.this, HHS_MainScreen.class);
                    mainIntent.setAction("shortcutNotesNew_HS");
                    mainIntent.putExtra(Intent.EXTRA_SUBJECT, intent.getStringExtra(Intent.EXTRA_SUBJECT));
                    mainIntent.putExtra(Intent.EXTRA_TEXT, intent.getStringExtra(Intent.EXTRA_TEXT));
                    startActivity(mainIntent);
                    Activity_splash.this.finish();
                    overridePendingTransition(0,0);
                }
            }, 500);
        } else if (Intent.ACTION_VIEW.equals(action)) {
            helper_security.decryptDatabases(Activity_splash.this);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Uri data = intent.getData();
                    assert data != null;
                    String link = data.toString();
                    sharedPref.edit().putString("loadURL", link).apply();
                    Intent mainIntent = new Intent(Activity_splash.this, HHS_MainScreen.class);
                    mainIntent.setAction("shortcutBrowser_HS");
                    startActivity(mainIntent);
                    Activity_splash.this.finish();
                    overridePendingTransition(0,0);
                }
            }, 500);
        } else if ("shortcutBookmarks".equals(action)) {
            helper_security.decryptDatabases(Activity_splash.this);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent mainIntent = new Intent(Activity_splash.this, HHS_MainScreen.class);
                    mainIntent.setAction("shortcutBookmarks_HS");
                    startActivity(mainIntent);
                    Activity_splash.this.finish();
                    overridePendingTransition(0,0);
                }
            }, 500);
        } else if ("shortcutNotes".equals(action)) {
            helper_security.decryptDatabases(Activity_splash.this);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent mainIntent = new Intent(Activity_splash.this, HHS_MainScreen.class);
                    mainIntent.setAction("shortcutNotes_HS");
                    startActivity(mainIntent);
                    Activity_splash.this.finish();
                    overridePendingTransition(0,0);
                }
            }, 500);
        } else if ("shortcutToDo".equals(action)) {
            helper_security.decryptDatabases(Activity_splash.this);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent mainIntent = new Intent(Activity_splash.this, HHS_MainScreen.class);
                    mainIntent.setAction("shortcutToDo_HS");
                    startActivity(mainIntent);
                    Activity_splash.this.finish();
                    overridePendingTransition(0,0);
                }
            }, 500);
        } else if ("shortcutBrowser".equals(action)) {
            helper_security.decryptDatabases(Activity_splash.this);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent mainIntent = new Intent(Activity_splash.this, HHS_MainScreen.class);
                    mainIntent.setAction("shortcutBrowser_HS");
                    startActivity(mainIntent);
                    Activity_splash.this.finish();
                    overridePendingTransition(0,0);
                }
            }, 500);
        } else if ("shortcutFiles".equals(action)) {
            helper_security.decryptDatabases(Activity_splash.this);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent mainIntent = new Intent(Activity_splash.this, HHS_MainScreen.class);
                    mainIntent.setAction("shortcutFiles_HS");
                    startActivity(mainIntent);
                    Activity_splash.this.finish();
                    overridePendingTransition(0,0);
                }
            }, 500);
        }
    }

    @Override
    public void onBackPressed() {
        helper_security.encryptDatabases(Activity_splash.this);
        finishAffinity();
    }
}
