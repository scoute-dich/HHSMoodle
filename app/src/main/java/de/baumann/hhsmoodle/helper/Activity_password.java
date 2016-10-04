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

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import de.baumann.hhsmoodle.R;

public class Activity_password extends AppCompatActivity {

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        text = (TextView) findViewById(R.id.textView);

        Button ib0 = (Button) findViewById(R.id.button10);
        assert ib0 != null;
        ib0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNow = text.getText().toString().trim();
                String pin = textNow + "0";
                text.setText(pin);
            }
        });

        Button ib1 = (Button) findViewById(R.id.button2);
        assert ib1 != null;
        ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNow = text.getText().toString().trim();
                String pin = textNow + "1";
                text.setText(pin);
            }
        });

        Button ib2 = (Button) findViewById(R.id.button3);
        assert ib2 != null;
        ib2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNow = text.getText().toString().trim();
                String pin = textNow + "2";
                text.setText(pin);
            }
        });

        Button ib3 = (Button) findViewById(R.id.button4);
        assert ib3 != null;
        ib3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNow = text.getText().toString().trim();
                String pin = textNow + "3";
                text.setText(pin);
            }
        });

        Button ib4 = (Button) findViewById(R.id.button5);
        assert ib4 != null;
        ib4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNow = text.getText().toString().trim();
                String pin = textNow + "4";
                text.setText(pin);
            }
        });

        Button ib5 = (Button) findViewById(R.id.button6);
        assert ib5 != null;
        ib5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNow = text.getText().toString().trim();
                String pin = textNow + "5";
                text.setText(pin);
            }
        });

        Button ib6 = (Button) findViewById(R.id.button8);
        assert ib6 != null;
        ib6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNow = text.getText().toString().trim();
                String pin = textNow + "6";
                text.setText(pin);
            }
        });

        Button ib7 = (Button) findViewById(R.id.button11);
        assert ib7 != null;
        ib7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNow = text.getText().toString().trim();
                String pin = textNow + "7";
                text.setText(pin);
            }
        });

        Button ib8 = (Button) findViewById(R.id.button12);
        assert ib8 != null;
        ib8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNow = text.getText().toString().trim();
                String pin = textNow + "8";
                text.setText(pin);
            }
        });

        Button ib9 = (Button) findViewById(R.id.button13);
        assert ib9 != null;
        ib9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textNow = text.getText().toString().trim();
                String pin = textNow + "9";
                text.setText(pin);
            }
        });

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String protect = sharedPref.getString("protect_PW", "");

        ImageButton enter = (ImageButton) findViewById(R.id.imageButton23);
        assert enter != null;
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Password = text.getText().toString().trim();

                if (Password.equals(protect)) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Activity_password.this);
                    sharedPref.edit()
                            .putBoolean("isOpened", false)
                            .apply();
                    finish();

                } else {
                    Snackbar.make(text, R.string.toast_wrongPW, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        ImageButton cancel = (ImageButton) findViewById(R.id.imageButton22);
        assert cancel != null;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text.setText("");
            }
        });

        Button clear = (Button) findViewById(R.id.button14);
        assert clear != null;
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SpannableString s;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    s = new SpannableString(Html.fromHtml(getString(R.string.pw_forgotten_dialog),Html.FROM_HTML_MODE_LEGACY));
                } else {
                    //noinspection deprecation
                    s = new SpannableString(Html.fromHtml(getString(R.string.pw_forgotten_dialog)));
                }

                Linkify.addLinks(s, Linkify.WEB_URLS);

                final AlertDialog.Builder dialog = new AlertDialog.Builder(Activity_password.this)
                        .setMessage(s)
                        .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    // clearing app data
                                    Runtime runtime = Runtime.getRuntime();
                                    runtime.exec("pm clear de.baumann.hhsmoodle");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                dialog.show();
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
