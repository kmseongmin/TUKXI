package com.example.tukxi

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tukxi.databinding.ActivityMapBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.widget.AutocompleteSupportFragment

class MapActivity : Fragment(), OnMapReadyCallback {
    private var _binding: ActivityMapBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null

    private lateinit var mapView: MapView
    private lateinit var quickMatchingbtn: Button
    private lateinit var searchRoombtn: Button
    private lateinit var makeRoombtn: Button
    private lateinit var startmapSearch:AutocompleteSupportFragment
    private lateinit var endmapSearch:AutocompleteSupportFragment
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityMapBinding.inflate(inflater, container, false)

        quickMatchingbtn = binding.quickMatchingbtn
        searchRoombtn = binding.searchRoombtn
        makeRoombtn = binding.makeRoombtn
        mapView = binding.mapFragment
        startmapSearch = childFragmentManager.findFragmentById(R.id.start_autocomplete_fragment) as AutocompleteSupportFragment
        startmapSearch.setHint("출발지")
        endmapSearch = childFragmentManager.findFragmentById(R.id.end_autocomplete_fragment) as AutocompleteSupportFragment
        endmapSearch.setHint("도착지")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            initializeMap()
        }
    }

    private fun initializeMap() {
        mapView.onCreate(Bundle())
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (googleMap != null) {
            try {
                googleMap?.isMyLocationEnabled = true
                getCurrentLocation()
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    private fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    googleMap?.addMarker(MarkerOptions().position(currentLocation).title("현재 위치"))
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }
}