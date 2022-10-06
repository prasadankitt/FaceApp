package com.example.faceapp

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserTable")
data class Profile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "Name") val name: String,
    @ColumnInfo(name  = "Age") val age: String,
    @ColumnInfo(name = "Profession") val profession: String,
//    @ColumnInfo(name = "Picture") val picture: Bitmap?
    @ColumnInfo(name = "Picture") val picture: String
)