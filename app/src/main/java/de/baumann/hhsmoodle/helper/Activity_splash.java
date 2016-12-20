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

package de.baumann.hhsmoodle.helper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import de.baumann.hhsmoodle.HHS_Browser;
import de.baumann.hhsmoodle.HHS_MainScreen;
import de.baumann.hhsmoodle.R;


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        TextInputLayout editUsernameLayout = (TextInputLayout) findViewById(R.id.editUsernameLayout);
        editUsernameLayout.setVisibility(View.INVISIBLE);
        TextInputLayout editPasswordLayout = (TextInputLayout) findViewById(R.id.editPasswordLayout);
        editPasswordLayout.setVisibility(View.INVISIBLE);

        editUsername = (EditText) findViewById(R.id.editUsername);
        assert editUsername != null;
        editUsername.setVisibility(View.INVISIBLE);
        editUsername.getBackground().mutate().setColorFilter(ContextCompat.getColor(Activity_splash.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        editPassword = (EditText) findViewById(R.id.editPassword);
        assert editPassword != null;
        editPassword.setVisibility(View.INVISIBLE);
        editPassword.getBackground().mutate().setColorFilter(ContextCompat.getColor(Activity_splash.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        Image = (ImageView) findViewById(R.id.image);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setVisibility(View.INVISIBLE);

        final String startType = sharedPref.getString("startType", "1");
        final String startURL = sharedPref.getString("favoriteURL", "https://moodle.huebsch.ka.schule-bw.de/moodle/");

        if (sharedPrefSec.getString("password") == null
                || sharedPrefSec.getString("username") == null
                || sharedPrefSec.getString("password").isEmpty()
                || sharedPrefSec.getString("username").isEmpty()) {

            editUsernameLayout.setVisibility(View.VISIBLE);
            editPasswordLayout.setVisibility(View.VISIBLE);
            editUsername.setVisibility(View.VISIBLE);
            editPassword.setVisibility(View.VISIBLE);
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

                        if (startType.equals("2")) {

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Intent mainIntent = new Intent(Activity_splash.this, HHS_Browser.class);
                                    mainIntent.putExtra("id", "1");
                                    mainIntent.putExtra("url", startURL);
                                    startActivity(mainIntent);
                                    Activity_splash.this.finish();
                                    overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                                }
                            }, 500);
                        } else if (startType.equals("1")){
                            new Handler().postDelayed(new Runnable() {
                                public void run() {

                                    Intent mainIntent = new Intent(Activity_splash.this, HHS_MainScreen.class);
                                    mainIntent.putExtra("id", "1");
                                    startActivity(mainIntent);
                                    Activity_splash.this.finish();
                                    overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                                }
                            }, 500);
                        }
                    }
                }
            });

        } else {
            if (startType.equals("2")) {

                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        Intent mainIntent = new Intent(Activity_splash.this, HHS_Browser.class);
                        mainIntent.putExtra("id", "1");
                        mainIntent.putExtra("url", startURL);
                        startActivity(mainIntent);
                        Activity_splash.this.finish();
                        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                    }
                }, 1000);
            } else if (startType.equals("1")){
                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        Intent mainIntent = new Intent(Activity_splash.this, HHS_MainScreen.class);
                        mainIntent.putExtra("id", "1");
                        startActivity(mainIntent);
                        Activity_splash.this.finish();
                        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                    }
                }, 1000);
            }
        }
    }

    @Override
    public void onBackPressed() {
        sharedPref.edit()
                .putBoolean("isOpened", true)
                .apply();
        finishAffinity();
    }
}
