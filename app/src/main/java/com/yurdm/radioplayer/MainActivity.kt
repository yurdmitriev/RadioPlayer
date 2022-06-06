package com.yurdm.radioplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yurdm.radioplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val navController = binding.fragment.findNavController()
        val navController = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        binding.bottomNav.setupWithNavController(navController.navController)
    }
}