package com.binish.parentallock.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.binish.parentallock.Models.ProfileModel;
import com.binish.parentallock.Utils.PasswordGeneration;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ParentalLock";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PASS_CHECK = "TablePassCheck";
    private static final String PASS_PACKAGE_NAME = "package_name";
    private static final String PASS_CHECK = "check_pass";
    private static final String TABLE_LOCK_UNLOCK = "TableLockUnlock";
    private static final String LOCK_UNLOCK_NAME = "package_name";
    private static final String LOCK_UNLOCK_PROFILE = "profile_name";
    private static final String LOCK_UNLOCK_CHECK = "check_lock_unlock";
    private static final String LOCK_UNLOCK_PASSWORD = "lock_unlock_password";
    private static final String TABLE_PROFILE_LIST = "TableProfileList";
    private static final String PROFILE_LIST_NAME = "profile_name";
    private static final String PROFILE_LIST_FROM = "profile_from";
    private static final String PROFILE_LIST_TO = "profile_to";
    private static final String PROFILE_LIST_IS_ACTIVE = "is_active";
    private static final String TABLE_UNIVERSAL_PASSWORD = "TablePassword";
    private static final String PASSWORD = "password";
    private static final String PASSWORD_UNIQUE_ID = "unique_id";

    Context context;
    SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        createProfilesTable();
        createUniversalPasswordTable();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTablePassCheck = "CREATE TABLE IF NOT EXISTS "+TABLE_PASS_CHECK+"("
                +PASS_PACKAGE_NAME+" TEXT PRIMARY KEY,"
                +PASS_CHECK+" TEXT" +")";

        db.execSQL(createTablePassCheck);
    }

    public void createLockUnlockTable(){
        db = this.getWritableDatabase();
        try{
            String createTableLockUnlock = "CREATE TABLE IF NOT EXISTS "+TABLE_LOCK_UNLOCK+ "("
                    +LOCK_UNLOCK_NAME+" TEXT PRIMARY KEY,"
                    +LOCK_UNLOCK_PROFILE+" TEXT,"
                    +LOCK_UNLOCK_PASSWORD+" TEXT,"
                    +LOCK_UNLOCK_CHECK+" TEXT"+")";
            db.execSQL(createTableLockUnlock);
            db.close();
        }catch (SQLException e){
            Log.i("LockUnlockTable","Exception: "+e);
            db.close();
        }
    }

    private void createProfilesTable(){
        db= this.getWritableDatabase();
        String createTableLockUnlock = "CREATE TABLE IF NOT EXISTS "+TABLE_PROFILE_LIST+ "("
                +PROFILE_LIST_NAME+" TEXT PRIMARY KEY,"
                +PROFILE_LIST_TO+" TEXT,"
                +PROFILE_LIST_FROM+" TEXT,"
                +PROFILE_LIST_IS_ACTIVE+" TEXT"+")";
        db.execSQL(createTableLockUnlock);
        db.close();
    }

    private void createUniversalPasswordTable(){
        db = this.getWritableDatabase();
        String createTable = "CREATE TABLE IF NOT EXISTS "+TABLE_UNIVERSAL_PASSWORD+ "("
                +PASSWORD_UNIQUE_ID+" TEXT PRIMARY KEY,"
                +PASSWORD+" TEXT UNIQUE)";
        db.execSQL(createTable);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertLockUnlock(String packageName,boolean check){
        db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put(LOCK_UNLOCK_NAME,packageName);
        c.put(LOCK_UNLOCK_CHECK,String.valueOf(check));
        c.put(LOCK_UNLOCK_PROFILE,"");
        c.put(LOCK_UNLOCK_PASSWORD,"");
        db.replace(TABLE_LOCK_UNLOCK,null,c);
    }

    public boolean checkLockUnlock(String packageName){
        db = this.getReadableDatabase();
        String select = "SELECT * FROM "+TABLE_LOCK_UNLOCK+" WHERE "+LOCK_UNLOCK_NAME+"='"+packageName+"'";
        Cursor c = db.rawQuery(select,null);
        String check = "false";
        while(c.moveToNext()){
            check = c.getString(c.getColumnIndex(LOCK_UNLOCK_CHECK));
        }
        c.close();
        db.close();
        return check.equals("true");
    }

    public String getLockUnlockProfileName(String packageName){
        db = this.getReadableDatabase();
        String select = "SELECT * FROM "+TABLE_LOCK_UNLOCK+" WHERE "+LOCK_UNLOCK_NAME+"='"+packageName+"'";
        Cursor c = db.rawQuery(select,null);
        String profileName="";
        while (c.moveToNext()){
            profileName = c.getString(c.getColumnIndex(LOCK_UNLOCK_PROFILE));
        }
        c.close();
        db.close();
        return profileName;
    }
    public String getLockUnlockPassword(String packageName){
        db = this.getReadableDatabase();
        String select = "SELECT * FROM "+TABLE_LOCK_UNLOCK+" WHERE "+LOCK_UNLOCK_NAME+"='"+packageName+"'";
        Cursor c = db.rawQuery(select,null);
        String password="";
        while (c.moveToNext()){
            password = c.getString(c.getColumnIndex(LOCK_UNLOCK_PASSWORD));
        }
        c.close();
        db.close();
        return password;
    }

    public void updateLockUnlockPassword(String packageName,String password){
        db= this.getWritableDatabase();
        ContentValues cb = new ContentValues();
        cb.put(LOCK_UNLOCK_PASSWORD,password);
        db.update(TABLE_LOCK_UNLOCK,cb,LOCK_UNLOCK_NAME+"='"+packageName+"'",null);
        db.close();
    }

    public void updateLockUnlockProfile(String packageName,String profileName){
        db = this.getWritableDatabase();
        ContentValues cb = new ContentValues();
        cb.put(LOCK_UNLOCK_PROFILE,profileName);
        db.update(TABLE_LOCK_UNLOCK,cb,LOCK_UNLOCK_NAME+"='"+packageName+"'",null);
        db.close();
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
        return  (check.equals("true"));
    }

    public void dropTablePassCheck(){
        db = this.getWritableDatabase();
        String dropTable= "DROP TABLE IF EXISTS "+TABLE_PASS_CHECK;
        db.execSQL(dropTable);
        onCreate(db);
        db.close();
    }

    public void insertProfile(String profileName,String unlockFrom,String unlockTo,boolean isActive){
        db = this.getWritableDatabase();
        ContentValues cb = new ContentValues();
        cb.put(PROFILE_LIST_NAME,profileName);
        cb.put(PROFILE_LIST_FROM,unlockFrom);
        cb.put(PROFILE_LIST_TO,unlockTo);
        cb.put(PROFILE_LIST_IS_ACTIVE,String.valueOf(isActive));
        db.insert(TABLE_PROFILE_LIST,null,cb);
        db.close();
    }

    public ArrayList<ProfileModel> getAllProfiles(){
        db = this.getReadableDatabase();
        String selectAll = "SELECT * FROM "+TABLE_PROFILE_LIST;
        Cursor c = db.rawQuery(selectAll,null);
        ArrayList<ProfileModel> list = new ArrayList<>();
        while(c.moveToNext()){
            ProfileModel profile = new ProfileModel();
            profile.setProfileName(c.getString(c.getColumnIndex(PROFILE_LIST_NAME)));
            profile.setUnlockFrom(c.getString(c.getColumnIndex(PROFILE_LIST_FROM)));
            profile.setUnlockTo(c.getString(c.getColumnIndex(PROFILE_LIST_TO)));
            profile.setActive(c.getString(c.getColumnIndex(PROFILE_LIST_IS_ACTIVE)).equalsIgnoreCase("true"));
            list.add(profile);
        }
        c.close();
        db.close();
        return list;
    }

    public void updateProfile(String profileName,String unlockFrom,String unlockTo,boolean isActive){
        db = this.getWritableDatabase();
        ContentValues cb = new ContentValues();
        cb.put(PROFILE_LIST_NAME,profileName);
        cb.put(PROFILE_LIST_FROM,unlockFrom);
        cb.put(PROFILE_LIST_TO,unlockTo);
        cb.put(PROFILE_LIST_IS_ACTIVE,String.valueOf(isActive));
        db.replace(TABLE_PROFILE_LIST,null,cb);
        db.close();
    }

    public ProfileModel getIndividualProfile(String profileName){
        db = this.getReadableDatabase();
        String select = "SELECT * FROM "+TABLE_PROFILE_LIST+" WHERE "+PROFILE_LIST_NAME+"='"+profileName+"'";
        Cursor c = db.rawQuery(select,null);
        ProfileModel profileModel = new ProfileModel();
        while(c.moveToNext()){
            profileModel.setProfileName(c.getString(c.getColumnIndex(PROFILE_LIST_NAME)));
            profileModel.setUnlockFrom(c.getString(c.getColumnIndex(PROFILE_LIST_FROM)));
            profileModel.setUnlockTo(c.getString(c.getColumnIndex(PROFILE_LIST_TO)));
            profileModel.setActive((c.getString(c.getColumnIndex(PROFILE_LIST_IS_ACTIVE)).equalsIgnoreCase("true")));
        }
        c.close();
        db.close();
        return profileModel;
    }

    public void deleteIndividualProfile(String profileName){
        db = this.getWritableDatabase();
        db.delete(TABLE_PROFILE_LIST,PROFILE_LIST_NAME+"='"+profileName+"'",null);
        ContentValues cb = new ContentValues();
        cb.put(LOCK_UNLOCK_PROFILE,"");
        db.update(TABLE_LOCK_UNLOCK,cb,LOCK_UNLOCK_PROFILE+"='"+profileName+"'",null);
        db.close();
    }

    public void insertPassword(String password){
        db = this.getWritableDatabase();
        String hashPassword = PasswordGeneration.getSecurePassword(password);
        ContentValues cb = new ContentValues();
        cb.put(PASSWORD_UNIQUE_ID,1);
        cb.put(PASSWORD,hashPassword);
        db.replace(TABLE_UNIVERSAL_PASSWORD,null,cb);
        db.close();
    }
}
