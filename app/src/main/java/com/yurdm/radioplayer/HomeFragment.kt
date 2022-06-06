package com.yurdm.radioplayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.yurdm.radioplayer.databinding.FragmentHomeBinding
import com.yurdm.radioplayer.repository.Repository
import com.yurdm.radioplayer.viewmodel.HomeViewModel
import com.yurdm.radioplayer.viewmodel.HomeViewModelFactory

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: RadioRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = Repository()
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        adapter = RadioRecyclerAdapter(layoutInflater) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.inflate(inflater, view.rootView as ViewGroup?, false)

        binding.radioList.adapter = adapter
        binding.radioList.layoutManager = GridLayoutManager(context, 2)

        viewModel.listRadios()
        viewModel.res.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                response.body()?.let { adapter.submitList(it) }
            } else {
                Toast.makeText(context, "Failed!", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }
}