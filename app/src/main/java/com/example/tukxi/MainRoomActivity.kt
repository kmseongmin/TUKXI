package com.example.tukxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.tukxi.databinding.ActivityMainroomBinding

class MainRoomActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainroomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainroomBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
    private fun onSetUpNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment

        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener{ _, destination, _ ->
        }
        NavigationUI.setupWithNavController(binding.bottomNav, navController)
    }
}

