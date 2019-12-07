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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.baumann.hhsmoodle.R;

public class helper_main {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

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

    public static class Item{
        final String text;
        public final int icon;
        Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }
        @Override
        public String toString() {
            return text;
        }
    }

    static void switchIcon (Activity activity, String string, String fieldDB, ImageView be) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        assert be != null;

        switch (string) {
            case "15":be.setImageResource(R.drawable.circle_pink);
                sharedPref.edit().putString(fieldDB, "15").apply();break;
            case "16":be.setImageResource(R.drawable.circle_purple);
                sharedPref.edit().putString(fieldDB, "16").apply();break;
            case "17":be.setImageResource(R.drawable.circle_blue);
                sharedPref.edit().putString(fieldDB, "17").apply();break;
            case "18":be.setImageResource(R.drawable.circle_teal);
                sharedPref.edit().putString(fieldDB, "18").apply();break;
            case "19":be.setImageResource(R.drawable.circle_green);
                sharedPref.edit().putString(fieldDB, "19").apply();break;
            case "20":be.setImageResource(R.drawable.circle_lime);
                sharedPref.edit().putString(fieldDB, "20").apply();break;
            case "21":be.setImageResource(R.drawable.circle_yellow);
                sharedPref.edit().putString(fieldDB, "21").apply();break;
            case "22":be.setImageResource(R.drawable.circle_orange);
                sharedPref.edit().putString(fieldDB, "22").apply();break;
            case "23":be.setImageResource(R.drawable.circle_brown);
                sharedPref.edit().putString(fieldDB, "23").apply();break;
            default:be.setImageResource(R.drawable.circle_red);
                sharedPref.edit().putString(fieldDB, "14").apply();break;
        }
    }


    // Strings, files, ...

    static String secString (String string) {
        return  string.replaceAll("'", "\'\'");
    }

    public static String createDate () {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return  format.format(date);
    }

    static void grantPermissionsStorage(final Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int hasWRITE_EXTERNAL_STORAGE = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
                View dialogView = View.inflate(activity, R.layout.dialog_action, null);
                TextView textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(helper_main.textSpannable(activity.getString(R.string.app_permissions)));
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
                helper_main.setBottomSheetBehavior(bottomSheetDialog, dialogView, BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    public static void applyTheme(Context context) {
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

    static void setBottomSheetBehavior (final BottomSheetDialog dialog, final View view, int beh) {
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBehavior.setState(beh);
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
}