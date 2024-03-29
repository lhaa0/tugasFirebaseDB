package com.rizki.androidlatihan15_firebasedb

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.add_data.*
import java.io.IOException
import java.util.*

class AddData : AppCompatActivity() {

    val REQUEST_IMAGE = 10002
    val PERMISSION_REQUEST_CODE = 10003
    lateinit var filePathImage: Uri
    var value = 0.0

    lateinit var dbRef: DatabaseReference
    lateinit var helperPref: PrefsHelper
    var counterId: Int = 0

    lateinit var fStorage: FirebaseStorage
    lateinit var fStorageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_data)

        helperPref = PrefsHelper(this)

        counterId = helperPref.getCounterId()

        if (intent.getBooleanExtra("update", false)) {
            val modelBook = intent.getSerializableExtra("data") as ModelBook
            penulis.setText(modelBook.penulis)
            buku.setText(modelBook.judulBuku)
            tanggal.setText(modelBook.tanggal)
            desc.setText(modelBook.desc)
            counterId = modelBook.key!!.toInt()
        }

        save.setOnClickListener {
            val nama = penulis.text.toString()
            val judul = buku.text.toString()
            val tgl = tanggal.text.toString()
            val deskripsi = desc.text.toString()

            if (nama.isNotEmpty() && judul.isNotEmpty() && tgl.isNotEmpty() && deskripsi.isNotEmpty() && !Uri.EMPTY.equals(
                    filePathImage
                )
            ) {
                uploadFile()
                if(!intent.getBooleanExtra("update", false)) {
                    helperPref.saveCounterId(counterId + 1)
                }
            } else {
                Toast.makeText(this, "inputan tidak boleh ada yg kosong", Toast.LENGTH_SHORT).show()
            }
        }

        fStorage = FirebaseStorage.getInstance()
        fStorageRef = fStorage.reference

        img_view.setOnClickListener {
            when {
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) -> {
                    if (ContextCompat.checkSelfPermission(
                            this@AddData,
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

    fun simpanToFirebase(nama: String, judul: String, tgl: String, desc: String, key: String, url: String) {
        val uidUser = helperPref.getUID()

        dbRef = FirebaseDatabase.getInstance().getReference("dataBuku/$uidUser/$key")
        dbRef.child("/id").setValue(uidUser)
        dbRef.child("/penulis").setValue(nama)
        dbRef.child("/judulBuku").setValue(judul)
        dbRef.child("/tanggal").setValue(tgl)
        dbRef.child("/desc").setValue(desc)
        dbRef.child("/image").setValue(url)

        Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_IMAGE -> {
                filePathImage = data?.data!!
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver, filePathImage
                    )
                    Glide.with(this)
                        .load(bitmap)
                        .centerCrop()
                        .into(img_view)
                } catch (x: IOException) {
                    x.printStackTrace()
                }
            }
        }
    }

    fun GetFileExtension(uri: Uri): String? {
        val contentResolver = this.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    fun uploadFile() {
        val nameX = UUID.randomUUID().toString()
        val ref = fStorageRef
            .child("images/${helperPref.getUID()}/${nameX}.${GetFileExtension(filePathImage)}")
        ref.putFile(filePathImage)
            .addOnSuccessListener {
                Toast.makeText(this@AddData, "Berhasil upload", Toast.LENGTH_SHORT).show()
                val nama = penulis.text.toString()
                val judul = buku.text.toString()
                val tgl = tanggal.text.toString()
                val deskripsi = desc.text.toString()


                ref.downloadUrl.addOnSuccessListener {
                    simpanToFirebase(nama, judul, tgl, deskripsi, counterId.toString(), it.toString())
                }
//
                progress.visibility = View.GONE
            }
            .addOnFailureListener {
                it.printStackTrace()
                progress.visibility = View.GONE
            }
            .addOnProgressListener {
                value = (100.0 * it.bytesTransferred / it.totalByteCount)
                Log.e("loading", value.toString())
                progress.visibility = View.VISIBLE
                progress.progress = value.toInt()
            }
    }
}