package com.example.tukxi

import android.location.Geocoder
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.tukxi.databinding.FragmentRoomviewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.play.integrity.internal.c
import com.google.firebase.database.*
import kotlin.math.*

class RoomViewFragment : Fragment() {
    private var _binding: FragmentRoomviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    //출발지 도착지 저장 변수
    private var startname: String? = null
    private var endname: String? = null
    //출발지 도착지 각각의 경위도 정보 따로
    private var startLatitude: Double = 0.0
    private var startLongitude: Double = 0.0
    private var endLatitude: Double = 0.0
    private var endLongitude: Double = 0.0
    //출발지 도착지 경위도 저장 변수
    private lateinit var startLatLng: LatLng
    private lateinit var endLatLng: LatLng
    //파이어베이스에서 가져올 출발지 도착지 경위도 따로 저장 변수
    private var fbstartLatitude: Double? = 0.0
    private var fbstartLongitude: Double? = 0.0
    private var fbendLatitude: Double? = 0.0
    private var fbendLongitude: Double? = 0.0
    //파이어베이스에서 가져온 출발지 도착지 경위도 저장 변수
    private lateinit var fbstartLatLng: LatLng
    private lateinit var fbendLatLng: LatLng
    private var peoplecount : Int? = 0
    private var hour : Int? = 0
    private var min : Int? = 0
    //출발지 도착지 설정 edt텍스트
    private lateinit var endedt: EditText
    private lateinit var startedt: EditText
    private lateinit var searchbtn: Button
    private var ampm : String? = ""

    data class LatLng(val latitude: Double, val longitude: Double)

    //두 위치 사이의 거리
    fun calculateDistanceInMeters(location1: LatLng, location2: LatLng): Double {
        val earthRadius = 6371000.0 // 지구의 반지름 (미터)

        val lat1Rad = Math.toRadians(location1.latitude)
        val lat2Rad = Math.toRadians(location2.latitude)
        val latDiffRad = Math.toRadians(location2.latitude - location1.latitude)
        val lngDiffRad = Math.toRadians(location2.longitude - location1.longitude)

        val a = sin(latDiffRad / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(lngDiffRad / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    //center의 위치와 point 위치 사이의 거리가 radius(m)보다 작다면 true
    fun isWithinRadius(center: LatLng, point: LatLng, radius: Double): Boolean {
        val distance = calculateDistanceInMeters(center, point)
        return distance <= radius
    }
    private fun updateFirebaseValue(chatRoomId: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val setval = database.child("chatRooms").child(chatRoomId.toString()).child("peoplecount")

        // 변경하고자 하는 데이터의 참조 경로를 지정합니다.
        setval.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val value = currentData.getValue(Int::class.java) ?: 0
                currentData.value = value + 1
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null) {
                    // 업데이트 중에 오류가 발생한 경우 처리할 코드를 작성합니다.
                } else {
                    // 업데이트가 성공적으로 완료된 경우 처리할 코드를 작성합니다.
                }
            }
        })
    }
    private fun getChatRoomNames() {
        val chatRoomsRef = database.child("chatRooms")

        chatRoomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val containerLayout = view?.findViewById<LinearLayout>(R.id.roomNamescontainer)

                containerLayout?.removeAllViews()

                for (roomSnapshot in dataSnapshot.children) {
                    val roomName = roomSnapshot.child("roomname").getValue(String::class.java)
                    val chatRoomId = roomSnapshot.key
                    if (roomName != null) {
                        peoplecount = roomSnapshot.child("peoplecount").getValue(Int::class.java)
                        hour = roomSnapshot.child("hour").getValue(Int::class.java)
                        min = roomSnapshot.child("min").getValue(Int::class.java)
                        ampm = roomSnapshot.child("ampm").getValue(String::class.java)
                        fbstartLatitude = roomSnapshot.child("startLatLng").child("latitude").getValue(Double::class.java)
                        fbstartLongitude = roomSnapshot.child("startLatLng").child("longitude").getValue(Double::class.java)
                        fbendLatitude = roomSnapshot.child("endLatlng").child("latitude").getValue(Double::class.java)
                        fbendLongitude = roomSnapshot.child("endLatlng").child("longitude").getValue(Double::class.java)
                        if(fbstartLatitude != null && fbstartLongitude != null &&
                                fbendLatitude != null && fbendLongitude != null){
                            fbstartLatLng = LatLng(fbstartLatitude!!, fbstartLongitude!!)
                            fbendLatLng = LatLng(fbendLatitude!!, fbendLongitude!!)
                            val radius = 1000.0

                            if(isWithinRadius(startLatLng,fbstartLatLng,radius)&&isWithinRadius(endLatLng,fbendLatLng,radius))
                            {
                                createButtonForChatRoom(containerLayout, roomName, chatRoomId.toString())
                            }
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                println("방 이름 조회에 실패했습니다: ${error.message}")
            }
        })
    }
    var mode = 0

    private fun createButtonForChatRoom(containerLayout: LinearLayout?, roomName: String, chatRoomId : String) {
        if(isAdded) {
            val button = Button(requireActivity())
            button.text = "방 이름 : " + roomName +
                    "\n 현재 인원 수 : $peoplecount / 4" +
                    "\n 출발 시간 : $ampm $hour 시 $min 분"
            val bundle = Bundle()
            button.setOnClickListener {
                bundle.putString("chatRoomClickId", chatRoomId)
                bundle.putInt("mode", mode)

                // 버튼 클릭 시 방에 접속하는 동작을 구현하세요
                val navController = findNavController()
                navController.navigate(R.id.roomInFragment, bundle)
            }
            containerLayout?.addView(button)
        }
    }
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
        database = FirebaseDatabase.getInstance().reference
        // 방 이름들을 가져와서 버튼 생성
        Toast.makeText(requireContext(), "방 목록을 불러오는 중입니다...\n" +
                "잠시만 기다려주십시오.." , Toast.LENGTH_SHORT).show()

        arguments?.let { bundle ->
            // MapActivity로부터 출발지 도착지의 정보를 받음
            startLatitude = bundle.getDouble("startLatitude")
            startLongitude = bundle.getDouble("startLongitude")
            endLatitude = bundle.getDouble("endLatitude")
            endLongitude = bundle.getDouble("endLongitude")
            startname = bundle.getString("startname")
            endname = bundle.getString("endname")
        }
        startedt = binding.startedt
        endedt = binding.endedt
        searchbtn = binding.searchbtn
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyAdHvlLbQv5ykMeeoCph3ZFAK11X-bIKDA")
        }

        searchbtn.setOnClickListener {
            val startlocation = startedt.text.toString()
            val endlocation = endedt.text.toString()
            if (startlocation.isNotEmpty() && endlocation.isNotEmpty()) {
                val geocoder = Geocoder(requireContext())
                try {
                    val startaddresses = geocoder.getFromLocationName(startlocation, 1)
                    val endaddresses = geocoder.getFromLocationName(endlocation, 1)
                    if (startaddresses != null && endaddresses !=null) {
                        if (startaddresses.isNotEmpty()&&endaddresses.isNotEmpty()) {
                            val startaddress = startaddresses[0]
                            val endaddress = endaddresses[0]
                            startLatLng = LatLng(
                                startaddress.latitude,
                                startaddress.longitude
                            )
                            endLatLng = LatLng(
                                endaddress.latitude,
                                endaddress.longitude
                            )
                        } else {
                            Toast.makeText(requireContext(), "정확한 주소를 입력해 주세요!!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(requireContext(), "검색어를 입력해 주세요!!", Toast.LENGTH_SHORT).show()
            }
            getChatRoomNames()
        }

        //경위도값 저장

        startLatLng = LatLng(startLatitude, startLongitude)
        endLatLng = LatLng(endLatitude, endLongitude)


        getChatRoomNames()
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