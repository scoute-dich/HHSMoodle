package de.baumann.hhsmoodle;

class Notes_NotificationNote
{
    public final int id;
    public String title;
    public String text;
    public boolean isVisible;

    public Notes_NotificationNote(int id, String title, String text, boolean isVisible)
    {
        this.id = id;
        this.title = title;
        this.text = text;
        this.isVisible = isVisible;
    }
}
