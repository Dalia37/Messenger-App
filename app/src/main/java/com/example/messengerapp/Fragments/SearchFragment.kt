package com.example.messengerapp.Fragments

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.AdapterClasses.UserAdapter
import com.example.messengerapp.ModelClasess.Users
import com.example.messengerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.coroutines.handleCoroutineException
import java.lang.NullPointerException

public  class SearchFragment : Fragment() {
    private var userAdapter :UserAdapter? = null
    private var mUsers :List<Users>?= null
    private var recyclerview : RecyclerView?=null
    private var searchEditText :EditText ?= null

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
            val view : View = inflater.inflate(R.layout.fragment_search, container, false)
                   recyclerview=view.findViewById(R.id.searchList)
                   recyclerview?.setHasFixedSize(true)
                   recyclerview?.layoutManager =LinearLayoutManager(context)
                   searchEditText = view.findViewById(R.id.searchUserET)
                   mUsers = ArrayList()
                  retrieveAllUsers()
        searchEditText?.addTextChangedListener(object  :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchForUsers(p0.toString().toLowerCase())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        return view
    }

    private fun retrieveAllUsers() {
        var firebaseuserID = FirebaseAuth.getInstance().currentUser!!.uid
       val refUsers  = FirebaseDatabase.getInstance().reference.child("users")
        refUsers.addValueEventListener(object  :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                if (searchEditText?.text.toString()== ""){
                    for (snapshot in p0.children){
                        val user :Users ? = p0.getValue(Users::class.java)
                        if (!(user!!.getUID()).equals(firebaseuserID))
                        {
                            (mUsers as ArrayList<Users>).add(user)

                        }
                    }
                    userAdapter = UserAdapter(context!! , mUsers!! , false )
                    recyclerview!!.adapter=userAdapter





                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })


    }


    private fun searchForUsers(str :String){
        var firebaseuserID = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers  = FirebaseDatabase.getInstance().reference.child("users")
            .orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object  :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in p0.children){
                    val user :Users ? = snapshot.getValue(Users::class.java)
                    if (!(user!!.getUID()).equals(firebaseuserID))
                    {
                        (mUsers as ArrayList<Users>).add(user)

                    }
                }
                userAdapter = UserAdapter(context!! , mUsers!! , false )
                recyclerview!!.adapter=userAdapter






            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }
}