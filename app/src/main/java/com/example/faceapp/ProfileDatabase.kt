package com.example.faceapp

import android.content.Context
import androidx.room.*

// Annotates class to be a Room Database with a table (entity) of the Word class
@TypeConverters(Converter::class)
@Database(entities = [Profile::class], version = 1)
public abstract class ProfileDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ProfileDatabase? = null

        fun getDatabase(context: Context): ProfileDatabase =
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            INSTANCE?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }
        fun buildDatabase(context: Context)=
            Room.databaseBuilder(context,ProfileDatabase::class.java,
                "profileDatabase").build()
    }
}