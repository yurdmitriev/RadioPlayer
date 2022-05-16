package com.yurdm.radioplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RadioRecyclerAdapter(
    private val inflater: LayoutInflater,
    private val onClick: (MainActivity.Radio) -> Unit
) : ListAdapter<MainActivity.Radio, RadioRecyclerAdapter.ViewHolder>(RadioDiffCallback) {
    class ViewHolder(view: View, onClick: (MainActivity.Radio) -> Unit) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.tv_title)
        private val logo = view.findViewById<ImageView>(R.id.tv_logo)
        private var radio: MainActivity.Radio? = null

        init {
            view.setOnClickListener {
                radio?.let {
                    onClick(it)
                }
            }
        }

        fun bind(radio: MainActivity.Radio) {
            this.radio = radio
            title.text = radio.title
            Glide.with(this.itemView).load(radio.logo).fitCenter().into(this.logo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.radio_item_layout, parent, false)

        return ViewHolder(view, onClick);
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val radio = getItem(position)
        holder.bind(radio)
    }

    object RadioDiffCallback : DiffUtil.ItemCallback<MainActivity.Radio>() {
        override fun areItemsTheSame(
            oldItem: MainActivity.Radio,
            newItem: MainActivity.Radio
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MainActivity.Radio,
            newItem: MainActivity.Radio
        ): Boolean {
            return oldItem.id == newItem.id && oldItem.title == newItem.title &&
                    oldItem.logo == newItem.logo && oldItem.stream == newItem.stream
        }

    }
}