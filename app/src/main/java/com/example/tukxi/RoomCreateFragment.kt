package com.example.tukxi

import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.tukxi.databinding.FragmentRoomcreateBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import org.checkerframework.checker.units.qual.min
import kotlin.math.*
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase


class MyViewModel : ViewModel() {
    val startnames: MutableLiveData<String> = MutableLiveData()
    val endnames: MutableLiveData<String> = MutableLiveData()
    val startLatitudes : MutableLiveData<Double> = MutableLiveData()
    val startLongtitudes : MutableLiveData<Double> = MutableLiveData()
    val endLatitudes : MutableLiveData<Double> = MutableLiveData()
    val endLongtitudes : MutableLiveData<Double> = MutableLiveData()
}

class RoomCreateFragment : Fragment(){
    private var _binding: FragmentRoomcreateBinding? = null
    private val binding get() = _binding!!
    private var chatRoomId: String? = null
    private var roomname : String? = null
    private var myhour : Int? = null
    private var mymin : Int? = null
    private var startLatitude : Double? = null
    private var startLongitude : Double? = null
    private var endLatitude : Double? = null
    private var endLongitude : Double? = null
    private lateinit var startLatLng : LatLng
    private lateinit var endLatLng : LatLng
    private lateinit var startnameTextview : TextView
    private lateinit var endnameTextview : TextView
    private lateinit var startname : String
    private lateinit var endname : String
    private var peoplecount : Int = 1
    private val sharedviewModel: MyViewModel by viewModels({requireParentFragment()})
    var typemode = 0
    // 변수 초기화 예시

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

    }
    fun createChatRoom(roomname: String, hour:Int, min:Int, startLatlng: LatLng, endLatlng : LatLng, peoplecount : Int) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomsRef = database.child("chatRooms") // 채팅방 이름
        val distance = calculateDistanceInMeters(startLatlng, endLatlng)
        val chatRoom = ChatRoom(roomname,hour,min,distance,startLatlng,endLatlng, peoplecount)
        val chatRoomRef = chatRoomsRef.push()

        chatRoomRef.setValue(chatRoom)
            .addOnSuccessListener {
                // 채팅방 생성 성공 시 처리할 로직
                println("채팅방이 생성되었습니다.")
                println("채팅방 이름: $roomname")
            }
            .addOnFailureListener { e ->
                // 채팅방 생성 실패 시 처리할 로직
                println("채팅방 생성에 실패했습니다: ${e.message}")
            }
        chatRoomId = chatRoomRef.key
        println("생성된 채팅방 ID: $chatRoomId")
    }
    data class LatLng(val latitude: Double, val longitude: Double)

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

    class ChatRoom(val roomname: String, val hour: Int, val min: Int, val distance : Double, val startLatLng : LatLng, val endLatlng : LatLng, var peoplecount: Int)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoomcreateBinding.inflate(inflater, container, false)
        val view = binding.root
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        var Nickname : String? = null
        if(currentUser != null) {
            val Email = currentUser.email
            val db = FirebaseFirestore.getInstance()
            val collectionReference = FirebaseFirestore.getInstance().collection("UserInformation")

            collectionReference
                .whereEqualTo("Email", Email) // 필드의 값을 지정하여 해당 문서를 찾습니다.
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (documentSnapshot in querySnapshot.documents) {
                        // 문서에 접근하여 데이터를 가져옵니다.
                        val data = documentSnapshot.data
                        Nickname = documentSnapshot.get("Nickname").toString()
                        binding.tvUserNickname.text = Nickname + "님 안녕하세요"
                        binding.editTextText.hint = Nickname + "님의 방"
                            // 가져온 데이터를 활용하는 로직을 작성합니다.
                    }
                }
                .addOnFailureListener { e ->
                    // 데이터 가져오기 실패 시 처리할 로직
                }

        }
        arguments?.let { bundle ->
            myhour = bundle.getInt("hour")
            mymin = bundle.getInt("min")
            // MapActivity로부터 출발지 도착지의 정보를 받음
            startLatitude = bundle.getDouble("startLatitude")
            startLongitude = bundle.getDouble("startLongitude")
            endLatitude = bundle.getDouble("endLatitude")
            endLongitude = bundle.getDouble("endLongitude")
            startname = bundle.getString("startname").toString()
            endname = bundle.getString("endname").toString()
            typemode = bundle.getInt("typemode")
        }

        //경위도값 저장

        //출발지 도착지 이름 입력
        startnameTextview = binding.startnametextView
        endnameTextview = binding.endnametextView

        if(typemode == 1) {
            startname = sharedviewModel.startnames.value.toString()
            endname = sharedviewModel.endnames.value.toString()
            startLatitude = sharedviewModel.startLatitudes.value
            startLongitude = sharedviewModel.startLongtitudes.value
            endLatitude = sharedviewModel.endLatitudes.value
            endLongitude = sharedviewModel.endLongtitudes.value
            startnameTextview.setText("출발지 : " + sharedviewModel.startnames.value.toString())
            endnameTextview.setText("도착지 : $endname")
        }
        else if(typemode == 0) {
            sharedviewModel.startnames.setValue(startname)
            sharedviewModel.endnames.setValue(endname)
            sharedviewModel.startLatitudes.setValue(startLatitude)
            sharedviewModel.startLongtitudes.setValue(startLongitude)
            sharedviewModel.endLatitudes.setValue(endLatitude)
            sharedviewModel.endLongtitudes.setValue(endLongitude)
            startnameTextview.setText("출발지 : " + startname)
            endnameTextview.setText("도착지 : " + endname)
        }
        startLatLng = LatLng(startLatitude!!.toDouble(), startLongitude!!.toDouble())
        endLatLng = LatLng(endLatitude!!.toDouble(), endLongitude!!.toDouble())
        var mode = 1
        binding.btnTime.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.clockFragment)
        }
        binding.button2.setOnClickListener{
            var roomname = binding.editTextText.text.toString() // 만약 채팅방 이름이 2글자 이하이면 다시 생성하도록 토스트메시지 출력 구현해야함
            if(roomname.length == 0){
                roomname = binding.editTextText.hint.toString()
            }
            else if (roomname.length < 3 && roomname.length > 0) {
                // 토스트 메시지 등으로 유효성 검사를 추가하고, 적절한 조치를 취할 수 있습니다. 토스트 아직 미구현
                Toast.makeText(requireContext(),"방 이름을 세 글자 이상으로 설정해주세요!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(mymin == 0 || myhour == 0){
                Toast.makeText(requireContext(), "출발시간을 꼭 설정해주세요!", Toast.LENGTH_SHORT).show()
               return@setOnClickListener
            }
            val chatnames = roomname
            createChatRoom(chatnames,myhour!!.toInt(),mymin!!.toInt(), startLatLng, endLatLng, peoplecount) // 방생성

           val bundle = Bundle().apply {
               putString("roomname", roomname) // roomname 값을 Bundle에 담기
               putInt("hour",myhour!!.toInt())
               putInt("min",mymin!!.toInt())
               putString("chatRoomId",chatRoomId.toString())
               putInt("mode", mode)
               putDouble("startLatitude", startLatitude!!.toDouble())
               putDouble("startLongtitude", startLongitude!!.toDouble())
               putDouble("endLatitude",endLatitude!!.toDouble())
               putDouble("endLongtitude",endLongitude!!.toDouble())
               putString("startname", startname)
               putString("endname", endname)
           }

           val navController = findNavController()
            navController.navigate(R.id.roomInFragment,bundle)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // 프래그먼트가 실행 된 이후에 보일 화면
        super.onViewCreated(view, savedInstanceState)

        sharedviewModel.startnames.observe(viewLifecycleOwner) { startname ->
            startnameTextview.text = "출발지 : $startname"
        }

        sharedviewModel.endnames.observe(viewLifecycleOwner) { endname ->
            endnameTextview.text = "도착지 : $endname"
        }
    }

    override fun onPause() {
        super.onPause()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
