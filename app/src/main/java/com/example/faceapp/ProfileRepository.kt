package com.example.faceapp

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class ProfileRepository(private val profileDao: ProfileDao)
{
        val allUser : LiveData<List<Profile>> = profileDao.getUser()
        //Room will run it off the main thread no need to ensure long running background work is running
        suspend fun insert(user: Profile) {
            profileDao.insert(user)
        }
        suspend fun delete(user: Profile) {
            profileDao.delete(user)
        }
        suspend fun deleteAll() {
            profileDao.deleteAll()
        }
}