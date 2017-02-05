package com.multipong.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MultipongDatabase extends SQLiteOpenHelper {

    // Database name
    private static final String DATABASE_NAME = "Multipong";

    // Database version
    private static final int DATABASE_VERSION = 1;

    // Contacts table name
    public static final String STATS_TABLE = "stats";
    // Stats Table Columns names
    public static final String STATS_KEY_ID = "id";
    public static final String STATS_KEY_NAME = "name";
    public static final String STATS_KEY_MODALITY = "modality";
    public static final String STATS_KEY_SCORE = "score";
    // Stats creation string
    String CREATE_STATS_TABLE = "CREATE TABLE " + STATS_TABLE + "("
            + STATS_KEY_ID + " INTEGER PRIMARY KEY,"
            + STATS_KEY_NAME + " TEXT,"
            + STATS_KEY_MODALITY + " TEXT,"
            + STATS_KEY_SCORE + " INTEGER"
            + ")";

    public MultipongDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + STATS_TABLE);
        onCreate(db);
    }
}
