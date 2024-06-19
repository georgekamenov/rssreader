package info.kamenov.F104606.rssreader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SettingsDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Settings.db";
    private Context context;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SettingsContract.SettingsEntry.TABLE_NAME + " (" +
                    SettingsContract.SettingsEntry._ID + " INTEGER PRIMARY KEY," +
                    SettingsContract.SettingsEntry.COLUMN_FEED + " TEXT," +
                    SettingsContract.SettingsEntry.COLUMN_ACTIVE + " BOOLEAN," +
                    SettingsContract.SettingsEntry.COLUMN_DEFAULT + " BOOLEAN)";

    public SettingsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
//        initDb(db);
    }

//    private void initDb(SQLiteDatabase db) {
//        DBManager dbManager = new DBManager(context);
//        dbManager.open();
//        dbManager.insert(BBCFeeds.TOP_STORIES.toString(), true, false);
//        dbManager.close();
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
