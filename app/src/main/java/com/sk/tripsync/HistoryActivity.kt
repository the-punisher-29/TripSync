package com.sk.tripsync

import Trip
import TripAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import android.widget.Toast

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonShare: Button
    private lateinit var trips: List<Trip>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recycler_view_recent_trips)
        buttonShare = findViewById(R.id.button_share)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Sample data
        trips = listOf(
            Trip("1", "Arman Gupta", "+1234567890", "CAB123", true),
            Trip("2", "Hritin Raj", "+0987654321", "CAB456", false),
            Trip("3", "Akash Jatt", "+0987654321", "CAB456", false),
            Trip("4", "Gigga Nigaa", "+0987654321", "CAB456", false),
            Trip("5", "Harsh Singh", "+0987654321", "CAB456", false),
            // Add more trips as needed
        )

        val adapter = TripAdapter(trips)
        recyclerView.adapter = adapter

        buttonShare.setOnClickListener {
            showTripSelectionDialog()
        }
    }

    private fun showTripSelectionDialog() {
        val tripOptions = trips.map { trip ->
            "${trip.tripId} - ${trip.driverName} (${trip.cabNumber})"
        }.toTypedArray()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select a Trip")
        builder.setItems(tripOptions) { _, which ->
            val selectedTrip = trips[which]
            shareTripDetails(selectedTrip)
        }

        // Add an option to share the ongoing trip by default
        val ongoingTrip = trips.find { it.isOngoing }
        if (ongoingTrip != null) {
            builder.setNeutralButton("Share Ongoing Trip") { _, _ ->
                shareTripDetails(ongoingTrip)
            }
        }

        builder.create().show()
    }

    private fun openMessagingApp(tripDetails: String) {
        // Create an intent to open the messaging app
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            // Uri scheme for SMS
            data = Uri.parse("smsto:")
            // Add the message body as an extra
            putExtra("sms_body", tripDetails)
        }

        // Check if there's an app that can handle this intent
        if (smsIntent.resolveActivity(packageManager) != null) {
            // Start the messaging app
            startActivity(smsIntent)
        } else {
            // Handle the case where no SMS app is available
            Toast.makeText(this, "No SMS app available", Toast.LENGTH_SHORT).show()
        }
    }


    private fun shareTripDetails(trip: Trip) {
        val tripDetails = "Trip ID: ${trip.tripId}\n" +
                "Driver: ${trip.driverName}\n" +
                "Phone: ${trip.driverPhoneNumber}\n" +
                "Cab No: ${trip.cabNumber}\n" +
                "Status: ${if (trip.isOngoing) "Ongoing" else "Completed"}"

        // Copy to clipboard
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Trip Details", tripDetails)
        clipboard.setPrimaryClip(clip)

        // Open the messaging app with the trip details
        openMessagingApp(tripDetails)
    }}

