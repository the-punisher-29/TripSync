package com.sk.tripsync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val buttonRides: Button = findViewById(R.id.button_rides)
        val buttonReviews: Button = findViewById(R.id.button_reviews)

        buttonRides.setOnClickListener {
            // Start the RidesActivity
            val intent = Intent(this@AdminActivity, RidesActivity::class.java)
            startActivity(intent)
        }

        buttonReviews.setOnClickListener {
            // Start the ReviewActivity
            val intent = Intent(this@AdminActivity, ReviewActivity::class.java)
            startActivity(intent)
        }
    }
}
