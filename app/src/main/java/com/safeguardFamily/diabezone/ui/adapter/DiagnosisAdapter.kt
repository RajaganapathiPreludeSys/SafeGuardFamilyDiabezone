package com.safeguardFamily.diabezone.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.model.response.Diagnosi

class DiagnosisAdapter(items: List<Diagnosi>) :
    RecyclerView.Adapter<DiagnosisAdapter.ViewHolder>() {

    private val mItems: List<Diagnosi>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_diagnosis, parent, false
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
        private val tvDuration: TextView
        private val tvStatus: TextView
        fun setOnBoardingData(item: Diagnosi) {
            tvTitle.text = item.title
            tvDuration.text = "Duration - ${item.duration}"
            tvStatus.text = "Status - ${item.status}"
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvDuration = itemView.findViewById(R.id.tvDuration)
            tvStatus = itemView.findViewById(R.id.tvStatus)
        }
    }

    init {
        this.mItems = items
    }
}