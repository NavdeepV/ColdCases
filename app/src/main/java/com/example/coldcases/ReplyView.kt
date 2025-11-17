package com.example.coldcases


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


//adapter for recyclerview for displaying list of replies
class ReplyView(private val replies: List<Reply>) :
    RecyclerView.Adapter<ReplyView.ReplyViewHolder>() {

    //class that holds references to views associated with a reply
    inner class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val replyAuthor: TextView = itemView.findViewById(R.id.replyAuthor)
        val replyText: TextView = itemView.findViewById(R.id.replyText)
    }

    //inflate the layout XML for each reply into a usable view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reply_item, parent, false)
        return ReplyViewHolder(view)
    }


    //bind reply data to the views
    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        val reply = replies[position]
        holder.replyAuthor.text = reply.author
        holder.replyText.text = reply.text
    }


    //get number of replies in list, linked to recyclerview for display
    override fun getItemCount(): Int = replies.size
}
