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
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;

public class helper_main {

    private static SharedPreferences sharedPref;

    // Layouts -> filter, header, ...

    public static void setImageHeader (Activity activity, ImageView imageView) {

        if(imageView != null) {
            TypedArray images = activity.getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imageView.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }
    }

    public static void changeFilter (Activity activity, String filter, String filterBY) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPref.edit().putString(filter, filterBY).apply();
    }

    public static void showFilter (Activity activity, RelativeLayout layout, ImageView imageView, EditText editText,
                                   String text, String hint, boolean showKeyboard) {
        layout.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        editText.setText(text);
        editText.setHint(hint);
        editText.requestFocus();
        if (showKeyboard) {
            helper_main.showKeyboard(activity, editText);
        }
    }

    public static void hideFilter (Activity activity, RelativeLayout layout, ImageView imageView) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
        imageView.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
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

    public static void makeToast(Activity activity, String Text) {
        LayoutInflater inflater = activity.getLayoutInflater();

        View toastLayout = inflater.inflate(R.layout.toast,
                (ViewGroup) activity.findViewById(R.id.toast_root_view));

        TextView header = (TextView) toastLayout.findViewById(R.id.toast_message);
        header.setText(Text);

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastLayout);
        toast.show();
    }


    // Activities -> start, end, ...

    public static void switchToActivity(Activity activity, Class to, boolean finishActivity) {
        Intent intent = new Intent(activity, to);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
        if (finishActivity) {
            activity.finish();
        }
    }

    public static void onStart (final Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark));
        }
    }

    public static void onClose (final Activity activity) {
        PreferenceManager.setDefaultValues(activity, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        final ProgressDialog progressDialog;

        if (sharedPref.getBoolean("backup_aut", false)) {

            try {
                helper_security.encryptBackup(activity, "/bookmarks_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            try {
                helper_security.encryptBackup(activity, "/courses_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            try {
                helper_security.encryptBackup(activity, "/notes_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            try {
                helper_security.encryptBackup(activity, "/random_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            try {
                helper_security.encryptBackup(activity, "/subject_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            try {
                helper_security.encryptBackup(activity, "/schedule_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            try {
                helper_security.encryptBackup(activity, "/todo_DB_v01.db");} catch (Exception e) {e.printStackTrace();}

            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(activity.getString(R.string.app_close));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    sharedPref.edit().putString("loadURL", "").apply();
                    helper_security.encryptDatabases(activity);
                    if (progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    activity.finishAffinity();
                }
            }, 1500);

        } else {
            sharedPref.edit().putString("loadURL", "").apply();
            helper_security.encryptDatabases(activity);
            activity.finishAffinity();
        }
    }

    // used Methods

    public static class Item{
        public final String text;
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

        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        assert be != null;

        switch (string) {
            case "01":be.setImageResource(R.drawable.school_grey_dark);sharedPref.edit().putString(fieldDB, "01").apply();break;
            case "02":be.setImageResource(R.drawable.ic_view_dashboard_grey600_48dp);sharedPref.edit().putString(fieldDB, "02").apply();break;
            case "03":be.setImageResource(R.drawable.ic_face_profile_grey600_48dp);sharedPref.edit().putString(fieldDB, "03").apply();break;
            case "04":be.setImageResource(R.drawable.ic_calendar_grey600_48dp);sharedPref.edit().putString(fieldDB, "04").apply();break;
            case "05":be.setImageResource(R.drawable.ic_chart_areaspline_grey600_48dp);sharedPref.edit().putString(fieldDB, "05").apply();break;
            case "06":be.setImageResource(R.drawable.ic_bell_grey600_48dp);sharedPref.edit().putString(fieldDB, "06").apply();break;
            case "07":be.setImageResource(R.drawable.ic_settings_grey600_48dp);sharedPref.edit().putString(fieldDB, "07").apply();break;
            case "08":be.setImageResource(R.drawable.ic_web_grey600_48dp);sharedPref.edit().putString(fieldDB, "08").apply();break;
            case "09":be.setImageResource(R.drawable.ic_magnify_grey600_48dp);sharedPref.edit().putString(fieldDB, "09").apply();break;
            case "10":be.setImageResource(R.drawable.ic_pencil_grey600_48dp);sharedPref.edit().putString(fieldDB, "10").apply();break;
            case "11":be.setImageResource(R.drawable.ic_check_grey600_48dp);sharedPref.edit().putString(fieldDB, "11").apply();break;
            case "12":be.setImageResource(R.drawable.ic_clock_grey600_48dp);sharedPref.edit().putString(fieldDB, "12").apply();break;
            case "13":be.setImageResource(R.drawable.ic_bookmark_grey600_48dp);sharedPref.edit().putString(fieldDB, "13").apply();break;
            case "14":be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString(fieldDB, "14").apply();break;
            case "15":be.setImageResource(R.drawable.circle_pink);sharedPref.edit().putString(fieldDB, "15").apply();break;
            case "16":be.setImageResource(R.drawable.circle_purple);sharedPref.edit().putString(fieldDB, "16").apply();break;
            case "17":be.setImageResource(R.drawable.circle_blue);sharedPref.edit().putString(fieldDB, "17").apply();break;
            case "18":be.setImageResource(R.drawable.circle_teal);sharedPref.edit().putString(fieldDB, "18").apply();break;
            case "19":be.setImageResource(R.drawable.circle_green);sharedPref.edit().putString(fieldDB, "19").apply();break;
            case "20":be.setImageResource(R.drawable.circle_lime);sharedPref.edit().putString(fieldDB, "20").apply();break;
            case "21":be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString(fieldDB, "21").apply();break;
            case "22":be.setImageResource(R.drawable.circle_orange);sharedPref.edit().putString(fieldDB, "22").apply();break;
            case "23":be.setImageResource(R.drawable.circle_brown);sharedPref.edit().putString(fieldDB, "23").apply();break;
            case "24":be.setImageResource(R.drawable.circle_grey);sharedPref.edit().putString(fieldDB, "24").apply();break;
        }
    }

    public static void switchIconDialog (Activity activity, int item, String fieldDB, ImageView be) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        assert be != null;

        if (item == 0) {
            be.setImageResource(R.drawable.school_grey_dark);sharedPref.edit().putString(fieldDB, "01").apply();
        } else if (item == 1) {
            be.setImageResource(R.drawable.ic_view_dashboard_grey600_48dp);sharedPref.edit().putString(fieldDB, "02").apply();
        } else if (item == 2) {
            be.setImageResource(R.drawable.ic_face_profile_grey600_48dp);sharedPref.edit().putString(fieldDB, "03").apply();
        } else if (item == 3) {
            be.setImageResource(R.drawable.ic_calendar_grey600_48dp);sharedPref.edit().putString(fieldDB, "04").apply();
        } else if (item == 4) {
            be.setImageResource(R.drawable.ic_chart_areaspline_grey600_48dp);sharedPref.edit().putString(fieldDB, "05").apply();
        } else if (item == 5) {
            be.setImageResource(R.drawable.ic_bell_grey600_48dp);sharedPref.edit().putString(fieldDB, "06").apply();
        } else if (item == 6) {
            be.setImageResource(R.drawable.ic_settings_grey600_48dp);sharedPref.edit().putString(fieldDB, "07").apply();
        } else if (item == 7) {
            be.setImageResource(R.drawable.ic_web_grey600_48dp);sharedPref.edit().putString(fieldDB, "08").apply();
        } else if (item == 8) {
            be.setImageResource(R.drawable.ic_magnify_grey600_48dp);sharedPref.edit().putString(fieldDB, "09").apply();
        } else if (item == 9) {
            be.setImageResource(R.drawable.ic_pencil_grey600_48dp);sharedPref.edit().putString(fieldDB, "10").apply();
        } else if (item == 10) {
            be.setImageResource(R.drawable.ic_check_grey600_48dp);sharedPref.edit().putString(fieldDB, "11").apply();
        } else if (item == 11) {
            be.setImageResource(R.drawable.ic_clock_grey600_48dp);sharedPref.edit().putString(fieldDB, "12").apply();
        } else if (item == 12) {
            be.setImageResource(R.drawable.ic_bookmark_grey600_48dp);sharedPref.edit().putString(fieldDB, "13").apply();
        } else if (item == 13) {
            be.setImageResource(R.drawable.circle_red);sharedPref.edit().putString(fieldDB, "14").apply();
        } else if (item == 14) {
            be.setImageResource(R.drawable.circle_pink);sharedPref.edit().putString(fieldDB, "15").apply();
        } else if (item == 15) {
            be.setImageResource(R.drawable.circle_purple);sharedPref.edit().putString(fieldDB, "16").apply();
        } else if (item == 16) {
            be.setImageResource(R.drawable.circle_blue);sharedPref.edit().putString(fieldDB, "17").apply();
        } else if (item == 17) {
            be.setImageResource(R.drawable.circle_teal);sharedPref.edit().putString(fieldDB, "18").apply();
        } else if (item == 18) {
            be.setImageResource(R.drawable.circle_green);sharedPref.edit().putString(fieldDB, "19").apply();
        } else if (item == 19) {
            be.setImageResource(R.drawable.circle_lime);sharedPref.edit().putString(fieldDB, "20").apply();
        } else if (item == 20) {
            be.setImageResource(R.drawable.circle_yellow);sharedPref.edit().putString(fieldDB, "21").apply();
        } else if (item == 21) {
            be.setImageResource(R.drawable.circle_orange);sharedPref.edit().putString(fieldDB, "22").apply();
        } else if (item == 22) {
            be.setImageResource(R.drawable.circle_brown);sharedPref.edit().putString(fieldDB, "23").apply();
        } else if (item == 23) {
            be.setImageResource(R.drawable.circle_grey);sharedPref.edit().putString(fieldDB, "24").apply();
        }
    }

    public static void createCalendarEvent (Activity activity, String title, String description) {

        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, title);
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, description);
        activity.startActivity(calIntent);
    }

    public static void showKeyboard(final Activity activity, final EditText editText) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                editText.setSelection(editText.length());
            }
        }, 200);
    }

    public static void hideKeyboard(final Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    // Strings, files, ...

    public static File appDir () {
        return  new File(Environment.getExternalStorageDirectory() + "/Android/data/de.baumann.hhsmoodle/");
    }

    public static File newFile () {
        return  new File(appDir() + "/" + newFileName());
    }

    public static String newFileDest () {
        return  ("/Android/data/de.baumann.hhsmoodle/");
    }

    public static String newFileName () {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        return  dateFormat.format(date) + ".jpg";
    }

    public static String createDate () {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return  format.format(date);
    }

    public static String createDateSecond () {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        return  format.format(date);
    }


    // Open files

    public static void openAtt (Activity activity, View view, String fileString) {
        File file = new File(fileString);
        final String fileExtension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
        String text = activity.getString(R.string.toast_extension) + ": " + fileExtension;

        switch (fileExtension) {
            case ".gif":case ".bmp":case ".tiff":case ".svg":case ".png":case ".jpg":case ".jpeg":
                helper_main.openFile(activity, file, "image/*", view);
                break;
            case ".m3u8":case ".mp3":case ".wma":case ".midi":case ".wav":case ".aac":case ".aif":case ".amp3":case ".weba":
                helper_main.openFile(activity, file, "audio/*", view);
                break;
            case ".mpeg":case ".mp4":case ".ogg":case ".webm":case ".qt":case ".3gp":case ".3g2":case ".avi":case ".f4v":
            case ".flv":case ".h261":case ".h263":case ".h264":case ".asf":case ".wmv":
                helper_main.openFile(activity, file, "video/*", view);
                break;
            case ".rtx":case ".csv":case ".txt":case ".vcs":case ".vcf":case ".css":case ".ics":case ".conf":case ".config":case ".java":
                helper_main.openFile(activity, file, "text/*", view);
                break;
            case ".html":
                helper_main.openFile(activity, file, "text/html", view);
                break;
            case ".apk":
                helper_main.openFile(activity, file, "application/vnd.android.package-archive", view);
                break;
            case ".pdf":
                helper_main.openFile(activity, file, "application/pdf", view);
                break;
            case ".doc":
                helper_main.openFile(activity, file, "application/msword", view);
                break;
            case ".xls":
                helper_main.openFile(activity, file, "application/vnd.ms-excel", view);
                break;
            case ".ppt":
                helper_main.openFile(activity, file, "application/vnd.ms-powerpoint", view);
                break;
            case ".docx":
                helper_main.openFile(activity, file, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", view);
                break;
            case ".pptx":
                helper_main.openFile(activity, file, "application/vnd.openxmlformats-officedocument.presentationml.presentation", view);
                break;
            case ".xlsx":
                helper_main.openFile(activity, file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", view);
                break;
            case ".odt":
                helper_main.openFile(activity, file, "application/vnd.oasis.opendocument.text", view);
                break;
            case ".ods":
                helper_main.openFile(activity, file, "application/vnd.oasis.opendocument.spreadsheet", view);
                break;
            case ".odp":
                helper_main.openFile(activity, file, "application/vnd.oasis.opendocument.presentation", view);
                break;
            case ".zip":
                helper_main.openFile(activity, file, "application/zip", view);
                break;
            case ".rar":
                helper_main.openFile(activity, file, "application/x-rar-compressed", view);
                break;
            case ".epub":
                helper_main.openFile(activity, file, "application/epub+zip", view);
                break;
            case ".cbz":
                helper_main.openFile(activity, file, "application/x-cbz", view);
                break;
            case ".cbr":
                helper_main.openFile(activity, file, "application/x-cbr", view);
                break;
            case ".fb2":
                helper_main.openFile(activity, file, "application/x-fb2", view);
                break;
            case ".rtf":
                helper_main.openFile(activity, file, "application/rtf", view);
                break;
            case ".opml":
                helper_main.openFile(activity, file, "application/opml", view);
                break;

            default:
                Snackbar snackbar = Snackbar
                        .make(view, text, Snackbar.LENGTH_LONG);
                snackbar.show();
                break;
        }
    }

    private static void openFile(Activity activity, File file, String string, View view) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(contentUri,string);

        } else {
            intent.setDataAndType(Uri.fromFile(file),string);
        }

        try {
            activity.startActivity (intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(view, R.string.toast_install_app, Snackbar.LENGTH_LONG).show();
        }
    }
}