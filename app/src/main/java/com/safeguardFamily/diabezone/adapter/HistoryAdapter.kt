package com.safeguardFamily.diabezone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.model.response.History

class HistoryAdapter(items: List<History>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val mItems: List<History>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_history_vault, parent, false
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
        private val tvName: TextView
        private val tvDate: TextView
        private val tvTime: TextView
        private val tvHospital: TextView
        private val ivAlert: ImageView
        fun setOnBoardingData(item: History) {
            tvTitle.text = item.title
            tvName.text = item.preparedBy
            tvHospital.text = item.hospital
            tvDate.text = DateUtils.displayingDateFormatTwoFromAPIDateTime(item.reportDate!!)
            tvTime.text = DateUtils.displayingTimeFormat(item.reportDate!!)
            if (item.isAlert!!) {
                tvTitle.setTextColor(itemView.rootView.context.getColor(R.color.red))
                ivAlert.setImageDrawable(itemView.rootView.context.getDrawable(R.drawable.ic_red_drop))
            } else {
                tvTitle.setTextColor(itemView.rootView.context.getColor(R.color.blue))
                ivAlert.setImageDrawable(null)
            }
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvName = itemView.findViewById(R.id.tvName)
            tvDate = itemView.findViewById(R.id.tvDate)
            tvTime = itemView.findViewById(R.id.tvTime)
            tvHospital = itemView.findViewById(R.id.tvHospital)
            ivAlert = itemView.findViewById(R.id.ivAlert)
        }
    }

    init {
        this.mItems = items
    }
}