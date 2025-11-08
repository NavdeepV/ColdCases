package com.example.coldcases

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CasesActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var casesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cases)

        mapView = findViewById(R.id.mapView)
        casesContainer = findViewById(R.id.casesContainer)
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnProfile: ImageButton = findViewById(R.id.btnProfile)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        btnBack.setOnClickListener { finish() }
        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        addCaseItem(
            status = "Missing Person",
            location = "Last seen in Toronto, ON",
            description = "Ongoing investigation since 2021"
        )

        addCaseItem(
            status = "Unsolved Robbery",
            location = "Occurred on June 15, 2023",
            description = "No suspects identified yet"
        )

        addCaseItem(
            status = "Cold Case",
            location = "Disappeared in 2015, Oshawa",
            description = "Presumed missing"
        )
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val toronto = LatLng(43.65107, -79.347015)
        googleMap.addMarker(MarkerOptions().position(toronto).title("Toronto"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toronto, 10f))

        googleMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
            isCompassEnabled = true
        }
    }

    private fun addCaseItem(status: String, location: String, description: String) {
        val caseView = layoutInflater.inflate(R.layout.item_case_card, casesContainer, false)

        val caseStatus = caseView.findViewById<TextView>(R.id.caseStatus)
        val caseLocation = caseView.findViewById<TextView>(R.id.caseLocation)
        val caseDescription = caseView.findViewById<TextView>(R.id.caseDescription)
        val statusIndicator = caseView.findViewById<View>(R.id.statusIndicator)

        caseStatus.text = status
        caseLocation.text = "Location: $location"
        caseDescription.text = "Description: $description"

        statusIndicator.setBackgroundColor(
            when (status.lowercase()) {
                "missing person" -> Color.GREEN
                else -> Color.RED
            }
        )

        casesContainer.addView(caseView)
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
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
