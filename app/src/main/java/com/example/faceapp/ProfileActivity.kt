package com.example.faceapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {
//    val GALLERY_REQUEST_CODE = 2
//    val CAMERA_REQUEST_CODE = 1
    private var imageUri: Uri? = null
    lateinit var viewModel: ProfileViewModel
    lateinit var setprofilepicture: ImageView
    lateinit var name: EditText
    lateinit var age: EditText
    lateinit var profession: EditText
    lateinit var submit: Button
    var actualPicture : Bitmap? = null
//    var picture: String? = null

    //Replacement for Activity Result
    private val contractGall = registerForActivityResult(ActivityResultContracts.GetContent())
    {
        actualPicture = MediaStore.Images.Media.getBitmap(contentResolver,it)

        imageUri = getImageUriFromBitmap(applicationContext,actualPicture!!)
        Glide.with(this)
            .load(imageUri)
            .into(setprofilepicture)
//        setprofilepicture.setImageURI(it)
    }
    private val contractCam = registerForActivityResult(ActivityResultContracts.TakePicture())
    {
        Glide.with(this)
            .load(imageUri)
            .into(setprofilepicture)
//        setprofilepicture.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initializerFun()

        setprofilepicture.setOnClickListener{
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItem = arrayOf("Open Gallery","Open Camera")
            pictureDialog.setItems(pictureDialogItem) {
                    dialog, which ->
                when(which){
                    0->galleryCheckPermission()
                    1->cameraCheckPermission()
                }
            }
            pictureDialog.show()
        }

        submit.setOnClickListener {
            if(imageUri != null && name.text.isNotEmpty() && age.text.isNotEmpty() && profession.text.isNotEmpty()) {
                val userObject = Profile(
                    0,
                    name.text.toString(),
                    age.text.toString(),
                    profession.text.toString(),
                    imageUri.toString()
                )
                viewModel.insert(userObject)
//                Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show()

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


    private fun cameraCheckPermission()
    {
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA).withListener(
                object : MultiplePermissionsListener
                {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()) {
                            camera()
                        }
                    }
                }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRotationalDialogForPermission()
                    }
            }
            ).onSameThread().check()
    }
    private fun galleryCheckPermission()
    {
        Dexter.withContext(this).withPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object: PermissionListener{
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(this@ProfileActivity,"Storage Permission Denied",Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery()
    {
//        intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, GALLERY_REQUEST_CODE)
        contractGall.launch("image/*")
    }

    private fun camera(){
//        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(intent,CAMERA_REQUEST_CODE)
        imageUri = createImageUri()!!
        contractCam.launch(imageUri)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if(resultCode == Activity.RESULT_OK)
//        {
//            when(requestCode)
//            {
//                CAMERA_REQUEST_CODE->{
//                    picture = data?.extras?.get("data") as Bitmap
//                    setprofilepicture.setImageBitmap(picture)
//                }
//                CAMERA_REQUEST_CODE->{
//                    actualPicture = data?.extras?.get("data") as Bitmap
//
//                    imageUri = getImageUriFromBitmap(applicationContext,actualPicture!!)
//                    Glide.with(this)
//                        .load(imageUri)
//                        .into(setprofilepicture)
//                }
//
//                GALLERY_REQUEST_CODE->{
//                    data!!.data.also{ imageUri = it }
//                    picture = MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
//                    setprofilepicture.setImageBitmap(picture)
//                }
//                GALLERY_REQUEST_CODE->{
//                    data!!.data.also{ imageUri = it }
//                    actualPicture = MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
//
//                    imageUri = getImageUriFromBitmap(applicationContext,actualPicture!!)
//                    Glide.with(this)
//                        .load(imageUri)
//                        .into(setprofilepicture)
//                }
//            }
//        }
//    }

    private fun showRotationalDialogForPermission(){
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions"
            +"required for this feature. It can be enable from App Settings")
            .setPositiveButton("Go to settings"
            ) { _, _ ->
                try
                {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }
                catch (e: ActivityNotFoundException)
                {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel"
            ) { dialog,_ ->
                dialog.dismiss()
            }.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(applicationContext,MainActivity::class.java)
        startActivity(intent)
    }

    fun getImageUriFromBitmap(context: Context,bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, bytes)
        val path  = MediaStore.Images.Media.insertImage(context.contentResolver,bitmap,"File",null)
        return  Uri.parse(path.toString())
    }

    private fun createImageUri(): Uri? {
        val time = SimpleDateFormat("dd-mm-yyyy").format(Date())
        val image = File(applicationContext.filesDir,"IMG${time}.png")
        return FileProvider.getUriForFile(applicationContext,
            "com.example.faceapp.fileProvider", image)
    }
}
