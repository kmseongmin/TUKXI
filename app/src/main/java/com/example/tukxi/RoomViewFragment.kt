package com.example.tukxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import com.example.tukxi.databinding.FragmentRoomviewBinding
import com.google.android.play.integrity.internal.c
import com.google.firebase.database.*


class RoomViewFragment : Fragment() {
    private var _binding: FragmentRoomviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference


    private fun getChatRoomNames() {
        val chatRoomsRef = database.child("chatRooms")

        chatRoomsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val containerLayout = view?.findViewById<LinearLayout>(R.id.buttonContainerLayout)

                containerLayout?.removeAllViews()

                for (roomSnapshot in dataSnapshot.children) {
                    val roomName = roomSnapshot.child("roomname").getValue(String::class.java)
                    val chatRoomId = roomSnapshot.key
                    if (roomName != null) {
                        createButtonForChatRoom(containerLayout, roomName, chatRoomId.toString())

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                println("방 이름 조회에 실패했습니다: ${error.message}")
            }
        })
    }

    private fun createButtonForChatRoom(containerLayout: LinearLayout?, roomName: String, chatRoomId : String) {
        if(isAdded) {
            val button = Button(requireActivity())
            button.text = roomName
            val bundle = Bundle()
            button.setOnClickListener {
                bundle.putString("chatRoomClickId", chatRoomId)
                bundle.putInt("mode", 0)
                // 버튼 클릭 시 방에 접속하는 동작을 구현하세요
                val navController = findNavController()
                navController.navigate(R.id.roomInFragment, bundle)
            }
            containerLayout?.addView(button)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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