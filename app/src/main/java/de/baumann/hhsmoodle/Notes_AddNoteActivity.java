package de.baumann.hhsmoodle;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_in = new Intent(Notes_AddNoteActivity.this, HHS_MainScreen.class);
                    startActivity(intent_in);
                }
            });

            if (sharedPref.getBoolean ("longPress", false)){
                toolbar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        finishAffinity();
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
            this.titleInput.setSelection(titleInput.getText().length());
            this.textInput.setText(extras.getString(TEXT, ""));
            this.textInput.setSelection(textInput.getText().length());
            this.noteIndex = extras.getInt(NOTE_INDEX, -1);
            if (noteIndex > -1) {
                setTitle(getString(R.string.edit_note));
            }
        } else {
            String title = sharedPref.getString("noteTitle", "");
            String text = sharedPref.getString("noteContent", "");

            this.titleInput.setText(title);
            this.titleInput.setSelection(titleInput.getText().length());
            this.textInput.setText(text);
            this.textInput.setSelection(textInput.getText().length());

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
            Intent intent_in = new Intent(Notes_AddNoteActivity.this, HHS_MainScreen.class);
            startActivity(intent_in);
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

        if (id == R.id.action_help) {
            final SpannableString s = new SpannableString(Html.fromHtml(getString(R.string.helpNot_text)));
            Linkify.addLinks(s, Linkify.WEB_URLS);

            final AlertDialog.Builder dialog = new AlertDialog.Builder(Notes_AddNoteActivity.this)
                    .setTitle(R.string.helpNot_title)
                    .setMessage(s)
                    .setPositiveButton(getString(R.string.toast_yes), null);
            dialog.show();
        }
        return true;
    }
}
