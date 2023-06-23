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
        setUpNavigation()

    }
    private fun setUpNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment

        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener{ _, destination, _ ->
        }
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        binding.bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mapActivity_item -> {
                    // 프레그먼트1로 이동
                    navController.popBackStack(R.id.mapActivity, false)
                    navController.navigate(R.id.mapActivity)
                    true
                }
                R.id.roomViewFragment_item -> {
                    // 프레그먼트2로 이동
                    navController.popBackStack(R.id.roomViewFragment, false)
                    navController.navigate(R.id.roomViewFragment)
                    true
                }
                R.id.currentRoomFragment_item -> {
                    // 프레그먼트3로 이동
                    navController.popBackStack(R.id.currentRoomFragment, false)
                    navController.navigate(R.id.currentRoomFragment)
                    true
                }
                R.id.myFragment_item -> {
                    // 프레그먼트4로 이동
                    navController.popBackStack(R.id.myFragment, false)
                    navController.navigate(R.id.myFragment)
                    true
                }
                else -> false
            }
        }
    }
}

