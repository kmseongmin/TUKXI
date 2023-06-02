package com.example.tukxi

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.tukxi.databinding.FragmentRoominBinding
import com.google.firebase.database.*
//import kotlinx.coroutines.NonCancellable.message
import org.checkerframework.checker.units.qual.min


class RoomInFragment() : Fragment(), Parcelable {
    private var _binding: FragmentRoominBinding? = null
    private val binding get() = _binding!!
    private var chatRoomId: String? = null
    private var myhour : Int? = null
    private var mymin : Int? = null
    private var roomname : String? = null

    data class ChatMessage(val senderId: String = "", val message: String = "")

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
    private val textViews = mutableListOf<TextView>()
    private val maxTextViewCount = 10
    constructor(parcel: Parcel) : this() {
        chatRoomId = parcel.readString()
    }

    private var message : String? = null
    private var fragmentContext : Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
    private fun addTextView(name : String?, senderId: String) {
        val newTextView = TextView(fragmentContext)

        newTextView.text = senderId + " : " + name
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        newTextView.layoutParams = layoutParams
        //textViews.add(newTextView)
        //binding.textViewContainer.addView(newTextView)


        //val containerLayout = view?.findViewById<LinearLayout>(R.id.textViewContainer)

        binding.textViewContainer.addView(newTextView)

        //if (textViews.size > maxTextViewCount) {
          //  val textViewToRemove = textViews.removeFirstOrNull()
          //  binding.textViewContainer.removeView(textViewToRemove)
        //} //최신 10개의 텍스트만 보여줌
    }
    // 채팅 메시지 수신
    fun receiveMessage(chatRoomId: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomRef = database.child("chatRooms").child(chatRoomId).child("messages")

        chatRoomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                binding.textViewContainer.removeAllViews()
                textViews.clear()

                for (messageSnapshot in dataSnapshot.children) {
                    val chatMessage = messageSnapshot.getValue(RoomInFragment.ChatMessage::class.java)
                    val senderId = chatMessage?.senderId
                    message = chatMessage?.message

                    if (senderId != null && message != null) {
                        println("발신자 ID: $senderId")
                        println("메시지 내용: $message")
                        addTextView(message, senderId)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("채팅 메시지 수신에 실패했습니다: ${error.message}")
            }
        })
    }

    private fun getChatRoomMessages(chatRoomClickId: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomRef = database.child("chatRooms").child("message")


        chatRoomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 채팅 내역을 가져와 처리하는 로직을 여기에 구현하세요.
                for (messageSnapshot in dataSnapshot.children) {
                    // 각 메시지의 데이터를 가져오기
                    val senderId = messageSnapshot.child("senderId").getValue(String::class.java)
                    val message = messageSnapshot.child("chatRooms").child("message").getValue(String::class.java)
                    sendMessage(chatRoomClickId, senderId.toString(), message.toString())
                    receiveMessage(chatRoomClickId)
                    // 채팅 내역을 화면에 표시하는 로직을 구현하세요.
                    // ...
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatRoomFragment", "채팅 내역 가져오기 실패: ${error.message}")
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoominBinding.inflate(inflater, container, false)
        val view = binding.root
        var chatRoomClickId : String? = null
        var mode : Int? = null
        arguments?.let { bundle ->
            roomname = bundle.getString("roomname")
            myhour = bundle.getInt("hour")
            mymin = bundle.getInt("min")
            chatRoomId= bundle.getString("chatroomid")
            chatRoomClickId = bundle.getString("chatRoomClickId") // 방 조회에서 받은 id
            mode = bundle.getInt("mode")
        }
        if(mode==0){
            receiveMessage(chatRoomClickId.toString())
        }
        val chatroomid = chatRoomId.toString() // 방생성에서 넘어온 Id
        val senderId = "jyk1234567"

        binding.button3.setOnClickListener { // 메시지 전송
            val message = binding.messages.text.toString()
            if(mode == 0){
                getChatRoomMessages(chatRoomClickId.toString())
                sendMessage(chatRoomClickId.toString(), senderId, message)
                receiveMessage(chatRoomClickId.toString())
            }
            else if(mode == 1) {
                sendMessage(chatroomid, senderId, message)
                receiveMessage(chatroomid)
            }
        }
        // 채팅방Id를 통해 보내는 사람과 메시지를 전달한다

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // 프래그먼트가 실행 된 이후에 보일 화면
        super.onViewCreated(view, savedInstanceState)
        val chatRoomClickId = arguments?.getString("chatRoomClickId")
        val mode = arguments?.getInt("mode")
        if (mode == 0) {
            receiveMessage(chatRoomClickId.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(chatRoomId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoomInFragment> {
        override fun createFromParcel(parcel: Parcel): RoomInFragment {
            return RoomInFragment(parcel)
        }

        override fun newArray(size: Int): Array<RoomInFragment?> {
            return arrayOfNulls(size)
        }
    }
}