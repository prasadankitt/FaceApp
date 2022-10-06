package com.example.faceapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(context: Context) : ViewModel() {

    private val dao = ProfileDatabase.getDatabase(context).profileDao()
      private val repository = ProfileRepository(dao)
      val allUser = repository.allUser

    fun insert(user: Profile)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(user)
        }
    }
    fun delete(user: Profile)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(user)
        }
    }
    fun deleteAll()
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}