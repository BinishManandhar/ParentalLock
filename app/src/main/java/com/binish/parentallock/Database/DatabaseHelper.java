package com.binish.parentallock.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ParentalLock";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PASS_CHECK = "TablePassCheck";
    private static final String PASS_PACKAGE_NAME = "package_name";
    private static final String PASS_CHECK = "check_pass";

    Context context;
    SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTablePassCheck = "CREATE TABLE IF NOT EXISTS "+TABLE_PASS_CHECK+"("
                +PASS_PACKAGE_NAME+" TEXT PRIMARY KEY,"
                +PASS_CHECK+" TEXT" +")";

        db.execSQL(createTablePassCheck);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertPassCheck(String packageName,boolean check){
        db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(PASS_PACKAGE_NAME,packageName);
        c.put(PASS_CHECK,String.valueOf(check));
        db.insert(TABLE_PASS_CHECK,null,c);
        db.close();
    }

    public void changePassCheck(String packageName,boolean check){
        db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(PASS_CHECK,String.valueOf(check));
        db.update(TABLE_PASS_CHECK,c,PASS_PACKAGE_NAME+"='"+packageName+"'",null);
        db.close();
    }

    public boolean getPassCheck(String packageName){
        db = this.getReadableDatabase();
        String getPass = "SELECT * FROM "+TABLE_PASS_CHECK+" WHERE "+PASS_PACKAGE_NAME+"='"+packageName+"'";
        Cursor c = db.rawQuery(getPass, null);
        String check="true";
        while (c.moveToNext()) {
            check = c.getString(c.getColumnIndex(PASS_CHECK));
        }
        c.close();
        db.close();
        Log.i("PassValue","DB: "+check);
        return  (check.equals("true"));
    }

    public void dropTablePassCheck(){
        db = this.getWritableDatabase();
        String dropTable= "DROP TABLE IF EXISTS "+TABLE_PASS_CHECK;
        db.execSQL(dropTable);
        onCreate(db);
        db.close();
    }
}
