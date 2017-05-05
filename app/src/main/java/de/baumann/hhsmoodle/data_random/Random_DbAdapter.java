package de.baumann.hhsmoodle.data_random;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Random_DbAdapter {

    //define static variable
    private static final int dbVersion =6;
    private static final String dbName = "random_DB_v01.db";
    private static final String dbTable = "random";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,dbName,null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement, random_title, random_content, random_icon, random_attachment, random_creation, UNIQUE(random_title))");
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

    public Random_DbAdapter(Context context) {
        this.c = context;
    }
    public void open() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper(c);
        sqlDb = dbHelper.getWritableDatabase();
    }

    //insert data
    @SuppressWarnings("SameParameterValue")
    public void insert(String random_title, String random_content, String random_icon, String random_attachment, String random_creation) {
        if(!isExist(random_title)) {
            sqlDb.execSQL("INSERT INTO random (random_title, random_content, random_icon, random_attachment, random_creation) VALUES('" + random_title + "','" + random_content + "','" + random_icon + "','" + random_attachment + "','" + random_creation + "')");
        }
    }
    //check entry already in database or not
    public boolean isExist(String random_title){
        String query = "SELECT random_title FROM random WHERE random_title='"+random_title+"' LIMIT 1";
        @SuppressLint("Recycle") Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }
    //edit data
    public void update(int id,String random_title,String random_content,String random_icon,String random_attachment, String random_creation) {
        sqlDb.execSQL("UPDATE "+dbTable+" SET random_title='"+random_title+"', random_content='"+random_content+"', random_icon='"+random_icon+"', random_attachment='"+random_attachment+"', random_creation='"+random_creation+"'   WHERE _id=" + id);
    }

    //delete data
    public void delete(int id) {
        sqlDb.execSQL("DELETE FROM "+dbTable+" WHERE _id="+id);
    }


    //fetch data
    public Cursor fetchAllData() {
        String[] columns = new String[]{"_id", "random_title", "random_content", "random_icon","random_attachment","random_creation"};
        return sqlDb.query(dbTable, columns, null, null, null, null, "random_title" + " COLLATE NOCASE ASC;");
    }
}
