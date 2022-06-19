package com.yurdm.radioplayer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.yurdm.radioplayer.databinding.FragmentSearchBinding
import com.yurdm.radioplayer.repository.Repository
import com.yurdm.radioplayer.viewmodel.RadiosViewModel
import com.yurdm.radioplayer.viewmodel.RadiosViewModelFactory

class SearchFragment : Fragment() {
    lateinit var binding: FragmentSearchBinding
    lateinit var viewModel: RadiosViewModel
    lateinit var adapter: RadioRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repository = Repository()
        val factory = RadiosViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(RadiosViewModel::class.java)

        adapter = RadioRecyclerAdapter(inflater, R.layout.search_item_layout) {
            MainActivity.radio.value = it
        }

        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.searchResults.adapter = adapter
        binding.searchResults.layoutManager = LinearLayoutManager(context)

        viewModel.radioList.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                response.body()?.let { adapter.submitList(it) }
            } else {
                Toast.makeText(context, "Failed!", Toast.LENGTH_LONG).show()
            }
        }

        binding.searchBox.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.toString()?.let { q -> viewModel.search(q) }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            }
        )

        return binding.root
    }
}