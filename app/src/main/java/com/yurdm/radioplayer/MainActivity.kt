package com.yurdm.radioplayer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.yurdm.radioplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        binding.bottomNav.setupWithNavController(fragment.navController)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestServerAuthCode(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            token = this.getSharedPreferences("credentials", Context.MODE_PRIVATE)
                .getString("token", null)
        }
    }

    companion object {
        lateinit var gso: GoogleSignInOptions
        var token: String? = null
        var account: GoogleSignInAccount? = null
    }
}