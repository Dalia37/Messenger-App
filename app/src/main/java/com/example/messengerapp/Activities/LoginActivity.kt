package com.example.messengerapp.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.messengerapp.MainActivity
import com.example.messengerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_welcom.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

       toolbar_Action()
        mAuth = FirebaseAuth.getInstance()

        login_btn.setOnClickListener {
            loginUser()
        }



    }

    private fun loginUser() {
        val email :String = email_login.text.toString()
        val password :String  = passpword_login.text.toString()
         if (email.isEmpty()){
             email_login.error = "Enter Email"
             email_login.requestFocus()
         }else
        if (password.isEmpty()){
            passpword_login.error="Enter Password"
            passpword_login.requestFocus()
        }else {
        mAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener{
            task ->
            if (task.isSuccessful){
                val intent =Intent(this@LoginActivity , MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this@LoginActivity ,"Error Message :" + task.exception!!.message.toString(),
                    Toast.LENGTH_LONG).show()
                println(task.exception!!.message)

            }
        }
        }


    }


    @SuppressLint("RestrictedApi")
    private fun toolbar_Action() {
        val toolbar_log : Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar_log)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDefaultDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar_log.setNavigationOnClickListener{
            val intent = Intent(this@LoginActivity , WelcomActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return true
    }
}