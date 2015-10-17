package co.edu.udea.cmovil.gr11.yamba;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
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
            case StatusContract.STATUS_DIR: // Se eliminarán cvaros registros
                where = (selection == null) ? "1" : selection;
                break;
            case StatusContract.STATUS_ITEM:// Se eliminará un solo registro
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
            //Informa que la información para esta URI ha Cambiado
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
    } //Retorna el MIME type apropiado que se definió en StatusContract

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;

        if (sURIMatcher.match(uri) != StatusContract.STATUS_DIR){
            //Verifica que no sea el directorio completo.
            throw new IllegalArgumentException("Illegal uri: " + uri);
        }
        SQLiteDatabase db = dbhelper.getWritableDatabase(); //Obtiene instancia de la DB
        long rowId = db.insertWithOnConflict(StatusContract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE); //Inserta

        if (rowId != -1){
            long id = values.getAsLong(StatusContract.Column.ID);
            ret = ContentUris.withAppendedId(uri, id);
            Log.d(TAG, "Insert Uri: " + ret);
            //Notify that data for this uri has changed
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
        sURIMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE, StatusContract.STATUS_DIR); // Si la URI no termina con un número, se refiere a la colección
        sURIMatcher.addURI(StatusContract.AUTHORITY, StatusContract.TABLE + "/#", StatusContract.STATUS_ITEM); // Si la URI termina con un número, se refiere a un estado
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(StatusContract.TABLE); //Se qespecifica en que tabla se trabajará

        switch (sURIMatcher.match(uri)){
            case StatusContract.STATUS_DIR:
                break;
            case StatusContract.STATUS_ITEM:
                qb.appendWhere(StatusContract.Column.ID + " = "+ uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Illegal uri: " + uri);
        }

        String orderBy = (TextUtils.isEmpty(sortOrder)) ? StatusContract.DEFAULT_SORT: sortOrder; //Para ordenar los registros a obtener
        SQLiteDatabase db = dbhelper.getReadableDatabase(); //obtener instancia de la BD (Para leer)
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy); //Leer

        //Registrar cambios en la URI
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, "queried records: " + cursor.getCount());
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String where;
        switch (sURIMatcher.match(uri)){
            case StatusContract.STATUS_DIR:
                where = selection; // Se actualizarán varios registros (La URI no tiene un ID)
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
            //Informa que la información para la URI ha cambiado
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "updated records: " + ret);
        return ret;
    }
}
