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

package de.baumann.hhsmoodle.data_notes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.popup.Popup_camera;
import de.baumann.hhsmoodle.popup.Popup_files;

public class Notes_helper {

    public static void newNote (final Activity from, String title, String content) {

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(from);
        final Button attachment;
        final ImageButton attachmentRem;
        final ImageButton attachmentCam;
        final EditText titleInput;
        final EditText textInput;

        sharedPref.edit()
                .putString("handleTextTitle", title)
                .putString("handleTextText", content)
                .apply();

        LayoutInflater inflater = from.getLayoutInflater();

        final ViewGroup nullParent = null;
        View dialogView = inflater.inflate(R.layout.dialog_edit_note, nullParent);


        attachmentRem = (ImageButton) dialogView.findViewById(R.id.button_rem);
        attachmentRem.setImageResource(R.drawable.close_red);
        attachment = (Button) dialogView.findViewById(R.id.button_att);
        attachmentCam = (ImageButton) dialogView.findViewById(R.id.button_cam);
        attachmentCam.setImageResource(R.drawable.camera);

        attachment.setText(R.string.choose_att);
        attachmentRem.setVisibility(View.GONE);
        attachmentCam.setVisibility(View.VISIBLE);

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

                helper_main.isOpened(from);
                Intent mainIntent = new Intent(from, Popup_files.class);
                mainIntent.setAction("file_chooseAttachment");
                from.startActivity(mainIntent);

                attachment.setText(from.getString(R.string.note_att));
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
        be.setImageResource(R.drawable.circle_green);
        sharedPref.edit().putString("handleTextIcon", "3").apply();

        be.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final Notes_helper.Item[] items = {
                        new Item(from.getString(R.string.note_priority_0), R.drawable.circle_green),
                        new Item(from.getString(R.string.note_priority_1), R.drawable.circle_yellow),
                        new Item(from.getString(R.string.note_priority_2), R.drawable.circle_red),
                };

                ListAdapter adapter = new ArrayAdapter<Notes_helper.Item>(
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

        AlertDialog.Builder builder = new AlertDialog.Builder(from);
        builder.setTitle(R.string.note_edit);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

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
                        if (sharedPref.getString("fromCourseList", "false").equals("true")) {
                            sharedPref.edit().putString("fromCourseList", "false").apply();
                            dialog.cancel();
                            from.finish();
                        } else {
                            sharedPref.edit().putString("fromCourseList", "false").apply();
                            dialog.cancel();
                        }
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do stuff, possibly set wantToCloseDialog to true then...

                Notes_DbAdapter db = new Notes_DbAdapter(from);
                db.open();

                String inputTitle = titleInput.getText().toString().trim();
                String inputContent = textInput.getText().toString().trim();
                String attachment = sharedPref.getString("handleTextAttachment", "");

                if(db.isExist(inputTitle)){
                    Snackbar.make(titleInput, from.getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                }else{
                    db.insert(inputTitle, inputContent, sharedPref.getString("handleTextIcon", ""), attachment, helper_main.createDate());
                    sharedPref.edit()
                            .putString("handleTextTitle", "")
                            .putString("handleTextText", "")
                            .putString("handleTextIcon", "")
                            .putString("handleTextAttachment", "")
                            .putString("handleTextCreate", "")
                            .putString("editTextFocus", "")
                            .apply();
                    if (sharedPref.getString("fromCourseList", "false").equals("true")) {
                        sharedPref.edit().putString("fromCourseList", "false").apply();
                        dialog.cancel();
                        from.finish();
                    } else {
                        sharedPref.edit().putString("fromCourseList", "false").apply();
                        dialog.cancel();
                    }
                }
            }
        });
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
}
