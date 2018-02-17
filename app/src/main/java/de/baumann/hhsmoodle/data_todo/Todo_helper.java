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

package de.baumann.hhsmoodle.data_todo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_todo;
import de.baumann.hhsmoodle.helper.helper_main;

public class Todo_helper {

    public static void newTodo (final Activity activity, String title, final String content, final String icon, String button_neutral, final boolean finishFromActivity) {

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        View dialogView = View.inflate(activity, R.layout.dialog_edit_title, null);
        final EditText edit_title = dialogView.findViewById(R.id.pass_title);
        edit_title.setText(title);
        edit_title.setHint(R.string.bookmark_edit_title);

        builder.setTitle(R.string.todo_title);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.setNeutralButton(button_neutral, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        builder.setNegativeButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                if (finishFromActivity) {
                    activity.finish();
                }
            }
        });

        final android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do stuff, possibly set wantToCloseDialog to true then...

                String inputTitle = edit_title.getText().toString().trim();

                Todo_DbAdapter db = new Todo_DbAdapter(activity);
                db.open();

                if(db.isExist(helper_main.secString(inputTitle))){
                    Snackbar.make(edit_title, activity.getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                }else{
                    dialog.dismiss();
                    sharedPref.edit().putString("toDo_title", inputTitle).apply();
                    sharedPref.edit().putString("toDo_seqno", "").apply();
                    sharedPref.edit().putString("toDo_icon", icon).apply();
                    sharedPref.edit().putString("toDo_create", helper_main.createDate()).apply();
                    sharedPref.edit().putString("toDo_attachment", "true").apply();
                    helper_main.switchToActivity(activity, Activity_todo.class, finishFromActivity);
                }
            }
        });
        dialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File f = new File(activity.getFilesDir() + "title.txt");

                try {
                    FileOutputStream fOut = new FileOutputStream(f);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(content);
                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }

                StringBuilder text = new StringBuilder();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String line;

                    while ((line = br.readLine()) != null) {
                        String add = line + "|[-]";
                        text.append(add);
                        text.append('\n');
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String textAdd = text.substring(0, text.length()-1);
                sharedPref.edit().putString("toDo_text", textAdd).apply();
                //noinspection ResultOfMethodCallIgnored
                f.delete();
                Snackbar.make(edit_title, activity.getString(R.string.toast_contentAdded), Snackbar.LENGTH_LONG).show();
            }
        });

        helper_main.showKeyboard(activity,edit_title);
    }
}
