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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.baumann.hhsmoodle.R;

public class helper_main {

    public static void showHelpDialog (Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.setTitle(R.string.dialog_help_title);
        builder.setMessage(helper_main.textSpannable(activity.getString(R.string.dialog_help_text)));

        AlertDialog dialog = builder.create();
        dialog.show();

        ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

    }

    // Layouts -> filter, header, ...

    public static void setImageHeader (Activity activity, ImageView imageView) {

        if(imageView != null) {
            TypedArray images = activity.getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imageView.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }
    }


    // Messages, Toasts, ...

    public static SpannableString textSpannable (String text) {
        SpannableString s;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            s = new SpannableString(Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY));
        } else {
            //noinspection deprecation
            s = new SpannableString(Html.fromHtml(text));
        }
        Linkify.addLinks(s, Linkify.WEB_URLS);
        return s;
    }

    // Activities -> start, end, ...

    public static void switchToActivity(Activity activity, Class to) {
        Intent intent = new Intent(activity, to);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
    }

    // used Methods

    public static class Item{
        final String text;
        public final int icon;
        public Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public static void switchIcon (Activity activity, String string, String fieldDB, ImageView be) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        assert be != null;

        switch (string) {
            case "01":be.setImageResource(R.drawable.school_grey_dark);
                sharedPref.edit().putString(fieldDB, "01").apply();break;
            case "02":be.setImageResource(R.drawable.ic_view_dashboard_grey600_48dp);
                sharedPref.edit().putString(fieldDB, "02").apply();break;
            case "03":be.setImageResource(R.drawable.ic_face_profile_grey600_48dp);
                sharedPref.edit().putString(fieldDB, "03").apply();break;
            case "04":be.setImageResource(R.drawable.ic_calendar_grey600_48dp);
                sharedPref.edit().putString(fieldDB, "04").apply();break;
            case "05":be.setImageResource(R.drawable.ic_chart_areaspline_grey600_48dp);
                sharedPref.edit().putString(fieldDB, "05").apply();break;
            case "06":be.setImageResource(R.drawable.ic_bell_grey600_48dp);
                sharedPref.edit().putString(fieldDB, "06").apply();break;
            case "07":be.setImageResource(R.drawable.ic_settings_grey600_48dp);
                sharedPref.edit().putString(fieldDB, "07").apply();break;
            case "08":be.setImageResource(R.drawable.ic_web_grey600_48dp);
                sharedPref.edit().putString(fieldDB, "08").apply();break;
            case "09":be.setImageResource(R.drawable.ic_magnify_grey600_48dp);
                sharedPref.edit().putString(fieldDB, "09").apply();break;
            case "10":be.setImageResource(R.drawable.ic_pencil_grey600_48dp);
                sharedPref.edit().putString(fieldDB, "10").apply();break;
            case "11":be.setImageResource(R.drawable.ic_check_grey600_48dp);
                sharedPref.edit().putString(fieldDB, "11").apply();break;
            case "12":be.setImageResource(R.drawable.ic_clock_grey600_48dp);
                sharedPref.edit().putString(fieldDB, "12").apply();break;
            case "13":be.setImageResource(R.drawable.ic_bookmark_grey600_48dp);
                sharedPref.edit().putString(fieldDB, "13").apply();break;
            case "14":be.setImageResource(R.drawable.circle_red);
                sharedPref.edit().putString(fieldDB, "14").apply();break;
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
            case "24":be.setImageResource(R.drawable.circle_grey);
                sharedPref.edit().putString(fieldDB, "24").apply();break;
        }
    }

    public static void showKeyboard(final Activity activity, final EditText editText) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(imm).showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                editText.setSelection(editText.length());
            }
        }, 200);
    }


    // Strings, files, ...

    public static String secString (String string) {
        return  string.replaceAll("'", "\'\'");
    }

    public static String createDate () {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return  format.format(date);
    }
}