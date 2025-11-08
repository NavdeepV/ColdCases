package com.example.coldcases

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var textCold: View
    private lateinit var textCases: View
    private lateinit var loginSignupButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textCold = findViewById(R.id.textCold)
        textCases = findViewById(R.id.textCases)
        loginSignupButton = findViewById(R.id.loginSignupContainer)

        applyFadeInAnimation(textCold, 0)
        applyFadeInAnimation(textCases, 300)
        applyFadeInAnimation(loginSignupButton, 600)

        loginSignupButton.setOnClickListener {
            startActivity(Intent(this, LoginSignupActivity::class.java))
        }
    }

    private fun applyFadeInAnimation(view: View, delay: Long) {
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 500
        fadeIn.startOffset = delay
        fadeIn.fillAfter = true
        view.startAnimation(fadeIn)
    }
}