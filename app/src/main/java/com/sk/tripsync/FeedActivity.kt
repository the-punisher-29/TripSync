package com.sk.tripsync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class FeedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val editFromLatitude: EditText = findViewById(R.id.edit_from_latitude)
        val editFromLongitude: EditText = findViewById(R.id.edit_from_longitude)
        val editToLatitude: EditText = findViewById(R.id.edit_to_latitude)
        val editToLongitude: EditText = findViewById(R.id.edit_to_longitude)
        val buttonFindTrip: Button = findViewById(R.id.button_find_trip)

        buttonFindTrip.setOnClickListener {
            val fromLatitude = editFromLatitude.text.toString().toDoubleOrNull()
            val fromLongitude = editFromLongitude.text.toString().toDoubleOrNull()
            val toLatitude = editToLatitude.text.toString().toDoubleOrNull()
            val toLongitude = editToLongitude.text.toString().toDoubleOrNull()

            if (fromLatitude != null && fromLongitude != null && toLatitude != null && toLongitude != null) {
                val intent = Intent(this@FeedActivity, TravelerActivity::class.java).apply {
                    putExtra("START_LATITUDE", fromLatitude)
                    putExtra("START_LONGITUDE", fromLongitude)
                    putExtra("END_LATITUDE", toLatitude)
                    putExtra("END_LONGITUDE", toLongitude)
                }
                startActivity(intent)
            }
        }
    }
}
