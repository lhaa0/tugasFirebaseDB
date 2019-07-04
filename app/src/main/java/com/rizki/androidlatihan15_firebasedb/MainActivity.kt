package com.rizki.androidlatihan15_firebasedb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.e
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 7

    private lateinit var mGoogleSignIn: GoogleSignInClient
    private lateinit var fAuth: FirebaseAuth
    private lateinit var helperPref: PrefsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        helperPref = PrefsHelper(this)
        fAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignIn = GoogleSignIn.getClient(this, gso)
        sign_button.setOnClickListener {
            signInGoogle()
        }

        tSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                et_nama.visibility = GONE
                btn_submit.text = "Login"
            } else {
                et_nama.visibility = VISIBLE
                btn_submit.text = "Register"
            }
        }

        btn_submit.setOnClickListener {
            if (tSwitch.isChecked) {
                val Semail = et_email.text.toString()
                val Spassword = et_password.text.toString()
                if (Semail.isNotEmpty() && Spassword.isNotEmpty()) {
                    fAuth.signInWithEmailAndPassword(Semail, Spassword)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                updateUI(fAuth.currentUser)
                            }
                        }
                } else
                    Toast.makeText(this, "email atau password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                val Semail = et_email.text.toString()
                val Spassword = et_password.text.toString()
                if (Semail.isNotEmpty() && Spassword.isNotEmpty() && Spassword.length >= 6) {
                    fAuth.createUserWithEmailAndPassword(Semail, Spassword)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val user = fAuth.currentUser
                                val dbRef = FirebaseDatabase.getInstance().getReference("user/${user!!.uid}")
                                dbRef.child("/nama").setValue(et_nama.text.toString())
                                updateUI(user)
                            }
                        }
                } else
                    Toast.makeText(this, "Isi dengan benar / password min 6 char", Toast.LENGTH_SHORT).show()
            }
        }


    }

    fun signInGoogle() {
        val signIntent = mGoogleSignIn.signInIntent
        startActivityForResult(signIntent, RC_SIGN_IN)
    }

    fun firebaseAutWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        fAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = fAuth.currentUser
                val dbRef = FirebaseDatabase.getInstance().getReference("user/${user!!.uid}")
                dbRef.child("/nama").setValue(user.displayName)
                updateUI(user)
            } else {
                e("TAG_ERROR", it.exception!!.message)
            }
        }
    }

    fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            helperPref.saveUID(user.uid)
            startActivity(Intent(this, MainPage::class.java))
            finish()
        } else {
            e("TAG_ERROR", "User tidak ada")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAutWithGoogle(account!!)
            } catch (x: ApiException) {
                x.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = fAuth.currentUser
        if (user != null) {
            updateUI(user)
        }
    }
}
