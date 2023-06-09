package com.example.tukxi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.tukxi.databinding.FragmentMyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MyFragment : Fragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!
    private lateinit var nicknameTextview: TextView
    private lateinit var emailTextView: TextView
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
        emailTextView = view.findViewById(R.id.tv_userEmail)
        myInfoButton = view.findViewById(R.id.btn_myInfo)
        logoutButton = view.findViewById(R.id.btn_Logout)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore  = FirebaseFirestore.getInstance()

        val sharedPreferences = requireContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val userEmail = firebaseAuth.currentUser?.email
        emailTextView.text = userEmail
        if(userEmail!=null){
            firestore.collection("UserInformation")
                .whereEqualTo("Email",userEmail)
                .get()
                .addOnSuccessListener { querySnapshot->
                    if(!querySnapshot.isEmpty){
                        val document = querySnapshot.documents[0]
                        nicknameTextview.text = document.getString("Nickname")
                    }
                }
        }

        myInfoButton.setOnClickListener{
            startActivity(Intent(activity,MyInfoActivity::class.java))
        }
        logoutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            editor.putBoolean("autoLogin",false).clear().commit()
            val intent = Intent(activity,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
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