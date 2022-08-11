package com.safeguardFamily.diabezone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.databinding.ItemTimeBinding

class TimeAdapter(
    private val times: List<String>
) : RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {

    var selectedPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        return TimeViewHolder(
            ItemTimeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(times[position])
    }

    override fun getItemCount(): Int = times.size

    inner class TimeViewHolder(
        private val binding: ItemTimeBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(times: String) {
            binding.item = times
            binding.selected = selectedPos == adapterPosition
            binding.llTimeContainer.setOnClickListener {
                notifyItemChanged(selectedPos)
                selectedPos = adapterPosition
                notifyItemChanged(selectedPos)
            }
        }
    }
}