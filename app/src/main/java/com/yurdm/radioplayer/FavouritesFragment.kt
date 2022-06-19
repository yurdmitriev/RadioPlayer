package com.yurdm.radioplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yurdm.radioplayer.MainActivity.Companion.token
import com.yurdm.radioplayer.databinding.FragmentFavouritesBinding
import com.yurdm.radioplayer.repository.Repository
import com.yurdm.radioplayer.viewmodel.RadiosViewModel
import com.yurdm.radioplayer.viewmodel.RadiosViewModelFactory

class FavouritesFragment : Fragment() {
    lateinit var binding: FragmentFavouritesBinding
    lateinit var viewModel: RadiosViewModel
    lateinit var adapter: RadioRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repository = Repository()
        val factory = RadiosViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(RadiosViewModel::class.java)
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)

        adapter = RadioRecyclerAdapter(inflater, R.layout.search_item_layout) {
            MainActivity.radio.value = it
        }

        binding.favouritesList.adapter = adapter
        binding.favouritesList.layoutManager = LinearLayoutManager(context)

//        if (token != null) {
//            viewModel.listFavourites(token!!)
//        } else {
//            // TODO: Not authorized
//        }

        viewModel.listRadios()

        viewModel.radioList.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                response.body()?.let { adapter.submitList(it) }
            } else {
                Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show()
                println(response)
            }
        }

        return binding.root
    }
}