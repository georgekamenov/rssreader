package info.kamenov.F104606.rssreader.db;

import android.provider.BaseColumns;

public class SettingsContract {
    private SettingsContract() {}

    /* Inner class that defines the table contents */
    public static class SettingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "settings";
        public static final String COLUMN_FEED = "feed";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_DEFAULT = "is_default";
    }
}
