package com.sk.tripsync

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class TravelerActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private val OPEN_ROUTE_SERVICE_API_KEY = "5b3ce3597851110001cf6248cb359a7eb3e34fae854bc8397565d55d"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_traveler)

        // Set user agent for osmdroid
        Configuration.getInstance().userAgentValue = "MyAppName/1.0"

        mapView = findViewById(R.id.map_view)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        val mapController: IMapController = mapView.controller

        // Coordinates for Patna, Bihar, India
        val patna = GeoPoint(25.5941, 85.1376)
        mapController.setCenter(patna)

        // Set the zoom level to focus on the city (adjust as needed)
        mapController.setZoom(13.0)  // Adjust the zoom level to fit the city

        // Check if coordinates are provided
        val startLatitude = intent.getDoubleExtra("START_LATITUDE", 0.0)
        val startLongitude = intent.getDoubleExtra("START_LONGITUDE", 0.0)
        val endLatitude = intent.getDoubleExtra("END_LATITUDE", 0.0)
        val endLongitude = intent.getDoubleExtra("END_LONGITUDE", 0.0)

        if (startLatitude != 0.0 && startLongitude != 0.0 &&
            endLatitude != 0.0 && endLongitude != 0.0) {
            val startPoint = GeoPoint(startLatitude, startLongitude)
            val endPoint = GeoPoint(endLatitude, endLongitude)

            // Fetch and display route
            fetchRoute(startPoint, endPoint)
        }

        // Set up buttons
        val buttonFeed: Button = findViewById(R.id.button_feed)
        val buttonHistory: Button = findViewById(R.id.button_history)

        buttonFeed.setOnClickListener {
            val intent = Intent(this@TravelerActivity, FeedActivity::class.java)
            startActivity(intent)
        }

        buttonHistory.setOnClickListener {
            val intent = Intent(this@TravelerActivity, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchRoute(start: GeoPoint, end: GeoPoint) {
        Thread {
            try {
                val url = URL(
                    "https://api.openrouteservice.org/v2/directions/driving-car?api_key=$OPEN_ROUTE_SERVICE_API_KEY" +
                            "&start=${start.longitude},${start.latitude}" +
                            "&end=${end.longitude},${end.latitude}"
                )
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")

                val inStream = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var inputLine: String?
                while (inStream.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                inStream.close()

                // Log the response for debugging
                Log.d("API_RESPONSE", response.toString())

                // Parse JSON response
                val jsonResponse = JSONObject(response.toString())
                if (jsonResponse.has("routes")) {
                    val routes = jsonResponse.getJSONArray("routes")
                    val coordinates = routes.getJSONObject(0).getJSONObject("geometry").getJSONArray("coordinates")

                    val geoPoints: MutableList<GeoPoint> = ArrayList()
                    for (i in 0 until coordinates.length()) {
                        val point = coordinates.getJSONArray(i)
                        geoPoints.add(GeoPoint(point.getDouble(1), point.getDouble(0)))
                    }

                    runOnUiThread {
                        val polyline = Polyline()
                        polyline.setPoints(geoPoints)
                        mapView.overlays.add(polyline)
                        mapView.invalidate()
                    }
                } else {
                    Log.e("API_ERROR", "No routes found in the response")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDetach()
    }
}
