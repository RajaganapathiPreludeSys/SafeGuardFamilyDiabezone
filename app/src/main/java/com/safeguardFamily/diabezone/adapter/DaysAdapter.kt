package com.safeguardFamily.diabezone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.databinding.ItemDaysBinding

class DaysAdapter(items: List<String>) :
    RecyclerView.Adapter<DaysAdapter.ViewHolder?>() {

    private val mItems: List<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemDaysBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(mItems[position])

    override fun getItemCount() = mItems.size

    class ViewHolder(private val binding: ItemDaysBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.tvDay.text = item
        }
    }

    init {
        this.mItems = items
    }

}