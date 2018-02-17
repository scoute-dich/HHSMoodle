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

package de.baumann.hhsmoodle.data_bookmarks;

import android.app.Activity;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.helper_main;

public class Bookmarks_helper {

    public static void insertDefaultBookmarks (Activity activity) {

        Bookmarks_DbAdapter db = new Bookmarks_DbAdapter(activity);
        db.open();

        db.insert(activity.getString(R.string.text_tit_1), "https://moodle.huebsch.ka.schule-bw.de/moodle/my/", "02", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_2), "https://moodle.huebsch.ka.schule-bw.de/moodle/user/profile.php", "03", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_8), "https://moodle.huebsch.ka.schule-bw.de/moodle/calendar/view.php", "04", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_3), "https://moodle.huebsch.ka.schule-bw.de/moodle/grade/report/overview/index.php", "05", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_4), "https://moodle.huebsch.ka.schule-bw.de/moodle/message/index.php", "06", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_5), "https://moodle.huebsch.ka.schule-bw.de/moodle/user/preferences.php", "07", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_6), "http://www.huebsch-ka.de/", "08", "", helper_main.createDate());
    }
}
