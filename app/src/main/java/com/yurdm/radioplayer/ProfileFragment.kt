package com.yurdm.radioplayer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.yurdm.radioplayer.MainActivity.Companion.RC_SIGN_IN
import com.yurdm.radioplayer.MainActivity.Companion.account
import com.yurdm.radioplayer.MainActivity.Companion.signInIntent
import com.yurdm.radioplayer.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.signInButton.setOnClickListener {
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.logoutBtn.setOnClickListener {
            val signInClient = GoogleSignIn.getClient(inflater.context, MainActivity.gso)
            signInClient.signOut().addOnCompleteListener {
                account = null
                updateUI()
            }
        }

        updateUI()

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                account = task.getResult(ApiException::class.java)
                updateUI()
            } catch (e: ApiException) {
                Toast.makeText(context, "Auth failed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUI() {
        if (account == null) {
            binding.authorized.visibility = View.GONE
            binding.unauthorized.visibility = View.VISIBLE
//            binding.authorized.alpha = 0F
//            binding.unauthorized.alpha = 1F
        } else {
            binding.authorized.visibility = View.VISIBLE
            binding.unauthorized.visibility = View.GONE
//            binding.authorized.alpha = 1F
//            binding.unauthorized.alpha = 0F
            binding.userName.text = account?.displayName
            binding.userEmail.text = account?.email
            Glide.with(this).load(account?.photoUrl).fitCenter().into(binding.userImage)
        }
    }
}