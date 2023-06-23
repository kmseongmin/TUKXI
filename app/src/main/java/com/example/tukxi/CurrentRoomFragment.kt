package com.example.tukxi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.tukxi.databinding.ActivityMainroomBinding
import com.example.tukxi.databinding.FragmentCurrentroomBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener

class SharedViewModel : ViewModel() {
    var roomname: MutableLiveData<String> = MutableLiveData()
    var myhour: MutableLiveData<Int> = MutableLiveData()
    var mymin: MutableLiveData<Int> = MutableLiveData()
    var chatRoomId: MutableLiveData<String> = MutableLiveData()
    var chatRoomClickId: MutableLiveData<String> = MutableLiveData()
    var mode: MutableLiveData<Int> = MutableLiveData()
    var startname: MutableLiveData<String> = MutableLiveData()
    var endname: MutableLiveData<String> = MutableLiveData()

}
class CurrentRoomFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentCurrentroomBinding? = null
    private lateinit var database: DatabaseReference
    private val binding get() = _binding!!
    private var roomname: String? = null
    private var myhour: Int? = 0
    private var mymin: Int? = 0
    private var chatRoomId: String? = null
    private var chatRoomClickId: String? = null
    private var mode: Int? = 2
    private var startname : String? = null
    private var endname : String? = null
    private var peoplecount : Int? = 0

    private fun getpeoplecount(chatRoomId : String) {
        val chatRoomId = chatRoomId // 가져올 특정 chatroom의 ID
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomRef = database.child("chatRooms").child(chatRoomId)

        chatRoomRef.child("peoplecount").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                peoplecount = dataSnapshot.getValue(Int::class.java)
                if (peoplecount != null) {
                    binding.participantsTextView.text = "참여중인 사람 : $peoplecount / 4"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // 데이터 가져오기가 실패한 경우 처리할 로직을 여기에 구현합니다.
                Log.e("Firebase", "Failed to retrieve peoplecount: ${error.message}")
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let { bundle ->
            roomname = bundle.getString("roomname").toString()
            myhour = bundle.getInt("hour")
            mymin = bundle.getInt("min")
            chatRoomId = bundle.getString("chatRoomId").toString()
            chatRoomClickId = bundle.getString("chatRoomClickId").toString() // 방 조회에서 받은 id
            mode = bundle.getInt("mode")
            startname = bundle.getString("startname").toString()
            endname = bundle.getString("endname").toString()
        }

        if(startname != null && endname != null) {
            sharedViewModel.roomname.value = roomname
            sharedViewModel.myhour.value = myhour
            sharedViewModel.mymin.value = mymin
            sharedViewModel.chatRoomId.value = chatRoomId
            sharedViewModel.chatRoomClickId.value = chatRoomClickId
            sharedViewModel.mode.value = mode
            sharedViewModel.startname.value = startname
            sharedViewModel.endname.value = endname
        }else{
            roomname = sharedViewModel.roomname.value
            myhour = sharedViewModel.myhour.value
            mymin = sharedViewModel.mymin.value
            chatRoomId = sharedViewModel.chatRoomId.value
            chatRoomClickId = sharedViewModel.chatRoomClickId.value
            mode = sharedViewModel.mode.value
            startname = sharedViewModel.startname.value
            endname = sharedViewModel.endname.value
        }

        if(mode==0){
            chatRoomClickId?.let { getpeoplecount(it) }
        }
        else if(mode==1){
            chatRoomId?.let { getpeoplecount(it) }
        }
        val bundle = Bundle().apply {
            putString("roomname", roomname) // roomname 값을 Bundle에 담기
            putInt("hour", myhour!!.toInt())
            putInt("min", mymin!!.toInt())
            putString("chatRoomId", chatRoomId)
            putString("chatRoomClickId", chatRoomClickId)
            putInt("mode", mode!!.toInt())
        }

        //currentRoomMenuItem 메뉴 활성화
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        val menu = bottomNavigationView.menu
        val currentRoomMenuItem = menu.findItem(R.id.currentRoomFragment_item)
        if(!currentRoomMenuItem.isEnabled){
            currentRoomMenuItem.isEnabled = true
        }

        val navController = findNavController()
        binding.returnbutton.setOnClickListener{
            navController.navigate(R.id.roomInFragment,bundle)
        }
        binding.chatRoomNameTextView.text = "채팅방 이름 : $roomname"
        binding.departureTextView.text = "출발지: $startname"
        binding.destinationTextView.text = "목적지: $endname"

    }
    private fun updateFirebaseValue(chatRoomId: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val setval = database.child("chatRooms").child(chatRoomId.toString()).child("peoplecount")

        // 변경하고자 하는 데이터의 참조 경로를 지정합니다.
        setval.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val value = currentData.getValue(Int::class.java) ?: 0
                currentData.value = value - 1
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentroomBinding.inflate(inflater, container, false)
        val view = binding.root

        val navController = findNavController()


        binding.exit.setOnClickListener{
            chatRoomClickId?.let { it1 -> updateFirebaseValue(it1) }
            chatRoomId?.let { it1 -> updateFirebaseValue(it1) }
            navController.popBackStack()
            navController.navigate(R.id.mapActivity)
        }
        database = FirebaseDatabase.getInstance().reference

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bottom_nav_menu, menu)
        val currentRoomItem = menu.findItem(R.id.currentRoomFragment)

        currentRoomItem.isEnabled = true // 활성화

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}