package com.safeguardFamily.diabezone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.model.ProfessorModel

class NotificationAdapter(items: List<ProfessorModel>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder?>() {

    private val mItems: List<ProfessorModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_notification, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.setOnBoardingData(mItems[position])
    }

    override fun getItemCount(): Int {return mItems.size }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView
        private val tvDesc: TextView
        private val ivProfileImage: ImageView
        fun setOnBoardingData(item: ProfessorModel) {
            tvTitle.text = item.title
            tvDesc.text = item.desc
            Glide.with(itemView.context).load(item.image).into(ivProfileImage)
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvDesc = itemView.findViewById(R.id.tvDesc)
            ivProfileImage = itemView.findViewById(R.id.ivProfileImages)
        }
    }

    init {
        this.mItems = items
    }
}