package com.volcano.clipbox.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.volcano.clipbox.ClipBoxApplication;
import com.volcano.clipbox.R;
import com.volcano.clipbox.Util.Utils;
import com.volcano.clipbox.analytics.MixpanelManager;
import com.volcano.clipbox.model.Clip;

import java.util.ArrayList;

/**
 * Clipbox DatabaseHelper
 */
public final class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final int DATABASE_VERSION       = 2;
    private static final String DATABASE_NAME       = "clipbox.db";

    private static final String TABLE_CLIPS         = "clips";

    private static final String COLUMN_ID           = "Id";
    private static final String COLUMN_VALUE        = "Value";
    private static final String COLUMN_CREATE_DATE  = "CreateDate";
    private static final String COLUMN_FAVORITE     = "Favorite";

    private static final DatabaseHelper sInstance = new DatabaseHelper(ClipBoxApplication.getInstance());

    public static DatabaseHelper getInstance() {
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CLIPS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_VALUE +" TEXT, " +
                COLUMN_CREATE_DATE + " TEXT, " +
                COLUMN_FAVORITE + " INTEGER)");

        // Default Clips
        db.execSQL("INSERT INTO " + TABLE_CLIPS + " VALUES(1, '" + ClipBoxApplication.getInstance().getString(R.string.label_intro_3) + "', '2015-01-01 12:00:00', 0)");
        db.execSQL("INSERT INTO " + TABLE_CLIPS + " VALUES(2, '" + ClipBoxApplication.getInstance().getString(R.string.label_intro_2) + "', '2015-01-01 12:00:00', 0)");
        db.execSQL("INSERT INTO " + TABLE_CLIPS + " VALUES(3, '" + ClipBoxApplication.getInstance().getString(R.string.label_intro_1) + "', '2015-01-01 12:00:00', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE " + TABLE_CLIPS + " ADD COLUMN " + COLUMN_FAVORITE + " INTEGER DEFAULT 0;");
                // We want many updates, So no break statement here

            case 2:
                // Upgrade for version 3
        }

        MixpanelManager.getIntance().trackDatabaseUpgradedEvent(oldVersion, newVersion);
        Log.i(TAG, "Database upgraded from " + oldVersion + " to " + newVersion);
    }

    public synchronized void addClip(Clip clip) {
        final ContentValues values = new ContentValues();
        values.put(COLUMN_VALUE, (clip.value));
        values.put(COLUMN_CREATE_DATE, Utils.DateToString(clip.createDate));
        values.put(COLUMN_FAVORITE, Utils.getInt(clip.favorited));

        final SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CLIPS, null, values);
        closeDatabase(db);
        Log.i(TAG, "A new clip is added to database.");
    }

    public synchronized void deleteClip(Clip clip) {
        final SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CLIPS, COLUMN_ID + " = ?", new String[]{ String.valueOf(clip.id) });
        closeDatabase(db);

        Log.i(TAG, "Clip deleted from database.");
    }

    public synchronized ArrayList<Clip> getClips() {
        return getClips(null);
    }

    public synchronized ArrayList<Clip> getClips(String query) {
        final ArrayList<Clip> clips = new ArrayList<>();
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLIPS +
                (TextUtils.isEmpty(query) ? "" : " WHERE " + COLUMN_VALUE + " LIKE '%" + query + "%'")
                + " ORDER BY " + COLUMN_ID + " DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            final Clip clip = fromCursor(cursor);
            clips.add(clip);
            cursor.moveToNext();
        }
        cursor.close();
        closeDatabase(db);

        return  clips;
    }

    public synchronized Clip getLastClip() {
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLIPS + " ORDER BY " + COLUMN_ID
                + " DESC LIMIT 1", null);

        Clip clip = null;
        if (cursor.moveToFirst()) {
            clip = fromCursor(cursor);
        }
        cursor.close();
        closeDatabase(db);

        return  clip;
    }

    public synchronized boolean updateClip(Clip clip) {
        final ContentValues values = new ContentValues();
        values.put(COLUMN_VALUE, clip.value);
        values.put(COLUMN_CREATE_DATE, Utils.DateToString(clip.createDate));
        values.put(COLUMN_FAVORITE, Utils.getInt(clip.favorited));

        final SQLiteDatabase db = this.getWritableDatabase();
        final int i = db.update(TABLE_CLIPS, values , COLUMN_ID + " = ?" , new String[]{String.valueOf(clip.id)});
        db.close();
        return  i > 0 ;
    }

    private void closeDatabase(SQLiteDatabase db) {
        db.close();
    }

    private Clip fromCursor(Cursor cursor) {
        return new Clip(cursor.getInt(0), cursor.getString(1),
                Utils.StringToDate(cursor.getString(2)), Utils.getBoolean(cursor.getInt(3)));
    }
}
