package de.baumann.hhsmoodle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Receive boot completed action
 */
public class Notes_BootCompletedReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String notesJson = context.getSharedPreferences("Notes_MainActivity", Context.MODE_PRIVATE)
            .getString(Notes_Globals.NOTES_PREF_NAME, "[]");
        Log.d(Notes_Globals.TAG, "Boot completed: " + notesJson);

        ArrayList<Notes_NotificationNote> noteList = Notes_Globals.jsonToNoteList(notesJson);
        for (int i = 0; i < noteList.size(); ++i)
        {
            Notes_NotificationNote n = noteList.get(i);
            if (n.isVisible)
            {
                context.startService(
                    new Intent(context, Notes_NotificationService.class)
                        .putExtra(Notes_NotificationService.ID, n.id)
                        .putExtra(Notes_NotificationService.SHOW, n.isVisible)
                        .putExtra(Notes_NotificationService.TITLE, n.title)
                        .putExtra(Notes_NotificationService.TEXT, n.text));
            }
        }
    }
}
