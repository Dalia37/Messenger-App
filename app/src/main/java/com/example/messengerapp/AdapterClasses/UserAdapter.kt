package com.example.messengerapp.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerapp.Activities.MessageChatActivity
import com.example.messengerapp.ModelClasess.Users
import com.example.messengerapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_search_item_layout.view.*
import org.w3c.dom.Comment

class UserAdapter
    (
    mcontext:Context ,
    mUser : List<Users> ,
    isChatChek :Boolean

):RecyclerView.Adapter<UserAdapter.ViewHolder?>() {
    private val mcontext :Context
    private val mUser :List<Users>
    private var isChatChek :Boolean

    init {
        this.mcontext= mcontext
        this.mUser = mUser
        this.isChatChek  = isChatChek
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view :View = LayoutInflater.from(mcontext).inflate(R.layout.user_search_item_layout,parent , false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return mUser.size

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user : Users?= mUser[position]
        holder.usernameTxT.text = user!!.getUserName()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(holder.profileImageView)

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "send Message" ,
                "Visit Profile"
            )

            val builder :AlertDialog.Builder = AlertDialog.Builder(mcontext)
            builder.setTitle("What do you want ?")
            builder.setItems(options , DialogInterface.OnClickListener { dialogInterface, i ->
                if (position == 0 ){
                    val intent = Intent(mcontext , MessageChatActivity::class.java)
                    intent.putExtra("visit_id" , user.getUID())
                    mcontext.startActivity(intent)
                }
                else {
                    if (position == 1 ){

                    }
                }
            })
            builder.show()
        }
    }


    class ViewHolder(itemView :View):RecyclerView.ViewHolder(itemView)
    {
        var usernameTxT :TextView
        var profileImageView :CircleImageView
        var onlineImageView:CircleImageView
        var offlineImageView :CircleImageView
        var lastMessageTxT :TextView

        init {
            usernameTxT = itemView.findViewById(R.id.username)
            profileImageView = itemView.findViewById(R.id.profile_image)
            onlineImageView = itemView.findViewById(R.id.image_online)
            offlineImageView = itemView.findViewById(R.id.image_offline)
            lastMessageTxT = itemView.findViewById(R.id.message_last)


        }



    }



}