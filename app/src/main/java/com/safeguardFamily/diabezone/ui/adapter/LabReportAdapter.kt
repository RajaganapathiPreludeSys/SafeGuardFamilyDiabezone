package com.safeguardFamily.diabezone.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.common.Bundle.TAG
import com.safeguardFamily.diabezone.common.DateUtils
import com.safeguardFamily.diabezone.model.response.LabReport

class LabReportAdapter(
    items: List<LabReport>,
    private var onItemClicked: ((time: String) -> Unit)
) : RecyclerView.Adapter<LabReportAdapter.ViewHolder>() {

    private val mItems: List<LabReport>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_lab_report, parent, false
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
        private val tvPreparedBy: TextView
        private val tvDate: TextView
        private val tvTime: TextView
        private val tvAlert: TextView
        private val ivAlert: ImageView
        private val ivNext: ImageView
        private val llAlert: RelativeLayout
        fun setOnBoardingData(item: LabReport) {
            tvTitle.text = item.title
            tvPreparedBy.text = item.preparedBy
            tvDate.text = DateUtils.displayingDateFormatTwoFromAPIDateTime(item.reportDate!!)
            tvTime.text = DateUtils.displayingTimeFormat(item.reportDate!!)
            if (item.comment!!.length > 2) {
                llAlert.visibility = View.VISIBLE
                tvAlert.text = item.comment
                print("RRR == ${item.isAlert} == ${adapterPosition}")
                Log.d(TAG, "RRR == ${item.isAlert} == $adapterPosition")
                if (item.isAlert!!) {
                    tvAlert.setTextColor(itemView.rootView.context.getColor(R.color.red))
                    ivAlert.setImageDrawable(itemView.rootView.context.getDrawable(R.drawable.ic_red_drop))
                } else {
                    tvAlert.setTextColor(itemView.rootView.context.getColor(R.color.grey))
                    ivAlert.setImageDrawable(itemView.rootView.context.getDrawable(R.drawable.ic_comment))
                }
            }
            ivNext.setOnClickListener {
                onItemClicked(item.pdfUrl!!)

                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
                    param(
                        FirebaseAnalytics.Param.CONTENT,
                        "Open PDF from Health Vault Lab report ${item.title}"
                    )
                }
            }
        }

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvPreparedBy = itemView.findViewById(R.id.tvPreparedBy)
            tvDate = itemView.findViewById(R.id.tvDate)
            tvTime = itemView.findViewById(R.id.tvTime)
            tvAlert = itemView.findViewById(R.id.tvAlert)
            ivAlert = itemView.findViewById(R.id.ivAlert)
            ivNext = itemView.findViewById(R.id.ivNext)
            llAlert = itemView.findViewById(R.id.llAlert)
        }
    }

    init {
        this.mItems = items
    }
}