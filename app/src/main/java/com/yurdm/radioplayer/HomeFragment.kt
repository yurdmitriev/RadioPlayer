package com.yurdm.radioplayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yurdm.radioplayer.repository.Repository
import com.yurdm.radioplayer.viewmodel.HomeViewModel
import com.yurdm.radioplayer.viewmodel.HomeViewModelFactory

class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = Repository()
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewModel.listRadios()
        viewModel.res.observe(viewLifecycleOwner) { response ->
            Log.d("Response", response[0].toString())
        }

        return view
    }
}