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
        }
        val navController = findNavController()

        if (mode == 0) { // 방 조회를 통해서 Roomin 이후에 넘어온 경우
            binding.chatRoomNameTextView.text = "채팅방 이름 : $roomname"
            binding.returnbutton.setOnClickListener {
                navController.navigateUp()
            }
        } else if (mode == 1) {

        } else if (mode == 2) { // 현재 참여중인 방이 없는 경우

            Toast.makeText(
                requireContext(),
                "현재 참여중인 방이 없습니다. \n 먼저 방 조회 또는 방 생성을 통해 방에 참가해주세요.",
                Toast.LENGTH_SHORT
            ).show()
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