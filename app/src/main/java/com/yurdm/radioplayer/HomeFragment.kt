package com.yurdm.radioplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yurdm.radioplayer.databinding.FragmentHomeBinding
import com.yurdm.radioplayer.repository.Repository
import com.yurdm.radioplayer.viewmodel.HomeViewModel
import com.yurdm.radioplayer.viewmodel.HomeViewModelFactory

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var categoryAdapter: CategoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = Repository()
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        categoryAdapter = CategoryRecyclerAdapter(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.categoriesList.adapter = categoryAdapter
        binding.categoriesList.layoutManager = LinearLayoutManager(context)

        viewModel.listCategories()
        viewModel.categoryList.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                response.body()?.let { categoryAdapter.submitList(it) }
            } else {
                Toast.makeText(context, "Failed!", Toast.LENGTH_LONG).show()
            }
        }

        binding.swipeContainer.setOnRefreshListener {
            viewModel.listCategories()
            binding.swipeContainer.isRefreshing = false
        }

        return binding.root
    }
}