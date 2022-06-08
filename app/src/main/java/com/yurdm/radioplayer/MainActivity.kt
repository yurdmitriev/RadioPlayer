package com.yurdm.radioplayer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.yurdm.radioplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        binding.bottomNav.setupWithNavController(fragment.navController)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        val signInClient = GoogleSignIn.getClient(this, gso)
        signInIntent = signInClient.signInIntent

        account = GoogleSignIn.getLastSignedInAccount(this);
    }

    companion object {
        lateinit var gso: GoogleSignInOptions
        lateinit var signInIntent: Intent
        var account: GoogleSignInAccount? = null
        const val RC_SIGN_IN = 1
    }
}