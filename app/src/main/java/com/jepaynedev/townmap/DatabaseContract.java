package com.jepaynedev.townmap;

import android.provider.BaseColumns;

/**
 * Created by James Payne on 7/18/2016.
 * jepayne1138@gmail.com
 */
public class DatabaseContract {

    // Prevent accidental instantiation with an empty constructor
    public DatabaseContract() {}

    // Define table contents for User table
    public static abstract class User implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_ID = "google_id";
        public static final String COLUMN_NAME_TRAINER_NAME = "trainer_name";
        public static final String COLUMN_NAME_TRAINER_LEVEL = "trainer_level";
    }

    // Define table constants for Catche table
    public static abstract class Catche implements BaseColumns {
        public static final String TABLE_NAME = "catch";
        public static final String COLUMN_NAME_ID = "catch_id";
        public static final String COLUMN_NAME_TRAINER_NAME = "trainer_name";
        public static final String COLUMN_NAME_TRAINER_LEVEL = "trainer_level";
        public static final String COLUMN_NAME_USING_LURE = "usring_lure";
        public static final String COLUMN_NAME_USING_INCENSE = "usring_incense";
        public static final String COLUMN_NAME_CREATURE_ID = "creature_id";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_CATCH_TIME = "catch_time";
    }

    // Define table constants for Creature table
    public static abstract class Creature implements BaseColumns {
        public static final String TABLE_NAME = "creature";
        public static final String COLUMN_NAME_CREATURE_ID = "creature_id";
        public static final String COLUMN_NAME_CREATURE_NAME = "creature_name";
        public static final String COLUMN_NAME_CREATURE_ICON = "creature_icon";
    }
}
