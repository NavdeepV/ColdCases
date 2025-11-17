package com.example.coldcases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


//data class representing a single reply to a comment
data class Reply(
    val author: String,
    val text: String
)

//data class representing a single comment with respective replies
data class Comment(
    val author: String,
    val text: String,
    val replies: MutableList<Reply> = mutableListOf()
) {
    var docID: String = "" //firestore document ID for specific comment and details
}


//adapter for recyclerview for displaying comment details
class CommentView(
    private val comments: MutableList<Comment>,

    //function called to handle sending a reply to a comment
    private val onReplySend: (comment: Comment, replyText: String) -> Unit
) : RecyclerView.Adapter<CommentView.CommentViewHolder>() {

    //class that holds references to views associated with comment
    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val commentAuthor: TextView = itemView.findViewById(R.id.commentAuthor)
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val replyBtn: TextView = itemView.findViewById(R.id.replyBtn)
        val replyInputContainer: LinearLayout = itemView.findViewById(R.id.replyInputContainer)
        val replyInput: EditText = itemView.findViewById(R.id.replyInput)
        val sendReplyBtn: ImageButton = itemView.findViewById(R.id.sendReplyBtn)
        val repliesRecycler: RecyclerView = itemView.findViewById(R.id.repliesRecycler) //recyclerview for the nested replies
    }

    //inflate the layout XML for each comment into a usable view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    //bind comment data to the views
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]


        holder.commentAuthor.text = comment.author
        holder.commentText.text = comment.text

        //nested recyclerview for replies
        val repliesAdapter = ReplyView(comment.replies)
        holder.repliesRecycler.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.repliesRecycler.adapter = repliesAdapter

        //change visibility of reply input field with button press
        holder.replyBtn.setOnClickListener {
            if (holder.replyInputContainer.visibility == View.VISIBLE) {
                holder.replyInputContainer.visibility = View.GONE
                holder.replyInput.text.clear()
            } else {
                holder.replyInputContainer.visibility = View.VISIBLE
                holder.replyInput.requestFocus()
            }
        }

        //send reply with send button through callback to CommunityActivity
        holder.sendReplyBtn.setOnClickListener {
            val replyText = holder.replyInput.text.toString().trim()
            if (replyText.isNotEmpty()) {
                onReplySend(comment, replyText)
                holder.replyInput.text.clear()
                holder.replyInputContainer.visibility = View.GONE
            }
        }
    }

    //get number of comments in list, linked to recyclerview for display
    override fun getItemCount(): Int = comments.size
}
