package de.baumann.hhsmoodle.data_courses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import de.baumann.hhsmoodle.R;

public class Courses_DbAdapter {

    //define static variable
    private static final int dbVersion =6;
    private static final String dbName = "courses_DB_v01.db";
    private static final String dbTable = "courses";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,dbName,null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement, courses_title, courses_content, courses_icon, courses_attachment, courses_creation, UNIQUE(courses_title))");
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

    public Courses_DbAdapter(Context context) {
        this.c = context;
    }
    public void open() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper(c);
        sqlDb = dbHelper.getWritableDatabase();
    }

    //insert data
    @SuppressWarnings("SameParameterValue")
    public void insert(String courses_title, String courses_content, String courses_icon, String courses_attachment, String courses_creation) {
        if(!isExist(courses_title)) {
            sqlDb.execSQL("INSERT INTO courses (courses_title, courses_content, courses_icon, courses_attachment, courses_creation) VALUES('" + courses_title + "','" + courses_content + "','" + courses_icon + "','" + courses_attachment + "','" + courses_creation + "')");
        }
    }
    //check entry already in database or not
    public boolean isExist(String courses_title){
        String query = "SELECT courses_title FROM courses WHERE courses_title='"+courses_title+"' LIMIT 1";
        @SuppressLint("Recycle") Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }
    //edit data
    public void update(int id,String courses_title,String courses_content,String courses_icon,String courses_attachment, String courses_creation) {
        sqlDb.execSQL("UPDATE "+dbTable+" SET courses_title='"+courses_title+"', courses_content='"+courses_content+"', courses_icon='"+courses_icon+"', courses_attachment='"+courses_attachment+"', courses_creation='"+courses_creation+"'   WHERE _id=" + id);
    }

    //delete data
    public void delete(int id) {
        sqlDb.execSQL("DELETE FROM "+dbTable+" WHERE _id="+id);
    }


    //fetch data
    public Cursor fetchAllData(Context context) {

        PreferenceManager.setDefaultValues(context, R.xml.user_settings, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String[] columns = new String[]{"_id", "courses_title", "courses_content", "courses_icon","courses_attachment","courses_creation"};

        if (sp.getString("sortDBC", "title").equals("title")) {
            return sqlDb.query(dbTable, columns, null, null, null, null, "courses_title");

        } else if (sp.getString("sortDBC", "title").equals("create")) {
            return sqlDb.query(dbTable, columns, null, null, null, null, "courses_creation");
        }

        return null;
    }

    //fetch data by filter
    Cursor fetchDataByFilter(String inputText,String filterColumn) throws SQLException {
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
