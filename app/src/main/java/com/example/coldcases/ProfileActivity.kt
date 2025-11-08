package com.example.coldcases

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class ProfileActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etName: EditText
    private lateinit var etBirthday: EditText
    private lateinit var etAge: EditText
    private lateinit var tvEmail: TextView
    private lateinit var btnLogout: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        etUsername = findViewById(R.id.etUsername)
        etName = findViewById(R.id.etName)
        etBirthday = findViewById(R.id.etBirthday)
        etAge = findViewById(R.id.etAge)
        tvEmail = findViewById(R.id.tvEmail)
        btnLogout = findViewById(R.id.btnLogout)

        loadUserData()

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginSignupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return
        val email = auth.currentUser?.email ?: "N/A"
        tvEmail.text = "Email: $email"

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document: DocumentSnapshot ->
                if (document.exists()) {
                    etUsername.setText(document.getString("username") ?: "")
                    etName.setText(document.getString("name") ?: "")
                    etBirthday.setText(document.getString("birthday") ?: "")
                    val age = document.getLong("age")?.toInt() ?: 0
                    etAge.setText(age.toString())

                    if (age < 18) {
                        showAgeWarning()
                    }
                }
            }
            .addOnFailureListener {
                // handle errors
            }
    }

    private fun showAgeWarning() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Warning!")
        builder.setMessage(
            "You are under 18. This app contains real crime stories, images, and videos. " +
                    "You must agree to continue."
        )
        builder.setCancelable(false)
        builder.setPositiveButton("I Agree") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setNegativeButton("Exit") { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        builder.show()
    }
}