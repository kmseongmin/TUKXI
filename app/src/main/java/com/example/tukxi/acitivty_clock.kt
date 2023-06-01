package com.example.tukxi

import android.os.Bundle
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity

class acitivty_clock: AppCompatActivity() {
    var time_picker: TimePicker? = null
    var tv_time: TextView? = null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)

        //시간 연결
        time_picker = findViewById<TimePicker>(R.id.time_picker)
        tv_time = findViewById<TextView>(R.id.tv_time)

        //TimePicker 클릭 이벤트
        time_picker!!.setOnTimeChangedListener { timePicker, hour, min -> //오전 오후 확인하기 위한 if문
            var hour = hour
            if (hour > 12) {
                hour -= 12
                tv_time!!.text = "PM" + hour + "시" + min + "분 선택"
            } else {
                tv_time!!.text = "AM" + hour + "시" + min + "분 선택"
            }
        }
    }
}

