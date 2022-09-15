package com.safeguardFamily.diabezone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.model.response.EmergencyContact

class EmergencyContactAdapter(items: List<EmergencyContact>) :
    RecyclerView.Adapter<EmergencyContactAdapter.ViewHolder>() {

    private val mItems: List<EmergencyContact>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_emergency_contact, parent, false
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
        private val tvContactCount: TextView
        private val tvName: TextView
        private val tvRelation: TextView
        private val tvContact1: TextView
        private val tvContact2: TextView
        fun setOnBoardingData(item: EmergencyContact) {
            tvContactCount.text = "Emergency Contact ${adapterPosition + 1}"
            tvName.text = item.name
            tvRelation.text = item.relation
            tvContact1.text = item.mobile1
            tvContact2.text = item.mobile2
        }

        init {
            tvContactCount = itemView.findViewById(R.id.tvContactCount)
            tvName = itemView.findViewById(R.id.tvName)
            tvRelation = itemView.findViewById(R.id.tvRelation)
            tvContact1 = itemView.findViewById(R.id.tvContact1)
            tvContact2 = itemView.findViewById(R.id.tvContact2)
        }
    }

    init {
        this.mItems = items
    }
}