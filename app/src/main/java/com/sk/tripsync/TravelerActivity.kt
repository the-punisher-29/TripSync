package com.sk.tripsync

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import kotlin.math.sqrt

private fun getBoundingBox(geoPoints: List<GeoPoint>): BoundingBox {
    val minLat = geoPoints.minOf { it.latitude }
    val maxLat = geoPoints.maxOf { it.latitude }
    val minLon = geoPoints.minOf { it.longitude }
    val maxLon = geoPoints.maxOf { it.longitude }

    return BoundingBox(maxLat, maxLon, minLat, minLon)
}

interface OpenRouteServiceApi {
    @GET("v2/directions/driving-car")
    fun getDirections(
        @Query("api_key") apiKey: String,
        @Query("start") start: String,
        @Query("end") end: String
    ): Call<DirectionsResponse>
}

class TravelerActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private val OPEN_ROUTE_SERVICE_API_KEY = "5b3ce3597851110001cf6248cb359a7eb3e34fae854bc8397565d55d"
    private val TAG = "TravelerActivity"
    private val NUMBER_OF_CABS = 7

    private val service: OpenRouteServiceApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenRouteServiceApi::class.java)
    }

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

        // Retrieve coordinates from intent
        val fromLatitude = intent.getDoubleExtra("START_LATITUDE", 0.0)
        val fromLongitude = intent.getDoubleExtra("START_LONGITUDE", 0.0)
        val toLatitude = intent.getDoubleExtra("END_LATITUDE", 0.0)
        val toLongitude = intent.getDoubleExtra("END_LONGITUDE", 0.0)

        if (fromLatitude != 0.0 && fromLongitude != 0.0 && toLatitude != 0.0 && toLongitude != 0.0) {
            val startPoint = GeoPoint(fromLatitude, fromLongitude)
            val endPoint = GeoPoint(toLatitude, toLongitude)

            // Place markers and fetch route
            placeMarker(startPoint, "Start Point")
            placeMarker(endPoint, "Destination")
            fetchRoute(startPoint, endPoint)

            // Generate and display cabs
            val cabs = generateDummyCabs(startPoint)
            displayCabs(cabs, startPoint)
        } else {
            Toast.makeText(this, "Invalid coordinates provided", Toast.LENGTH_SHORT).show()
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

    private fun fetchRoute(pickup: GeoPoint, destination: GeoPoint) {
        val call = service.getDirections(
            OPEN_ROUTE_SERVICE_API_KEY,
            "${pickup.longitude},${pickup.latitude}",
            "${destination.longitude},${destination.latitude}"
        )

        call.enqueue(object : Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                if (response.isSuccessful) {
                    val directionsResponse = response.body()
                    Log.d(TAG, "API Response: $directionsResponse")
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
                        Log.e(TAG, "No coordinates found in the response")
                    }
                } else {
                    Log.e(TAG, "API call failed with response code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Log.e(TAG, "API call failed: ${t.message}")
            }
        })
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun placeMarker(location: GeoPoint, title: String, icon: Drawable? = null) {
        val marker = Marker(mapView)
        marker.position = location
        marker.icon = icon ?: resizeDrawable(resources.getDrawable(R.drawable.default_location_icon, null), 32, 32) // Default icon if none provided
        marker.title = title
        mapView.overlays.add(marker)
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

    private fun generateDummyCabs(center: GeoPoint): List<GeoPoint> {
        val cabs = mutableListOf<GeoPoint>()
        val random = java.util.Random()
        for (i in 1..NUMBER_OF_CABS) {
            val latOffset = random.nextDouble() * 0.01 - 0.005
            val lonOffset = random.nextDouble() * 0.01 - 0.005
            val cabLocation = GeoPoint(center.latitude + latOffset, center.longitude + lonOffset)
            cabs.add(cabLocation)
        }
        return cabs
    }

    private fun displayCabs(cabs: List<GeoPoint>, startPoint: GeoPoint) {
        // Use the correct drawable for cab markers
        val cabMarkerIcon = resizeDrawable(resources.getDrawable(R.drawable.default_cab_icon, null), 32, 32)

        var closestCab: GeoPoint? = null
        var minDistance = Double.MAX_VALUE

        for (cab in cabs) {
            placeMarker(cab, "Cab", cabMarkerIcon) // Pass the correct icon for cabs
            val distance = calculateDistance(startPoint, cab)
            if (distance < minDistance) {
                minDistance = distance
                closestCab = cab
            }
        }

        closestCab?.let {
            // Use the same icon for the closest cab as for other cabs
            placeMarker(it, "Closest Cab", cabMarkerIcon)
            Toast.makeText(this, "Closest cab is at: ${it.latitude}, ${it.longitude}", Toast.LENGTH_LONG).show()
        }
    }


    private fun calculateDistance(point1: GeoPoint, point2: GeoPoint): Double {
        val earthRadius = 6371e3 // Earth radius in meters
        val lat1 = Math.toRadians(point1.latitude)
        val lat2 = Math.toRadians(point2.latitude)
        val deltaLat = Math.toRadians(point2.latitude - point1.latitude)
        val deltaLon = Math.toRadians(point2.longitude - point1.longitude)

        val a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadius * c // Distance in meters
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
