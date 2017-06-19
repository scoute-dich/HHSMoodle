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

    public static void switchIcon (Activity activity, String string, String fieldDB, ImageView iv_icon) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        assert iv_icon != null;

        switch (string) {
            case "3":
                iv_icon.setImageResource(R.drawable.circle_green);
                sharedPref.edit().putString(fieldDB, "3").apply();
                break;
            case "2":
                iv_icon.setImageResource(R.drawable.circle_yellow);
                sharedPref.edit().putString(fieldDB, "2").apply();
                break;
            case "1":
                iv_icon.setImageResource(R.drawable.circle_red);
                sharedPref.edit().putString(fieldDB, "1").apply();
                break;
            default:
                iv_icon.setImageResource(R.drawable.circle_green);
                sharedPref.edit().putString(fieldDB, "3").apply();
                break;
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