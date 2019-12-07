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

package de.baumann.hhsmoodle.bookmarks;

import android.app.Activity;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.helper_main;

public class bookmarks_helper {

    public static void insertDefaultBookmarks (Activity activity) {
        bookmarks_database db = new bookmarks_database(activity);
        db.open();
        db.insert(activity.getString(R.string.text_tit_1), "https://moodle.huebsch.ka.schule-bw.de/moodle/my/", "14", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_2), "https://moodle.huebsch.ka.schule-bw.de/moodle/user/profile.php", "15", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_8), "https://moodle.huebsch.ka.schule-bw.de/moodle/calendar/view.php", "16", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_3), "https://moodle.huebsch.ka.schule-bw.de/moodle/grade/report/overview/index.php", "17", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_4), "https://moodle.huebsch.ka.schule-bw.de/moodle/message/index.php", "18", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_5), "https://moodle.huebsch.ka.schule-bw.de/moodle/user/preferences.php", "19", "", helper_main.createDate());
    }
}
