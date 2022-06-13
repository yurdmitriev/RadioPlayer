package com.yurdm.radioplayer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.yurdm.radioplayer.MainActivity.Companion.account
import com.yurdm.radioplayer.MainActivity.Companion.gso
import com.yurdm.radioplayer.MainActivity.Companion.token
import com.yurdm.radioplayer.databinding.FragmentProfileBinding
import com.yurdm.radioplayer.repository.Repository
import com.yurdm.radioplayer.viewmodel.ProfileViewModel
import com.yurdm.radioplayer.viewmodel.ProfileViewModelFactory
import okhttp3.Headers
import retrofit2.http.Header


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repository = Repository()
        val factory = ProfileViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        val authLauncher = registerForActivityResult(LoginContract()) { result ->
            account = result
            viewModel.receiveToken(result?.serverAuthCode.toString())

            viewModel.res.observe(viewLifecycleOwner) { response ->

                if (response.isSuccessful) {
                    val headers: Headers = response.headers()
                    token = headers["Access-Token"]
                    val pref = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
                    with(pref.edit()) {
                        putString("token", token)
                        apply()
                    }
                    updateUI()
                } else {
                    println(response.code())
                }
            }
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
                val pref = requireActivity().getSharedPreferences("credentials", Context.MODE_PRIVATE)
                with(pref.edit()) {
                    putString("token", null)
                    apply()
                }
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
            binding.apiToken.text = token
            Glide.with(this).load(account?.photoUrl).fitCenter().into(binding.userImage)
        }
    }
}