package com.example.faceapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class ImageUtils {
    
    companion object {
        fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
            return try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                
                if (bitmap != null) {
                    saveBitmapToInternalStorage(context, bitmap)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        
        fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri? {
            return saveBitmapToInternalStorage(context, bitmap)
        }
        
        private fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): Uri? {
            return try {
                // Create internal storage directory
                val internalDir = File(context.filesDir, "images")
                if (!internalDir.exists()) {
                    internalDir.mkdirs()
                }
                
                // Generate unique filename
                val fileName = "image_${System.currentTimeMillis()}.jpg"
                val imageFile = File(internalDir, fileName)
                
                // Save bitmap to file
                val outputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.flush()
                outputStream.close()
                
                // Return file URI
                Uri.fromFile(imageFile)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
