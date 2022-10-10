package com.example.faceapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.github.drjacky.imagepicker.ImagePicker
import com.github.drjacky.imagepicker.constant.ImageProvider
import java.io.ByteArrayOutputStream


class ProfileActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    lateinit var viewModel: ProfileViewModel
    lateinit var setprofilepicture: ImageView
    lateinit var name: EditText
    lateinit var age: EditText
    lateinit var profession: EditText
    lateinit var submit: Button
    var actualPicture : Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initializerFun()

        setprofilepicture.setOnClickListener{
            ImagePicker.with(this)
                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
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
                intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else
            {
                Toast.makeText(this,"All Fields are mandatory",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializerFun()
    {
        viewModel = ProfileViewModel(this)
        setprofilepicture = findViewById(R.id.setprofilepicture)
        name = findViewById(R.id.name)
        age = findViewById(R.id.age)
        profession = findViewById(R.id.profession)
        submit = findViewById(R.id.submit)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(applicationContext,MainActivity::class.java)
        startActivity(intent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                actualPicture = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                val tempUri: Uri = getImageUriFromBitmap(applicationContext, actualPicture!!)
                imageUri = tempUri
                Glide.with(this)
                    .load(imageUri)
                    .into(setprofilepicture)
            }
        }

    private fun getImageUriFromBitmap(context: Context,bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, bytes)
        val path  = MediaStore.Images.Media.insertImage(context.contentResolver,bitmap,"File",null)
        return  Uri.parse(path.toString())
    }

}
