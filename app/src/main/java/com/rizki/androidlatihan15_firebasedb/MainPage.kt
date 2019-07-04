package com.rizki.androidlatihan15_firebasedb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.e
import android.view.Display
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.main_page.*

class MainPage : AppCompatActivity(), AdapterBook.FirebaseDataListener {


    private var bookAdapter : AdapterBook? = null
//    private var rcView : RecyclerView? = null
    private var list : MutableList<ModelBook> = ArrayList()
    lateinit var dbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)

        fab_exit.setOnClickListener {
            val fAuth = FirebaseAuth.getInstance()
            fAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.setHasFixedSize(true)

        val prefsHelper = PrefsHelper(this)

        val dbRefUser = FirebaseDatabase.getInstance().getReference("user/${prefsHelper.getUID()}")
        dbRefUser.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(data: DataSnapshot) {
                supportActionBar!!.setTitle(data.child("/nama").value.toString())
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(applicationContext, "dibatalkan", Toast.LENGTH_SHORT).show()
            }

        })

        dbRef = FirebaseDatabase.getInstance().getReference("dataBuku/${prefsHelper.getUID()}")

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(it: DataSnapshot) {
                list = ArrayList()
                for (dataSnapshot in it.children){
                    val addDataAll = dataSnapshot.getValue(ModelBook::class.java)
                    addDataAll!!.key = dataSnapshot.key
                    list.add(addDataAll)
                }
                bookAdapter = AdapterBook(this@MainPage, list)
                rcView.adapter = bookAdapter
            }

            override fun onCancelled(it: DatabaseError) {
                e("TAG_ERROR", it.message)
            }

        })

        fab_.setOnClickListener {
            startActivity(Intent(this, AddData::class.java))
        }
    }


    override fun onDeleteData(buku: ModelBook) {
        val prefsHelper = PrefsHelper(this)
        dbRef = FirebaseDatabase.getInstance().getReference("dataBuku/${prefsHelper.getUID()}")
        dbRef.child(buku.key!!).removeValue().addOnSuccessListener {
            Toast.makeText(this, "data berhasil dihapus", Toast.LENGTH_SHORT).show()
            bookAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onUpdateData(buku: ModelBook) {
        val intent = Intent(this, AddData::class.java)
        intent.putExtra("data", buku)
        intent.putExtra("update", true)
        startActivity(intent)
    }

}
