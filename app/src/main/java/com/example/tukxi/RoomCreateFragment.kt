package com.example.tukxi

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tukxi.databinding.FragmentRoomcreateBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

class RoomCreateFragment : Fragment(){
    private var _binding: FragmentRoomcreateBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoomcreateBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.button2.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.roomInFragment)
        }
        binding.btnTime.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.clockFragment)
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