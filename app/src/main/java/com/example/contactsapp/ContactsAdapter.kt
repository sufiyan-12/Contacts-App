package com.example.contactsapp

import android.content.Context
import android.provider.ContactsContract.Contacts
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ContactsAdapter(private val listener: myContactsItemClicked) :
    RecyclerView.Adapter<contactsViewHolder>() {

    private val list =  ArrayList<MyContacts>()
    private lateinit var context: Context
    private var lastPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): contactsViewHolder {
        context = parent.context
        val view: View = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false)
        val viewHolder =  contactsViewHolder(view)
        view.setOnClickListener {
            listener.onItemClick(list[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: contactsViewHolder, position: Int) {
        holder.contactName.text = list[position].name
        holder.contactImage.setImageResource(R.drawable.ic_account)
        holder.callBtn.setOnClickListener {
            Toast.makeText(context, "call to "+list[position].name, Toast.LENGTH_SHORT).show()
        }
        holder.messageBtn.setOnClickListener {
            Toast.makeText(context, "Message to "+list[position].name, Toast.LENGTH_SHORT).show()
        }
        putAnimation(holder.itemView, position)
    }

    private fun putAnimation(view: View, position: Int) {
        if(position > lastPosition){
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            view.startAnimation(animation)
            lastPosition++
        }
    }

    fun updateContacts(newList: ArrayList<MyContacts>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}

class contactsViewHolder(view: View): RecyclerView.ViewHolder(view){
    val contactImage: ImageView = view.findViewById(R.id.contactImage)
    val contactName: TextView = view.findViewById(R.id.contactName)
    val contactPosition: TextView = view.findViewById(R.id.contactPosition)
    val callBtn: ImageButton = view.findViewById(R.id.callBtn)
    val messageBtn: ImageButton = view.findViewById(R.id.messageBtn)
}

interface myContactsItemClicked{
    fun onItemClick(item: MyContacts)
}