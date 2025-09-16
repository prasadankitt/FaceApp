package com.example.faceapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserTable")
data class Profile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "Name") val name: String,
    @ColumnInfo(name = "NameIV") val nameIV: String,
    @ColumnInfo(name  = "Age") val age: String,
    @ColumnInfo(name  = "AgeIV") val ageIV: String,
    @ColumnInfo(name = "Profession") val profession: String,
    @ColumnInfo(name = "ProfessionIV") val professionIV: String,
    @ColumnInfo(name = "Picture") val picture: String,
    @ColumnInfo(name = "PictureIV") val pictureIV: String,
)
