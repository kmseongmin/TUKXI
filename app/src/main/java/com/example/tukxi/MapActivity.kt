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
import android.location.Geocoder
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker

class MapViewModel : ViewModel() {
    var startLatLng: LatLng? = null
    var endLatLng: LatLng? = null
}
class MapActivity : Fragment(), OnMapReadyCallback {
    private var _binding: ActivityMapBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null

    private lateinit var mapView: MapView
    private lateinit var quickMatchingbtn: Button
    private lateinit var searchRoombtn: Button
    private lateinit var makeRoombtn: Button
    private lateinit var startedt:EditText
    private lateinit var endedt:EditText
    private lateinit var startbtn:Button
    private lateinit var endbtn:Button
    // 출발지와 도착지 마커 변수
    private var startMarker: Marker? = null
    private var endMarker: Marker? = null
    // 출발지와 도착지 위치 저장하는 변수
    private var startLatLng: LatLng? = null
    private var endLatLng: LatLng? = null
    private var savedCameraPosition: CameraPosition? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var mapViewModel: MapViewModel

    private fun showStartMarker() {
        if (mapViewModel.startLatLng != null) {
            startMarker = googleMap?.addMarker(MarkerOptions().position(mapViewModel.startLatLng!!).title("출발지"))
        }
    }

    private fun showEndMarker() {
        if (mapViewModel.endLatLng != null) {
            endMarker = googleMap?.addMarker(MarkerOptions().position(mapViewModel.endLatLng!!).title("도착지"))
        }
    }

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
        startedt = binding.startEdittext
        endedt = binding.endEdittext
        startbtn = binding.startButton
        endbtn = binding.endButton

        mapViewModel = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)

        makeRoombtn.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.roomCreateFragment)
        }
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyAdHvlLbQv5ykMeeoCph3ZFAK11X-bIKDA") // 여기서 "YOUR_API_KEY"를 실제 API 키로 대체해야 합니다.
        }

// 출발지 검색 관련 코드
        startbtn.setOnClickListener {
            val location = startedt.text.toString()
            if (location.isNotEmpty()) {
                val geocoder = Geocoder(requireContext())
                try {
                    val addresses = geocoder.getFromLocationName(location, 1)
                    if (addresses != null) {
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val latLng = LatLng(address.latitude, address.longitude)
                            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                            if (startMarker != null) {
                                startMarker?.position = latLng
                                startLatLng = latLng
                            } else {
                                startMarker = googleMap?.addMarker(MarkerOptions().position(latLng).title("출발지"))
                                startLatLng = latLng
                            }
                        } else {
                            // 주소를 찾을 수 없음
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                // 검색어를 입력해주세요.
            }
        }

// 도착지 검색 관련 코드
        endbtn.setOnClickListener {
            val location = endedt.text.toString()
            if (location.isNotEmpty()) {
                val geocoder = Geocoder(requireContext())
                try {
                    val addresses = geocoder.getFromLocationName(location, 1)
                    if (addresses != null) {
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val latLng = LatLng(address.latitude, address.longitude)
                            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                            if (endMarker != null) {
                                endMarker?.position = latLng
                                endLatLng = latLng
                            } else {
                                endMarker = googleMap?.addMarker(MarkerOptions().position(latLng).title("도착지"))
                                endLatLng = latLng
                            }
                        } else {
                            // 주소를 찾을 수 없음
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                // 검색어를 입력해주세요.
            }
        }

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

        showStartMarker()
        showEndMarker()
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
        mapViewModel.startLatLng = startLatLng
        mapViewModel.endLatLng = endLatLng
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
        savedCameraPosition?.let {
            googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(it))
        }
        // 마커 다시 생성
        if (startLatLng != null) {
            startMarker = googleMap?.addMarker(MarkerOptions().position(startLatLng!!).title("출발지"))
        }
        if (endLatLng != null) {
            endMarker = googleMap?.addMarker(MarkerOptions().position(endLatLng!!).title("도착지"))
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        savedCameraPosition = googleMap?.cameraPosition
        // 마커 제거
        startMarker?.remove()
        endMarker?.remove()
        startMarker = null
        endMarker = null
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