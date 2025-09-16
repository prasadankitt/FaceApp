package com.example.faceapp.data

import android.content.Context
import androidx.room.*

@Database(entities = [Profile::class], version = 1,exportSchema = false)
abstract class ProfileDatabase : RoomDatabase() {

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
