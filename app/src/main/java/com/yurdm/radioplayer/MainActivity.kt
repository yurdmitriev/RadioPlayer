package com.yurdm.radioplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kittinunf.fuel.Fuel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yurdm.radioplayer.databinding.ActivityMainBinding
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: RadioRecyclerAdapter
    private val list: MutableList<Radio> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RadioRecyclerAdapter(layoutInflater) {
            val intent = Intent(this, RadioActivity::class.java)
            intent.putExtra("title", it.title)
            intent.putExtra("logo", it.logo)
            intent.putExtra("stream", it.stream)
            startActivity(intent)
        }

        binding.list.adapter = adapter
        binding.list.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()

        Fuel.get("https://radioplayer-api.herokuapp.com/api/radio").responseString { result ->
            val json = result.get()
            val typeToken = object : TypeToken<List<Radio>>() {}.type
            val radios = Gson().fromJson<List<Radio>>(json, typeToken)

            list.clear()
            list += radios

            adapter.submitList(list.toList())
        }
    }

    data class Radio(val id: Int, val title: String, val stream: String, val logo: String = "")
}