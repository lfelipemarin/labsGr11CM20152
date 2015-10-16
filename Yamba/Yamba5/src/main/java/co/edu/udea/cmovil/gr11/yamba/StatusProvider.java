package co.edu.udea.cmovil.gr11.yamba;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.IllegalFormatCodePointException;

public class StatusProvider extends ContentProvider {

    private static final String TAG = StatusProvider.class.getSimpleName();
    private DbHelper dbhelper;

    public StatusProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String where;
        switch (sURIMatcher.match(uri)){
            case StatusContract.STATUS_DIR:
                where = (selection == null) ? "1" : selection;
                break;
            case StatusContract.STATUS_ITEM:
                long id = ContentUris.parseId(uri);
                where = StatusContract.Column.ID
                        + "="
                        + id
                        +(TextUtils.isEmpty(selection) ? "" : "and ( "
                        + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        int ret = db.delete(StatusContract.TABLE, where, selectionArgs);

        if (ret > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "delete records: " + ret);
        return ret;

    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)){
            case StatusContract.STATUS_DIR:
                Log.d(TAG, "goType: " + StatusContract.STATUS_TYPE_DIR);
                return StatusContract.STATUS_TYPE_DIR;
            case StatusContract.STATUS_ITEM:
                Log.d(TAG, "goType: " + StatusContract.STATUS_TYPE_ITEM);
                return StatusContract.STATUS_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;

        if (sURIMatcher.match(uri) != StatusContract.STATUS_DIR){
            throw new IllegalArgumentException("Illegal uri: " + uri);
        }
        SQLiteDatabase db = dbhelper.getWritableDatabase(); //Obtiene instancia de la DB
        long rowId = db.insertWithOnConflict(StatusContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE); //Inserta

        if (rowId != -1){
            long id = values.getAsLong(StatusContract.Column.ID);
            ret = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "Insert Uri: " + ret);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ret;
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreated");
        dbhelper = new DbHelper(getContext());
        return true;
    }

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE, StatusContract.STATUS_DIR);
        sURIMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE + "/#", StatusContract.STATUS_ITEM);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String where;
        switch (sURIMatcher.match(uri)){
            case StatusContract.STATUS_DIR:
                where = selection;
                break;
            case StatusContract.STATUS_ITEM:
                Long id = ContentUris.parseId(uri);
                where = StatusContract.Column.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : "and ( "
                        + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        int ret = db.update(StatusContract.TABLE, values, where, selectionArgs); //Actualiza

        if (ret > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "updated records: " + ret);
        return ret;
    }
}
