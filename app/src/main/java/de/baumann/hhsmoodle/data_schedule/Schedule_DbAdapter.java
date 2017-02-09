package de.baumann.hhsmoodle.data_schedule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Schedule_DbAdapter {

    //define static variable
    private static final int dbVersion =6;
    private static final String dbName = "schedule_DB_v01.db";
    private static final String dbTable = "schedule";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,dbName,null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement, schedule_title, schedule_content, schedule_icon, schedule_attachment, schedule_creation, schedule_id, UNIQUE(schedule_id))");
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

    public Schedule_DbAdapter(Context context) {
        this.c = context;
    }
    public void open() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper(c);
        sqlDb = dbHelper.getWritableDatabase();
    }

    //insert data
    @SuppressWarnings("SameParameterValue")
    public void insert(String schedule_title, String schedule_content, String schedule_icon, String schedule_attachment, String schedule_creation, String schedule_id) {
        if(!isExist(schedule_title)) {
            sqlDb.execSQL("INSERT INTO schedule (schedule_title, schedule_content, schedule_icon, schedule_attachment, schedule_creation, schedule_id) VALUES('" + schedule_title + "','" + schedule_content + "','" + schedule_icon + "','" + schedule_attachment + "','" + schedule_creation + "','" + schedule_id + "')");
        }
    }
    //check entry already in database or not
    private boolean isExist(String schedule_id){
        String query = "SELECT schedule_id FROM schedule WHERE schedule_id='"+schedule_id+"' LIMIT 1";
        @SuppressLint("Recycle") Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }
    //edit data
    @SuppressWarnings("UnusedParameters")
    public void update(int id, String schedule_title, String schedule_content, String schedule_icon, String schedule_attachment, String schedule_creation, String schedule_id) {
        sqlDb.execSQL("UPDATE "+dbTable+" SET schedule_title='"+schedule_title+"', schedule_content='"+schedule_content+"', schedule_icon='"+schedule_icon+"', schedule_attachment='"+schedule_attachment+"', schedule_creation='"+schedule_creation+"', schedule_creation='"+schedule_creation+"'   WHERE _id=" + id);
    }

    //fetch data
    public Cursor fetchAllData() {
        String[] columns = new String[]{"_id", "schedule_title", "schedule_content", "schedule_icon","schedule_attachment","schedule_creation","schedule_id"};
        return sqlDb.query(dbTable, columns, null, null, null, null, "schedule_id");
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
