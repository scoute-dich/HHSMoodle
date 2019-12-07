package de.baumann.hhsmoodle.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import de.baumann.hhsmoodle.R;


public class helper_security {

    private static String protect;
    private static SharedPreferences sharedPref;

    static void setLoginData (final Activity activity) {
        try {

            final Class_SecurePreferences sharedPrefSec = new Class_SecurePreferences(activity, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppTheme_Fullscreen);
            final View dialogView = View.inflate(activity, R.layout.dialog_edit_login, null);

            final EditText pass_userName = dialogView.findViewById(R.id.pass_userName);
            pass_userName.setText(sharedPrefSec.getString("username"));
            final EditText pass_userPW = dialogView.findViewById(R.id.pass_userPW);
            pass_userPW.setText(sharedPrefSec.getString("password"));
            final Button fab = dialogView.findViewById(R.id.fab);

            builder.setView(dialogView);


            final AlertDialog dialog = builder.create();
            dialog.show();

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String username = pass_userName.getText().toString().trim();
                    String password = pass_userPW.getText().toString().trim();

                    if (username.isEmpty() || password.length() < 8) {
                        Snackbar snackbar = Snackbar
                                .make(fab, activity.getString(R.string.login_text_edit), Snackbar.LENGTH_LONG)
                                .setAction(activity.getString(R.string.toast_cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.cancel();
                                    }
                                });
                        snackbar.show();
                    } else {
                        sharedPrefSec.put("username", username);
                        sharedPrefSec.put("password", password);
                        dialog.cancel();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void checkPin (final Activity activity) {

        PreferenceManager.setDefaultValues(activity, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        Class_SecurePreferences sharedPrefSec = new Class_SecurePreferences(activity, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        protect = sharedPrefSec.getString("settings_security_pin");

        if (protect != null  && protect.length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppTheme_Fullscreen);
            final View dialogView = View.inflate(activity, R.layout.dialog_enter_pin, null);

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
