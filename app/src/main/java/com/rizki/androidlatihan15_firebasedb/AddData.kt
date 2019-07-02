package com.rizki.androidlatihan15_firebasedb

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.add_data.*

class AddData : AppCompatActivity() {

    lateinit var dbRef : DatabaseReference
    lateinit var helperPref : PrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_data)

        helperPref = PrefsHelper(this)

        save.setOnClickListener {
            val nama = penulis.text.toString()
            val judul = buku.text.toString()
            val tgl = tanggal.text.toString()
            val deskripsi = desc.text.toString()

            if (nama.isNotEmpty() && judul.isNotEmpty() && tgl.isNotEmpty() && deskripsi.isNotEmpty()){
                simpanToFirebase(nama, judul, tgl, deskripsi)
            } else {
                Toast.makeText(this, "inputan tidak boleh ada yg kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun simpanToFirebase(nama : String, judul : String, tgl : String, desc : String) {
        val uidUser = helperPref.getUID()
        val counterID = helperPref.getCounterId()

        dbRef = FirebaseDatabase.getInstance().getReference("dataBuku/$uidUser/$counterID")
        dbRef.child("/id").setValue(uidUser)
        dbRef.child("/penulis").setValue(nama)
        dbRef.child("/judulBuku").setValue(judul)
        dbRef.child("/tanggal").setValue(tgl)
        dbRef.child("/desc").setValue(desc)

        Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        helperPref.saveCounterId(counterID + 1)
        finish()
    }
}