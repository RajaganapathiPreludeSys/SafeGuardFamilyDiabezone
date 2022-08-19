package com.safeguardFamily.diabezone.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.common.DateUtils.formatTo12Hrs
import com.safeguardFamily.diabezone.databinding.ItemTimeBinding

class TimeAdapter(
    private val times: List<String>,
    private var tempSlot: String? = "",
    private var onItemClicked: ((time: String) -> Unit)
) : RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {

    var selectedPos = -1
    var tempPos = -1

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
            binding.item = formatTo12Hrs(times)!!.uppercase()
            if (tempSlot?.length!! > 1) {
                if (times == tempSlot) {
                    selectedPos = adapterPosition
                    tempPos = adapterPosition
                    onItemClicked(times)
                }
            }
            binding.selected = selectedPos == adapterPosition
            binding.llTimeContainer.setOnClickListener {
                if (tempSlot?.length!! > 1) {
                    tempSlot = ""
                    notifyItemChanged(tempPos)
                }
                notifyItemChanged(selectedPos)
                selectedPos = adapterPosition
                notifyItemChanged(selectedPos)
                onItemClicked(times)
            }
        }
    }
}