package com.jepaynedev.townmap.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.Hashtable;

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

    private Cursor query(String sql) {
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

    public Hashtable<Integer, String> getCreatureNameMap() {
        Hashtable<Integer, String> creatureTable = new Hashtable<>();

        // Query the creature database for creature ids and names
        Cursor cursor = query(DatabaseSchema.Creature.GET_CREATURE_NAMES);

        // Get the indices of id and name columns
        try {
            int columnCreatureId = cursor.getColumnIndexOrThrow(
                    DatabaseContract.Creature.COLUMN_NAME_CREATURE_ID);
            int columnCreatureName = cursor.getColumnIndexOrThrow(
                    DatabaseContract.Creature.COLUMN_NAME_CREATURE_NAME);

            // Iterate over the cursor to build the Hashtable
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                creatureTable.put(
                        cursor.getInt(columnCreatureId), cursor.getString(columnCreatureName));
                cursor.moveToNext();
            }
        } catch (IllegalArgumentException error) {
            // TODO: Handle bad query or corrupted database
            error.printStackTrace();
        }

        // Return the creature name map
        return creatureTable;
    }
}
