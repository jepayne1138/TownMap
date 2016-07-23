package com.jepaynedev.townmap.database;

/**
 * Created by James Payne on 7/18/2016.
 * jepayne1138@gmail.com
 */
public class DatabaseSchema {

    // SQL statement for User table
    public static abstract class User {
        public static final String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + DatabaseContract.User.TABLE_NAME + " ("
                    + DatabaseContract.User.COLUMN_NAME_USER_ID + " INTEGER PRIMARY KEY,"
                    + DatabaseContract.User.COLUMN_NAME_GOOGLE_ID + " INTEGER,"
                    + DatabaseContract.User.COLUMN_NAME_TRAINER_NAME + " TEXT,"
                    + DatabaseContract.User.COLUMN_NAME_TRAINER_LEVEL + " INTEGER"
                    + ");";
        public static final String DROP_TABLE =
                "DROP TABLE IF EXIST " + DatabaseContract.User.TABLE_NAME;
    }

    // SQL statement for Catch table
    public static abstract class Catch {
        public static final String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + DatabaseContract.Catch.TABLE_NAME + " ("
                    + DatabaseContract.Catch.COLUMN_NAME_CATCH_ID + " INTEGER PRIMARY KEY,"
                    + DatabaseContract.Catch.COLUMN_NAME_TRAINER_NAME + " TEXT,"
                    + DatabaseContract.Catch.COLUMN_NAME_TRAINER_LEVEL + " INTEGER,"
                    + DatabaseContract.Catch.COLUMN_NAME_USING_LURE + " INTEGER,"
                    + DatabaseContract.Catch.COLUMN_NAME_USING_INCENSE + " INTEGER,"
                    + DatabaseContract.Catch.COLUMN_NAME_CREATURE_ID + " INTEGER,"
                    + DatabaseContract.Catch.COLUMN_NAME_LATITUDE + " REAL,"
                    + DatabaseContract.Catch.COLUMN_NAME_LONGITUDE + " REAL,"
                    + DatabaseContract.Catch.COLUMN_NAME_CATCH_TIME + " REAL"
                    + ");";
        public static final String DROP_TABLE =
                "DROP TABLE IF EXIST " + DatabaseContract.Catch.TABLE_NAME;
    }

    // SQL statements for Creature table
    public static abstract class Creature {
        public static final String GET_CREATURE_NAMES =
                "SELECT "
                    + DatabaseContract.Creature.COLUMN_NAME_CREATURE_ID + ", "
                    + DatabaseContract.Creature.COLUMN_NAME_CREATURE_NAME
                + " FROM " + DatabaseContract.Creature.TABLE_NAME + ";";
    }
}
