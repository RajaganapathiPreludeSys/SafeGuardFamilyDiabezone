package com.safeguardFamily.diabezone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.model.response.Notification

class NotificationAdapter(
    items: List<Notification>,
    private var onItemClicked: ((time: String) -> Unit)
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder?>() {

    private val mItems: List<Notification>

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

    override fun getItemCount(): Int {
        return mItems.size
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView
        private val tvDesc: TextView
        private val ivProfileImage: ImageView
        private val rlContainer: RelativeLayout

        fun setOnBoardingData(item: Notification) {
            tvTitle.text = item.title
            tvDesc.text = item.ndesc
            Glide.with(itemView.context).load(item.pic).into(ivProfileImage)
            rlContainer.setOnClickListener {
                onItemClicked(item.screen!!)
            }
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvDesc = itemView.findViewById(R.id.tvDesc)
            ivProfileImage = itemView.findViewById(R.id.ivProfileImages)
            rlContainer = itemView.findViewById(R.id.rlContainer)

        }
    }

    init {
        this.mItems = items
    }
}