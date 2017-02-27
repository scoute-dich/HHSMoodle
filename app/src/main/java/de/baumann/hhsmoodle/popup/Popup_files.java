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

package de.baumann.hhsmoodle.popup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_password;
import de.baumann.hhsmoodle.data_courses.Courses_DbAdapter;
import de.baumann.hhsmoodle.data_files.Files_DbAdapter;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;

import static android.content.ContentValues.TAG;
import static java.lang.String.valueOf;

public class Popup_files extends Activity {

    private ListView lv = null;
    private Files_DbAdapter db;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(Popup_files.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        String pw = sharedPrefSec.getString("protect_PW");

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pw != null  && pw.length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                helper_main.switchToActivity(Popup_files.this, Activity_password.class, false);
            }
        }

        setContentView(R.layout.activity_popup);

        lv = (ListView)findViewById(R.id.dialogList);

        //calling Notes_DbAdapter
        db = new Files_DbAdapter(Popup_files.this);
        db.open();
        setFilesList();
        onNewIntent(getIntent());
    }

    protected void onNewIntent(final Intent intent) {

        String action = intent.getAction();
        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if ("file_chooseText".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    final String files_icon = row2.getString(row2.getColumnIndexOrThrow("files_icon"));
                    final String files_attachment = row2.getString(row2.getColumnIndexOrThrow("files_attachment"));
                    final File pathFile = new File(files_attachment);

                    if(pathFile.isDirectory()) {
                        try {
                            sharedPref.edit().putString("files_startFolder", files_attachment).apply();
                            setFilesList();
                        } catch (Exception e) {
                            Snackbar.make(lv, R.string.toast_directory, Snackbar.LENGTH_LONG).show();
                        }
                    } else if(files_attachment.equals("")) {
                        try {
                            final File pathActual = new File(sharedPref.getString("files_startFolder",
                                    Environment.getExternalStorageDirectory().getPath()));
                            sharedPref.edit().putString("files_startFolder", pathActual.getParent()).apply();
                            setFilesList();
                        } catch (Exception e) {
                            Snackbar.make(lv, R.string.toast_directory, Snackbar.LENGTH_LONG).show();
                        }
                    } else if (files_icon.equals(".txt")){
                        StringBuilder text = new StringBuilder();
                        final String fileName = pathFile.getAbsolutePath().substring(pathFile.getAbsolutePath().lastIndexOf("/")+1);
                        final String fileNameWE = fileName.substring(0, fileName.lastIndexOf("."));

                        final Courses_DbAdapter db = new Courses_DbAdapter(Popup_files.this);
                        db.open();

                        try {
                            BufferedReader br = new BufferedReader(new FileReader(pathFile));
                            String line;

                            while ((line = br.readLine()) != null) {
                                text.append(line);
                                text.append('\n');
                            }
                            br.close();
                        } catch (IOException e) {
                            Snackbar.make(lv, R.string.number_error_read, Snackbar.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                        if(db.isExist(fileNameWE)){
                            Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                        }else{
                            try {
                                String textAdd = text.substring(0, text.length()-1);
                                db.insert(fileNameWE, textAdd, "", "", helper_main.createDate());
                                finish();
                            } catch (Exception e) {
                                Snackbar.make(lv, R.string.number_error_read, Snackbar.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Snackbar.make(lv, R.string.toast_textFile, Snackbar.LENGTH_LONG).show();
                    }
                }
            });

        } else if ("file_chooseAttachment".equals(action)) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                    final String files_attachment = row2.getString(row2.getColumnIndexOrThrow("files_attachment"));
                    final String files_title = row2.getString(row2.getColumnIndexOrThrow("files_title"));
                    final File pathFile = new File(files_attachment);

                    if(pathFile.isDirectory()) {
                        try {
                            sharedPref.edit().putString("files_startFolder", files_attachment).apply();
                            setFilesList();
                        } catch (Exception e) {
                            Snackbar.make(lv, R.string.toast_directory, Snackbar.LENGTH_LONG).show();
                        }
                    } else if(files_attachment.equals("")) {
                        try {
                            final File pathActual = new File(sharedPref.getString("files_startFolder",
                                    Environment.getExternalStorageDirectory().getPath()));
                            sharedPref.edit().putString("files_startFolder", pathActual.getParent()).apply();
                            setFilesList();
                        } catch (Exception e) {
                            Snackbar.make(lv, R.string.toast_directory, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        sharedPref.edit().putString("handleTextAttachment", files_attachment).apply();
                        sharedPref.edit().putString("handleTextAttachmentTitle", files_title).apply();
                        finish();
                    }
                }
            });

        }
    }

    private void setFilesList() {

        Popup_files.this.deleteDatabase("files_DB_v01.db");

        File f = new File(sharedPref.getString("files_startFolder",
                Environment.getExternalStorageDirectory().getPath() + "/HHS_Moodle/"));
        final File[] files = f.listFiles();

        if (files.length == 0) {
            Snackbar.make(lv, R.string.toast_files, Snackbar.LENGTH_LONG).show();
        }

        // looping through all items <item>
        for (File file : files) {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            String file_Name = file.getName().substring(0,1).toUpperCase() + file.getName().substring(1).toLowerCase();
            String file_Size = getReadableFileSize(file.length());
            String file_date = formatter.format(new Date(file.lastModified()));
            String file_path = file.getAbsolutePath();

            String file_ext;
            if (file.isDirectory()) {
                file_ext = ".";
            } else {
                file_ext = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
            }

            db.open();
            if(db.isExist(file_Name)) {
                Log.i(TAG, "Entry exists" + file_Name);
            } else {
                db.insert(file_Name, file_Size, file_ext, file_path, file_date);
            }
        }

        try {
            db.insert("...", "", "", "", "");
        } catch (Exception e) {
            Snackbar snackbar = Snackbar
                    .make(lv, R.string.toast_directory, Snackbar.LENGTH_LONG)
                    .setAction(R.string.toast_yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sharedPref.edit().putInt("keyboard", 0).apply();
                            helper_main.onClose(Popup_files.this);
                        }
                    });
            snackbar.show();
        }

        //display data
        final int layoutstyle=R.layout.list_item_notes;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.textView_create_notes
        };
        String[] column = new String[] {
                "files_title",
                "files_content",
                "files_creation"
        };
        final Cursor row = db.fetchAllData(Popup_files.this);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(Popup_files.this, layoutstyle, row, column, xml_id, 0) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String files_icon = row2.getString(row2.getColumnIndexOrThrow("files_icon"));
                final String files_attachment = row2.getString(row2.getColumnIndexOrThrow("files_attachment"));
                final File pathFile = new File(files_attachment);

                View v = super.getView(position, convertView, parent);
                final ImageView iv = (ImageView) v.findViewById(R.id.icon_notes);

                iv.setVisibility(View.VISIBLE);

                if (pathFile.isDirectory()) {
                    iv.setImageResource(R.drawable.folder);
                } else {
                    switch (files_icon) {
                        case ".gif":
                        case ".bmp":
                        case ".tiff":
                        case ".svg":
                        case ".png":
                        case ".jpg":
                        case ".JPG":
                        case ".jpeg":
                            try {
                                Uri uri = Uri.fromFile(pathFile);
                                Picasso.with(Popup_files.this).load(uri).resize(76, 76).centerCrop().into(iv);
                            } catch (Exception e) {
                                Log.w("HHS_Moodle", "Error Load image", e);
                            }
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
                            iv.setImageResource(R.drawable.file_music);
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
                            iv.setImageResource(R.drawable.file_video);
                            break;
                        case ".vcs":
                        case ".vcf":
                        case ".css":
                        case ".ics":
                        case ".conf":
                        case ".config":
                        case ".java":
                        case ".html":
                            iv.setImageResource(R.drawable.file_xml);
                            break;
                        case ".apk":
                            iv.setImageResource(R.drawable.android);
                            break;
                        case ".pdf":
                            iv.setImageResource(R.drawable.file_pdf);
                            break;
                        case ".rtf":
                        case ".csv":
                        case ".txt":
                        case ".doc":
                        case ".xls":
                        case ".ppt":
                        case ".docx":
                        case ".pptx":
                        case ".xlsx":
                        case ".odt":
                        case ".ods":
                        case ".odp":
                            iv.setImageResource(R.drawable.file_document);
                            break;
                        case ".zip":
                        case ".rar":
                            iv.setImageResource(R.drawable.zip_box);
                            break;
                        case "":
                            iv.setImageResource(R.drawable.arrow_up);
                            break;
                        default:
                            iv.setImageResource(R.drawable.file);
                            break;
                    }
                }
                return v;
            }
        };
        lv.setAdapter(adapter);
    }

    private static String getReadableFileSize(long size) {
        final int BYTES_IN_KILOBYTES = 1024;
        final DecimalFormat dec = new DecimalFormat("###.#");
        final String KILOBYTES = " KB";
        final String MEGABYTES = " MB";
        final String GIGABYTES = " GB";
        float fileSize = 0;
        String suffix = KILOBYTES;

        if (size > BYTES_IN_KILOBYTES) {
            fileSize = size / BYTES_IN_KILOBYTES;
            if (fileSize > BYTES_IN_KILOBYTES) {
                fileSize = fileSize / BYTES_IN_KILOBYTES;
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES;
                    suffix = GIGABYTES;
                } else {
                    suffix = MEGABYTES;
                }
            }
        }
        return valueOf(dec.format(fileSize) + suffix);
    }


    @Override
    public void onBackPressed() {
        helper_main.isClosed(Popup_files.this);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_files.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_files.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(Popup_files.this);
    }
}