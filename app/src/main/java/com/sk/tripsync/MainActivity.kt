package com.sk.tripsync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sk.tripsync.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnTraveler: Button = findViewById(R.id.btn_traveler)
        btnTraveler.setOnClickListener {
            val intent = Intent(this, TravelerActivity::class.java)
            startActivity(intent)
        }
    }
}