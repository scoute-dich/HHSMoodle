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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_password;
import de.baumann.hhsmoodle.data_notes.Notes_DbAdapter;
import de.baumann.hhsmoodle.data_todo.Todo_DbAdapter;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;

public class Popup_note extends Activity {

    //calling variables
    private Notes_DbAdapter db;
    private ListView lv = null;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(Popup_note.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        String pw = sharedPrefSec.getString("protect_PW");

        PreferenceManager.setDefaultValues(Popup_note.this, R.xml.user_settings, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(Popup_note.this);

        if (pw != null  && pw.length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                helper_main.switchToActivity(Popup_note.this, Activity_password.class, false);
            }
        }

        setContentView(R.layout.activity_popup);
        lv = (ListView) findViewById(R.id.dialogList);

        //calling Notes_DbAdapter
        db = new Notes_DbAdapter(Popup_note.this);
        db.open();

        setNotesList();
    }

    private void setNotesList() {

        //display data
        final int layoutstyle=R.layout.list_item_notes;
        int[] xml_id = new int[] {
                R.id.textView_title_notes,
                R.id.textView_des_notes,
                R.id.textView_create_notes
        };
        String[] column = new String[] {
                "note_title",
                "note_content",
                "note_creation"
        };
        final String search = sharedPref.getString("filter_note_subject", "");
        final Cursor row = db.fetchDataByFilter(search, "note_title");
        
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(Popup_note.this, layoutstyle,row,column, xml_id, 0) {
            @Override
            public View getView (final int position, View convertView, ViewGroup parent) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String note_title = row2.getString(row2.getColumnIndexOrThrow("note_title"));
                final String note_content = row2.getString(row2.getColumnIndexOrThrow("note_content"));
                final String note_icon = row2.getString(row2.getColumnIndexOrThrow("note_icon"));
                final String note_attachment = row2.getString(row2.getColumnIndexOrThrow("note_attachment"));
                final String note_creation = row2.getString(row2.getColumnIndexOrThrow("note_creation"));

                View v = super.getView(position, convertView, parent);
                ImageView iv_icon = (ImageView) v.findViewById(R.id.icon_notes);
                ImageView iv_attachment = (ImageView) v.findViewById(R.id.att_notes);

                switch (note_icon) {
                    case "3":
                        iv_icon.setImageResource(R.drawable.circle_green);
                        break;
                    case "2":
                        iv_icon.setImageResource(R.drawable.circle_yellow);
                        break;
                    case "1":
                        iv_icon.setImageResource(R.drawable.circle_red);
                        break;
                }

                switch (note_attachment) {
                    case "":
                        iv_attachment.setVisibility(View.GONE);
                        break;
                    default:
                        iv_attachment.setVisibility(View.VISIBLE);
                        iv_attachment.setImageResource(R.drawable.ic_attachment);
                        break;
                }

                File file = new File(note_attachment);
                if (!file.exists()) {
                    iv_attachment.setVisibility(View.GONE);
                }

                iv_icon.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        final Item[] items = {
                                new Item(getString(R.string.note_priority_0), R.drawable.circle_green),
                                new Item(getString(R.string.note_priority_1), R.drawable.circle_yellow),
                                new Item(getString(R.string.note_priority_2), R.drawable.circle_red),
                        };

                        ListAdapter adapter = new ArrayAdapter<Item>(
                                Popup_note.this,
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
                                int dp5 = (int) (24 * getResources().getDisplayMetrics().density + 0.5f);
                                tv.setCompoundDrawablePadding(dp5);

                                return v;
                            }
                        };

                        new AlertDialog.Builder(Popup_note.this)
                                .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                })
                                .setAdapter(adapter, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int item) {
                                        if (item == 0) {
                                            db.update(Integer.parseInt(_id),note_title, note_content, "3", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 1) {
                                            db.update(Integer.parseInt(_id),note_title, note_content, "2", note_attachment, note_creation);
                                            setNotesList();
                                        } else if (item == 2) {
                                            db.update(Integer.parseInt(_id),note_title, note_content, "1", note_attachment, note_creation);
                                            setNotesList();
                                        }
                                    }
                                }).show();
                    }
                });
                iv_attachment.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        helper_main.openAtt(Popup_note.this, lv, note_attachment);
                    }
                });
                return v;
            }
        };

        lv.setAdapter(adapter);
        //onClick function
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String note_title = row2.getString(row2.getColumnIndexOrThrow("note_title"));
                final String note_content = row2.getString(row2.getColumnIndexOrThrow("note_content"));
                final String note_icon = row2.getString(row2.getColumnIndexOrThrow("note_icon"));
                final String note_attachment = row2.getString(row2.getColumnIndexOrThrow("note_attachment"));
                final String note_creation = row2.getString(row2.getColumnIndexOrThrow("note_creation"));

                final Button attachment2;
                final TextView textInput;

                LayoutInflater inflater = Popup_note.this.getLayoutInflater();

                final ViewGroup nullParent = null;
                final View dialogView = inflater.inflate(R.layout.dialog_note_show, nullParent);

                final String attName = note_attachment.substring(note_attachment.lastIndexOf("/")+1);
                final String att = getString(R.string.app_att) + ": " + attName;

                attachment2 = (Button) dialogView.findViewById(R.id.button_att);
                if (attName.equals("")) {
                    attachment2.setVisibility(View.GONE);
                } else {
                    attachment2.setText(att);
                }
                File file2 = new File(note_attachment);
                if (!file2.exists()) {
                    attachment2.setVisibility(View.GONE);
                }

                textInput = (TextView) dialogView.findViewById(R.id.note_text_input);
                textInput.setText(note_content);
                Linkify.addLinks(textInput, Linkify.WEB_URLS);

                attachment2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        helper_main.openAtt(Popup_note.this, lv, note_attachment);
                    }
                });

                final ImageView be = (ImageView) dialogView.findViewById(R.id.imageButtonPri);

                switch (note_icon) {
                    case "3":
                        be.setImageResource(R.drawable.circle_green);
                        break;
                    case "2":
                        be.setImageResource(R.drawable.circle_yellow);
                        break;
                    case "1":
                        be.setImageResource(R.drawable.circle_red);
                        break;
                }

                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Popup_note.this)
                        .setTitle(note_title)
                        .setView(dialogView)
                        .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(R.string.note_edit, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                sharedPref.edit()
                                        .putString("handleTextTitle", note_title)
                                        .putString("handleTextText", note_content)
                                        .putString("handleTextIcon", note_icon)
                                        .putString("handleTextSeqno", _id)
                                        .putString("handleTextAttachment", note_attachment)
                                        .putString("handleTextCreate", note_creation)
                                        .apply();
                                editNote(Popup_note.this);
                            }
                        });
                dialog.show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor row2 = (Cursor) lv.getItemAtPosition(position);
                final String _id = row2.getString(row2.getColumnIndexOrThrow("_id"));
                final String note_title = row2.getString(row2.getColumnIndexOrThrow("note_title"));
                final String note_content = row2.getString(row2.getColumnIndexOrThrow("note_content"));
                final String note_icon = row2.getString(row2.getColumnIndexOrThrow("note_icon"));
                final String note_attachment = row2.getString(row2.getColumnIndexOrThrow("note_attachment"));
                final String note_creation = row2.getString(row2.getColumnIndexOrThrow("note_creation"));

                final CharSequence[] options = {
                        getString(R.string.note_edit),
                        getString(R.string.note_share),
                        getString(R.string.todo_menu),
                        getString(R.string.bookmark_createEvent),
                        getString(R.string.note_remove_note)};
                new AlertDialog.Builder(Popup_note.this)
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(getString(R.string.note_edit))) {
                                    sharedPref.edit()
                                            .putString("handleTextTitle", note_title)
                                            .putString("handleTextText", note_content)
                                            .putString("handleTextIcon", note_icon)
                                            .putString("handleTextSeqno", _id)
                                            .putString("handleTextAttachment", note_attachment)
                                            .putString("handleTextCreate", note_creation)
                                            .apply();
                                    editNote(Popup_note.this);
                                }

                                if (options[item].equals (getString(R.string.note_share))) {
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, note_title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, note_content);
                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.note_share_2))));
                                }

                                if (options[item].equals (getString(R.string.todo_menu))) {

                                    Todo_DbAdapter db = new Todo_DbAdapter(Popup_note.this);
                                    db.open();
                                    if(db.isExist(note_title)){
                                        Snackbar.make(lv, getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                                    }else{
                                        db.insert(note_title, note_content, "3", "true", helper_main.createDate());
                                        ViewPager viewPager = (ViewPager) Popup_note.this.findViewById(R.id.viewpager);
                                        viewPager.setCurrentItem(2);
                                        Popup_note.this.setTitle(R.string.todo_title);
                                        dialog.dismiss();
                                    }
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, note_title);
                                    calIntent.putExtra(CalendarContract.Events.DESCRIPTION, note_content);
                                    startActivity(calIntent);
                                }

                                if (options[item].equals(getString(R.string.note_remove_note))) {
                                    Snackbar snackbar = Snackbar
                                            .make(lv, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    db.delete(Integer.parseInt(_id));
                                                    setNotesList();
                                                }
                                            });
                                    snackbar.show();
                                }
                            }
                        }).show();

                return true;
            }
        });

        if (lv.getAdapter().getCount() == 0) {
            new android.app.AlertDialog.Builder(this)
                    .setMessage(helper_main.textSpannable(getString(R.string.toast_noEntry)))
                    .setPositiveButton(this.getString(R.string.toast_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                    .show();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 2000);
        }
    }

    public static class Item{
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

    private void editNote(final Activity from) {

        final Button attachment;
        final ImageButton attachmentRem;
        final ImageButton attachmentCam;
        final EditText titleInput;
        final EditText textInput;
        final String priority = sharedPref.getString("handleTextIcon", "");

        LayoutInflater inflater = from.getLayoutInflater();

        final ViewGroup nullParent = null;
        View dialogView = inflater.inflate(R.layout.dialog_edit_note, nullParent);

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

        if (file.startsWith(from.getString(R.string.todo_title) + ": ")) {
            attachment.setText(file);
            attachmentRem.setVisibility(View.VISIBLE);
            attachmentCam.setVisibility(View.GONE);
        }

        titleInput = (EditText) dialogView.findViewById(R.id.note_title_input);
        textInput = (EditText) dialogView.findViewById(R.id.note_text_input);
        titleInput.setText(sharedPref.getString("handleTextTitle", ""));
        titleInput.setSelection(titleInput.getText().length());
        textInput.setText(sharedPref.getString("handleTextText", ""));
        textInput.setSelection(textInput.getText().length());

        titleInput.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                sharedPref.edit().putString("editTextFocus", "title").apply();
                return false;
            }
        });

        textInput.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View arg0, MotionEvent arg1) {
                sharedPref.edit().putString("editTextFocus", "text").apply();
                return false;
            }
        });

        attachment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                helper_main.isOpened(Popup_note.this);
                Intent mainIntent = new Intent(Popup_note.this, Popup_files.class);
                mainIntent.setAction("file_chooseAttachment");
                startActivity(mainIntent);

                attachment.setText(getString(R.string.note_att));
                attachmentRem.setVisibility(View.VISIBLE);
                attachmentCam.setVisibility(View.GONE);
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

                File f = helper_main.newFile();
                final String fileName = f.getAbsolutePath();
                String attName = fileName.substring(fileName.lastIndexOf("/")+1);
                String att = from.getString(R.string.app_att) + ": " + attName;
                attachment.setText(att);
                attachmentRem.setVisibility(View.VISIBLE);
                attachmentCam.setVisibility(View.GONE);
                sharedPref.edit().putString("handleTextAttachment", fileName).apply();

                InputMethodManager imm = (InputMethodManager)from.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(titleInput.getWindowToken(), 0);
                helper_main.switchToActivity(from, Popup_camera.class, false);
            }
        });

        helper_main.showKeyboard(from,titleInput);

        final ImageButton be = (ImageButton) dialogView.findViewById(R.id.imageButtonPri);
        ImageButton ib_paste = (ImageButton) dialogView.findViewById(R.id.imageButtonPaste);
        assert be != null;

        switch (priority) {
            case "3":
                be.setImageResource(R.drawable.circle_green);
                sharedPref.edit()
                        .putString("handleTextIcon", "3")
                        .apply();
                break;
            case "2":
                be.setImageResource(R.drawable.circle_yellow);
                sharedPref.edit()
                        .putString("handleTextIcon", "2")
                        .apply();
                break;
            case "1":
                be.setImageResource(R.drawable.circle_red);
                sharedPref.edit()
                        .putString("handleTextIcon", "1")
                        .apply();
                break;

            default:
                be.setImageResource(R.drawable.circle_green);
                sharedPref.edit()
                        .putString("handleTextIcon", "3")
                        .apply();
                break;
        }

        be.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final Item[] items = {
                        new Item(from.getString(R.string.note_priority_0), R.drawable.circle_green),
                        new Item(from.getString(R.string.note_priority_1), R.drawable.circle_yellow),
                        new Item(from.getString(R.string.note_priority_2), R.drawable.circle_red),
                };

                ListAdapter adapter = new ArrayAdapter<Item>(
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
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0) {
                                    be.setImageResource(R.drawable.circle_green);
                                    sharedPref.edit()
                                            .putString("handleTextIcon", "3")
                                            .apply();
                                } else if (item == 1) {
                                    be.setImageResource(R.drawable.circle_yellow);
                                    sharedPref.edit()
                                            .putString("handleTextIcon", "2")
                                            .apply();
                                } else if (item == 2) {
                                    be.setImageResource(R.drawable.circle_red);
                                    sharedPref.edit()
                                            .putString("handleTextIcon", "1")
                                            .apply();
                                }
                            }
                        }).show();
            }
        });

        ib_paste.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final CharSequence[] options = {
                        from.getString(R.string.paste_date),
                        from.getString(R.string.paste_time)};
                new android.app.AlertDialog.Builder(from)
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals(from.getString(R.string.paste_date))) {
                                    String dateFormat = sharedPref.getString("dateFormat", "1");

                                    switch (dateFormat) {
                                        case "1":

                                            Date date = new Date();
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                            String dateNow = format.format(date);

                                            if(sharedPref.getString("editTextFocus", "").equals("text")) {
                                                textInput.getText().insert(textInput.getSelectionStart(), dateNow);
                                            } else {
                                                titleInput.getText().insert(titleInput.getSelectionStart(), dateNow);
                                            }
                                            break;

                                        case "2":

                                            Date date2 = new Date();
                                            SimpleDateFormat format2 = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                                            String dateNow2 = format2.format(date2);

                                            if(sharedPref.getString("editTextFocus", "").equals("text")) {
                                                textInput.getText().insert(textInput.getSelectionStart(), dateNow2);
                                            } else {
                                                titleInput.getText().insert(titleInput.getSelectionStart(), dateNow2);
                                            }
                                            break;
                                    }
                                }

                                if (options[item].equals (from.getString(R.string.paste_time))) {
                                    Date date = new Date();
                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                    String timeNow = format.format(date);
                                    if(sharedPref.getString("editTextFocus", "").equals("text")) {
                                        textInput.getText().insert(textInput.getSelectionStart(), timeNow);
                                    } else {
                                        titleInput.getText().insert(titleInput.getSelectionStart(), timeNow);
                                    }
                                }
                            }
                        }).show();
            }
        });

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(from);
        builder.setTitle(R.string.note_edit);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                Notes_DbAdapter db = new Notes_DbAdapter(from);
                db.open();

                String inputTitle = titleInput.getText().toString().trim();
                String inputContent = textInput.getText().toString().trim();
                String attachment = sharedPref.getString("handleTextAttachment", "");
                String create = sharedPref.getString("handleTextCreate", "");
                String seqno = sharedPref.getString("handleTextSeqno", "");

                db.update(Integer.parseInt(seqno), inputTitle, inputContent, sharedPref.getString("handleTextIcon", ""), attachment, create);
                dialog.dismiss();
                setNotesList();
            }
        })
                .setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        sharedPref.edit()
                                .putString("handleTextTitle", "")
                                .putString("handleTextText", "")
                                .putString("handleTextIcon", "")
                                .putString("handleTextAttachment", "")
                                .putString("handleTextCreate", "")
                                .putString("editTextFocus", "")
                                .apply();
                        dialog.cancel();
                    }
                });

        final android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        helper_main.isClosed(Popup_note.this);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_note.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_note.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(Popup_note.this);
    }
}