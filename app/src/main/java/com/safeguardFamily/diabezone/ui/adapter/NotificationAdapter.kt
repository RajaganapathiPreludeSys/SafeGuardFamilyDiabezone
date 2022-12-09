package com.safeguardFamily.diabezone.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.model.response.Notification

class NotificationAdapter(
    items: List<Notification>,
    private var onItemClicked: ((time: String) -> Unit)
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder?>() {

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
        private val tvDate: TextView
        private val tvTime: TextView
        private val rlContainer: LinearLayout
        private val llDateTimeContainer: LinearLayout

        fun setOnBoardingData(item: Notification) {
            tvTitle.text = item.title
            if (item.ndate!!.length > 1) {
                tvDesc.visibility = View.GONE
                llDateTimeContainer.visibility = View.VISIBLE
                tvDate.text = DateUtils.displayingDateFromAPI(item.ndate!!)
                tvTime.text = DateUtils.displayingTimeFromAPI(item.ndate!!)
            } else {
                tvDesc.visibility = View.VISIBLE
                llDateTimeContainer.visibility = View.GONE
                tvDesc.text = item.ndesc
            }

            rlContainer.setOnClickListener {
                onItemClicked(item.screen!!)
            }
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvDesc = itemView.findViewById(R.id.tvDesc)
            tvDate = itemView.findViewById(R.id.tvDate)
            tvTime = itemView.findViewById(R.id.tvTime)
            rlContainer = itemView.findViewById(R.id.rlContainer)
            llDateTimeContainer = itemView.findViewById(R.id.llDateTimeContainer)
        }
    }

    init {
        this.mItems = items
    }
}