package com.example.tukxi

import android.os.Bundle
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
    private val binding get() = _binding!!
    private var roomname: String? = null
    private var myhour: Int? = 0
    private var mymin: Int? = 0
    private var chatRoomId: String? = null
    private var chatRoomClickId: String? = null
    private var mode: Int? = 2
    private var startname : String? = null
    private var endname : String? = null
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

        binding.exit.setOnClickListener{

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentroomBinding.inflate(inflater, container, false)
        val view = binding.root

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