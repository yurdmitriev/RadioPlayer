package com.yurdm.radioplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.yurdm.radioplayer.MainActivity.Companion.account
import com.yurdm.radioplayer.MainActivity.Companion.gso
import com.yurdm.radioplayer.MainActivity.Companion.token
import com.yurdm.radioplayer.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val authLauncher = registerForActivityResult(LoginContract()) { result ->
            account = result
            updateUI()
        }

        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.signInButton.setOnClickListener {
            authLauncher.launch(gso)
        }

        binding.logoutBtn.setOnClickListener {
            val signInClient = GoogleSignIn.getClient(inflater.context, gso)
            signInClient.signOut().addOnCompleteListener {
                account = null
                token = null
                updateUI()
            }
        }

        updateUI()

        return binding.root
    }

    private fun updateUI() {
        if (account == null) {
            binding.authorized.visibility = View.GONE
            binding.unauthorized.visibility = View.VISIBLE
        } else {
            binding.authorized.visibility = View.VISIBLE
            binding.unauthorized.visibility = View.GONE
            binding.userName.text = account?.displayName
            binding.userEmail.text = account?.email
            Glide.with(this).load(account?.photoUrl).fitCenter().into(binding.userImage)
        }
    }
}