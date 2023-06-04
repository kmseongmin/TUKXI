package com.example.tukxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.tukxi.databinding.FragmentRoomviewBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.play.integrity.internal.c
import com.google.firebase.database.*


class RoomViewFragment : Fragment() {
    private var _binding: FragmentRoomviewBinding? = null
    //출발지 도착지 경위도 저장 변수
    private lateinit var startLatLng: LatLng
    private lateinit var endLatLng: LatLng
    //출발지 도착지 저장 변수
    private var startname: String? = null
    private var endname: String? = null
    //출발지 도착지 각각의 경위도 정보 따로
    private var startLatitude: Double = 0.0
    private var startLongitude: Double = 0.0
    private var endLatitude: Double = 0.0
    private var endLongitude: Double = 0.0
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoomviewBinding.inflate(inflater, container, false)
        val view = binding.root

        arguments?.let { bundle ->
            // MapActivity로부터 출발지 도착지의 정보를 받음
            startLatitude = bundle.getDouble("startLatitude")
            startLongitude = bundle.getDouble("startLongitude")
            endLatitude = bundle.getDouble("endLatitude")
            endLongitude = bundle.getDouble("endLongitude")
            startname = bundle.getString("startname")
            endname = bundle.getString("endname")
        }
        //경위도값 저장
        startLatLng = LatLng(startLatitude, startLongitude)
        endLatLng = LatLng(endLatitude, endLongitude)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // 프래그먼트가 실행 된 이후에 보일 화면
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}