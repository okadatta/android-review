package okadatta.util.androidreview.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LaunchHistorySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB = "okadatta.util.androidreview.db";
    private static final int DB_VERSION = 1;
    private static final String tableName = "launch_history";
    private static final String CREATE_TABLE = "create table " + tableName + " (col_datetime INTEGER not null)";
    private static final String DROP_TABLE = "drop table " + tableName + ";";
    public static final int storePeriodInDay = 180;
    public static final int storeCount = 512;

    public LaunchHistorySQLiteOpenHelper(Context c) {
        super(c, DB, null, DB_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void deleteAll(SQLiteDatabase db) {
        db.delete(tableName, null, null);
    }

    public static String getTableName(){
        return tableName;
    }
}
