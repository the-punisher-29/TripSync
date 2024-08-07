package com.sk.tripsync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RidesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rides)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_rides)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val rides = listOf(
            Ride("John Doe", "Location A", "Location B", "$20", "2024-08-07"),
            Ride("Jane Smith", "Location C", "Location D", "$25", "2024-08-06"),
            // Add more dummy data here
        )

        val adapter = RideAdapter(rides)
        recyclerView.adapter = adapter
    }
}
