package com.sk.tripsync

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        val buttonSend: Button = findViewById(R.id.button_send)

        buttonSend.setOnClickListener {
            val feedback = findViewById<EditText>(R.id.edit_text_feedback).text.toString()
            // Process the feedback (e.g., send to server or save locally)
            Toast.makeText(this, "Feedback sent: $feedback", Toast.LENGTH_SHORT).show()
            finish() // Close the activity
        }
    }
}
