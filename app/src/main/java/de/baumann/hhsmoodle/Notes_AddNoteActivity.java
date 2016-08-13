package de.baumann.hhsmoodle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import de.baumann.hhsmoodle.helper.Start;

public class Notes_AddNoteActivity extends AppCompatActivity {

    private EditText titleInput;
    private EditText textInput;
    private int noteIndex; // index for the edited notes_note if this activity is opened for notes_note editing

    // Request codes
    public static final int ADD_REQ = 1;
    public static final int EDIT_REQ = 2;

    // Extra names
    public static final String TITLE = "TITLE";
    public static final String TEXT = "TEXT";
    public static final String NOTE_INDEX = "NOTE_INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_activity_add_note);

        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar != null) {
            final String startType = sharedPref.getString("startType", "1");
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (startType.equals("2")) {
                        Intent intent_in = new Intent(Notes_AddNoteActivity.this, Start.class);
                        startActivity(intent_in);
                        finish();
                    } else if (startType.equals("1")) {
                        Intent intent_in = new Intent(Notes_AddNoteActivity.this, HHS_MainScreen.class);
                        startActivity(intent_in);
                        finish();
                    }
                }
            });

            if (sharedPref.getBoolean ("longPress", false)){
                toolbar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        finish();
                        return true;
                    }
                });
            }
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.titleInput = (EditText) findViewById(R.id.note_title_input);
        this.textInput = (EditText) findViewById(R.id.note_text_input);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.titleInput.setText(extras.getString(TITLE, ""));
            this.textInput.setText(extras.getString(TEXT, ""));
            this.noteIndex = extras.getInt(NOTE_INDEX, -1);
            if (noteIndex > -1) {
                setTitle(getString(R.string.edit_note));
            }
        } else {
            String title = sharedPref.getString("noteTitle", "");
            String text = sharedPref.getString("noteContent", "");

            this.titleInput.setText(title);
            this.textInput.setText(text);

            sharedPref.edit()
                    .putString("noteTitle", "")
                    .putString("noteContent", "")
                    .apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent_in = new Intent(Notes_AddNoteActivity.this, Notes_MainActivity.class);
            startActivity(intent_in);
            finish();
        }

        if (id == R.id.save_note) {
            String title = this.titleInput.getText().toString();
            String text = this.textInput.getText().toString();
            Log.d(Notes_Globals.TAG, "Saving notes_note. Title: " + title + ", Text: " + text);

            Intent data = new Intent();
            data.putExtra(TITLE, title);
            data.putExtra(TEXT, text);
            data.putExtra(NOTE_INDEX, this.noteIndex);

            setResult(RESULT_OK, data);

            final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPref.edit()
                    .putString("noteTitle", "")
                    .putString("noteContent", "")
                    .apply();

            finish();
        }

        return true;
    }
}
