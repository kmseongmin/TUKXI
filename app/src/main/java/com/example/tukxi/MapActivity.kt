package com.example.tukxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tukxi.databinding.ActivityMapBinding

class MapActivity : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container : ViewGroup?,
                              savedInstanceState: Bundle?) : View?{

        return inflater.inflate(R.layout.activity_map, container, false)
    }
}