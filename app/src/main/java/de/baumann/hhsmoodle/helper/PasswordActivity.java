/*
    This file is part of the Diaspora Native WebApp.

    Diaspora Native WebApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Diaspora Native WebApp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Diaspora Native WebApp.

    If not, see <http://www.gnu.org/licenses/>.
 */

package de.baumann.hhsmoodle.helper;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import de.baumann.hhsmoodle.R;


public class PasswordActivity extends AppCompatActivity {

    private EditText editPassword;
    private ImageView Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        EditText editUsername = (EditText) findViewById(R.id.editUsername);
        assert editUsername != null;
        editUsername.setVisibility(View.INVISIBLE);
        editUsername.getBackground().mutate().setColorFilter(ContextCompat.getColor(PasswordActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        editPassword = (EditText) findViewById(R.id.editPassword);
        assert editPassword != null;
        editPassword.setVisibility(View.INVISIBLE);
        editPassword.getBackground().mutate().setColorFilter(ContextCompat.getColor(PasswordActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        Image = (ImageView) findViewById(R.id.image);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setVisibility(View.INVISIBLE);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String protect = sharedPref.getString("protect_PW", "");

        editPassword.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Password = editPassword.getText().toString().trim();

                if (Password.equals(protect)) {

                    finish();

                } else {
                    editPassword.setText("");
                    Snackbar.make(Image, R.string.toast_wrongPW, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit()
                .putBoolean("isOpened", true)
                .apply();
        finishAffinity();
    }
}
