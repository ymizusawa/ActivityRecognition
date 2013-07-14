package jp.yoshi_misa.recognition;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class RecognitionContentProvider extends ContentProvider {

    /** SQLiteデータベースのファイル名. */
    private static final String SQLITE_FILENAME = "recognition.db";

    private RecognitionSQLiteOpenHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        final int version;
        try {
            version = getContext().getPackageManager().getPackageInfo(
                    getContext().getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        mOpenHelper = new RecognitionSQLiteOpenHelper(getContext(),
                SQLITE_FILENAME, null, version);

        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(uri.getPathSegments().get(0), projection,
                selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final long rowId = db.insertOrThrow(uri.getPathSegments().get(0), null,
                values);
        Uri returnUri = ContentUris.withAppendedId(uri, rowId);
        getContext().getContentResolver().notifyChange(returnUri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int count = db.delete(uri.getPathSegments().get(0), selection,
                selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int count = db.update(uri.getPathSegments().get(0), values,
                selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    // private void checkUri(Uri uri) {
    // final int code = sUriMatcher.match(uri);
    // for (final Contract contract : Contract.values()) {
    // if (code == contract.allCode) {
    // return;
    // } else if (code == contract.byIdCode) {
    // return;
    // }
    // }
    // throw new IllegalArgumentException("unknown uri : " + uri);
    // }

    public class RecognitionSQLiteOpenHelper extends SQLiteOpenHelper {

        public RecognitionSQLiteOpenHelper(Context context, String name,
                CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.beginTransaction();
            try {
                db.execSQL("CREATE TABLE recognition (_id INTEGER PRIMARY KEY AUTOINCREMENT, activity_type INTEGER, confidence INTEGER, time INTEGER, elapsed_realtime_millis INTEGER)");
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

}
