package com.yurdm.radioplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.yurdm.radioplayer.databinding.ActivityRadioBinding

class RadioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityRadioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val logoUrl = intent.getStringExtra("logo") ?: ""

        Glide.with(binding.root).load(logoUrl).placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.imageView)

        binding.tvTitle.text = intent.getStringExtra("title") ?: ""
        binding.tvStream.text = intent.getStringExtra("stream") ?: ""
    }
}