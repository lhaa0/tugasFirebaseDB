package com.rizki.androidlatihan15_firebasedb

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log.e
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.upload_image.*
import java.io.IOException
import java.util.*

class UploadFireStorage : AppCompatActivity() {

    lateinit var helper: PrefsHelper
    val REQUEST_IMAGE = 10002
    val PERMISSION_REQUEST_CODE = 10003
    lateinit var filePathImage: Uri
    var value = 0.0

    lateinit var dbRef: DatabaseReference
    lateinit var fStorage: FirebaseStorage
    lateinit var fStorageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upload_image)
        helper = PrefsHelper(this)
        fStorage = FirebaseStorage.getInstance()
        fStorageRef = fStorage.reference

        img_view.setOnClickListener {
            when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) -> {
                    if (ContextCompat.checkSelfPermission(
                            this@UploadFireStorage,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(
                                arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ),
                                PERMISSION_REQUEST_CODE
                            )
                        }
                    } else {
                        imageChooser()
                    }
                }
                else -> {
                    imageChooser()
                }
            }
        }

        btn_upload.setOnClickListener {
            uploadFile()
        }
    }

    private fun imageChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_IMAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED)
                    Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show()
                else
                    imageChooser()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when(requestCode) {
            REQUEST_IMAGE -> {
                filePathImage = data?.data!!
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver, filePathImage)
                    Glide.with(this)
                        .load(bitmap)
                        .centerCrop()
                        .into(img_view)
                } catch (x : IOException) {
                    x.printStackTrace()
                }
            }
        }
    }

    fun GetFileExtension(uri: Uri) : String?{
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    fun uploadFile(){
        val nameX = UUID.randomUUID().toString()
        val ref = fStorageRef.child("images/${helper.getUID()}/${nameX}.${GetFileExtension(filePathImage)}")
        ref.putFile(filePathImage)
            .addOnSuccessListener {
                Toast.makeText(this@UploadFireStorage, "Berhasil upload", Toast.LENGTH_SHORT).show()
                progress.visibility = GONE
            }
            .addOnFailureListener {
                it.printStackTrace()
                progress.visibility = GONE
            }
            .addOnProgressListener {
                value = (100.0*it.bytesTransferred/it.totalByteCount)
                e("loading", value.toString())
                progress.visibility = VISIBLE
                progress.progress = value.toInt()
            }
    }
}