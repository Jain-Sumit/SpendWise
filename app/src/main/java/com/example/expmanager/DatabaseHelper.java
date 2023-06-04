package com.example.expmanager;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME ="MY_COMPANY.DB";
    static final int DATABASE_VERSION=1;
    static final String DATABASE_TABLE = "USERS";
    static final String USER_ID = "_ID";
    static final String CATEGORY="Category";


    public DatabaseHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DB_QUERY = "CREATE TABLE " +DATABASE_TABLE +"(" + USER_ID + " INTEGER ,"+ CATEGORY +" TEXT)";
        db.execSQL(CREATE_DB_QUERY);

        String query = "INSERT INTO USERS (_ID,Category) VALUES (1, 'food'), (1, 'maintenance'), (1, 'education'),(1, 'entertainment'), (1, 'fuel'), (1, 'grocery'),(1, 'health'), (1, 'mobile recharge'), (1, 'rent'),(1, 'stationary'), (1, 'travel'), (1, 'shopping'),(1, 'income salary'), (1, 'income cash')";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
        onCreate(db);
    }

    public void insertLabel(String label){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CATEGORY,label);
        db.insert(DATABASE_TABLE,null,values);
        db.close();
    }

    public List<String> getAllLabels(){
        List<String> list = new ArrayList<String>();
        String selectQuery = "SELECT * FROM "+DATABASE_TABLE+" ORDER BY Category ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                list.add(cursor.getString(1));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
    public void deleteLabel(String label) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, CATEGORY + " = ?",
                new String[] { label });
        db.close();
    }
}