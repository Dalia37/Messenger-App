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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth :FirebaseAuth
    private lateinit var refUsers :DatabaseReference
    private var FirebaseUserId :String = ""
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        toolbarAction()

        mAuth = FirebaseAuth.getInstance()

        register_btn.setOnClickListener {
            registerUser()
        }







    }

    private fun registerUser() {
        val name: String = username_register.text.toString()
        val email: String = email_register.text.toString()
        val password: String = passpword_register.text.toString()
        if (name.isEmpty()) {
            username_register.error = "Enter UserName"
            username_register.requestFocus()
        } else
            if (email.isEmpty()) {
                username_register.error = "Enter Email"
                username_register.requestFocus()
            } else if (password.isEmpty()) {
                username_register.error = "Enter password"
                username_register.requestFocus()
            } else{
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseUserId = mAuth.currentUser!!.uid
                            refUsers = FirebaseDatabase.getInstance().reference.child("users")
                                .child(FirebaseUserId)

                            val userHashMap = HashMap<String, Any>()
                            userHashMap["uid"] = FirebaseUserId
                            userHashMap["username"] = name
                            userHashMap["profile"] =
                                "https://firebasestorage.googleapis.com/v0/b/messengerapp-69bc7.appspot.com/o/profile.png?alt=media&token=55f1b7bc-9fdd-4585-b855-2d25a48309a3"
                            userHashMap["cover"] =
                                "https://firebasestorage.googleapis.com/v0/b/messengerapp-69bc7.appspot.com/o/cover.jpg?alt=media&token=78f10d39-3a09-4ef3-b40f-a029b0a7180b"
                            userHashMap["status"] = "offline"
                            userHashMap["search"] = name.toLowerCase()
                            userHashMap["facebook"] = "https://m.facebook.com"
                            userHashMap["instagram"] = "https://m.instagram.com"
                            userHashMap["website"] = "https://www.google.com"

                            refUsers.updateChildren(userHashMap).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val intent =
                                        Intent(this@RegisterActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }

                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Error Message :" + task.exception!!.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
    }
    }




    @SuppressLint("RestrictedApi")
    fun toolbarAction(){
        val toolbar_rig : Toolbar = findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar_rig)
        supportActionBar!!.title = "Register"
        supportActionBar!!.setDefaultDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar_rig.setNavigationOnClickListener{
            val intent = Intent(this@RegisterActivity , WelcomActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}