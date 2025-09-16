package com.example.faceapp.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.faceapp.R
import com.example.faceapp.data.Profile
import com.example.faceapp.utils.CryptoManager
import com.example.faceapp.utils.ImageUtils
import com.example.faceapp.viewmodel.ProfileViewModel
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider

class ProfileActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    lateinit var viewModel: ProfileViewModel
    lateinit var setprofilepicture: ImageView
    lateinit var name: EditText
    lateinit var age: EditText
    lateinit var profession: EditText
    lateinit var submit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initializerFun()

        setprofilepicture.setOnClickListener{
            ImagePicker.with(this)
                .provider(ImageProvider.BOTH)
                .createIntentFromDialog { launcher.launch(it) }
        }

        submit.setOnClickListener {
            if(imageUri != null && name.text.isNotEmpty() && age.text.isNotEmpty() && profession.text.isNotEmpty()) {

                val (nameEncrypted,nameIv) = CryptoManager().encrypt(name.text.toString().toByteArray())
                val (ageEncrypted,ageIv) = CryptoManager().encrypt(age.text.toString().toByteArray())
                val (professionEncrypted,professionIv) = CryptoManager().encrypt(profession.text.toString().toByteArray())
                val (imageUriEncrypted,imageUriIv) = CryptoManager().encrypt(imageUri.toString().toByteArray())

                val userObject = Profile(
                    0,
                    nameEncrypted,nameIv,
                    ageEncrypted,ageIv,
                    professionEncrypted,professionIv,
                    imageUriEncrypted,imageUriIv
                )
                viewModel.insert(userObject)
                finish() // Just finish this activity to return to MainActivity
            }
            else
            {
                Toast.makeText(this,"All Fields are mandatory",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializerFun()
    {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        setprofilepicture = findViewById(R.id.setprofilepicture)
        name = findViewById(R.id.name)
        age = findViewById(R.id.age)
        profession = findViewById(R.id.profession)
        submit = findViewById(R.id.submit)
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        // Just finish this activity to return to MainActivity
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data
                val bitmap = it.data?.extras?.get("data") as? Bitmap
                
                // Handle both URI and bitmap cases
                val finalUri = when {
                    bitmap != null -> {
                        // Camera captured bitmap
                        ImageUtils.getImageUriFromBitmap(this, bitmap)
                    }
                    uri != null -> {
                        // Gallery or file URI
                        ImageUtils.saveImageToInternalStorage(this, uri)
                    }
                    else -> null
                }
                
                imageUri = finalUri
                
                if (imageUri != null) {
                    Glide.with(this)
                        .load(imageUri)
                        .into(setprofilepicture)
                } else {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }
}