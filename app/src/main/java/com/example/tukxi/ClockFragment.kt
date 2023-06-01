package com.example.tukxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tukxi.databinding.FragmentClockBinding

class ClockFragment: Fragment() {
    var time_picker: TimePicker? = null
    var tv_time: TextView? = null
    private var _binding: FragmentClockBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentClockBinding.inflate(inflater, container, false)
        val view = binding.root
        val bundle = Bundle()
        //time_picker = findViewById<TimePicker>(R.id.time_picker)
        time_picker = binding.timePicker
        //tv_time = findViewById<TextView>(R.id.tv_time)
        tv_time = binding.tvTime
        //TimePicker 클릭 이벤트
        time_picker!!.setOnTimeChangedListener { timePicker, hour, min -> //오전 오후 확인하기 위한 if문
            var hour = hour
            if (hour > 12) {
                hour -= 12
                tv_time!!.text = "PM" + hour + "시" + min + "분 선택"
            } else {
                tv_time!!.text = "AM" + hour + "시" + min + "분 선택"
            }
            val myhour = hour
            bundle.putInt("hour",myhour)
            bundle.putInt("min",min)
        }

        binding.setButton.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.roomCreateFragment, bundle)
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


