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

package de.baumann.hhsmoodle;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.provider.Settings;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

class Class_Helper {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    static void insertDefaultBookmarks (Activity activity) {
        Bookmarks_Database db = new Bookmarks_Database(activity);
        db.open();
        db.insert(activity.getString(R.string.text_tit_1), "https://moodle.huebsch.ka.schule-bw.de/moodle/my/", "14", "", "");
        db.insert(activity.getString(R.string.text_tit_2), "https://moodle.huebsch.ka.schule-bw.de/moodle/user/profile.php", "15", "", "");
        db.insert(activity.getString(R.string.text_tit_8), "https://moodle.huebsch.ka.schule-bw.de/moodle/calendar/view.php", "16", "", "");
        db.insert(activity.getString(R.string.text_tit_3), "https://moodle.huebsch.ka.schule-bw.de/moodle/grade/report/overview/index.php", "17", "", "");
        db.insert(activity.getString(R.string.text_tit_4), "https://moodle.huebsch.ka.schule-bw.de/moodle/message/index.php", "18", "", "");
        db.insert(activity.getString(R.string.text_tit_5), "https://moodle.huebsch.ka.schule-bw.de/moodle/user/preferences.php", "19", "", "");
    }

    // Layouts -> filter, header, ...

    static void setImageHeader (Activity activity, ImageView imageView) {
        if(imageView != null) {
            TypedArray images = activity.getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imageView.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }
    }

    // Messages, Toasts, ...

    static SpannableString textSpannable (String text) {
        SpannableString s;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            s = new SpannableString(Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY));
        } else {
            s = new SpannableString(Html.fromHtml(text));
        }
        Linkify.addLinks(s, Linkify.WEB_URLS);
        return s;
    }

    // Activities -> start, end, ...

    static void switchToActivity(Activity activity, Class to) {
        Intent intent = new Intent(activity, to);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
    }

    // used Methods

    static void switchIcon(Activity activity, String string, ImageView be) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        assert be != null;

        switch (string) {
            case "15":be.setImageResource(R.drawable.circle_pink);
                sharedPref.edit().putString("bookmarks_icon", "15").apply();break;
            case "16":be.setImageResource(R.drawable.circle_purple);
                sharedPref.edit().putString("bookmarks_icon", "16").apply();break;
            case "17":be.setImageResource(R.drawable.circle_blue);
                sharedPref.edit().putString("bookmarks_icon", "17").apply();break;
            case "18":be.setImageResource(R.drawable.circle_teal);
                sharedPref.edit().putString("bookmarks_icon", "18").apply();break;
            case "19":be.setImageResource(R.drawable.circle_green);
                sharedPref.edit().putString("bookmarks_icon", "19").apply();break;
            case "20":be.setImageResource(R.drawable.circle_lime);
                sharedPref.edit().putString("bookmarks_icon", "20").apply();break;
            case "21":be.setImageResource(R.drawable.circle_yellow);
                sharedPref.edit().putString("bookmarks_icon", "21").apply();break;
            case "22":be.setImageResource(R.drawable.circle_orange);
                sharedPref.edit().putString("bookmarks_icon", "22").apply();break;
            case "23":be.setImageResource(R.drawable.circle_brown);
                sharedPref.edit().putString("bookmarks_icon", "23").apply();break;
            default:be.setImageResource(R.drawable.circle_red);
                sharedPref.edit().putString("bookmarks_icon", "14").apply();break;
        }
    }

    // Strings, files, ...

    static String secString (String string) {
        return  string.replaceAll("'", "\'\'");
    }

    static void grantPermissionsStorage(final Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int hasWRITE_EXTERNAL_STORAGE = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
                View dialogView = View.inflate(activity, R.layout.dialog_action, null);
                TextView textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(Class_Helper.textSpannable(activity.getString(R.string.app_permissions)));
                Button action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_ASK_PERMISSIONS);
                        bottomSheetDialog.cancel();
                    }
                });
                Button action_cancel = dialogView.findViewById(R.id.action_cancel);
                action_cancel.setText(R.string.menu_more_settings);
                action_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        activity.startActivity(intent);
                        bottomSheetDialog.cancel();
                    }
                });
                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
                Class_Helper.setBottomSheetBehavior(bottomSheetDialog, dialogView);
            }
        }
    }

    static void applyTheme(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String showNavButton = Objects.requireNonNull(sharedPref.getString("sp_theme", "1"));
        switch (showNavButton) {
            case "0":
                context.setTheme(R.style.AppTheme_system);
                break;
            case "2":
                context.setTheme(R.style.AppTheme_dark);
                break;
            default:
                context.setTheme(R.style.AppTheme);
                break;
        }
    }

    static void setBottomSheetBehavior(final BottomSheetDialog dialog, final View view) {
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.cancel();
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    // Security

    private static String protect;
    private static SharedPreferences sharedPref;

    static void setLoginData (final Activity activity) {
        try {

            final Class_SecurePreferences sharedPrefSec = new Class_SecurePreferences(activity);

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


    static void checkPin (final Activity activity) {

        PreferenceManager.setDefaultValues(activity, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        Class_SecurePreferences sharedPrefSec = new Class_SecurePreferences(activity);
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