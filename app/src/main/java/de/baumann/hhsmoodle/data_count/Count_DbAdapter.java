package de.baumann.hhsmoodle.data_count;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import de.baumann.hhsmoodle.R;

public class Count_DbAdapter {

    //define static variable
    private static final int dbVersion =6;
    private static final String dbName = "count_DB_v01.db";
    private static final String dbTable = "count";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,dbName,null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement, count_title, count_content, count_icon, count_attachment, count_creation, UNIQUE(count_title))");
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

    public Count_DbAdapter(Context context) {
        this.c = context;
    }
    public void open() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper(c);
        sqlDb = dbHelper.getWritableDatabase();
    }

    //insert data
    @SuppressWarnings("SameParameterValue")
    public void insert(String count_title, String count_content, String count_icon, String count_attachment, String count_creation) {
        if(!isExist(count_title)) {
            sqlDb.execSQL("INSERT INTO count (count_title, count_content, count_icon, count_attachment, count_creation) VALUES('" + count_title + "','" + count_content + "','" + count_icon + "','" + count_attachment + "','" + count_creation + "')");
        }
    }
    //check entry already in database or not
    public boolean isExist(String count_title){
        String query = "SELECT count_title FROM count WHERE count_title='"+count_title+"' LIMIT 1";
        @SuppressLint("Recycle") Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }
    //edit data
    public void update(int id,String count_title,String count_content,String count_icon,String count_attachment, String count_creation) {
        sqlDb.execSQL("UPDATE "+dbTable+" SET count_title='"+count_title+"', count_content='"+count_content+"', count_icon='"+count_icon+"', count_attachment='"+count_attachment+"', count_creation='"+count_creation+"'   WHERE _id=" + id);
    }

    //delete data
    public void delete(int id) {
        sqlDb.execSQL("DELETE FROM "+dbTable+" WHERE _id="+id);
    }


    //fetch data
    public Cursor fetchAllData(Context context) {

        PreferenceManager.setDefaultValues(context, R.xml.user_settings, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String[] columns = new String[]{"_id", "count_title", "count_content", "count_icon","count_attachment","count_creation"};

        if (sp.getString("sortDBC", "title").equals("title")) {
            return sqlDb.query(dbTable, columns, null, null, null, null, "count_title");

        } else if (sp.getString("sortDBC", "title").equals("icon")) {
            return sqlDb.query(dbTable, columns, null, null, null, null, "count_icon");

        } else if (sp.getString("sortDBC", "title").equals("create")) {
            return sqlDb.query(dbTable, columns, null, null, null, null, "count_creation");

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
