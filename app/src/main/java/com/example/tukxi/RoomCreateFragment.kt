package com.example.tukxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tukxi.databinding.FragmentRoomcreateBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import org.checkerframework.checker.units.qual.min

class RoomCreateFragment : Fragment(){
    private var _binding: FragmentRoomcreateBinding? = null
    private val binding get() = _binding!!
    private var chatRoomId: String? = null
    private var roomname : String? = null
    private var myhour : Int? = null
    private var mymin : Int? = null
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }
    fun createChatRoom(roomname: String, hour:Int, min:Int) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomsRef = database.child("chatRooms") // 채팅방 이름

        val chatRoom = ChatRoom(roomname,hour,min)
        val chatRoomRef = chatRoomsRef.push()
        binding.textViewContainer.removeAllViews()
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

    class ChatRoom(val roomname: String, val hour: Int, val min: Int)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoomcreateBinding.inflate(inflater, container, false)
        val view = binding.root
        var startLatitude : Double? = null
        var startLongitude : Double? = null
        var endLatitude : Double? = null
        var endLongitude : Double? = null
        var startname : String? = null
        var endname : String? = null
        arguments?.let { bundle ->
            myhour = bundle.getInt("hour")
            mymin = bundle.getInt("min")
            // MapActivity로부터 출발지 도착지의 정보를 받음
            startLatitude = bundle.getDouble("startLatitude")
            startLongitude = bundle.getDouble("startLongitude")
            endLatitude = bundle.getDouble("endLatitude")
            endLongitude = bundle.getDouble("endLongitude")
            startname = bundle.getString("startname")
            endname = bundle.getString("endname")
        }
        //경위도값 저장
        var startLatLng = LatLng(startLatitude!!.toDouble(), startLongitude!!.toDouble())
        var endLatLng = LatLng(endLatitude!!.toDouble(), endLongitude!!.toDouble())

        //출발지 도착지 이름 입력
        var startnameTextview = binding.startnametextView
        var endnameTextview = binding.endnametextView
        startnameTextview.setText("출발지 : " + startname)
        endnameTextview.setText("도착지 : " + endname)

        var mode = 1
        binding.button2.setOnClickListener{
            val roomname = binding.editTextText.text.toString() // 만약 채팅방 이름이 2글자 이하이면 다시 생성하도록 토스트메시지 출력 구현해야함
            if (roomname.length < 3) {
                // 토스트 메시지 등으로 유효성 검사를 추가하고, 적절한 조치를 취할 수 있습니다. 토스트 아직 미구현
                return@setOnClickListener
            }
            val chatnames = roomname.toString()
            createChatRoom(chatnames,myhour!!.toInt(),mymin!!.toInt()) // 방생성

           val bundle = Bundle().apply {
                putString("roomname", roomname) // roomname 값을 Bundle에 담기
                putInt("hour",myhour!!.toInt())
                putInt("min",mymin!!.toInt())
                putString("chatRoomId",chatRoomId.toString())
                putInt("mode", mode)
            }
           val navController = findNavController()
            navController.navigate(R.id.roomInFragment,bundle)
        }
        binding.btnTime.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.clockFragment)
        }
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