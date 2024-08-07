package com.sk.tripsync

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.*
import android.graphics.Color
import org.osmdroid.util.BoundingBox


private fun getBoundingBox(geoPoints: List<GeoPoint>): BoundingBox {
    val minLat = geoPoints.minOf { it.latitude }
    val maxLat = geoPoints.maxOf { it.latitude }
    val minLon = geoPoints.minOf { it.longitude }
    val maxLon = geoPoints.maxOf { it.longitude }

    return BoundingBox(maxLat, maxLon, minLat, minLon)
}


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
        mapController.setZoom(13.0)

        // Example start and end points
        val startPoint = GeoPoint(25.5941, 85.1376) // Replace with your start coordinates
        val endPoint = GeoPoint(25.5960, 85.1380)   // Replace with your end coordinates

        // Fetch and display route
        fetchRoute(startPoint, endPoint)

        // Place a dummy location and create some dummy cab pins
        placeDummyLocationAndCabs(patna)

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

                    // Draw the route on the map
                    runOnUiThread {
                        val polyline = Polyline()
                        polyline.setPoints(geoPoints)
                        polyline.outlinePaint.color = Color.BLUE // Set the color to blue
                        polyline.outlinePaint.strokeWidth = 5f // Set a reasonable width for visibility
                        mapView.overlays.add(polyline)
                        mapView.invalidate()

                        // Adjust the map to fit the route
                        val boundingBox = BoundingBox(
                            geoPoints.maxOf { it.latitude },
                            geoPoints.maxOf { it.longitude },
                            geoPoints.minOf { it.latitude },
                            geoPoints.minOf { it.longitude }
                        )
                        mapView.zoomToBoundingBox(boundingBox, true)
                    }
                } else {
                    Log.e("API_ERROR", "No routes found in the response")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
    private fun placeDummyLocationAndCabs(dummyLocation: GeoPoint) {
        val locationIcon = resizeDrawable(resources.getDrawable(R.drawable.default_location_icon, null), 32, 32)
        val cabIcon = resizeDrawable(resources.getDrawable(R.drawable.default_cab_icon, null), 32, 32)

        val dummyMarker = Marker(mapView)
        dummyMarker.position = dummyLocation
        dummyMarker.icon = locationIcon
        dummyMarker.title = "Dummy Location"
        mapView.overlays.add(dummyMarker)

        val cabs = mutableListOf<GeoPoint>()
        for (i in 1 until 5) {
            val latOffset = (Math.random() - 0.5) * 0.02
            val lonOffset = (Math.random() - 0.5) * 0.02
            val cabLocation = GeoPoint(dummyLocation.latitude + latOffset, dummyLocation.longitude + lonOffset)
            cabs.add(cabLocation)
            val cabMarker = Marker(mapView)
            cabMarker.position = cabLocation
            cabMarker.icon = cabIcon
            cabMarker.title = "Cab $i"
            mapView.overlays.add(cabMarker)
        }

        val closestCab = findClosestCab(dummyLocation, cabs)
        if (closestCab != null) {
            val marker = Marker(mapView)
            marker.position = closestCab
            marker.icon = resizeDrawable(resources.getDrawable(R.drawable.default_cab_icon, null), 32, 32)
            marker.title = "Closest Cab"
            mapView.overlays.add(marker)

            // Track route between user location and closest cab
            trackRouteBetweenUserAndCab(dummyLocation, closestCab)
        }
    }

    private fun trackRouteBetweenUserAndCab(userLocation: GeoPoint, closestCab: GeoPoint) {
        fetchRoute(userLocation, closestCab)
    }

    private fun findClosestCab(dummyLocation: GeoPoint, cabs: List<GeoPoint>): GeoPoint? {
        var closestCab: GeoPoint? = null
        var shortestDistance = Double.MAX_VALUE

        for (cab in cabs) {
            val distance = dummyLocation.distanceToAsDouble(cab)
            if (distance < shortestDistance) {
                shortestDistance = distance
                closestCab = cab
            }
        }
        return closestCab
    }

    private fun resizeDrawable(drawable: Drawable, widthDp: Int, heightDp: Int): Drawable {
        val widthPx = (widthDp * resources.displayMetrics.density).toInt()
        val heightPx = (heightDp * resources.displayMetrics.density).toInt()

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, widthPx, heightPx)
        drawable.draw(canvas)

        return BitmapDrawable(resources, bitmap)
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

