package com.example.tukxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.tukxi.databinding.FragmentRoomviewBinding
import com.google.android.play.integrity.internal.c
import com.google.firebase.database.*


class RoomViewFragment : Fragment() {
    private var _binding: FragmentRoomviewBinding? = null
    private val binding get() = _binding!!
    private var chatRoomId: String? = null

    fun createChatRoom(name: String, description: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomsRef = database.child("chatRoom3") // 채팅방 이름

        val chatRoom = ChatRoom(name, description)
        val chatRoomRef = chatRoomsRef.push()

        chatRoomRef.setValue(chatRoom)
            .addOnSuccessListener {
                // 채팅방 생성 성공 시 처리할 로직
                println("채팅방이 생성되었습니다.")
                println("채팅방 이름: $name")
                println("채팅방 설명: $description")
            }
            .addOnFailureListener { e ->
                // 채팅방 생성 실패 시 처리할 로직
                println("채팅방 생성에 실패했습니다: ${e.message}")
            }
        chatRoomId = chatRoomRef.key
        println("생성된 채팅방 ID: $chatRoomId")
    }
    class ChatRoom(val name: String, val description: String)

    private fun getChatRoomName(chatRoomId: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomRef = database.child("chatRooms").child(chatRoomId)

        chatRoomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chatRoom = dataSnapshot.getValue(ChatRoom::class.java)
                val chatRoomName = chatRoom?.name

                if (chatRoomName != null) {
                    println("채팅방 이름: $chatRoomName")
                } else {
                    println("채팅방 이름을 가져오지 못했습니다.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("채팅방 이름을 가져오는데 실패했습니다: ${error.message}")
            }
        })
    }

    data class ChatMessage(val senderId: String, val message: String)

    // 채팅 메시지 전송
    fun sendMessage(chatRoomId: String, senderId: String, message: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomRef = database.child("chatRooms").child(chatRoomId).child("messages")
        val messageRef = chatRoomRef.push()

        val chatMessage = ChatMessage(senderId, message)
        messageRef.setValue(chatMessage)
            .addOnSuccessListener {
                println("메시지가 전송되었습니다.")
                println("발신자 ID: $senderId")
                println("메시지 내용: $message")
            }
            .addOnFailureListener { e ->
                println("메시지 전송에 실패했습니다: ${e.message}")
            }
    }

    // 채팅 메시지 수신
    fun receiveMessages(chatRoomId: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomRef = database.child("chatRooms").child(chatRoomId).child("messages")

        chatRoomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (messageSnapshot in dataSnapshot.children) {
                    val chatMessage = messageSnapshot.getValue(ChatMessage::class.java)
                    val senderId = chatMessage?.senderId
                    val message = chatMessage?.message

                    if (senderId != null && message != null) {
                        println("발신자 ID: $senderId")
                        println("메시지 내용: $message")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("채팅 메시지 수신에 실패했습니다: ${error.message}")
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)


        createChatRoom("My Chat Room2", "This is a chat room for testing")
        // 채팅방 생성 : 이름과 채팅방설명을 저장한다
        //getChatRoomName("$chatRoomId")
        // 채팅방 이름을 ID를 통해 가져온다
        val chatname = "$chatRoomId"
        val senderId = "sender123"
        val message = "Hello, World!"
        sendMessage(chatname, senderId, message)
        // 채팅방Id를 통해 보내는 사람과 메시지를 전달한다
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoomviewBinding.inflate(inflater, container, false)
        val view = binding.root

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