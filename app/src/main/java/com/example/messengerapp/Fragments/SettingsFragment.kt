package com.example.messengerapp.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.messengerapp.ModelClasess.Users
import com.example.messengerapp.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {

     var usersReference : DatabaseReference? = null
    var firebaseuser :FirebaseUser ?= null
    private val RequestCode =438
    private var imageUri :Uri ?= null
    private var  storageref  : StorageReference ?= null
    private var coverChecker :String? = ""
    private var socialChecker :String? = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view  =  inflater.inflate(R.layout.fragment_settings, container, false)
        firebaseuser = FirebaseAuth.getInstance().currentUser
        usersReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseuser!!.uid)
        storageref = FirebaseStorage.getInstance().reference.child("User Images")

        usersReference!!.addValueEventListener(object  :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user :Users ? = p0.getValue(Users::class.java)

                    if (context != null){
                        view.username_settings.text = user!!.getUserName()
                        Picasso.get().load(user.getProfile()).into(view.profile_image_settings)
                        Picasso.get().load(user.getCover()).placeholder(R.drawable.cover).into(view.cover_image_settings)
//                        Glide.with(context!!).load(user.getCover()).into(view.cover_image_settings)

                    }

                }

            }

            override fun onCancelled(p0: DatabaseError) {
            }


        })
        view.profile_image_settings.setOnClickListener {
            pickImage()
        }
        view.cover_image_settings.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }
        view.set_facebook.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }
        view.set_instagram.setOnClickListener {
            socialChecker = "instagram"
            setSocialLinks()
        }
        view.set_website.setOnClickListener {
            socialChecker = "website"
            setSocialLinks()
        }

        return view
    }

    private fun setSocialLinks() {

        val builder : AlertDialog.Builder =
            AlertDialog.Builder(requireContext() , R.style.Theme_AppCompat_DayNight_Dialog_Alert )
        if (socialChecker == "website"){
            builder.setTitle("Write Url:")
        }
        else {
            builder.setTitle("Enter username ")
        }
        val edittext = EditText(context)

        if (socialChecker == "website"){
            edittext.hint = "e.g www.google.com"
        }
        else {
            edittext.hint = "e.g dalia37"
        }
        builder.setView(edittext)
        builder.setPositiveButton("Create" , DialogInterface.OnClickListener{
            dialogInterface, i ->
            var str = edittext.text.toString()
            if (str == ""){
                Toast.makeText(context , "please write something.." , Toast.LENGTH_LONG).show()

            }else{
                savesocialLink(str)
            }

        })
        builder.setNegativeButton("Cancel" , DialogInterface.OnClickListener{
                dialogInterface, i ->
            dialogInterface.cancel()

    })
        builder.show()



    }

    private fun savesocialLink(str: String) {
        val  mapSocial =HashMap<String ,Any>()

        when(socialChecker){
            "facebook " -> {
                mapSocial["facebook"] = "https://m.facebook.com/$str"


            }
            "instagram " -> {
                mapSocial["instagram"] = "https://m.facebook.com/$str"


            }
            "website " -> {
                mapSocial["website"] = "https://$str"
            }
        }
                usersReference!!.updateChildren(mapSocial).addOnCompleteListener { 
                    task ->
                    if (task.isSuccessful){
                        Toast.makeText(context , "updated successfully" , Toast.LENGTH_LONG).show()

                    }


                }



    }

    private fun pickImage() {
        val intent =Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent , RequestCode)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode && resultCode ==Activity.RESULT_OK && data!!.data != null){
            imageUri = data.data!!
            Toast.makeText(context , "uploading..." , Toast.LENGTH_LONG).show()
            uploadeImageTodatabase()

        }
    }

    private fun uploadeImageTodatabase() {
        val progreesbar =ProgressDialog(context)
        progreesbar.setMessage("image is uploading , please wait....")
        progreesbar.show()

        if (imageUri != null){
            val fileRef = storageref!!.child(System.currentTimeMillis().toString()+ ".jpg")
            var uploadTask : StorageTask<*>
            uploadTask= fileRef.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot , Task<Uri>>{
                task ->
                if (!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }

                }
                return@Continuation fileRef.downloadUrl

            }).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val dawnloadUri = task.result
                    val uri = dawnloadUri.toString()

                    if (coverChecker == "cover"){
                        val  mapCoverImage =HashMap<String ,Any>()
                        mapCoverImage["cover"] = uri
                        usersReference!!.updateChildren(mapCoverImage)
                        coverChecker = ""

                    }else{
                        val  mapProfileImage =HashMap<String ,Any>()
                        mapProfileImage["profile"] = uri
                        usersReference!!.updateChildren(mapProfileImage)
                        coverChecker = ""

                    }
                    progreesbar.dismiss()
                }

            }
        }
    }


}