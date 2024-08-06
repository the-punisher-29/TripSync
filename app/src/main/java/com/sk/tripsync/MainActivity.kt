package com.sk.tripsync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val travelerButton = findViewById<Button>(R.id.btn_traveler)
        val companionButton = findViewById<Button>(R.id.btn_companion)
        val adminButton = findViewById<Button>(R.id.btn_admin)

        travelerButton.setOnClickListener {
            navigateToLoginActivity("Traveler")
        }

        companionButton.setOnClickListener {
            navigateToLoginActivity("Companion")
        }

        adminButton.setOnClickListener {
            navigateToLoginActivity("Admin")
        }
    }

    private fun navigateToLoginActivity(userType: String) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("USER_TYPE", userType)
        startActivity(intent)
    }
}
