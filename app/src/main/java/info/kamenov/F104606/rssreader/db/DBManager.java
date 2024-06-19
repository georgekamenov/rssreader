package info.kamenov.F104606.rssreader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class DBManager {

    private SettingsDbHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }
    public DBManager open() throws SQLException {
        dbHelper = new SettingsDbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String feed, boolean isActive, boolean isDefault) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(SettingsContract.SettingsEntry.COLUMN_FEED, feed);
        contentValue.put(SettingsContract.SettingsEntry.COLUMN_ACTIVE, isActive);
        contentValue.put(SettingsContract.SettingsEntry.COLUMN_DEFAULT, isDefault);
        database.insert(SettingsContract.SettingsEntry.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { BaseColumns._ID, SettingsContract.SettingsEntry.COLUMN_FEED,
                SettingsContract.SettingsEntry.COLUMN_ACTIVE, SettingsContract.SettingsEntry.COLUMN_DEFAULT };
        Cursor cursor = database.query(SettingsContract.SettingsEntry.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String feed, boolean isActive, boolean isDefault) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SettingsContract.SettingsEntry.COLUMN_FEED, feed);
        contentValues.put(SettingsContract.SettingsEntry.COLUMN_ACTIVE, isActive);
        contentValues.put(SettingsContract.SettingsEntry.COLUMN_DEFAULT, isDefault);
        int i = database.update(SettingsContract.SettingsEntry.TABLE_NAME, contentValues, BaseColumns._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(SettingsContract.SettingsEntry.TABLE_NAME, BaseColumns._ID + "=" + _id, null);
    }

}
