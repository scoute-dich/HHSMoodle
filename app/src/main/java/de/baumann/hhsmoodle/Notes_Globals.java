package de.baumann.hhsmoodle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

final class Notes_Globals
{
    public static final String TAG = "Notification Notes";

    public static final String NOTES_PREF_NAME = "notes";

    public static String noteListToJson(ArrayList<Notes_NotificationNote> noteList)
    {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < noteList.size(); ++i)
        {
            JSONObject jsonObject = new JSONObject();
            Notes_NotificationNote n = noteList.get(i);
            try
            {
                jsonObject.put("id", n.id);
                jsonObject.put("title", n.title);
                jsonObject.put("text", n.text);
                jsonObject.put("isVisible", n.isVisible);
                jsonArray.put(jsonObject);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return jsonArray.toString();
    }

    public static ArrayList<Notes_NotificationNote> jsonToNoteList(String json)
    {
        ArrayList<Notes_NotificationNote> noteList = new ArrayList<>();

        try
        {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Notes_NotificationNote n = new Notes_NotificationNote(
                    jsonObject.getInt("id"),
                    jsonObject.getString("title"),
                    jsonObject.getString("text"),
                    jsonObject.getBoolean("isVisible"));
                noteList.add(n);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return noteList;
    }
    
    /**
     * Do not let this class be instantiated.
     */
    private Notes_Globals()
    {}
}
