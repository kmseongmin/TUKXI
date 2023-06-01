package com.example.tukxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tukxi.databinding.FragmentRoomcreateBinding
import androidx.fragment.app.Fragment

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
            val fragment = RoomInFragment() // 이동하고자 하는 Fragment의 인스턴스 생성
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.roomCreateFragment, fragment) // R.id.container는 Fragment가 표시될 레이아웃의 ID입니다.
            transaction.addToBackStack(null) // Back 버튼을 눌렀을 때 이전 Fragment로 돌아갈 수 있도록 백스택에 추가합니다.
            transaction.commit()
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