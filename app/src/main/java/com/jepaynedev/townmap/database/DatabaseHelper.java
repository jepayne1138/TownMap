package com.jepaynedev.townmap.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by James Payne on 7/18/2016.
 * jepayne1138@gmail.com
 *
 * https://developer.android.com/training/basics/data-storage/databases.html
 *
 * Assistance with modifications from:
 * http://stackoverflow.com/a/9109728
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TownMapDatabase.db";
    private final Context context;
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context.getDatabasePath(DATABASE_NAME);
        this.context = context;
    }

    public void createDataBase() throws IOException  // Not SQLiteException?
    {
        //If the database does not exist, copy it from the assets.
        if(!checkDataBase())
        {
            this.getReadableDatabase();
            this.close();
            try
            {
                //Copy the database from assests
                copyDataBase();
            }
            catch (IOException mIOException)
            {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private boolean checkDataBase() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    //Copy the database from assets
    private void copyDataBase() throws IOException
    {
        InputStream inputStream = context.getAssets().open(DATABASE_NAME);
        File outFile = context.getDatabasePath(DATABASE_NAME);
        OutputStream outputStream = new FileOutputStream(outFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0)
        {
            outputStream.write(buffer , 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    //Open the database, so we can query it
    public boolean openDataBase() throws SQLException
    {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        database = SQLiteDatabase.openDatabase(
                dbFile.getPath(), null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return database != null;  // Return False if we were unable to create the database
    }

    @Override
    public synchronized void close()
    {
        if(database != null)
            database.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DatabaseSchema.User.CREATE_TABLE);
        sqLiteDatabase.execSQL(DatabaseSchema.Catch.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL(DatabaseSchema.User.DROP_TABLE);
        sqLiteDatabase.execSQL(DatabaseSchema.Catch.DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
