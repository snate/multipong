package com.multipong.persistence.save;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.multipong.persistence.MultipongDatabase;
import com.multipong.persistence.pojos.Stats;

public class StatsSaver {

    private MultipongDatabase multipongDatabase;

    public StatsSaver(MultipongDatabase mpdb) {
        this.multipongDatabase = mpdb;
    }

    public void save(Stats stats) {
        String name = stats.getName();
        String modality = stats.getModality().toString();
        int score = stats.getScore();

        SQLiteDatabase db = multipongDatabase.getWritableDatabase();
        String countQuery = "SELECT * "
                + " FROM " + MultipongDatabase.STATS_TABLE
                + " WHERE "
                    + MultipongDatabase.STATS_KEY_NAME + " = ? AND "
                    + MultipongDatabase.STATS_KEY_MODALITY + " = ?";
        String[] whereArgs = new String[] { name, modality };
        Cursor cursor = db.rawQuery(countQuery, whereArgs);
        if(cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MultipongDatabase.STATS_KEY_ID));
            int oldScore = cursor.getInt(cursor.getColumnIndex(MultipongDatabase.STATS_KEY_SCORE));
            if (score > oldScore) {
                String updateQuery = "UPDATE " + MultipongDatabase.STATS_TABLE
                        + " SET " + MultipongDatabase.STATS_KEY_SCORE + "=" + score
                        + " WHERE "
                        + MultipongDatabase.STATS_KEY_ID + " = " + id;
                db.execSQL(updateQuery);
            }
        } else {
            ContentValues values = new ContentValues();
            values.put(MultipongDatabase.STATS_KEY_MODALITY, modality);
            values.put(MultipongDatabase.STATS_KEY_NAME, name);
            values.put(MultipongDatabase.STATS_KEY_SCORE, score);
            db.insert(MultipongDatabase.STATS_TABLE, null, values);
        }
        cursor.close();
        db.close();
    }
}
