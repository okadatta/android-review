package okadatta.util.androidreview.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import okadatta.util.androidreview.sqlite.LaunchHistorySQLiteOpenHelper;

public class LaunchHistoryDAO {

    public static void registerLaunchHistory(Context context){
        SQLiteDatabase database;
        ContentValues values;
        LaunchHistorySQLiteOpenHelper helper = new LaunchHistorySQLiteOpenHelper(context);
        database = helper.getWritableDatabase();

        // Record launch datetime
        values = new ContentValues();
        values.put("col_datetime", System.currentTimeMillis());
        database.insert(LaunchHistorySQLiteOpenHelper.getTableName(), null, values);

        // Delete launch history records that exceed the datetime defined at SQLiteOpenHelperClass
        List<String> deleteTarget1 = new ArrayList<>();
        Cursor cursor1 = database.query(LaunchHistorySQLiteOpenHelper.getTableName(), null, null, null, null, null, "col_datetime asc");
        boolean isNotEoF1 = cursor1.moveToFirst();
        while(isNotEoF1){
            if(cursor1.getLong(0) / 1000 < System.currentTimeMillis() / 1000 - LaunchHistorySQLiteOpenHelper.storePeriodInDay * 24 * 60 * 60) {
                deleteTarget1.add(Long.toString(cursor1.getLong(0)));
                isNotEoF1 = cursor1.moveToNext();
            } else {
                isNotEoF1 = false;
            }
        }
        cursor1.close();
        for (String target: deleteTarget1){
            String[] strArray = {target};
            database.delete(LaunchHistorySQLiteOpenHelper.getTableName(), "col_datetime=?", strArray);
        }

        // Delete launch history records that exceed the count defined at SQLiteOpenHelperClass
        List<String> deleteTarget2 = new ArrayList<>();
        Cursor cursor2 = database.query(LaunchHistorySQLiteOpenHelper.getTableName(), null, null, null, null, null, "col_datetime desc");
        boolean isNotEoF2 = cursor2.moveToFirst();
        int count = 0;
        while(isNotEoF2){
            count++;
            if(LaunchHistorySQLiteOpenHelper.storeCount < count) {
                deleteTarget2.add(Long.toString(cursor2.getLong(0)));
            }
            isNotEoF2 = cursor2.moveToNext();
        }
        cursor2.close();
        for (String target: deleteTarget2){
            String[] strArray = {target};
            database.delete(LaunchHistorySQLiteOpenHelper.getTableName(), "col_datetime=?", strArray);
        }

        // Close database
        database.close();

    }

    // Acquire launch count
    public static int getLaunchCount(Context context){
        // Open database
        SQLiteDatabase database;
        LaunchHistorySQLiteOpenHelper helper = new LaunchHistorySQLiteOpenHelper(context);
        database = helper.getReadableDatabase();

        // Count app launch records
        Cursor cursor = database.query(LaunchHistorySQLiteOpenHelper.getTableName(), null, null, null, null, null, null);
        boolean isNotEoF = cursor.moveToFirst();
        int count = 0;
        while(isNotEoF){
            count++;
            isNotEoF = cursor.moveToNext();
        }
        cursor.close();
        database.close();

        return count;
    }

    // Check app launch datetime at specified number of times before
    public static long getLaunchDateTimeMillis(Context context, int index){
        // Open database
        SQLiteDatabase database;
        LaunchHistorySQLiteOpenHelper helper = new LaunchHistorySQLiteOpenHelper(context);
        database = helper.getReadableDatabase();

        // Acquire launch datetime
        Cursor cursor = database.query(LaunchHistorySQLiteOpenHelper.getTableName(), null, null, null, null, null, "col_datetime desc");
        long ret = 0;
        int count = 0;
        boolean isNotEoF = cursor.moveToFirst();
        while (isNotEoF) {
            count++;
            if (index == count) {
                ret = cursor.getLong(0);
                break;
            } else if (index < count) {
                break;
            }
            isNotEoF = cursor.moveToNext();
        }
        cursor.close();
        database.close();

        return ret;
    }
}
