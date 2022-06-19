package com.yurdm.radioplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.yurdm.radioplayer.databinding.ActivityMainBinding
import com.yurdm.radioplayer.model.Radio


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var playerBinder: MediaPlayerService.PlayerServiceBinder? = null
    private var mediaController: MediaControllerCompat? = null
    private var callback: MediaControllerCompat.Callback? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            playerBinder = service as MediaPlayerService.PlayerServiceBinder

            try {
                mediaController = MediaControllerCompat(
                    this@MainActivity, playerBinder!!.mediaSessionToken
                )

                callback = object : MediaControllerCompat.Callback() {
                    override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                        if (state.state == PlaybackStateCompat.STATE_STOPPED) {
                            binding.homeRoot.transitionToState(R.id.initial)
                        } else {
                            binding.homeRoot.transitionToState(R.id.mini)
                        }
                    }
                }

                mediaController?.let { controller ->
                    binding.playerStop.setOnClickListener {
                        controller.transportControls.stop()
                        radio.value = null
                    }
                }

                mediaController!!.registerCallback(callback!!)

                Toast.makeText(this@MainActivity, "Service Bound", Toast.LENGTH_SHORT).show()
            } catch (e: RemoteException) {
                mediaController = null
                Toast.makeText(this@MainActivity, "Empty controller", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            playerBinder = null
            mediaController = null
        }
    }

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

        binding.homeRoot.transitionToState(R.id.initial)

        radio.observe(this) {
            if (radio.value != null) {
                Glide.with(this).load(it?.logo).optionalFitCenter().into(binding.playerThumb)
                binding.playerTitle.text = it?.title
                Intent(this@MainActivity, MediaPlayerService::class.java).apply {
                    this.action = "PLAY"
                    val bundle = Bundle()
                    bundle.putSerializable("radio", it)
                    this.putExtra("radio", it)
                    startService(this)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        Intent(this, MediaPlayerService::class.java).also { intent ->
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()

        playerBinder = null
        mediaController!!.unregisterCallback(callback!!)
        mediaController = null
        unbindService(serviceConnection)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        lateinit var gso: GoogleSignInOptions
        var radio: MutableLiveData<Radio?> = MutableLiveData()
        var token: String? = null
        var account: GoogleSignInAccount? = null
    }
}