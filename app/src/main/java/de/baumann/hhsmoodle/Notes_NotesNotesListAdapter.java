package de.baumann.hhsmoodle;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for the main Notes_NotificationNote list.
 */
class Notes_NotesNotesListAdapter
    extends RecyclerView.Adapter<Notes_NotesNotesListAdapter.ViewHolder>
    implements Notes_NotesController
{
    private final Activity context;
    private final FragmentManager fragmentManager;
    private ArrayList<Notes_NotificationNote> notes;

    /**
     * ViewHolder holds the view showing one Notes_NotificationNote.
     * It also listens to UI actions in the view and forwards the actions to Notes_NotesController.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener
    {
        public final TextView titleView;
        public final TextView textView;
        public final SwitchCompat switchView;
        private final Notes_NotesController notesNotesController;
        private final FragmentManager fragmentManager;

        public ViewHolder(View v, Notes_NotesController notesNotesController, FragmentManager fragmentManager)
        {
            super(v);

            this.titleView = (TextView) v.findViewById(R.id.note_title);
            this.textView = (TextView) v.findViewById(R.id.note_text);
            this.switchView = (SwitchCompat) v.findViewById(R.id.note_switch);
            this.notesNotesController = notesNotesController;
            this.fragmentManager = fragmentManager;

            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
            this.switchView.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View v)
        {
            this.notesNotesController.onNoteClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v)
        {
            Notes_DeleteNoteDialogFragment.newInstance(
                getAdapterPosition(), this.titleView.getText().toString()).show(
                this.fragmentManager, "deleteNoteDialog");
            return true;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            this.notesNotesController.onNoteCheckedChanged(getAdapterPosition(), isChecked);
        }
    }

    public Notes_NotesNotesListAdapter(Activity context, FragmentManager fragmentManager)
    {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.notes = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_note, parent, false);
        return new ViewHolder(v, this, this.fragmentManager);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Notes_NotificationNote n = this.notes.get(position);
        holder.titleView.setText(n.title);
        holder.textView.setText(n.text);
        holder.switchView.setChecked(n.isVisible);
    }

    @Override
    public int getItemCount()
    {
        return this.notes.size();
    }

    @Override
    public void onNoteClicked(int position)
    {
        Log.d(Notes_Globals.TAG, "Note clicked " + position);
        Notes_NotificationNote n = this.notes.get(position);
        Intent in = new Intent(this.context, Notes_AddNoteActivity.class);
        in.putExtra(Notes_AddNoteActivity.TITLE, n.title);
        in.putExtra(Notes_AddNoteActivity.TEXT, n.text);
        in.putExtra(Notes_AddNoteActivity.NOTE_INDEX, position);
        context.startActivityForResult(in, Notes_AddNoteActivity.EDIT_REQ);
    }

    @Override
    public void onNoteCheckedChanged(int position, boolean isChecked)
    {
        Notes_NotificationNote n = this.notes.get(position);
        if (isChecked != n.isVisible)
        {
            n.isVisible = isChecked;
            setNotification(n);
        }
    }

    public ArrayList<Notes_NotificationNote> getNotes()
    {
        return this.notes;
    }

    public void setNotes(ArrayList<Notes_NotificationNote> notes)
    {
        this.notes = notes;
        notifyDataSetChanged();
    }

    public void addNote(String title, String text)
    {
        Notes_NotificationNote n = new Notes_NotificationNote(getId(), title, text, true);
        this.notes.add(n);
        notifyItemInserted(this.notes.size() - 1);
        setNotification(n);
    }

    public void deleteNote(int position)
    {
        Notes_NotificationNote n = this.notes.get(position);
        this.notes.remove(position);
        notifyItemRemoved(position);
        n.isVisible = false;
        setNotification(n);
    }

    public void updateNote(int position, String title, String text)
    {
        Notes_NotificationNote n = this.notes.get(position);
        n.title = title;
        n.text = text;
        notifyItemChanged(position);

        if (n.isVisible)
            setNotification(n);
    }

    /**
     * Get unique notes_note id
     */
    private int getId()
    {
        boolean idOk = false;
        int id = 0;

        while (!idOk)
        {
            idOk = true;
            for (int i = 0; i < this.notes.size(); ++i)
            {
                if (this.notes.get(i).id == id)
                {
                    idOk = false;
                    ++id;
                    break;
                }
            }
        }

        return id;
    }

    private void setNotification(Notes_NotificationNote n)
    {
        Intent in = new Intent(this.context, Notes_NotificationService.class);

        in.putExtra(Notes_NotificationService.ID, n.id);
        in.putExtra(Notes_NotificationService.SHOW, n.isVisible);

        if (n.isVisible)
        {
            in.putExtra(Notes_NotificationService.TITLE, n.title);
            in.putExtra(Notes_NotificationService.TEXT, n.text);
        }

        this.context.startService(in);
    }
}
