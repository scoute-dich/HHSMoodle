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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static String protect;
    private static SharedPreferences sharedPref;

    public static void changeFilter (String filter, String filterBY) {
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


    public static void checkPin (final Activity activity) {

        PreferenceManager.setDefaultValues(activity, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(activity, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        protect = sharedPrefSec.getString("protect_PW");

        if (protect != null  && protect.length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.YourStyle);
            final View dialogView = View.inflate(activity, R.layout.dialog_password, null);

            final TextView text = (TextView) dialogView.findViewById(R.id.pass_userPin);

            Button ib0 = (Button) dialogView.findViewById(R.id.button0);
            assert ib0 != null;
            ib0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "0");
                }
            });

            Button ib1 = (Button) dialogView.findViewById(R.id.button1);
            assert ib1 != null;
            ib1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "1");
                }
            });

            Button ib2 = (Button) dialogView.findViewById(R.id.button2);
            assert ib2 != null;
            ib2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "2");
                }
            });

            Button ib3 = (Button) dialogView.findViewById(R.id.button3);
            assert ib3 != null;
            ib3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "3");
                }
            });

            Button ib4 = (Button) dialogView.findViewById(R.id.button4);
            assert ib4 != null;
            ib4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "4");
                }
            });

            Button ib5 = (Button) dialogView.findViewById(R.id.button5);
            assert ib5 != null;
            ib5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "5");
                }
            });

            Button ib6 = (Button) dialogView.findViewById(R.id.button6);
            assert ib6 != null;
            ib6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "6");
                }
            });

            Button ib7 = (Button) dialogView.findViewById(R.id.button7);
            assert ib7 != null;
            ib7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "7");
                }
            });

            Button ib8 = (Button) dialogView.findViewById(R.id.button8);
            assert ib8 != null;
            ib8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "8");
                }
            });

            Button ib9 = (Button) dialogView.findViewById(R.id.button9);
            assert ib9 != null;
            ib9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterNum(dialogView, "9");
                }
            });


            ImageButton enter = (ImageButton) dialogView.findViewById(R.id.imageButtonEnter);
            assert enter != null;

            final ImageButton cancel = (ImageButton) dialogView.findViewById(R.id.imageButtonCancel);
            assert cancel != null;
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    text.setText("");
                }
            });

            final Button clear = (Button) dialogView.findViewById(R.id.buttonReset);
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
        TextView text = (TextView) view.findViewById(R.id.pass_userPin);
        String textNow = text.getText().toString().trim();
        String pin = textNow + number;
        text.setText(pin);
    }

    public static void grantPermissions(final Activity activity) {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (sharedPref.getString ("show_permission", "true").equals("true")){
                int hasWRITE_EXTERNAL_STORAGE = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                    if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        new android.app.AlertDialog.Builder(activity)
                                .setTitle(R.string.app_permissions_title)
                                .setMessage(helper_main.textSpannable(activity.getString(R.string.app_permissions)))
                                .setNeutralButton(R.string.toast_notAgain, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        sharedPref.edit().putString("show_permission", "false").apply();
                                    }
                                })
                                .setPositiveButton(activity.getString(R.string.toast_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (android.os.Build.VERSION.SDK_INT >= 23)
                                            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                    REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                })
                                .setNegativeButton(activity.getString(R.string.toast_cancel), null)
                                .show();
                        return;
                    }
                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        }
    }

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

            try {helper_encryption.encryptBackup(activity, "/bookmarks_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            try {helper_encryption.encryptBackup(activity, "/courses_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            try {helper_encryption.encryptBackup(activity, "/notes_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            try {helper_encryption.encryptBackup(activity, "/random_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            try {helper_encryption.encryptBackup(activity, "/subject_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            try {helper_encryption.encryptBackup(activity, "/schedule_DB_v01.db");} catch (Exception e) {e.printStackTrace();}
            try {helper_encryption.encryptBackup(activity, "/todo_DB_v01.db");} catch (Exception e) {e.printStackTrace();}

            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(activity.getString(R.string.app_close));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    sharedPref.edit().putString("loadURL", "").apply();
                    helper_encryption.encryptDatabases(activity);
                    if (progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    activity.finishAffinity();
                }
            }, 1500);

        } else {
            sharedPref.edit().putString("loadURL", "").apply();
            helper_encryption.encryptDatabases(activity);
            activity.finishAffinity();
        }
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

    @SuppressWarnings("SameParameterValue")
    public static void switchToActivity(Activity activity, Class to, boolean finishActivity) {
        Intent intent = new Intent(activity, to);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
        if (finishActivity) {
            activity.finish();
        }
    }

    public static File newFile () {
        return  new File(Environment.getExternalStorageDirectory() + newFileDest() + newFileName());
    }

    public static String newFileDest () {
        return  ("/HHS_Moodle/");
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

    public static void showKeyboard(final Activity activity, final EditText editText) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                editText.setSelection(editText.length());
            }
        }, 200);
    }

    public static void openAtt (Activity activity, View view, String fileString) {
        File file = new File(fileString);
        final String fileExtension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
        String text = activity.getString(R.string.toast_extension) + ": " + fileExtension;

        switch (fileExtension) {
            case ".gif":
            case ".bmp":
            case ".tiff":
            case ".svg":
            case ".png":
            case ".jpg":
            case ".jpeg":
                helper_main.openFile(activity, file, "image/*", view);
                break;
            case ".m3u8":
            case ".mp3":
            case ".wma":
            case ".midi":
            case ".wav":
            case ".aac":
            case ".aif":
            case ".amp3":
            case ".weba":
                helper_main.openFile(activity, file, "audio/*", view);
                break;
            case ".mpeg":
            case ".mp4":
            case ".ogg":
            case ".webm":
            case ".qt":
            case ".3gp":
            case ".3g2":
            case ".avi":
            case ".f4v":
            case ".flv":
            case ".h261":
            case ".h263":
            case ".h264":
            case ".asf":
            case ".wmv":
                helper_main.openFile(activity, file, "video/*", view);
                break;
            case ".rtx":
            case ".csv":
            case ".txt":
            case ".vcs":
            case ".vcf":
            case ".css":
            case ".ics":
            case ".conf":
            case ".config":
            case ".java":
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

    public static void setImageHeader (Activity activity, ImageView imageView) {

        if(imageView != null) {
            TypedArray images = activity.getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imageView.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }
    }

    public static void createCalendarEvent (Activity activity, String title, String description) {

        Intent calIntent = new Intent(Intent.ACTION_INSERT);
        calIntent.setType("vnd.android.cursor.item/event");
        calIntent.putExtra(CalendarContract.Events.TITLE, title);
        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, description);
        activity.startActivity(calIntent);
    }
}