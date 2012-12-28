package com.mattprecious.otherdevice.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mattprecious.otherdevice.model.PrimaryProfile;
import com.mattprecious.otherdevice.model.SecondaryProfile;

public class DbAdapter {

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbAdapter(Context context) {
        dbHelper = new DbHelper(context);
    }

    public void openReadable() throws SQLException {
        db = dbHelper.getReadableDatabase();
    }

    public void openWritable() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List<PrimaryProfile> getPrimaryProfiles() {
        List<PrimaryProfile> profiles = new ArrayList<PrimaryProfile>();

        Cursor c = db.query(DbHelper.PRIMARY_PROFILES_TABLE_NAME, null, null, null, null, null,
                DbHelper.PRIMARY_PROFILES_KEY_NAME + " COLLATE NOCASE ASC");

        c.moveToFirst();
        while (!c.isAfterLast()) {
            PrimaryProfile profile = cursorToPrimaryProfile(c);
            profiles.add(profile);
            c.moveToNext();
        }

        c.close();
        return profiles;
    }

    public PrimaryProfile getPrimaryProfileByTag(String tag) {
        String selection = DbHelper.PRIMARY_PROFILES_KEY_TAG + "=?";
        String[] selectionArgs = { tag };
        Cursor c = db.query(DbHelper.PRIMARY_PROFILES_TABLE_NAME, null, selection, selectionArgs,
                null, null, null);

        PrimaryProfile profile = null;
        if (c.moveToFirst()) {
            profile = cursorToPrimaryProfile(c);
        }

        c.close();

        return profile;
    }

    public PrimaryProfile getPrimaryProfileByPackage(String packageName) {
        String selection = DbHelper.PRIMARY_PROFILES_KEY_PACKAGE + "=?";
        String[] selectionArgs = { packageName };
        Cursor c = db.query(DbHelper.PRIMARY_PROFILES_TABLE_NAME, null, selection, selectionArgs,
                null, null, null);

        PrimaryProfile profile = null;
        if (c.moveToFirst()) {
            profile = cursorToPrimaryProfile(c);
        }

        c.close();

        return profile;
    }

    public List<SecondaryProfile> getSecondaryProfiles() {
        List<SecondaryProfile> profiles = new ArrayList<SecondaryProfile>();

        Cursor c = db.query(DbHelper.SECONDARY_PROFILES_TABLE_NAME, null, null, null, null, null,
                DbHelper.SECONDARY_PROFILES_KEY_NAME + " COLLATE NOCASE ASC");

        c.moveToFirst();
        while (!c.isAfterLast()) {
            SecondaryProfile profile = cursorToSecondaryProfile(c);
            profiles.add(profile);
            c.moveToNext();
        }

        c.close();
        return profiles;
    }

    public SecondaryProfile getSecondaryProfileByTag(String tag) {
        String selection = DbHelper.SECONDARY_PROFILES_KEY_TAG + "=?";
        String[] selectionArgs = { tag };
        Cursor c = db.query(DbHelper.SECONDARY_PROFILES_TABLE_NAME, null, selection, selectionArgs,
                null, null, null);

        SecondaryProfile profile = null;
        if (c.moveToFirst()) {
            profile = cursorToSecondaryProfile(c);
        }

        c.close();

        return profile;
    }

    public boolean insertPrimaryProfile(PrimaryProfile profile) {
        ContentValues values = profileToValues(profile);
        long result = db.insert(DbHelper.PRIMARY_PROFILES_TABLE_NAME, null, values);

        return result > -1;
    }

    public boolean updatePrimaryProfile(PrimaryProfile profile) {
        ContentValues values = profileToValues(profile);
        String where = DbHelper.PRIMARY_PROFILES_KEY_ID + "=?";
        String[] whereArgs = { String.valueOf(profile.getId()) };
        int result = db.update(DbHelper.PRIMARY_PROFILES_TABLE_NAME, values, where, whereArgs);

        return result > 0;
    }

    public boolean deletePrimaryProfile(PrimaryProfile profile) {
        String where = DbHelper.PRIMARY_PROFILES_KEY_ID + "=?";
        String[] whereArgs = { String.valueOf(profile.getId()) };
        int result = db.delete(DbHelper.PRIMARY_PROFILES_TABLE_NAME, where, whereArgs);

        return result > 0;
    }

    public boolean insertSecondaryProfile(SecondaryProfile profile) {
        ContentValues values = profileToValues(profile);
        long result = db.insert(DbHelper.SECONDARY_PROFILES_TABLE_NAME, null, values);

        return result > -1;
    }

    public boolean updateSecondaryProfile(SecondaryProfile profile) {
        ContentValues values = profileToValues(profile);
        String where = DbHelper.SECONDARY_PROFILES_KEY_ID + "=?";
        String[] whereArgs = { String.valueOf(profile.getId()) };
        int result = db.update(DbHelper.SECONDARY_PROFILES_TABLE_NAME, values, where, whereArgs);

        return result > 0;
    }

    public boolean deleteSecondaryProfile(SecondaryProfile profile) {
        String where = DbHelper.SECONDARY_PROFILES_KEY_ID + "=?";
        String[] whereArgs = { String.valueOf(profile.getId()) };
        int result = db.delete(DbHelper.SECONDARY_PROFILES_TABLE_NAME, where, whereArgs);

        return result > 0;
    }

    private PrimaryProfile cursorToPrimaryProfile(Cursor c) {
        PrimaryProfile profile = new PrimaryProfile();
        profile.setId(c.getInt(c.getColumnIndex(DbHelper.PRIMARY_PROFILES_KEY_ID)));
        profile.setName(c.getString(c.getColumnIndex(DbHelper.PRIMARY_PROFILES_KEY_NAME)));
        profile.setTag(c.getString(c.getColumnIndex(DbHelper.PRIMARY_PROFILES_KEY_TAG)));
        profile.setPackageName(c.getString(c.getColumnIndex(DbHelper.PRIMARY_PROFILES_KEY_PACKAGE)));
        profile.setEnabled(c.getInt(c.getColumnIndex(DbHelper.PRIMARY_PROFILES_KEY_ENABLED)) > 0);
        return profile;
    }

    private ContentValues profileToValues(PrimaryProfile profile) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.PRIMARY_PROFILES_KEY_NAME, profile.getName());
        values.put(DbHelper.PRIMARY_PROFILES_KEY_TAG, profile.getTag());
        values.put(DbHelper.PRIMARY_PROFILES_KEY_PACKAGE, profile.getPackageName());
        values.put(DbHelper.PRIMARY_PROFILES_KEY_ENABLED, profile.isEnabled());
        return values;
    }

    private SecondaryProfile cursorToSecondaryProfile(Cursor c) {
        SecondaryProfile profile = new SecondaryProfile();
        profile.setId(c.getInt(c.getColumnIndex(DbHelper.SECONDARY_PROFILES_KEY_ID)));
        profile.setName(c.getString(c.getColumnIndex(DbHelper.SECONDARY_PROFILES_KEY_NAME)));
        profile.setTag(c.getString(c.getColumnIndex(DbHelper.SECONDARY_PROFILES_KEY_TAG)));
        profile.setEnabled(c.getInt(c.getColumnIndex(DbHelper.SECONDARY_PROFILES_KEY_ENABLED)) > 0);
        profile.setUnconnectedOnly(c.getInt(c
                .getColumnIndex(DbHelper.SECONDARY_PROFILES_KEY_UNCONNECTED_ONLY)) > 0);
        profile.setRingtone(c.getString(c.getColumnIndex(DbHelper.SECONDARY_PROFILES_KEY_RINGTONE)));
        profile.setVibrate(c.getInt(c.getColumnIndex(DbHelper.SECONDARY_PROFILES_KEY_VIBRATE)) > 0);
        profile.setLed(c.getInt(c.getColumnIndex(DbHelper.SECONDARY_PROFILES_KEY_LED)) > 0);
        return profile;
    }

    private ContentValues profileToValues(SecondaryProfile profile) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.SECONDARY_PROFILES_KEY_NAME, profile.getName());
        values.put(DbHelper.SECONDARY_PROFILES_KEY_TAG, profile.getTag());
        values.put(DbHelper.SECONDARY_PROFILES_KEY_ENABLED, profile.isEnabled());
        values.put(DbHelper.SECONDARY_PROFILES_KEY_UNCONNECTED_ONLY, profile.isUnconnectedOnly());
        values.put(DbHelper.SECONDARY_PROFILES_KEY_RINGTONE, profile.getRingtone());
        values.put(DbHelper.SECONDARY_PROFILES_KEY_VIBRATE, profile.isVibrate());
        values.put(DbHelper.SECONDARY_PROFILES_KEY_LED, profile.isLed());
        return values;
    }

    private class DbHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "data.db";

        private static final String PRIMARY_PROFILES_TABLE_NAME = "primary_profiles";
        private static final String PRIMARY_PROFILES_KEY_ID = "_id";
        private static final String PRIMARY_PROFILES_KEY_TAG = "tag";
        private static final String PRIMARY_PROFILES_KEY_NAME = "name";
        private static final String PRIMARY_PROFILES_KEY_PACKAGE = "package";
        private static final String PRIMARY_PROFILES_KEY_ENABLED = "enabled";

        private static final String PRIMARY_PROFILES_TABLE_CREATE = "CREATE TABLE "
                + PRIMARY_PROFILES_TABLE_NAME + "(" + PRIMARY_PROFILES_KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PRIMARY_PROFILES_KEY_TAG
                + " TEXT KEY NOT NULL, " + PRIMARY_PROFILES_KEY_NAME + " TEXT NOT NULL, "
                + PRIMARY_PROFILES_KEY_PACKAGE + " TEXT KEY NOT NULL, "
                + PRIMARY_PROFILES_KEY_ENABLED + " INTEGER);";

        private static final String SECONDARY_PROFILES_TABLE_NAME = "secondary_profiles";
        private static final String SECONDARY_PROFILES_KEY_ID = "_id";
        private static final String SECONDARY_PROFILES_KEY_TAG = "tag";
        private static final String SECONDARY_PROFILES_KEY_NAME = "name";
        private static final String SECONDARY_PROFILES_KEY_ENABLED = "enabled";
        private static final String SECONDARY_PROFILES_KEY_UNCONNECTED_ONLY = "unconnected_only";
        private static final String SECONDARY_PROFILES_KEY_RINGTONE = "ringtone";
        private static final String SECONDARY_PROFILES_KEY_VIBRATE = "vibrate";
        private static final String SECONDARY_PROFILES_KEY_LED = "led";

        private static final String SECONDARY_PROFILES_TABLE_CREATE = "CREATE TABLE "
                + SECONDARY_PROFILES_TABLE_NAME + "(" + SECONDARY_PROFILES_KEY_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SECONDARY_PROFILES_KEY_TAG
                + " TEXT KEY NOT NULL, " + SECONDARY_PROFILES_KEY_NAME + " TEXT NOT NULL, "
                + SECONDARY_PROFILES_KEY_ENABLED + " INTEGER, "
                + SECONDARY_PROFILES_KEY_UNCONNECTED_ONLY + " INTEGER, "
                + SECONDARY_PROFILES_KEY_RINGTONE + " TEXT, " + SECONDARY_PROFILES_KEY_VIBRATE
                + " INTEGER, " + SECONDARY_PROFILES_KEY_LED + " INTEGER);";

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(PRIMARY_PROFILES_TABLE_CREATE);
            db.execSQL(SECONDARY_PROFILES_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (oldVersion) {

            }
        }

    }

}
