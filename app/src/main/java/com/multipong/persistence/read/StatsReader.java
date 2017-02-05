package com.multipong.persistence.read;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.multipong.persistence.MultipongDatabase;
import com.multipong.persistence.pojos.Stats;

public class StatsReader {

    private MultipongDatabase multipongDatabase;

    public StatsReader(MultipongDatabase database) {
        multipongDatabase = database;
    }

    public Stats getBestScoreFor(Stats.Modality modality) {
        SQLiteDatabase db = multipongDatabase.getWritableDatabase();
        String countQuery = "SELECT * FROM " + MultipongDatabase.STATS_TABLE
                + " WHERE "
                + MultipongDatabase.STATS_KEY_MODALITY + " =? "
                + " ORDER BY " + MultipongDatabase.STATS_KEY_SCORE + " DESC"
                + " LIMIT 1";
        String[] whereArgs = new String[] { modality.toString() };
        Cursor cursor = db.rawQuery(countQuery, whereArgs);
        if (!cursor.moveToFirst())
            return null;
        String name = cursor.getString(cursor.getColumnIndex(MultipongDatabase.STATS_KEY_NAME));
        int score = cursor.getInt(cursor.getColumnIndex(MultipongDatabase.STATS_KEY_SCORE));
        Stats bestScore = new Stats().withScore(score)
                                     .withModality(modality)
                                     .withName(name);
        return bestScore;
    }
}
