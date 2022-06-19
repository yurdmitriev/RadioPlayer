package com.yurdm.radioplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yurdm.radioplayer.model.Category
import com.yurdm.radioplayer.model.Radio

class CategoryRecyclerAdapter(
    private val inflater: LayoutInflater,
    private val itemClick: (Radio) -> Unit
) : ListAdapter<Category, CategoryRecyclerAdapter.ViewHolder>(CategoryDiffCallback) {
    class ViewHolder(view: View, inflater: LayoutInflater, onClick: (Radio) -> Unit) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.categoryTitle)
        private val list = view.findViewById<RecyclerView>(R.id.radioList)
        private var category: Category? = null
        private var adapter: RadioRecyclerAdapter
        private var layoutManager: LinearLayoutManager

        init {
            adapter = RadioRecyclerAdapter(inflater, onClick = onClick)
            layoutManager = LinearLayoutManager(inflater.context, LinearLayoutManager.HORIZONTAL, false)
        }

        fun bind(category: Category) {
            this.category = category
            title.text = category.title
            list.adapter = adapter
            list.layoutManager = layoutManager

            adapter.submitList(category.items)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.category_item_layout, parent, false)

        return ViewHolder(view, inflater, itemClick);
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    object CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(
            oldItem: Category,
            newItem: Category
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Category,
            newItem: Category
        ): Boolean {
            return oldItem.id == newItem.id
        }

    }
}