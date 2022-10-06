package com.example.faceapp

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProfileDao {
    @Insert( onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: Profile)

    @Delete
    suspend fun delete(user: Profile)

    @Query("Select * from userTable order by id")
    fun getUser() : LiveData<List<Profile>>

    @Query("Delete from userTable")
    suspend fun deleteAll()
}