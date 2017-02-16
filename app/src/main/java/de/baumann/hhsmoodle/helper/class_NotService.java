package de.baumann.hhsmoodle.helper;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import de.baumann.hhsmoodle.popup.Popup_alarms;
import de.baumann.hhsmoodle.popup.Popup_todo_restart;

public class class_NotService extends IntentService {

    protected void onHandleIntent(Intent intent){
        String intentType = intent.getExtras().getString("caller");
        if(intentType == null) return;
        if(intentType.equals("RebootReceiver_not")) {

            Intent mainIntent = new Intent(class_NotService.this, Popup_todo_restart.class);
            startActivity(mainIntent);
            Intent mainIntent2 = new Intent(class_NotService.this, Popup_alarms.class);
            startActivity(mainIntent2);
        }
    }

    public class_NotService() {
        super("class_NotService");
        Log.d("hhs_moodle", "NotificationService");
    }
}