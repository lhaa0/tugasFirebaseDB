package com.rizki.androidlatihan15_firebasedb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.e
import android.view.Display
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.main_page.*

class MainPage : AppCompatActivity() {

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
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })

        dbRef = FirebaseDatabase.getInstance().getReference("dataBuku/${prefsHelper.getUID()}")

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(it: DataSnapshot) {
                list = ArrayList()
                for (dataSnapshot in it.children){
                    val addDataAll = dataSnapshot.getValue(ModelBook::class.java)
                    list.add(addDataAll!!)
                }
                bookAdapter = AdapterBook(applicationContext, list)
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
}
