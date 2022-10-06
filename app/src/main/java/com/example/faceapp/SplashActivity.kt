package com.example.faceapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            val intent = Intent(this@SplashActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
    }
}