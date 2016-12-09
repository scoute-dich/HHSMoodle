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
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.popup.Popup_camera;
import filechooser.ChooserDialog;

public class helper_notes {

    public static void editNote (final Activity from) {

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(from);
        final Button attachment;
        final ImageButton attachmentRem;
        final ImageButton attachmentCam;
        final EditText titleInput;
        final EditText textInput;
        final String priority = sharedPref.getString("handleTextIcon", "");

        LayoutInflater inflater = from.getLayoutInflater();

        final ViewGroup nullParent = null;
        View dialogView = inflater.inflate(R.layout.dialog_editnote, nullParent);

        String file = sharedPref.getString("handleTextAttachment", "");
        final String attName = file.substring(file.lastIndexOf("/")+1);

        attachmentRem = (ImageButton) dialogView.findViewById(R.id.button_rem);
        attachmentRem.setImageResource(R.drawable.close_red);
        attachment = (Button) dialogView.findViewById(R.id.button_att);
        attachmentCam = (ImageButton) dialogView.findViewById(R.id.button_cam);
        attachmentCam.setImageResource(R.drawable.camera);
        if (attName.equals("")) {
            attachment.setText(R.string.choose_att);
            attachmentRem.setVisibility(View.GONE);
            attachmentCam.setVisibility(View.VISIBLE);
        } else {
            attachment.setText(attName);
            attachmentRem.setVisibility(View.VISIBLE);
            attachmentCam.setVisibility(View.GONE);
        }
        File file2 = new File(file);
        if (!file2.exists()) {
            attachment.setText(R.string.choose_att);
            attachmentRem.setVisibility(View.GONE);
            attachmentCam.setVisibility(View.VISIBLE);
        }

        titleInput = (EditText) dialogView.findViewById(R.id.note_title_input);
        textInput = (EditText) dialogView.findViewById(R.id.note_text_input);
        titleInput.setText(sharedPref.getString("handleTextTitle", ""));
        titleInput.setSelection(titleInput.getText().length());
        textInput.setText(sharedPref.getString("handleTextText", ""));
        textInput.setSelection(textInput.getText().length());

        attachment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String startDir = Environment.getExternalStorageDirectory() + "/HHS_Moodle/";
                new ChooserDialog().with(from)
                        .withStartFile(startDir)
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(final File pathFile) {
                                final String fileName = pathFile.getAbsolutePath();
                                String attName = fileName.substring(fileName.lastIndexOf("/")+1);
                                attachment.setText(attName);
                                attachmentRem.setVisibility(View.VISIBLE);
                                attachmentCam.setVisibility(View.GONE);
                                sharedPref.edit().putString("handleTextAttachment", fileName).apply();
                            }
                        })
                        .build()
                        .show();
            }
        });

        attachmentRem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sharedPref.edit().putString("handleTextAttachment", "").apply();
                attachment.setText(R.string.choose_att);
                attachmentRem.setVisibility(View.GONE);
                attachmentCam.setVisibility(View.VISIBLE);
            }
        });

        attachmentCam.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                InputMethodManager imm = (InputMethodManager)from.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(titleInput.getWindowToken(), 0);
                helper_main.switchToActivity(from, Popup_camera.class, "", false);
            }
        });

        new Handler().postDelayed(new Runnable() {
            public void run() {
                helper_main.showKeyboard(from,titleInput);
            }
        }, 200);

        final ImageButton be = (ImageButton) dialogView.findViewById(R.id.imageButtonPri);
        assert be != null;

        switch (priority) {
            case "1":
                be.setImageResource(R.drawable.circle_green);
                sharedPref.edit()
                        .putString("handleTextIcon", "1")
                        .apply();
                break;
            case "2":
                be.setImageResource(R.drawable.circle_yellow);
                sharedPref.edit()
                        .putString("handleTextIcon", "2")
                        .apply();
                break;
            case "3":
                be.setImageResource(R.drawable.circle_red);
                sharedPref.edit()
                        .putString("handleTextIcon", "3")
                        .apply();
                break;

            default:
                be.setImageResource(R.drawable.circle_green);
                sharedPref.edit()
                        .putString("handleTextIcon", "1")
                        .apply();
                break;
        }

        be.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final helper_notes.Item[] items = {
                        new Item(from.getString(R.string.note_priority_0), R.drawable.circle_green),
                        new Item(from.getString(R.string.note_priority_1), R.drawable.circle_yellow),
                        new Item(from.getString(R.string.note_priority_2), R.drawable.circle_red),
                };

                ListAdapter adapter = new ArrayAdapter<helper_notes.Item>(
                        from,
                        android.R.layout.select_dialog_item,
                        android.R.id.text1,
                        items){
                    @NonNull
                    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                        //Use super class to create the View
                        View v = super.getView(position, convertView, parent);
                        TextView tv = (TextView)v.findViewById(android.R.id.text1);
                        tv.setTextSize(18);
                        tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                        //Add margin between image and text (support various screen densities)
                        int dp5 = (int) (24 * from.getResources().getDisplayMetrics().density + 0.5f);
                        tv.setCompoundDrawablePadding(dp5);

                        return v;
                    }
                };

                new android.app.AlertDialog.Builder(from)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0) {
                                    be.setImageResource(R.drawable.circle_green);
                                    sharedPref.edit()
                                            .putString("handleTextIcon", "1")
                                            .apply();
                                } else if (item == 1) {
                                    be.setImageResource(R.drawable.circle_yellow);
                                    sharedPref.edit()
                                            .putString("handleTextIcon", "2")
                                            .apply();
                                } else if (item == 2) {
                                    be.setImageResource(R.drawable.circle_red);
                                    sharedPref.edit()
                                            .putString("handleTextIcon", "3")
                                            .apply();
                                }
                            }
                        }).show();
            }
        });

        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(from)
                .setTitle(R.string.note_edit)
                .setView(dialogView)
                .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String seqno = sharedPref.getString("handleTextSeqno", "");

                        try {

                            final Database_Notes db = new Database_Notes(from);
                            String inputTitle = titleInput.getText().toString().trim();
                            String inputContent = textInput.getText().toString().trim();
                            String attachment = sharedPref.getString("handleTextAttachment", "");
                            String create = sharedPref.getString("handleTextCreate", "");

                            db.addBookmark(inputTitle, inputContent, sharedPref.getString("handleTextIcon", ""), attachment, create);
                            db.close();

                            if (seqno.length() > 0) {
                                db.deleteNote((Integer.parseInt(seqno)));
                                sharedPref.edit()
                                        .putString("handleTextSeqno", "")
                                        .apply();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sharedPref.edit()
                                .putString("handleTextTitle", "")
                                .putString("handleTextText", "")
                                .putString("handleTextIcon", "")
                                .putString("handleTextAttachment", "")
                                .putString("handleTextCreate", "")
                                .apply();
                        helper_notes.setNotesList(from);
                        if (sharedPref.getString("fromPopup", "").equals("0")) {
                            sharedPref.edit()
                                    .putString("fromPopup", "")
                                    .apply();
                            from.recreate();
                        }

                    }
                })
                .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        sharedPref.edit()
                                .putString("handleTextTitle", "")
                                .putString("handleTextText", "")
                                .putString("handleTextIcon", "")
                                .putString("handleTextCreate", "")
                                .apply();
                        dialog.cancel();
                    }
                });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialog) {
                if (sharedPref.getString("fromPopup", "").equals("0")) {
                    sharedPref.edit()
                            .putString("fromPopup", "")
                            .apply();
                }
            }
        });
        dialog.show();
    }

    private static class Item{
        public final String text;
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

    private static void setNotesList(final Activity from) {

        final ListView listView = (ListView)from.findViewById(R.id.notes);

        ArrayList<HashMap<String,String>> mapList = new ArrayList<>();

        try {
            Database_Notes db = new Database_Notes(from);
            ArrayList<String[]> bookmarkList = new ArrayList<>();
            db.getBookmarks(bookmarkList, from);
            if (bookmarkList.size() == 0) {
                db.loadInitialData();
                db.getBookmarks(bookmarkList, from);
            }
            db.close();

            for (String[] strAry : bookmarkList) {
                HashMap<String, String> map = new HashMap<>();
                map.put("seqno", strAry[0]);
                map.put("title", strAry[1]);
                map.put("cont", strAry[2]);
                map.put("icon", strAry[3]);
                map.put("attachment", strAry[4]);
                map.put("createDate", strAry[5]);
                mapList.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    from,
                    mapList,
                    R.layout.list_item_notes,
                    new String[] {"title", "cont", "createDate"},
                    new int[] {R.id.textView_title_notes, R.id.textView_des_notes, R.id.textView_create_notes}
            ) {
                @Override
                public View getView (final int position, View convertView, ViewGroup parent) {

                    @SuppressWarnings("unchecked")
                    HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                    final String title = map.get("title");
                    final String cont = map.get("cont");
                    final String seqnoStr = map.get("seqno");
                    final String icon = map.get("icon");
                    final String attachment = map.get("attachment");
                    final String create = map.get("createDate");

                    View v = super.getView(position, convertView, parent);
                    ImageView i=(ImageView) v.findViewById(R.id.icon_notes);
                    ImageView i2=(ImageView) v.findViewById(R.id.att_notes);

                    switch (icon) {
                        case "1":
                            i.setImageResource(R.drawable.circle_green);
                            break;
                        case "2":
                            i.setImageResource(R.drawable.circle_yellow);
                            break;
                        case "3":
                            i.setImageResource(R.drawable.circle_red);
                            break;
                    }
                    switch (attachment) {
                        case "":
                            i2.setVisibility(View.GONE);
                            break;
                        default:
                            i2.setVisibility(View.VISIBLE);
                            i2.setImageResource(R.drawable.ic_attachment);
                            break;
                    }

                    File file = new File(attachment);
                    if (!file.exists()) {
                        i2.setVisibility(View.GONE);
                    }

                    i.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            final helper_notes.Item[] items = {
                                    new Item(from.getString(R.string.note_priority_0), R.drawable.circle_green),
                                    new Item(from.getString(R.string.note_priority_1), R.drawable.circle_yellow),
                                    new Item(from.getString(R.string.note_priority_2), R.drawable.circle_red),
                            };

                            ListAdapter adapter = new ArrayAdapter<helper_notes.Item>(
                                    from,
                                    android.R.layout.select_dialog_item,
                                    android.R.id.text1,
                                    items){
                                @NonNull
                                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                                    //Use super class to create the View
                                    View v = super.getView(position, convertView, parent);
                                    TextView tv = (TextView)v.findViewById(android.R.id.text1);
                                    tv.setTextSize(18);
                                    tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                                    //Add margin between image and text (support various screen densities)
                                    int dp5 = (int) (5 * from.getResources().getDisplayMetrics().density + 0.5f);
                                    tv.setCompoundDrawablePadding(dp5);

                                    return v;
                                }
                            };

                            new AlertDialog.Builder(from)
                                    .setAdapter(adapter, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int item) {
                                            if (item == 0) {
                                                try {
                                                    final Database_Notes db = new Database_Notes(from);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.addBookmark(title, cont, "1", attachment, create);
                                                    db.close();
                                                    setNotesList(from);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else if (item == 1) {
                                                try {
                                                    final Database_Notes db = new Database_Notes(from);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.addBookmark(title, cont, "2", attachment, create);
                                                    db.close();
                                                    setNotesList(from);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else if (item == 2) {
                                                try {
                                                    final Database_Notes db = new Database_Notes(from);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.addBookmark(title, cont, "3", attachment, create);
                                                    db.close();
                                                    setNotesList(from);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }).show();
                        }
                    });

                    i2.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            File file = new File(attachment);
                            final String fileExtension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                            String text = (from.getString(R.string.toast_extension) + ": " + fileExtension);

                            switch (fileExtension) {
                                case ".gif":
                                case ".bmp":
                                case ".tiff":
                                case ".svg":
                                case ".png":
                                case ".jpg":
                                case ".jpeg":
                                    helper_main.openFile(from, file, "image/*", listView);
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
                                    helper_main.openFile(from, file, "audio/*", listView);
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
                                    helper_main.openFile(from, file, "video/*", listView);
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
                                    helper_main.openFile(from, file, "text/*", listView);
                                    break;
                                case ".html":
                                    helper_main.openFile(from, file, "text/html", listView);
                                    break;
                                case ".apk":
                                    helper_main.openFile(from, file, "application/vnd.android.package-archive", listView);
                                    break;
                                case ".pdf":
                                    helper_main.openFile(from, file, "application/pdf", listView);
                                    break;
                                case ".doc":
                                    helper_main.openFile(from, file, "application/msword", listView);
                                    break;
                                case ".xls":
                                    helper_main.openFile(from, file, "application/vnd.ms-excel", listView);
                                    break;
                                case ".ppt":
                                    helper_main.openFile(from, file, "application/vnd.ms-powerpoint", listView);
                                    break;
                                case ".docx":
                                    helper_main.openFile(from, file, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", listView);
                                    break;
                                case ".pptx":
                                    helper_main.openFile(from, file, "application/vnd.openxmlformats-officedocument.presentationml.presentation", listView);
                                    break;
                                case ".xlsx":
                                    helper_main.openFile(from, file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", listView);
                                    break;
                                case ".odt":
                                    helper_main.openFile(from, file, "application/vnd.oasis.opendocument.text", listView);
                                    break;
                                case ".ods":
                                    helper_main.openFile(from, file, "application/vnd.oasis.opendocument.spreadsheet", listView);
                                    break;
                                case ".odp":
                                    helper_main.openFile(from, file, "application/vnd.oasis.opendocument.presentation", listView);
                                    break;
                                case ".zip":
                                    helper_main.openFile(from, file, "application/zip", listView);
                                    break;
                                case ".rar":
                                    helper_main.openFile(from, file, "application/x-rar-compressed", listView);
                                    break;
                                case ".epub":
                                    helper_main.openFile(from, file, "application/epub+zip", listView);
                                    break;
                                case ".cbz":
                                    helper_main.openFile(from, file, "application/x-cbz", listView);
                                    break;
                                case ".cbr":
                                    helper_main.openFile(from, file, "application/x-cbr", listView);
                                    break;
                                case ".fb2":
                                    helper_main.openFile(from, file, "application/x-fb2", listView);
                                    break;
                                case ".rtf":
                                    helper_main.openFile(from, file, "application/rtf", listView);
                                    break;
                                case ".opml":
                                    helper_main.openFile(from, file, "application/opml", listView);
                                    break;

                                default:
                                    Toast.makeText(from, text, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });

                    return v;
                }
            };

            if (listView != null) {
                listView.setAdapter(simpleAdapter);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    } 
}
