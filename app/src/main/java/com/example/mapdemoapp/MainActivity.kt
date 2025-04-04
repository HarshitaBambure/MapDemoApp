package com.example.mapdemoapp

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


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

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
                val msg = "Lat: " + p.latitude + ", Lon: " + p.longitude
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
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
}