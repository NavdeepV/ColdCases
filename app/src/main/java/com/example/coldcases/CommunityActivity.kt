package com.example.coldcases

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CommunityActivity : AppCompatActivity() {


    private lateinit var commentsRecycler: RecyclerView //to show all comments for this case
    private lateinit var commentInput: EditText
    private lateinit var sendBtn: ImageButton
    private lateinit var noCommentsText: TextView
    private lateinit var adapter: CommentView //connects comment data to recyclerview

    private val db = FirebaseFirestore.getInstance() //firestore instance to save comments/replies
    private val comments = mutableListOf<Comment>()
    private lateinit var caseID: String //case identifier to open specific community page
    private lateinit var userName: String //current user for comment/reply display


    //deals with 'no comments yet' visibility when comments added
    private fun updateCommentsVisibility() {
        noCommentsText.visibility = if (comments.isEmpty()) View.VISIBLE else View.GONE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.community_activity)

        //UI elements
        commentsRecycler = findViewById(R.id.commentsRecycler)
        commentInput = findViewById(R.id.commentInput)
        sendBtn = findViewById(R.id.sendCommentBtn)
        noCommentsText = findViewById(R.id.noCommentsText)
        val caseTitleText = findViewById<TextView>(R.id.caseTitle)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        //get case info from intent
        val caseName = intent.getStringExtra("caseName") ?: "Unknown Case"
        caseTitleText.text = caseName
        caseID = caseName.replace(" ", "_") //simple unique ID based on name

        //get user's username using firebaseauth from email (everything before @ symbol)
        val currentUser = FirebaseAuth.getInstance().currentUser
        userName = currentUser?.email?.substringBefore("@") ?: "Anonymous"

        //RecyclerView setup for comments and pass lambda function for replies
        adapter = CommentView(comments) { comment, replyText ->
            addReply(comment, replyText)
        }
        commentsRecycler.layoutManager = LinearLayoutManager(this)
        commentsRecycler.adapter = adapter


        //back button goes back to cases page
        backButton.setOnClickListener { finish() }

        //send button submits comments
        sendBtn.setOnClickListener { addComment() }

        //loads all existing comments in firestore
        loadComments()
    }


    //function for adding comments both locally and in firestore
    private fun addComment() {
        val text = commentInput.text.toString().trim()
        if (text.isEmpty()) return

        //local update for immediate UI display
        val comment = Comment(userName, text)
        comments.add(comment)
        adapter.notifyDataSetChanged()
        //helps scroll to the comment when posted at bottom
        commentsRecycler.scrollToPosition(comments.size - 1)
        updateCommentsVisibility()
        commentInput.setText("")

        //update firestore to include comment details
        val commentData = hashMapOf(
            "author" to userName,
            "text" to text,
            "timestamp" to FieldValue.serverTimestamp(),
            "replies" to listOf<HashMap<String, String>>()
        )

        db.collection("cases")
            .document(caseID)
            .collection("comments")
            .add(commentData)
            .addOnSuccessListener { docRef ->
                //save firestore doc ID for adding replies later
                comment.docID = docRef.id
            }
    }


    //function for adding replies both locally and in firestore
    private fun addReply(comment: Comment, replyText: String) {

        //local update for immediate UI display
        val reply = Reply(userName, replyText)
        comment.replies.add(reply)
        adapter.notifyDataSetChanged()


        //update firestore to include reply details
        val replyData = hashMapOf(
            "author" to userName,
            "text" to replyText,

        )
        db.collection("cases")
            .document(caseID)
            .collection("comments")
            .document(comment.docID)
            //add reply to comment thread
            .update("replies", FieldValue.arrayUnion(replyData))
    }


    //loads comments and replies from firestore for respective case
    private fun loadComments() {
        db.collection("cases")
            .document(caseID)
            .collection("comments")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    comments.clear() //reset local list to avoid duplicates
                    for (doc in snapshot.documents) {
                        //get author and text
                        val authorEmail = doc.getString("author") ?: "Anonymous"
                        val author = authorEmail.substringBefore("@") // truncate email here
                        val text = doc.getString("text") ?: ""

                        //read data from firestore
                        val repliesList = doc.get("replies") as? List<Map<String, String>> ?: listOf()
                        val replies = repliesList.map { r ->
                            val replyAuthorEmail = r["author"] ?: "Anonymous"
                            val replyAuthor = replyAuthorEmail.substringBefore("@")
                            Reply(replyAuthor, r["text"] ?: "")
                        }.reversed().toMutableList() //reversed used to show most recent reply at the top

                        //create the comment object with respective replies
                        val comment = Comment(author, text, replies)
                        comment.docID = doc.id
                        comments.add(comment)
                    }
                    adapter.notifyDataSetChanged()
                    updateCommentsVisibility()
                }
            }
    }

}

