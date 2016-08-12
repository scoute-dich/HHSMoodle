package de.baumann.hhsmoodle;

/**
 * Interface for controller that handles Notes_NotificationNote UI actions.
 */
interface Notes_NotesController
{
    void onNoteClicked(int position);
    void onNoteCheckedChanged(int position, boolean isChecked);
}
