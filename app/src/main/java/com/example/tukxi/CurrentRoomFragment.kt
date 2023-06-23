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
import androidx.navigation.fragment.findNavController
import com.example.tukxi.databinding.FragmentCurrentroomBinding


class CurrentRoomFragment : Fragment() {
    private var _binding: FragmentCurrentroomBinding? = null
    private val binding get() = _binding!!
    private lateinit var roomname: String
    private var myhour: Int = 0
    private var mymin: Int = 0
    private lateinit var chatRoomId: String
    private lateinit var chatRoomClickId: String
    private var mode: Int = 2
    private lateinit var startedt : String
    private lateinit var endedt : String
    private lateinit var startname : String
    private lateinit var endname : String
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
        val bundle = Bundle().apply {
            putString("roomname", roomname) // roomname 값을 Bundle에 담기
            putInt("hour", myhour!!.toInt())
            putInt("min", mymin!!.toInt())
            putString("chatRoomId", chatRoomId)
            putString("chatRoomClickId", chatRoomClickId)
            putInt("mode", mode!!.toInt())
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