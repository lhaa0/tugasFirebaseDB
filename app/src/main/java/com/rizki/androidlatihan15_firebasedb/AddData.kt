package com.rizki.androidlatihan15_firebasedb

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.add_data.*
import kotlinx.android.synthetic.main.show_data.*

class AddData : AppCompatActivity() {

    lateinit var dbRef : DatabaseReference
    lateinit var helperPref : PrefsHelper
    var counterId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_data)

        helperPref = PrefsHelper(this)

        counterId = helperPref.getCounterId()

        if (intent.getBooleanExtra("update", false)){
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

            if (nama.isNotEmpty() && judul.isNotEmpty() && tgl.isNotEmpty() && deskripsi.isNotEmpty()){
                simpanToFirebase(nama, judul, tgl, deskripsi, counterId.toString())
                if (!intent.getBooleanExtra("update", false)) {
                    helperPref.saveCounterId(counterId + 1)
                }
                } else {
                Toast.makeText(this, "inputan tidak boleh ada yg kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun simpanToFirebase(nama : String, judul : String, tgl : String, desc : String, key: String) {
        val uidUser = helperPref.getUID()

        dbRef = FirebaseDatabase.getInstance().getReference("dataBuku/$uidUser/$key")
        dbRef.child("/id").setValue(uidUser)
        dbRef.child("/penulis").setValue(nama)
        dbRef.child("/judulBuku").setValue(judul)
        dbRef.child("/tanggal").setValue(tgl)
        dbRef.child("/desc").setValue(desc)

        Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        finish()
    }
}