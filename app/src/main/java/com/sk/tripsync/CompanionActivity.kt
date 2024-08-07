package com.sk.tripsync

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class CompanionActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var editTextTripId: EditText
    private lateinit var buttonOk: Button
    private lateinit var buttonFeedback: Button

    private val OPEN_ROUTE_SERVICE_API_KEY = "5b3ce3597851110001cf6248cb359a7eb3e34fae854bc8397565d55d" // Replace with your actual API key
    private val service: OpenRouteServiceApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenRouteServiceApi::class.java)
    }

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
        trips["1"] = Pair(GeoPoint(25.5941, 85.1376), GeoPoint(25.6141, 85.1576))

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
        drawRoute(startPoint, endPoint) // Draw the actual route
    }

    private fun placeMarker(location: GeoPoint, title: String) {
        val marker = Marker(mapView)
        marker.position = location
        marker.title = title
        mapView.overlays.add(marker)
    }

    private fun drawRoute(startPoint: GeoPoint, endPoint: GeoPoint) {
        val call = service.getDirections(
            OPEN_ROUTE_SERVICE_API_KEY,
            "${startPoint.longitude},${startPoint.latitude}",
            "${endPoint.longitude},${endPoint.latitude}"
        )

        call.enqueue(object : Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                if (response.isSuccessful) {
                    val directionsResponse = response.body()
                    val coordinates = directionsResponse?.routes?.get(0)?.geometry?.coordinates

                    if (coordinates != null) {
                        val geoPoints: MutableList<GeoPoint> = ArrayList()
                        for (coordinate in coordinates) {
                            geoPoints.add(GeoPoint(coordinate[1], coordinate[0]))
                        }

                        // Draw the route on the map
                        val polyline = Polyline()
                        polyline.setPoints(geoPoints)
                        polyline.outlinePaint.color = Color.BLUE
                        polyline.outlinePaint.strokeWidth = 5f
                        mapView.overlays.add(polyline)
                        mapView.invalidate()

                        // Adjust the map to fit the route
                        val boundingBox = BoundingBox.fromGeoPoints(geoPoints)
                        mapView.zoomToBoundingBox(boundingBox, true)
                    } else {
                        Toast.makeText(this@CompanionActivity, "No route found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CompanionActivity, "Failed to fetch route", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Toast.makeText(this@CompanionActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}



