package com.example.mapdemoapp

import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mapdemoapp.databinding.ActivityMainBinding
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.util.Locale


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private  var currentMarker: Marker?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        Configuration.getInstance().load(
            getApplicationContext(),
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        );
        setContentView(binding.root)
        setupMap()
    }

    private fun setupMap() {
        Configuration.getInstance().setUserAgentValue(getPackageName());
        binding.map.setTileSource(TileSourceFactory.USGS_SAT)
        binding.map.setMultiTouchControls(true)
        binding.map.getController().setZoom(5.0)
        binding.map.getController().setCenter(GeoPoint(20.5937, 78.9629))


        // Handle map tap
        val mapEventsOverlay = MapEventsOverlay(this, object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                showMarker(p)
                showAddress(p)
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        })

        binding.map.getOverlays().add(mapEventsOverlay)
    }

    public override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    public override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    private fun showMarker(point: GeoPoint) {
        if (currentMarker != null) {
            binding.map.getOverlays().remove(currentMarker)
        }

        currentMarker = Marker(binding.map)
        currentMarker?.setPosition(point)
        currentMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        currentMarker?.setTitle(
            """
            Lat: ${point.latitude}
            Lon: ${point.longitude}
            """.trimIndent()
        )

        binding.map.getOverlays().add(currentMarker)
        binding.map.invalidate() // refresh
    }
    private fun showAddress(point: GeoPoint) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1)
            if (addresses != null && !addresses.isEmpty()) {
                val address = addresses[0]
                val addr = address.getAddressLine(0)
                Toast.makeText(this, addr, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "No address found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Geocoder failed", Toast.LENGTH_SHORT).show()
        }
    }
}