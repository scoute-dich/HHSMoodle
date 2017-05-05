package de.baumann.hhsmoodle.data_todo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import de.baumann.hhsmoodle.R;

public class Todo_DbAdapter {

    //define static variable
    private static final int dbVersion =6;
    private static final String dbName = "todo_DB_v01.db";
    private static final String dbTable = "todo";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,dbName,null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement, todo_title, todo_content, todo_icon, todo_attachment, todo_creation, UNIQUE(todo_title))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+dbTable);
            onCreate(db);
        }
    }

    //establish connection with SQLiteDataBase
    private final Context c;
    private SQLiteDatabase sqlDb;

    public Todo_DbAdapter(Context context) {
        this.c = context;
    }
    public void open() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper(c);
        sqlDb = dbHelper.getWritableDatabase();
    }

    //insert data
    @SuppressWarnings("SameParameterValue")
    public void insert(String todo_title, String todo_content, String todo_icon, String todo_attachment, String todo_creation) {
        if(!isExist(todo_title)) {
            sqlDb.execSQL("INSERT INTO todo (todo_title, todo_content, todo_icon, todo_attachment, todo_creation) VALUES('" + todo_title + "','" + todo_content + "','" + todo_icon + "','" + todo_attachment + "','" + todo_creation + "')");
        }
    }
    //check entry already in database or not
    public boolean isExist(String todo_title){
        String query = "SELECT todo_title FROM todo WHERE todo_title='"+todo_title+"' LIMIT 1";
        @SuppressLint("Recycle") Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }
    //edit data
    public void update(int id,String todo_title,String todo_content,String todo_icon,String todo_attachment, String todo_creation) {
        sqlDb.execSQL("UPDATE "+dbTable+" SET todo_title='"+todo_title+"', todo_content='"+todo_content+"', todo_icon='"+todo_icon+"', todo_attachment='"+todo_attachment+"', todo_creation='"+todo_creation+"'   WHERE _id=" + id);
    }

    //delete data
    public void delete(int id) {
        sqlDb.execSQL("DELETE FROM "+dbTable+" WHERE _id="+id);
    }


    //fetch data
    public Cursor fetchAllData(Context context) {

        PreferenceManager.setDefaultValues(context, R.xml.user_settings, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String[] columns = new String[]{"_id", "todo_title", "todo_content", "todo_icon","todo_attachment","todo_creation"};

        if (sp.getString("sortDBT", "title").equals("title")) {
            return sqlDb.query(dbTable, columns, null, null, null, null, "todo_title" + " COLLATE NOCASE ASC;");

        } else if (sp.getString("sortDBT", "title").equals("icon")) {

            String orderBy = "todo_icon" + "," +
                    "todo_title" + " COLLATE NOCASE ASC;";

            return sqlDb.query(dbTable, columns, null, null, null, null, orderBy);

        } else if (sp.getString("sortDBT", "title").equals("create")) {

            String orderBy = "todo_creation" + "," +
                    "todo_title" + " COLLATE NOCASE ASC;";

            return sqlDb.query(dbTable, columns, null, null, null, null, orderBy);

        } else if (sp.getString("sortDBT", "title").equals("attachment")) {

            String orderBy = "todo_attachment" + "," +
                    "todo_title" + " COLLATE NOCASE ASC;";

            return sqlDb.query(dbTable, columns, null, null, null, null, orderBy);
        }

        return null;
    }

    //fetch data by filter
    public Cursor fetchDataByFilter(String inputText,String filterColumn) throws SQLException {
        Cursor row;
        String query = "SELECT * FROM "+dbTable;
        if (inputText == null  ||  inputText.length () == 0)  {
            row = sqlDb.rawQuery(query, null);
        }else {
            query = "SELECT * FROM "+dbTable+" WHERE "+filterColumn+" like '%"+inputText+"%'";
            row = sqlDb.rawQuery(query, null);
        }
        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }
}
