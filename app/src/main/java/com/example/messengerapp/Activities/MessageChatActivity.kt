package com.example.messengerapp.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.AdapterClasses.ChatsAdapter
import com.example.messengerapp.ModelClasess.Users
import com.example.messengerapp.ModelClasess.chat
import com.example.messengerapp.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_message_chat.*

class MessageChatActivity : AppCompatActivity() {
    var userIdVisit : String = ""
    var firebaseUser : FirebaseUser ?= null
    var chatsAdapter : ChatsAdapter?= null
    var mChatList : List<chat>?= null
    lateinit var recycler_view_chats : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")
        firebaseUser = FirebaseAuth.getInstance().currentUser

        recycler_view_chats = findViewById(R.id.recycler_view_chats)
        recycler_view_chats.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recycler_view_chats.layoutManager = linearLayoutManager

        val referance = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        referance.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                var user : Users?= p0.getValue(Users::class.java)
                if (user != null) {
                    username_mChat.text = user.getUserName()
                    Picasso.get().load(user.getProfile()).into(profile_image_mchat)

                    retrieveMessages(firebaseUser!!.uid , userIdVisit , user.getProfile())


                }
//                if (user != null) {
//                }

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        attact_image_file_btn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent , "Pick Image") , 438)
        }





        send_message_btn.setOnClickListener {
            val message = text_message.text.toString()
            if (message.isEmpty()){
                text_message.error= " please write a message"
                text_message.requestFocus()
            }else{
                sendMessageToUser(firebaseUser!!.uid , userIdVisit , message)

            }

        }
    }



    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {

        val referance = FirebaseDatabase.getInstance().reference
        val  messageKey = referance.push().key

        val messageHashMap = HashMap<String , Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        referance.child("chats").child(messageKey!!).setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val chatsListReferance = FirebaseDatabase.getInstance().reference.child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)
                    chatsListReferance.addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists()){
                                chatsListReferance.child("id").setValue(userIdVisit)
                            }
                            val chatsListReceiverRef = FirebaseDatabase.getInstance().reference.child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)
                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)

                        }

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })



                    //implement the notifications push using FCM

                    val referance = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
                }
            }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 438 && resultCode == RESULT_OK && data!= null&& data!!.data != null){
            val progreesbar =ProgressDialog(this)
            progreesbar.setMessage("image is uploading , please wait....")
            progreesbar.show()


            val fileUri = data.data
            val storageReferance = FirebaseStorage.getInstance().reference.child("Chat Images")

            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filepath = storageReferance.child("messageId.jpg")

            var uploadTask : StorageTask<*>
            uploadTask= filepath.putFile(fileUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot , Task<Uri>>{
                    task ->
                if (!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }

                }
                return@Continuation filepath.downloadUrl

            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dawnloadUri = task.result
                    val uri = dawnloadUri.toString()

                    val messageHashMap = HashMap<String , Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "Sent you an image"
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = uri
                    messageHashMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)

                    progreesbar.dismiss()




                }
            }

        }
    }
    private fun retrieveMessages(senderId: String, receiverId: String?, receiverImageUrl: String?) {
        mChatList= ArrayList()
        val referance = FirebaseDatabase.getInstance().reference.child("chats")

        referance.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList<chat>).clear()
                for (snapshot in p0.children)
                {
                    val chat = snapshot.getValue(chat::class.java)

                    if (chat!!.getreceiver().equals(senderId)&& chat.getsender().equals(receiverId)
                        || chat.getreceiver().equals(receiverId) && chat.getsender().equals(senderId))
                    {
                        (mChatList as ArrayList<chat>).add(chat)
                    }
                    chatsAdapter = ChatsAdapter(this@MessageChatActivity , (mChatList as ArrayList<chat>) ,
                    receiverImageUrl!!)
                    recycler_view_chats.adapter =chatsAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }
}