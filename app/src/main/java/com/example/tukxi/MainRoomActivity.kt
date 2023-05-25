package com.example.tukxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tukxi.databinding.ActivityMainroomBinding

class MainRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnmap.setOnClickListener{
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainRoom, MapActivity())
                .commit()
        }
    }
}

