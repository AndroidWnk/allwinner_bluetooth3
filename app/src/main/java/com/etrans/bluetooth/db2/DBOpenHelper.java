package com.etrans.bluetooth.db2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String PHONE_BOOK = "PHONE_BOOK";

    public static String getPhoneBookName() {
        return PHONE_BOOK;
    }


    private static final String CREATE_PHONE_BOOK_TAB = "CREATE TABLE IF NOT EXISTS  "
            + PHONE_BOOK
            + " "
            + "(ID , NAME , NUM , INITIAL_KEY , PY_KEY , INITIAL_KEY_T9 , PY_KEY_T9 ,"
            + " PRIMARY KEY (ID , NAME , NUM))";

    private static final String DB_NAME = "bt.db";
    private static final int VERSION = 1;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PHONE_BOOK_TAB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
