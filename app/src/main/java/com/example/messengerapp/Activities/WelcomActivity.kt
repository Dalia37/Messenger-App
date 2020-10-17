package com.example.messengerapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.messengerapp.MainActivity
import com.example.messengerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_welcom.*

class WelcomActivity : AppCompatActivity() {
    var firebaseuser : FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcom)

        register_welcome_btn.setOnClickListener {
            val intent =Intent(this@WelcomActivity , RegisterActivity::class.java)
            startActivity(intent)
            finish()

        }

        login_welcome_btn.setOnClickListener {
            val intent =Intent(this@WelcomActivity , LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    override fun onStart() {
        super.onStart()
        firebaseuser = FirebaseAuth.getInstance().currentUser
        if (firebaseuser != null){
            val intent =Intent(this@WelcomActivity , MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}