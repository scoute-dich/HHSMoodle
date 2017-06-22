/*
    This file is part of the Browser webview app.

    HHS Moodle WebApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HHS Moodle WebApp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Browser webview app.

    If not, see <http://www.gnu.org/licenses/>.
 */

package de.baumann.hhsmoodle.data_files;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_security;

import static android.content.ContentValues.TAG;
import static java.lang.String.valueOf;

public class Files_Fragment extends Fragment {

    //calling variables
    private Files_DbAdapter db;
    private SimpleCursorAdapter adapter;

    private ListView lv = null;
    private EditText filter;
    private SharedPreferences sharedPref;
    private ImageView imgHeader;
    private RelativeLayout filter_layout;
    private ViewPager viewPager;

    private int top;
    private int index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_screen_notes, container, false);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.edit().putString("files_startFolder",
                helper_main.appDir().getAbsolutePath()).apply();

        imgHeader = (ImageView) rootView.findViewById(R.id.imageView_header);
        helper_main.setImageHeader(getActivity(), imgHeader);

        filter_layout = (RelativeLayout) rootView.findViewById(R.id.filter_layout);
        filter_layout.setVisibility(View.GONE);
        lv = (ListView) rootView.findViewById(R.id.listNotes);
        filter = (EditText) rootView.findViewById(R.id.myFilter);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);

        ImageButton ib_hideKeyboard =(ImageButton) rootView.findViewById(R.id.ib_hideKeyboard);
        ib_hideKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filter.getText().length() > 0) {
                    filter.setText("");
                } else {
                    setTitle();
                    helper_main.hideFilter(getActivity(), filter_layout, imgHeader);
                    fillFileList();
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.home);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref.edit().putString("files_startFolder", helper_main.appDir().getAbsolutePath()).apply();
                fillFileList();
            }
        });

        fillFileList();
        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewPager.getCurrentItem() == 5) {
            if (filter_layout.getVisibility() == View.GONE) {
                fillFileList();
            }
        }
    }

    public void doBack() {
        if (filter_layout.getVisibility() == View.VISIBLE) {
            helper_main.hideFilter(getActivity(), filter_layout, imgHeader);
            fillFileList();
        } else {
            helper_main.onClose(getActivity());
        }
    }

    private void isEdited () {
        sharedPref.edit().putString("edit_yes", "true").apply();
        index = lv.getFirstVisiblePosition();
        View v = lv.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());
    }

    public void setFilesList() {

        getActivity().deleteDatabase("files_DB_v01.db");

        //calling Notes_DbAdapter
        db = new Files_DbAdapter(getActivity());
        db.open();

        String folder = sharedPref.getString("folder", "/Android/data/de.baumann.pdf/");

        File f = new File(sharedPref.getString("files_startFolder",
                folder));
        final File[] files = f.listFiles();

        try {
            if (files.length == 0) {
                Snackbar.make(lv, R.string.toast_files, Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Snackbar.make(lv, R.string.toast_directory, Snackbar.LENGTH_LONG).show();
        }

        // looping through all items <item>
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
                    file_ext = ".";
                }
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
        final Cursor row = db.fetchAllData(getActivity());
        adapter = new SimpleCursorAdapter(getActivity(), layoutstyle,row,column, xml_id, 0) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {

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
                        case "":
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    iv.setImageResource(R.drawable.arrow_up);
                                }
                            }, 200);
                            break;
                        case ".gif":case ".bmp":case ".tiff":case ".svg":
                        case ".png":case ".jpg":case ".JPG":case ".jpeg":
                            try {
                                Glide.with(getActivity())
                                        .load(files_attachment) // or URI/path
                                        .override(76, 76)
                                        .centerCrop()
                                        .into(iv); //imageView to set thumbnail to
                            } catch (Exception e) {
                                Log.w("HHS_Moodle", "Error load thumbnail", e);
                                iv.setImageResource(R.drawable.file_image);
                            }
                            break;
                        case ".m3u8":case ".mp3":case ".wma":case ".midi":case ".wav":case ".aac":
                        case ".aif":case ".amp3":case ".weba":case ".ogg":
                            iv.setImageResource(R.drawable.file_music);
                            break;
                        case ".mpeg":case ".mp4":case ".webm":case ".qt":case ".3gp":
                        case ".3g2":case ".avi":case ".f4v":case ".flv":case ".h261":case ".h263":
                        case ".h264":case ".asf":case ".wmv":
                            try {
                                Glide.with(getActivity())
                                        .load(files_attachment) // or URI/path
                                        .override(76, 76)
                                        .centerCrop()
                                        .into(iv); //imageView to set thumbnail to
                            } catch (Exception e) {
                                Log.w("HHS_Moodle", "Error load thumbnail", e);
                                iv.setImageResource(R.drawable.file_video);
                            }
                            break;
                        case ".vcs":case ".vcf":case ".css":case ".ics":case ".conf":case ".config":
                        case ".java":case ".html":
                            iv.setImageResource(R.drawable.file_xml);
                            break;
                        case ".apk":
                            iv.setImageResource(R.drawable.android);
                            break;
                        case ".pdf":
                            iv.setImageResource(R.drawable.file_pdf);
                            break;
                        case ".rtf":case ".csv":case ".txt":
                        case ".doc":case ".xls":case ".ppt":case ".docx":case ".pptx":case ".xlsx":
                        case ".odt":case ".ods":case ".odp":
                            iv.setImageResource(R.drawable.file_document);
                            break;
                        case ".zip":
                        case ".rar":
                            iv.setImageResource(R.drawable.zip_box);
                            break;
                        default:
                            iv.setImageResource(R.drawable.file);
                            break;
                    }
                }
                return v;
            }
        };

        //display data by filter
        final String note_search = sharedPref.getString("filter_filesBY", "files_title");
        sharedPref.edit().putString("filter_filesBY", "files_title").apply();
        filter.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }
        });
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return db.fetchDataByFilter(constraint.toString(),note_search);
            }
        });

        lv.setAdapter(adapter);
        lv.setSelectionFromTop(index, top);
        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                isEdited();
                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
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
                } else {
                    helper_main.openAtt(getActivity(), lv, files_attachment);
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                isEdited();
                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String files_title = row2.getString(row2.getColumnIndexOrThrow("files_title"));
                final String files_attachment = row2.getString(row2.getColumnIndexOrThrow("files_attachment"));

                final File pathFile = new File(files_attachment);

                if (pathFile.isDirectory()) {

                    Snackbar snackbar = Snackbar
                            .make(lv, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                            .setAction(R.string.toast_yes, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    sharedPref.edit().putString("files_startFolder", pathFile.getParent()).apply();
                                    deleteRecursive(pathFile);
                                    setFilesList();
                                }
                            });
                    snackbar.show();

                } else {

                    final CharSequence[] options = {
                            getString(R.string.choose_menu_2),
                            getString(R.string.choose_menu_3),
                            getString(R.string.choose_menu_4)};

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    dialog.setItems(options, new DialogInterface.OnClickListener() {
                        @SuppressWarnings("ResultOfMethodCallIgnored")
                        @Override
                        public void onClick(DialogInterface dialog, int item) {

                            if (options[item].equals(getString(R.string.choose_menu_2))) {

                                if (pathFile.exists()) {
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("image/png");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, files_title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, files_title);
                                    Uri bmpUri = Uri.fromFile(pathFile);
                                    sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.app_share_file))));
                                }
                            }
                            if (options[item].equals(getString(R.string.choose_menu_4))) {
                                Snackbar snackbar = Snackbar
                                        .make(lv, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.toast_yes, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                pathFile.delete();
                                                setFilesList();
                                            }
                                        });
                                snackbar.show();
                            }
                            if (options[item].equals(getString(R.string.choose_menu_3))) {

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                                View dialogView = View.inflate(getActivity(), R.layout.dialog_edit_file, null);

                                final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
                                edit_title.setText(files_title);

                                builder.setView(dialogView);
                                builder.setTitle(R.string.choose_title);
                                builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        String inputTag = edit_title.getText().toString().trim();

                                        File dir = pathFile.getParentFile();
                                        File to = new File(dir,inputTag);

                                        pathFile.renameTo(to);
                                        pathFile.delete();
                                        setFilesList();
                                    }
                                });
                                builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog dialog2 = builder.create();
                                // Display the custom alert dialog on interface
                                dialog2.show();
                                helper_main.showKeyboard(getActivity(),edit_title);
                            }
                        }
                    });
                    dialog.show();
                }

                return true;
            }
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
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
    public void onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.sort_attachment).setVisible(false);
        menu.findItem(R.id.sort_notification).setVisible(false);
        menu.findItem(R.id.sort_icon).setVisible(false);
        menu.findItem(R.id.sort_icon).setVisible(false);
        menu.findItem(R.id.filter_content).setVisible(false);
        menu.findItem(R.id.filter_att).setVisible(false);
        menu.findItem(R.id.filter_url).setVisible(false);
        menu.findItem(R.id.filter_teacher).setVisible(false);
        menu.findItem(R.id.filter_room).setVisible(false);
        menu.findItem(R.id.filter_course).setVisible(false);
        menu.findItem(R.id.filter_subject).setVisible(false);
        menu.findItem(R.id.filter_title_own).setVisible(false);
        setTitle();
        fillFileList();
        helper_main.hideKeyboard(getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String search;

        switch (item.getItemId()) {

            case R.id.action_help:
                helper_main.switchToActivity(getActivity(), Files_Help.class, false);
                return true;

            case R.id.filter_title:
                helper_main.changeFilter(getActivity(), "filter_filesBY", "files_title");
                fillFileList();
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        "", getString(R.string.action_filter_title), true);
                return true;
            case R.id.filter_ext:
                helper_main.changeFilter(getActivity(), "filter_filesBY", "files_icon");
                fillFileList();
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        "", getString(R.string.action_filter_ext), true);
                return true;

            case R.id.filter_today:
                helper_main.changeFilter(getActivity(), "filter_filesBY", "files_creation");
                fillFileList();
                search = dateFormat.format(cal.getTime());
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        search, getString(R.string.action_filter_create), false);
                return true;
            case R.id.filter_yesterday:
                helper_main.changeFilter(getActivity(), "filter_filesBY", "files_creation");
                fillFileList();
                cal.add(Calendar.DATE, -1);
                search = dateFormat.format(cal.getTime());
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        search, getString(R.string.action_filter_create), false);
                return true;
            case R.id.filter_before:
                helper_main.changeFilter(getActivity(), "filter_filesBY", "files_creation");
                fillFileList();
                cal.add(Calendar.DATE, -2);
                search = dateFormat.format(cal.getTime());
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        search, getString(R.string.action_filter_create), false);
                return true;
            case R.id.filter_month:
                helper_main.changeFilter(getActivity(), "filter_filesBY", "files_creation");
                setFilesList();
                DateFormat dateFormatMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                search = dateFormatMonth.format(cal.getTime());
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        search, getString(R.string.action_filter_create), false);
                return true;
            case R.id.filter_own:
                helper_main.changeFilter(getActivity(), "filter_filesBY", "files_creation");
                setFilesList();
                helper_main.showFilter(getActivity(), filter_layout, imgHeader, filter,
                        "", getString(R.string.action_filter_create), true);
                return true;

            case R.id.sort_title:
                sharedPref.edit().putString("sortDBF", "title").apply();
                setTitle();
                setFilesList();
                return true;
            case R.id.sort_ext:
                sharedPref.edit().putString("sortDBF", "file_ext").apply();
                setTitle();
                setFilesList();
                return true;
            case R.id.sort_creation:
                sharedPref.edit().putString("sortDBF", "file_date").apply();
                setTitle();
                setFilesList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setTitle () {
        if (sharedPref.getString("sortDBF", "title").equals("title")) {
            getActivity().setTitle(getString(R.string.choose_titleMain) + " | " + getString(R.string.sort_title));
        } else if (sharedPref.getString("sortDBF", "title").equals("file_ext")) {
            getActivity().setTitle(getString(R.string.choose_titleMain) + " | " + getString(R.string.sort_extension));
        } else {
            getActivity().setTitle(getString(R.string.choose_titleMain) + " | " + getString(R.string.sort_date));
        }
    }

    private void fillFileList() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int hasWRITE_EXTERNAL_STORAGE = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWRITE_EXTERNAL_STORAGE == PackageManager.PERMISSION_GRANTED) {
                setFilesList();
            } else {
                helper_security.grantPermissions(getActivity());
            }
        } else {
            setFilesList();
        }
    }
}