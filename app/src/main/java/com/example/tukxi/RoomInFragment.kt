package com.example.tukxi

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.tukxi.databinding.FragmentRoominBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import kotlin.concurrent.thread


class RoomInFragment() : Fragment(), Parcelable {
    private var _binding: FragmentRoominBinding? = null
    private val binding get() = _binding!!
    private var chatRoomId: String? = null
    private var myhour : Int? = null
    private var mymin : Int? = null
    private var roomname : String? = null
    private var roomid : String? = null
    private var ampm : String? = null
    private var user = FirebaseAuth.getInstance()
    private var uid = user.uid
    private lateinit var senderId : String
    private var Nickname : String? = null
    private var mode : Int? = 2
    private var peoplecount : Int? = 0
    private var nowpeoplecount : Int? = 0
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var bank:String? = ""
    private var accountNum:String? = ""
    private lateinit var chatRoomClickId : String
    fun getUserNickname(uid: String): String? {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        val documentSnapshot = Tasks.await(usersCollection.document(uid).get())

        return if (documentSnapshot.exists()) {
            documentSnapshot.getString("Nickname")
        } else {
            null
        }
    }

    class ChatRoom(val roomname: String, val hour: Int, val min: Int)

    private fun getChatRoomName(chatRoomId: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomRef = database.child("chatRooms").child(chatRoomId)

        chatRoomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chatRoom = dataSnapshot.getValue(ChatRoom::class.java)
                val chatRoomName = chatRoom?.roomname

                if (chatRoomName != null) {
                } else {
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    data class ChatMessage(val senderId: String = "", val message: String = "")

    // 채팅 메시지 전송
    fun sendMessage(chatRoomId: String, senderId: String, message: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomRef = database.child("chatRooms").child(chatRoomId).child("messages")
        val messageRef = chatRoomRef.push()

        val chatMessage = ChatMessage(senderId, message)
        messageRef.setValue(chatMessage)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
            }
    }
    private val textViews = mutableListOf<TextView>()

    constructor(parcel: Parcel) : this() {
        chatRoomId = parcel.readString()
    }

    private var message : String? = null
    private lateinit var fragmentContext : Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
    private fun addTextView(name : String?, senderId: String, fragmentContext: Context) {
        val newTextView = AppCompatTextView(fragmentContext)

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        if(senderId == Nickname) {
            val message = "$senderId\n$name"
            val spannable = SpannableString(message)

            // senderId 부분에 대한 스타일 적용
            val startIndex = 0
            val endIndex = senderId.length
            val span = ForegroundColorSpan(Color.BLUE)
            val relativeSizeSpan = RelativeSizeSpan(0.7f)
            spannable.setSpan(span, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(relativeSizeSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            newTextView.text = spannable
            layoutParams.gravity = Gravity.END
            newTextView.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        }
        else {
            newTextView.text = "$senderId :  $name"
            layoutParams.gravity = Gravity.START
        }
        newTextView.setBackgroundColor(Color.TRANSPARENT)
        binding.scrollvw.post {
            val lastChildIndex = binding.textViewContainer.childCount - 1
            if (lastChildIndex >= 0) {
                val lastChild = binding.textViewContainer.getChildAt(lastChildIndex)
                binding.scrollvw.scrollTo(0, lastChild!!.bottom)
            }
        }
        val margin = fragmentContext.resources.getDimensionPixelSize(R.dimen.text_view_margin)
        layoutParams.setMargins(margin, margin, margin, margin)

        newTextView.textSize = 25f
        newTextView.setTextColor(Color.BLACK)

        newTextView.layoutParams = layoutParams
        newTextView.setBackgroundResource(R.drawable.bin_yellowsend)

        binding.textViewContainer.addView(newTextView)
    }
    // 채팅 메시지 수신
    fun receiveMessage(chatRoomId: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomRef = database.child("chatRooms").child(chatRoomId).child("messages")

        chatRoomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                _binding?.let { binding ->
                    binding.textViewContainer.removeAllViews()
                    textViews.clear()
                }

                for (messageSnapshot in dataSnapshot.children) {
                    val chatMessage = messageSnapshot.getValue(RoomInFragment.ChatMessage::class.java)
                    val senderId = chatMessage?.senderId
                    message = chatMessage?.message

                    if (senderId != null) {
                        addTextView(message, senderId, fragmentContext)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getChatRoomMessages(chatRoomClickId: String) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatRoomRef = database.child("chatRooms").child(chatRoomId.toString()).child("message")


        chatRoomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 채팅 내역을 가져와 처리하는 로직을 여기에 구현하세요.
                for (messageSnapshot in dataSnapshot.children) {
                    // 각 메시지의 데이터를 가져오기
                    val senderId = messageSnapshot.child("senderId").getValue(String::class.java)
                    val message = messageSnapshot.child("chatRooms").child("message").getValue(String::class.java)
                    //sendMessage(chatRoomClickId, senderId.toString(), message.toString())
                    // 채팅 내역을 화면에 표시하는 로직을 구현하세요.
                    // ...
                }
                receiveMessage(chatRoomClickId)
            }

            override fun onCancelled(error: DatabaseError) {
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
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val userEmail = firebaseAuth.currentUser?.email
        val usersCollection = firestore.collection("UserInformation")
        val query = usersCollection.whereEqualTo("Email", userEmail)
        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val userDocument = querySnapshot.documents[0]
                bank = userDocument.getString("Bank")
                accountNum = userDocument.getString("AccountNum")
            }
        }

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
                        Nickname = documentSnapshot.get("Nickname").toString()
                        senderId = Nickname.toString()
                    }
                }
                .addOnFailureListener { e ->
                    // 데이터 가져오기 실패 시 처리할 로직
                }
        }
        senderId = Nickname.toString()
        arguments?.let { bundle ->
            roomname = bundle.getString("roomname")
            myhour = bundle.getInt("hour")
            mymin = bundle.getInt("min")
            chatRoomId= bundle.getString("chatRoomId")
            chatRoomClickId = bundle.getString("chatRoomClickId").toString() // 방 조회에서 받은 id
            mode = bundle.getInt("mode")
            nowpeoplecount = bundle.getInt("peoplecount")
        }
        if(mode==0){
            chatRoomId = ""
        }
        else if(mode==1){
            chatRoomClickId=""
        }
        val navController = findNavController()

        val bundle = Bundle().apply {
            putString("roomname", roomname) // roomname 값을 Bundle에 담기
            putInt("hour", myhour!!.toInt())
            putInt("min", mymin!!.toInt())
            putString("chatRoomId", chatRoomId)
            putString("chatRoomClickId", chatRoomClickId)
            putInt("mode", mode!!.toInt())
        }
        //if(mode==0){
            //receiveMessage(chatRoomClickId.toString())
        //}
        val chatroomid = chatRoomId.toString() // 방생성에서 넘어온 Id

        binding.messages.hint = "채팅을 입력하세요"
        if(mode==0 || mode ==1) {
            Toast.makeText(requireContext(), "채팅 내역을 불러오는 중입니다...", Toast.LENGTH_SHORT).show()
        }
        var flag = 1
            binding.button3.setOnClickListener { // 메시지 전송
                if (binding.messages.text.length == 0) {
                    Toast.makeText(requireContext(), "채팅을 입력하세요!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(flag==1) {
                    Toast.makeText(requireContext(), "채팅방을 정렬중입니다..", Toast.LENGTH_SHORT).show()
                    flag = 0
                }
                val chatname = "$chatRoomId"
                val message = binding.messages.text.toString()
                if (mode == 0) {
                    getChatRoomMessages(chatRoomClickId.toString())
                    sendMessage(chatRoomClickId.toString(), senderId, message)
                    receiveMessage(chatRoomClickId.toString())
                } else if (mode == 1) {
                    getChatRoomMessages(chatroomid)
                    sendMessage(chatroomid, senderId, message)
                    receiveMessage(chatroomid)
                }
                //val senderId = uid?.let { it1 -> getUserNickname(it1) }
                //if (senderId != null) {
                // sendMessage(chatname, senderId, message)
                //}
                //receiveMessage("$chatRoomId")
                binding.messages.text.clear()
            }
            // 채팅방Id를 통해 보내는 사람과 메시지를 전달한다
        binding.exit.setOnClickListener{
            if(mode==0) {
                updateFirebaseValue(chatRoomClickId.toString())
            }
            else if(mode ==1){
                updateFirebaseValue(chatroomid)
            }
            navController.navigate(R.id.mapActivity)
        }
        binding.moneybtn.setOnClickListener {
            if(binding.moneyedt.text.length == 0){
                Toast.makeText(requireContext(), "정산금액을 입력하세요", Toast.LENGTH_SHORT).show()
            }
            else{
                if(flag==1) {
                    Toast.makeText(requireContext(), "채팅방을 정렬중입니다..", Toast.LENGTH_SHORT).show()
                    flag = 0
                }
                val chatname = "$chatRoomId"
                var totalmoney = binding.moneyedt.text.toString().toInt()
                if(nowpeoplecount==0){
                    nowpeoplecount=100
                }
                var money = totalmoney/ nowpeoplecount!!//여기서 4는 현재참여중인 인원수로 바꿔야함
                val message = "총요금 : $totalmoney\n" + "보낼금액 : $money\n" +
                        "$bank $accountNum 로 보내주세요"//계좌번호가 들어가야함
                if (mode == 0) {
                    getChatRoomMessages(chatRoomClickId.toString())
                    sendMessage(chatRoomClickId.toString(), senderId, message)
                    receiveMessage(chatRoomClickId.toString())
                } else if (mode == 1) {
                    peoplecount = 1
                    getChatRoomMessages(chatroomid)
                    sendMessage(chatroomid, senderId, message)
                    receiveMessage(chatroomid)
                }
                binding.messages.text.clear()
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { // 프래그먼트가 실행 된 이후에 보일 화면
        super.onViewCreated(view, savedInstanceState)
        val chatRoomClickId = arguments?.getString("chatRoomClickId")
        val mode = arguments?.getInt("mode")
        if (mode == 0) {
            getChatRoomMessages(chatRoomClickId.toString())
            receiveMessage(chatRoomClickId.toString())
        }
        else if(mode == 1){
            getChatRoomMessages(chatRoomId.toString())
            receiveMessage(chatRoomId.toString())
        }
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
    override fun onPause() {
        super.onPause()
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