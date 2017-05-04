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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.baumann.hhsmoodle.activities.Activity_EditNote;
import de.baumann.hhsmoodle.helper.helper_main;

public class Notes_helper {

    public static void newNote (final Activity activity, String title, String text, String icon, String attachment,
                                String create, String seqno) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sharedPref.edit()
                .putString("handleTextTitle", title)
                .putString("handleTextText", text)
                .putString("handleTextIcon", icon)
                .putString("handleTextAttachment", attachment)
                .putString("handleTextCreate", create)
                .putString("handleTextSeqno", seqno)
                .apply();
        helper_main.switchToActivity(activity, Activity_EditNote.class, false);
    }
}
