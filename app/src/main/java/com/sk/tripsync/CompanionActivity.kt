package com.sk.tripsync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class CompanionActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var editTextTripId: EditText
    private lateinit var buttonOk: Button
    private lateinit var buttonFeedback: Button

    // This map should be populated with actual trip data
    private val trips = mutableMapOf<String, Pair<GeoPoint, GeoPoint>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_companion)

        Configuration.getInstance().userAgentValue = packageName

        mapView = findViewById(R.id.map_view)
        editTextTripId = findViewById(R.id.edit_text_trip_id)
        buttonOk = findViewById(R.id.button_ok)
        buttonFeedback = findViewById(R.id.button_feedback)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        val mapController: IMapController = mapView.controller
        val patna = GeoPoint(25.5941, 85.1376)
        mapController.setCenter(patna)
        mapController.setZoom(13.0)

        // Dummy data for testing
        // In a real application, this data would come from a database or passed from another activity
        trips["12345"] = Pair(GeoPoint(25.5941, 85.1376), GeoPoint(25.6141, 85.1576))

        buttonOk.setOnClickListener {
            val tripId = editTextTripId.text.toString()
            val trip = trips[tripId]
            if (trip != null) {
                val (startPoint, endPoint) = trip
                displayTrip(startPoint, endPoint)
            } else {
                Toast.makeText(this, "Trip not found", Toast.LENGTH_SHORT).show()
            }
        }

        buttonFeedback.setOnClickListener {
            val intent = Intent(this@CompanionActivity, FeedbackActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayTrip(startPoint: GeoPoint, endPoint: GeoPoint) {
        val mapController: IMapController = mapView.controller
        mapController.setCenter(startPoint)
        mapController.setZoom(13.0)

        placeMarker(startPoint, "Start Point")
        placeMarker(endPoint, "Destination")

        // Optionally draw a route or other information if needed
    }

    private fun placeMarker(location: GeoPoint, title: String) {
        val marker = Marker(mapView)
        marker.position = location
        marker.title = title
        mapView.overlays.add(marker)
    }
}

