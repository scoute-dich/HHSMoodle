package de.baumann.hhsmoodle.data_bookmarks;

import android.app.Activity;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.helper_main;

/**
 * Created by juergen on 06.02.17
 */

class Bookmarks_helper {

    static void insertDefaultBookmarks (Activity activity) {

        Bookmarks_DbAdapter db = new Bookmarks_DbAdapter(activity);
        db.open();

        db.insert(activity.getString(R.string.text_tit_1), "https://moodle.huebsch.ka.schule-bw.de/moodle/my/", "05", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_2), "https://moodle.huebsch.ka.schule-bw.de/moodle/user/profile.php", "06", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_8), "https://moodle.huebsch.ka.schule-bw.de/moodle/calendar/view.php", "07", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_3), "https://moodle.huebsch.ka.schule-bw.de/moodle/grade/report/overview/index.php", "08", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_4), "https://moodle.huebsch.ka.schule-bw.de/moodle/message/index.php", "09", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_5), "https://moodle.huebsch.ka.schule-bw.de/moodle/user/preferences.php", "10", "", helper_main.createDate());
        db.insert(activity.getString(R.string.text_tit_6), "http://www.huebsch-ka.de/", "11", "", helper_main.createDate());
    }
}
