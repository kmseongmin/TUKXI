package com.example.tukxi

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.VoicemailContract
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
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.gms.common.api.Status
import android.content.Context
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController

class MapActivity : Fragment(), OnMapReadyCallback {
    private var _binding: ActivityMapBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null

    private lateinit var mapView: MapView
    private lateinit var quickMatchingbtn: Button
    private lateinit var searchRoombtn: Button
    private lateinit var makeRoombtn: Button
    private lateinit var startmapSearch: AutocompleteSupportFragment
    private lateinit var endmapSearch: AutocompleteSupportFragment
    private lateinit var startplaceName: String
    private lateinit var startplaceAddress: String
    private lateinit var startplaceId: String
    private lateinit var endplaceName: String
    private lateinit var endplaceAddress: String
    private lateinit var endplaceId: String

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityMapBinding.inflate(inflater, container, false)

        quickMatchingbtn = binding.quickMatchingbtn
        searchRoombtn = binding.searchRoombtn
        makeRoombtn = binding.makeRoombtn
        mapView = binding.mapFragment

        makeRoombtn.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.roomCreateFragment)
        }
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyAdHvlLbQv5ykMeeoCph3ZFAK11X-bIKDA") // 여기서 "YOUR_API_KEY"를 실제 API 키로 대체해야 합니다.
        }
        // 출발지 검색 관련 코드
        startmapSearch = childFragmentManager.findFragmentById(R.id.start_autocomplete_fragment) as AutocompleteSupportFragment
        startmapSearch.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS))
        startmapSearch.setHint("출발지")
        startmapSearch.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                startplaceName = place.name as String
                startplaceAddress = place.address as String
                startplaceId = place.id as String
                // 선택된 장소를 처리하는 로직을 추가하세요.
            }

            override fun onError(status: Status) {
                // 오류 처리
            }
        })

        // 도착지 검색 관련 코드
        endmapSearch = childFragmentManager.findFragmentById(R.id.end_autocomplete_fragment) as AutocompleteSupportFragment
        endmapSearch.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS))
        endmapSearch.setHint("도착지")
        endmapSearch.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                endplaceName = place.name as String
                endplaceAddress = place.address as String
                endplaceId = place.id as String
                // 선택된 장소를 처리하는 로직을 추가하세요.
            }

            override fun onError(status: Status) {
                // 오류 처리
            }
        })

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