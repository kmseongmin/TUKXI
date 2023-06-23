package com.example.tukxi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tukxi.databinding.FragmentMyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.math.log


class MyFragment : Fragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!
    private lateinit var nicknameTextview: TextView
    private lateinit var bankTextView: TextView
    private lateinit var myInfoButton :Button
    private lateinit var logoutButton : Button
    private lateinit var autoLoginCheckBox: CheckBox
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        val view = binding.root
        nicknameTextview= view.findViewById(R.id.tv_UserNickname)
        bankTextView = view.findViewById(R.id.tv_bank)
        myInfoButton = view.findViewById(R.id.btn_myInfo)
        logoutButton = view.findViewById(R.id.btn_Logout)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore  = FirebaseFirestore.getInstance()

        val sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val userEmail = firebaseAuth.currentUser?.email

        if(userEmail!=null){
            firestore.collection("UserInformation")
                .whereEqualTo("Email",userEmail)
                .get()
                .addOnSuccessListener { querySnapshot->
                    if(!querySnapshot.isEmpty){
                        val document = querySnapshot.documents[0]
                        nicknameTextview.text = document.getString("Nickname")
                        bankTextView.text=document.getString("Bank")+"  "+document.getString("AccountNum")
                    }
                }
        }

        myInfoButton.setOnClickListener{
            //startActivity(Intent(activity,MyInfoActivity::class.java))
        }
        logoutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            editor.putBoolean("autoLogin",false).clear().commit()
            startActivity(Intent(activity,LoginActivity::class.java))
            activity?.finish()
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