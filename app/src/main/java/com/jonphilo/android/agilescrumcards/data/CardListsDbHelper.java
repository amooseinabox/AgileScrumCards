package com.jonphilo.android.agilescrumcards.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jonathanphilo on 5/21/15.
 */
public class CardListsDbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CardListsContract.CardLists.TABLE_NAME + " (" +
                    CardListsContract.CardLists._ID + " INTEGER PRIMARY KEY," +
                    CardListsContract.CardLists.COLUMN_NAME_LIST_ID + TEXT_TYPE + COMMA_SEP +
                    CardListsContract.CardLists.COLUMN_NAME_LIST_NAME + TEXT_TYPE + COMMA_SEP +
                    CardListsContract.CardLists.COLUMN_NAME_LIST_CONTENT + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS" + CardListsContract.CardLists.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CardLists.db";

    public CardListsDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }
}

