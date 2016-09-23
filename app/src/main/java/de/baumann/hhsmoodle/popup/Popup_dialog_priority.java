package de.baumann.hhsmoodle.popup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.CustomListAdapter_dialog_priority;
import de.baumann.hhsmoodle.helper.Database_Notes;

public class Popup_dialog_priority extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String[] itemTITLE ={
                getString(R.string.note_priority_0),
                getString(R.string.note_priority_1),
                getString(R.string.note_priority_2),
        };

        final String[] itemPRI ={
                "",
                "!",
                "!!",
        };

        Integer[] imgid={
                R.drawable.pr_green,
                R.drawable.pr_yellow,
                R.drawable.pr_red,
        };

        setContentView(R.layout.activity_popup);
        
        CustomListAdapter_dialog_priority adapter=new CustomListAdapter_dialog_priority(Popup_dialog_priority.this, itemTITLE, itemPRI, imgid);
        ListView listView = (ListView) findViewById(R.id.dialogList);
        listView.setAdapter(adapter);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Popup_dialog_priority.this);
        final String note_title = sharedPref.getString("note_title", "");
        final String note_cont = sharedPref.getString("note_cont", "");
        final String note_segno = sharedPref.getString("note_segno", "");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String note_pri = itemPRI[+position];

                try {
                    Database_Notes db = new Database_Notes(Popup_dialog_priority.this);
                    db.deleteNote((Integer.parseInt(note_segno)));
                    db.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    final Database_Notes db = new Database_Notes(Popup_dialog_priority.this);
                    db.addBookmark(note_title, note_cont, note_pri);
                    db.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent mainIntent = new Intent(Popup_dialog_priority.this, Popup_notes.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }
}
