package de.baumann.hhsmoodle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class Notes_DeleteNoteDialogFragment extends DialogFragment {

    public static Notes_DeleteNoteDialogFragment newInstance(int notePos, String noteTitle) {

        Notes_DeleteNoteDialogFragment frag = new Notes_DeleteNoteDialogFragment();
        Bundle args = new Bundle();
        args.putInt("notePos", notePos);
        args.putString("noteTitle", noteTitle);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final int notePos = getArguments().getInt("notePos");
        final String noteTitle = getArguments().getString("noteTitle");
        final String deletePrompt = getResources().getString(R.string.dialog_delete_note);
        assert noteTitle != null;
        final String dialogText = noteTitle.isEmpty() ?
            deletePrompt + "?" : deletePrompt + " \"" + noteTitle + "\"?";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(dialogText)
            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    Log.d(Notes_Globals.TAG, "Confirm delete of notes_note at position " + notePos);
                    ((Notes_MainActivity) getActivity()).deleteNote(notePos);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    Log.d(Notes_Globals.TAG, "Cancel delete notes_note at position " + notePos);
                }
            });

        return builder.create();
    }
}
