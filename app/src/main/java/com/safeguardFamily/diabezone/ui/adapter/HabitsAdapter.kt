package com.safeguardFamily.diabezone.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R

class HabitsAdapter(items: List<String>) :
    RecyclerView.Adapter<HabitsAdapter.ViewHolder>() {

    private val mItems: List<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_history, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setOnBoardingData(mItems[position])
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView
        fun setOnBoardingData(item: String) {
            tvTitle.text = item
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvText)
        }
    }

    init {
        this.mItems = items
    }
}