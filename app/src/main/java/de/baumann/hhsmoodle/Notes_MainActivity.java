package de.baumann.hhsmoodle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.baumann.hhsmoodle.helper.Start;

@SuppressWarnings("UnusedParameters")
public class Notes_MainActivity extends AppCompatActivity {
    /**
     * Observe changes in notes list and display an alternate view if the list is empty.
     */
    private static class EmptyNoteListObserver extends RecyclerView.AdapterDataObserver {
        private final RecyclerView noteList;
        private final View alternateView;

        EmptyNoteListObserver(RecyclerView noteList, View alternateView) {
            this.noteList = noteList;
            this.alternateView = alternateView;
            this.noteList.getAdapter().registerAdapterDataObserver(this);
        }

        @Override
        public void onChanged() {
            Log.d(Notes_Globals.TAG, "List changed. Count " + noteList.getAdapter().getItemCount());
            if (noteList.getAdapter().getItemCount() > 0) {
                this.noteList.setVisibility(View.VISIBLE);
                this.alternateView.setVisibility(View.GONE);
            } else {
                this.noteList.setVisibility(View.GONE);
                this.alternateView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            onChanged();
        }

        @Override
        public void	onItemRangeRemoved(int positionStart, int itemCount)
        {
            onChanged();
        }
    }

    /**
     * AddNoteResult is used to store the result from "Add notes_note" activity.
     */
    private static class AddNoteResult {
        public final int reqCode;
        public final String title;
        public final String text;
        public final int noteIndex;

        public AddNoteResult(int reqCode, String title, String text, int noteIndex) {
            this.reqCode = reqCode;
            this.title = title;
            this.text = text;
            this.noteIndex = noteIndex;
        }
    }

    private Notes_NotesNotesListAdapter notesListAdapter;
    private AddNoteResult addNoteResult;

    public void addNote(View view) {
        startActivityForResult(new Intent(this, Notes_AddNoteActivity.class), Notes_AddNoteActivity.ADD_REQ);
    }

    public void deleteNote(int notePos)
    {
        this.notesListAdapter.deleteNote(notePos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Notes_Globals.TAG, "Req " + requestCode + ", result " + resultCode);
        if (resultCode == RESULT_OK) {

            this.addNoteResult = new AddNoteResult(
                requestCode,
                data.getStringExtra(Notes_AddNoteActivity.TITLE),
                data.getStringExtra(Notes_AddNoteActivity.TEXT),
                data.getIntExtra(Notes_AddNoteActivity.NOTE_INDEX, -1));
            Log.d(Notes_Globals.TAG, "Caching result: " + this.addNoteResult.title + ": " + this.addNoteResult.text);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_activity_main);

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
                        Intent intent_in = new Intent(Notes_MainActivity.this, Start.class);
                        startActivity(intent_in);
                        finish();
                    } else if (startType.equals("1")) {
                        Intent intent_in = new Intent(Notes_MainActivity.this, HHS_MainScreen.class);
                        startActivity(intent_in);
                        finish();
                    }
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
        setTitle(R.string.menu_not);

        this.notesListAdapter = new Notes_NotesNotesListAdapter(this, getSupportFragmentManager());

        RecyclerView noteListView = (RecyclerView) findViewById(R.id.notes_recycler_view);
        assert noteListView != null;
        noteListView.setLayoutManager(new LinearLayoutManager(this));
        noteListView.setAdapter(this.notesListAdapter);

        this.addNoteResult = null;
        //noinspection UnusedAssignment
        EmptyNoteListObserver noteListObserver = new EmptyNoteListObserver(noteListView, findViewById(R.id.empty_text_view));

        if (sharedPref.getBoolean ("click", false)){
            sharedPref.edit()
                    .putBoolean("click", false)
                    .apply();
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.view2);
            assert fab != null;
            fab.performClick();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Notes_Globals.TAG, "Pausing");

        SharedPreferences.Editor prefs = getPreferences(Context.MODE_PRIVATE).edit();
        prefs.putString(Notes_Globals.NOTES_PREF_NAME, Notes_Globals.noteListToJson(this.notesListAdapter.getNotes()));
        prefs.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Notes_Globals.TAG, "Resuming");

        this.notesListAdapter.setNotes(Notes_Globals.jsonToNoteList(
            getPreferences(Context.MODE_PRIVATE).getString(Notes_Globals.NOTES_PREF_NAME, "[]")));

        // Unprocessed result from "Add notes_note" activity
        if (this.addNoteResult != null) {
            Log.d(Notes_Globals.TAG, "Result from Notes_AddNoteActivity: " + this.addNoteResult.reqCode + " - " +
                this.addNoteResult.title + " - " + this.addNoteResult.text + " - " + this.addNoteResult.noteIndex);

            switch (this.addNoteResult.reqCode) {
                case Notes_AddNoteActivity.ADD_REQ:
                    this.notesListAdapter.addNote(this.addNoteResult.title, this.addNoteResult.text);
                    break;

                case Notes_AddNoteActivity.EDIT_REQ:
                    this.notesListAdapter.updateNote(
                        this.addNoteResult.noteIndex, this.addNoteResult.title, this.addNoteResult.text);
                    break;
            }

            this.addNoteResult = null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent_in = new Intent(Notes_MainActivity.this, HHS_MainScreen.class);
        startActivity(intent_in);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent_in = new Intent(Notes_MainActivity.this, HHS_MainScreen.class);
            startActivity(intent_in);
            finish();
        }

        if (id == R.id.action_help) {
            final SpannableString s = new SpannableString(Html.fromHtml(getString(R.string.helpNot_text)));
            Linkify.addLinks(s, Linkify.WEB_URLS);

            final AlertDialog.Builder dialog = new AlertDialog.Builder(Notes_MainActivity.this)
                    .setTitle(R.string.helpNot_title)
                    .setMessage(s)
                    .setPositiveButton(getString(R.string.toast_yes), null);
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
