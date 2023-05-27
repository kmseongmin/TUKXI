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

class MapActivity : Fragment(), OnMapReadyCallback {
    private var _binding: ActivityMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView
    private lateinit var edtStart: EditText
    private lateinit var edtFinish: EditText
    private lateinit var quickMatchingbtn: Button
    private lateinit var searchRoombtn: Button
    private lateinit var makeRoombtn: Button
    private lateinit var googleMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityMapBinding.inflate(inflater, container, false)

        edtStart = binding.editTextStart
        edtFinish = binding.editTextFinish
        quickMatchingbtn = binding.quickMatchingbtn
        searchRoombtn = binding.searchRoombtn
        makeRoombtn = binding.makeRoombtn
        mapView = binding.mapFragment

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // 권한이 허용되었거나 이미 허용된 경우
            googleMap.isMyLocationEnabled = true // 위치 정보 활성화
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLocation = LatLng(location.latitude, location.longitude)
                googleMap.let {
                    it.addMarker(MarkerOptions().position(currentLocation).title("현재 위치"))
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        checkLocationPermission()
        getCurrentLocation()
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