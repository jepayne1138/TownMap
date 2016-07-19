package com.jepaynedev.townmap.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

/**
 * Created by James Payne on 7/19/2016.
 * jepayne1138@gmail.com
 */
public class DatabaseAdapter {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public DatabaseAdapter(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public DatabaseAdapter createDatabase() throws SQLException
    {
        try
        {
            dbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DatabaseAdapter open() throws SQLException
    {
        try
        {
            dbHelper.openDataBase();
            dbHelper.close();
            database = dbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
        return this;
    }

    public void close()
    {
        dbHelper.close();
    }

    public Cursor query(String sql) {
        try
        {
            Cursor mCur = database.rawQuery(sql, null);
            if (mCur!=null)
            {
                mCur.moveToNext();
            }
            return mCur;
        }
        catch (SQLException mSQLException)
        {
            throw mSQLException;
        }
    }

}
