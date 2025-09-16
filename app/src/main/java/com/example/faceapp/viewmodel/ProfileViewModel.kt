package com.example.faceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.faceapp.data.Profile
import com.example.faceapp.data.ProfileDatabase
import com.example.faceapp.data.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ProfileDatabase.getDatabase(application).profileDao()
    private val repository = ProfileRepository(dao)
    val allUser: LiveData<List<Profile>> = repository.allUser

    fun insert(user: Profile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(user)
        }
    }
    
    fun delete(user: Profile) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(user)
        }
    }
    
    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}
