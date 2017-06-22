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

package de.baumann.hhsmoodle.data_courses;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.activities.Activity_course;
import de.baumann.hhsmoodle.activities.Activity_random;
import de.baumann.hhsmoodle.helper.helper_main;

class Courses_helper {

    static void newCourse (final Activity activity, String title, final String content, final String icon, String button_neutral, final boolean finishFromActivity) {

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        View dialogView = View.inflate(activity, R.layout.dialog_edit_title, null);
        final EditText edit_title = (EditText) dialogView.findViewById(R.id.pass_title);
        edit_title.setText(title);
        edit_title.setHint(R.string.bookmark_edit_title);

        builder.setTitle(R.string.courseList_title);
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

                String inputTitle = edit_title.getText().toString().trim();
                Courses_DbAdapter db = new Courses_DbAdapter(activity);
                db.open();

                if(db.isExist(inputTitle)){
                    Snackbar.make(edit_title, activity.getString(R.string.toast_newTitle), Snackbar.LENGTH_LONG).show();
                }else{
                    dialog.dismiss();
                    sharedPref.edit().putString("courses_title", inputTitle).apply();
                    sharedPref.edit().putString("courses_seqno", "").apply();
                    sharedPref.edit().putString("courses_icon", icon).apply();
                    sharedPref.edit().putString("courses_create", helper_main.createDate()).apply();
                    sharedPref.edit().putString("courses_attachment", "").apply();
                    helper_main.switchToActivity(activity, Activity_course.class, finishFromActivity);
                }
            }
        });
        dialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do stuff, possibly set wantToCloseDialog to true then...
                sharedPref.edit().putString("courses_content", content).apply();
                Snackbar.make(edit_title, activity.getString(R.string.toast_contentAdded), Snackbar.LENGTH_LONG).show();
            }
        });

        helper_main.showKeyboard(activity,edit_title);
    }
}
