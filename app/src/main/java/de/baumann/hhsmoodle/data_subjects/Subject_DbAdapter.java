package de.baumann.hhsmoodle.data_subjects;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Subject_DbAdapter {

    //define static variable
    private static final int dbVersion =6;
    private static final String dbName = "subject_DB_v01.db";
    private static final String dbTable = "subject";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,dbName,null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement, subject_title, subject_content, subject_icon, subject_attachment, subject_creation, UNIQUE(subject_creation))");
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

    public Subject_DbAdapter(Context context) {
        this.c = context;
    }
    public void open() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper(c);
        sqlDb = dbHelper.getWritableDatabase();
    }

    //insert data
    @SuppressWarnings("SameParameterValue")
    public void insert(String subject_title, String subject_content, String subject_icon, String subject_attachment, String subject_creation) {
        if(!isExist(subject_title)) {
            sqlDb.execSQL("INSERT INTO subject (subject_title, subject_content, subject_icon, subject_attachment, subject_creation) VALUES('" + subject_title + "','" + subject_content + "','" + subject_icon + "','" + subject_attachment + "','" + subject_creation + "')");
        }
    }
    //check entry already in database or not
    public boolean isExist(String subject_creation){
        String query = "SELECT subject_creation FROM subject WHERE subject_creation='"+subject_creation+"' LIMIT 1";
        @SuppressLint("Recycle") Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }
    //edit data
    public void update(int id,String subject_title,String subject_content,String subject_icon,String subject_attachment, String subject_creation) {
        sqlDb.execSQL("UPDATE "+dbTable+" SET subject_title='"+subject_title+"', subject_content='"+subject_content+"', subject_icon='"+subject_icon+"', subject_attachment='"+subject_attachment+"', subject_creation='"+subject_creation+"'   WHERE _id=" + id);
    }

    //delete data
    public void delete(int id) {
        sqlDb.execSQL("DELETE FROM "+dbTable+" WHERE _id="+id);
    }


    //fetch data
    public Cursor fetchAllData() {
        String[] columns = new String[]{"_id", "subject_title", "subject_content", "subject_icon","subject_attachment","subject_creation"};
        return sqlDb.query(dbTable, columns, null, null, null, null, "subject_title" + " COLLATE NOCASE ASC;");
    }
}
