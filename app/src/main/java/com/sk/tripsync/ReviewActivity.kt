package com.sk.tripsync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_reviews)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val reviews = listOf(
            Review("Jane Doe", "This is a sample review.", "4.5/5", "2024-08-07"),
            Review("John Smith", "Great service, will use again.", "5/5", "2024-08-06"),
            // Add more dummy data here
        )

        val adapter = ReviewAdapter(reviews)
        recyclerView.adapter = adapter
    }
}
