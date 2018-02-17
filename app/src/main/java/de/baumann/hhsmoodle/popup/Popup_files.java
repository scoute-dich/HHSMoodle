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

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.data_courses.Courses_DbAdapter;
import de.baumann.hhsmoodle.data_files.Files_DbAdapter;
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

        setContentView(R.layout.activity_popup);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        lv = findViewById(R.id.dialogList);

        //calling Notes_DbAdapter
        db = new Files_DbAdapter(Popup_files.this);
        db.open();
        setFilesList();
        onNewIntent(getIntent());
    }

    protected void onNewIntent(final Intent intent) {

        String action = intent.getAction();

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

                        try {
                            File fileToCopy = new File(files_attachment);
                            File destinationFile = new File(helper_main.appDir() + "/" + files_title);

                            if (!destinationFile.exists()) {
                                FileInputStream fis = new FileInputStream(fileToCopy);
                                FileOutputStream fos = new FileOutputStream(destinationFile);

                                byte[] b = new byte[1024];
                                int noOfBytesRead;

                                while((noOfBytesRead = fis.read(b)) != -1)
                                    fos.write(b,0,noOfBytesRead);
                                fis.close();
                                fos.close();
                            }

                            sharedPref.edit().putString("handleTextAttachment", destinationFile.getAbsolutePath()).apply();
                            sharedPref.edit().putString("handleTextAttachmentTitle", files_title).apply();
                        } catch (Exception e) {
                            Snackbar.make(lv, R.string.toast_directory, Snackbar.LENGTH_LONG).show();
                        }

                        finish();
                    }
                }
            });

        }
    }

    private void setFilesList() {

        Popup_files.this.deleteDatabase("files_DB_v01.db");

        String path = sharedPref.getString("files_startFolder",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        File f = new File(path);
        final File[] files = f.listFiles();

        // looping through all items <item>
        if (files.length == 0) {
            Snackbar.make(lv, R.string.toast_files, Snackbar.LENGTH_LONG).show();
        }

        for (File file : files) {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            String file_Name = file.getName();
            String file_Size = getReadableFileSize(file.length());
            String file_date = formatter.format(new Date(file.lastModified()));
            String file_path = file.getAbsolutePath();

            String file_ext;
            if (file.isDirectory()) {
                file_ext = ".";
            } else {
                try {
                    file_ext = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                } catch (Exception e) {
                    file_ext = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/"));
                }
            }

            db.open();
            if(db.isExist(helper_main.secString(file_Name))) {
                Log.i(TAG, "Entry exists" + file_Name);
            } else {
                db.insert(helper_main.secString(file_Name), file_Size, helper_main.secString(file_ext), helper_main.secString(file_path), file_date);
            }
        }

        try {
            db.insert("...", "", "", "", "");
        } catch (Exception e) {
            Snackbar.make(lv, R.string.toast_directory, Snackbar.LENGTH_LONG).show();
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
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(Popup_files.this, layoutstyle,row,column, xml_id, 0) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {

                Cursor row = (Cursor) lv.getItemAtPosition(position);
                final String files_icon = row.getString(row.getColumnIndexOrThrow("files_icon"));
                final String files_attachment = row.getString(row.getColumnIndexOrThrow("files_attachment"));

                View v = super.getView(position, convertView, parent);
                final ImageView iv = v.findViewById(R.id.icon_notes);

                iv.setVisibility(View.VISIBLE);

                if (files_icon.matches("")) {
                    iv.setVisibility(View.INVISIBLE);
                    iv.setImageResource(R.drawable.arrow_up);
                } else if (files_icon.matches("(.)")) {
                    iv.setImageResource(R.drawable.folder);
                } else if (files_icon.matches("(.m3u8|.mp3|.wma|.midi|.wav|.aac|.aif|.amp3|.weba|.ogg)")) {
                    iv.setImageResource(R.drawable.file_music);
                } else if (files_icon.matches("(.mpeg|.mp4|.webm|.qt|.3gp|.3g2|.avi|.flv|.h261|.h263|.h264|.asf|.wmv)")) {
                    iv.setImageResource(R.drawable.file_video);
                } else if(files_icon.matches("(.gif|.bmp|.tiff|.svg|.png|.jpg|.JPG|.jpeg)")) {
                    try {
                        Glide.with(Popup_files.this)
                                .load(files_attachment) // or URI/path
                                .override(76, 76)
                                .centerCrop()
                                .into(iv); //imageView to set thumbnail to
                    } catch (Exception e) {
                        Log.w("HHS_Moodle", "Error load thumbnail", e);
                        iv.setImageResource(R.drawable.file_image);
                    }
                } else if (files_icon.matches("(.vcs|.vcf|.css|.ics|.conf|.config|.java|.html)")) {
                    iv.setImageResource(R.drawable.file_xml);
                } else if (files_icon.matches("(.apk)")) {
                    iv.setImageResource(R.drawable.android);
                } else if (files_icon.matches("(.pdf)")) {
                    iv.setImageResource(R.drawable.file_pdf);
                } else if (files_icon.matches("(.rtf|.csv|.txt|.doc|.xls|.ppt|.docx|.pptx|.xlsx|.odt|.ods|.odp)")) {
                    iv.setImageResource(R.drawable.file_document);
                } else if (files_icon.matches("(.zip|.rar)")) {
                    iv.setImageResource(R.drawable.zip_box);
                } else {
                    iv.setImageResource(R.drawable.file);
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
}